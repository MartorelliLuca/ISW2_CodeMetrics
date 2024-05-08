package metrics_computer;

import model.ClassInfo;
import model.VersionInfo;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.IOException;
import java.util.*;

import static metrics_computer.ChangedLocComputer.TouchedLocEnum.ADDED;
import static metrics_computer.ChangedLocComputer.TouchedLocEnum.DELETED;

public class ChangedLocComputer {

    private Repository repo ;
    private Git git ;

    enum TouchedLocEnum {
        ADDED,
        DELETED
    }

    public ChangedLocComputer(Repository repo, Git git) {
        this.repo = repo ;
        this.git = git ;
    }

    public void computeChangedLocMetrics(List<VersionInfo> versionInfoList) throws GitAPIException, IOException {

        for (VersionInfo versionInfo : versionInfoList) {
            computeChangedLocMetricsForVersion(versionInfo);
        }
    }

    private void computeChangedLocMetricsForVersion(VersionInfo versionInfo) throws GitAPIException, IOException {
        List<ClassInfo> classInfoList = versionInfo.getClassInfoList() ;
        List<RevCommit> commitList = versionInfo.getVersionCommitList() ;
        for (ClassInfo classInfo : classInfoList) {
            computeChangedLocMetricsForClass(classInfo, commitList);
        }
    }

    private void computeChangedLocMetricsForClass(ClassInfo classInfo, List<RevCommit> revCommitList) throws GitAPIException, IOException {
        List<Integer> addedList = new ArrayList<>() ;
        List<Integer> deletedList = new ArrayList<>() ;

        for (RevCommit commit : revCommitList) {
            Map<TouchedLocEnum, Integer> touchedLocMap = computeAddedAndDeletedLocInRevision(classInfo, commit) ;
            addedList.add(touchedLocMap.get(ADDED)) ;
            deletedList.add(touchedLocMap.get(DELETED)) ;
        }

        computeAddedLocMetrics(classInfo, addedList) ;
        computeTouchedLocMetrics(classInfo, addedList, deletedList) ;
        computeChurnMetrics(classInfo, addedList, deletedList) ;

    }

    private void computeChurnMetrics(ClassInfo classInfo, List<Integer> addedList, List<Integer> deletedList) {
        List<Integer> churnList = new ArrayList<>() ;
        for (int i = 0 ; i < addedList.size() ; i++) {
            int added = addedList.get(i) ;
            int deleted = deletedList.get(i) ;
            churnList.add(added - deleted) ;
        }

        int totalChurn = 0 ;
        int maxChurn = 0 ;
        float avgChurn ;

        for (Integer churn : churnList) {
            totalChurn += churn ;
            if (churn > maxChurn) {
                maxChurn = churn ;
            }
        }

        avgChurn = ((float) totalChurn) / addedList.size() ;

        classInfo.setChurn(totalChurn);
        classInfo.setMaxChurn(maxChurn);
        classInfo.setAvgChurn(avgChurn);

    }

    private void computeTouchedLocMetrics(ClassInfo classInfo, List<Integer> addedList, List<Integer> deletedList) {
        int touchedLoc = 0 ;
        for (Integer added : addedList) {
            touchedLoc = touchedLoc + added ;
        }
        for (Integer deleted : deletedList) {
            touchedLoc = touchedLoc + deleted ;
        }

        classInfo.setTouchedLoc(touchedLoc);
    }

    private void computeAddedLocMetrics(ClassInfo classInfo, List<Integer> addedList) {
        int totalAdded = 0 ;
        int maxAdded = 0 ;
        float avgAdded ;

        for (Integer added : addedList) {
            totalAdded = totalAdded + added ;
            if (added > maxAdded) {
                maxAdded = added ;
            }
        }

        avgAdded = ((float) totalAdded) / addedList.size() ;

        classInfo.setAddedLoc(totalAdded);
        classInfo.setMaxAddedLoc(maxAdded);
        classInfo.setAvgAddedLoc(avgAdded);
    }

    public Map<TouchedLocEnum, Integer> computeAddedAndDeletedLocInRevision(ClassInfo classInfo, RevCommit commit) throws IOException, GitAPIException {
        Integer addedLines = 0;
        Integer deletedLines = 0 ;

        if (commit.getParentCount() == 0) {
            try(TreeWalk treeWalk = new TreeWalk(repo)) {
                treeWalk.setFilter(PathFilter.create(classInfo.getName()));
                treeWalk.reset(commit.getTree().getId());
                treeWalk.setRecursive(true);

                if (treeWalk.next()) {
                    // File creato al primo commit del progetto
                    addedLines += classInfo.getLoc() ;
                }
            }
        }

        else {
            ObjectReader reader = repo.newObjectReader();
            CanonicalTreeParser commitTreeParser = new CanonicalTreeParser();
            commitTreeParser.reset(reader, commit.getTree());

            RevCommit prevCommit = commit.getParent(0);
            CanonicalTreeParser prevCommitTreeParser = new CanonicalTreeParser(null, reader, prevCommit.getTree());

            DiffCommand diffCommand = git.diff()
                    .setNewTree(commitTreeParser)
                    .setOldTree(prevCommitTreeParser)
                    .setShowNameAndStatusOnly(true)
                    .setPathFilter(PathFilter.create(classInfo.getName()));

            List<DiffEntry> diffEntryList = diffCommand.call();

            try (DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
                diffFormatter.setRepository(repo);
                diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
                diffFormatter.setDetectRenames(true);

                for (DiffEntry diffEntry : diffEntryList) {
                    if (diffEntry.getChangeType().equals(DiffEntry.ChangeType.MODIFY)) {
                        // File modificato tra i due Commit
                        for (Edit edit : diffFormatter.toFileHeader(diffEntry).toEditList()) {
                            addedLines += (edit.getEndB() - edit.getBeginB());
                            deletedLines += (edit.getEndA() - edit.getBeginA());
                        }
                    }
                    // TODO Togliere la condizione sulla modifica o lasciarla catturando tutte le modifiche possibii?
                    else if (diffEntry.getChangeType().equals(DiffEntry.ChangeType.ADD)) {
                        // File creato tra i due Ccommit
                        addedLines += classInfo.getLoc();
                    }
                }
            }
        }

        Map<TouchedLocEnum, Integer> touchedLocMap = new EnumMap<>(TouchedLocEnum.class) ;
        touchedLocMap.put(ADDED, addedLines) ;
        touchedLocMap.put(DELETED, deletedLines) ;
        return touchedLocMap ;
    }
}
