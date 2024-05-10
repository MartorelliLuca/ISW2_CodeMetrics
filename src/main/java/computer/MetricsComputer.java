package computer;

import model.retrieve.Change;
import model.retrieve.ClassInfo;
import model.retrieve.TicketInfo;
import model.retrieve.VersionInfo;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MetricsComputer {
    private final String projectName ;

    private final Repository repo ;
    private final Git git ;

    public MetricsComputer(String projectName, Repository repo, Git git) {
        this.projectName = projectName ;
        this.repo = repo ;
        this.git = git ;
    }

    public void computeMetrics(List<VersionInfo> versionInfoList, List<TicketInfo> ticketInfoList) throws IOException {
        Logger.getGlobal().log(Level.INFO, "{0}", "Calcolo Metriche per " + projectName.toUpperCase());

        for (VersionInfo versionInfo : versionInfoList) {
            if (versionInfo.getVersionCommitList().isEmpty()) {
                continue;
            }
            computeMetricsForVersion(versionInfo, ticketInfoList) ;
        }
    }

    private void computeMetricsForVersion(VersionInfo versionInfo, List<TicketInfo> ticketInfoList) throws IOException {
        List<ClassInfo> classInfoList = versionInfo.getClassInfoList() ;
        for (ClassInfo classInfo : classInfoList) {
            computeMetricsForClass(classInfo, versionInfo, ticketInfoList) ;
        }
    }

    private void computeMetricsForClass(ClassInfo classInfo, VersionInfo versionInfo, List<TicketInfo> ticketInfoList) throws IOException {
        List<RevCommit> versionCommitList = versionInfo.getVersionCommitList() ;

        setLocForClass(classInfo, versionCommitList.get(versionCommitList.size() - 1)) ;

        List<Change> changeList = new ArrayList<>() ;
        for (RevCommit commit : classInfo.getModifierCommitList()) {
            Change change = computeMetricsInRevision(classInfo, commit) ;
            if (change != null) {
                changeList.add(change) ;
            }
        }

        setAddedLocMetricsForClass(classInfo, changeList, classInfo.getModifierCommitList().size()) ;
        setTouchedLocMetricsForClass(classInfo, changeList) ;
        setChurnMetricsForClass(classInfo, changeList, classInfo.getModifierCommitList().size()) ;
        setAuthorMetricsForClass(classInfo, changeList) ;
        setNumberOfRevisionMetricsForClass(classInfo, changeList) ;
        setNumberOfDefectsFixed(classInfo, ticketInfoList) ;
    }

    // TODO Capire bene come impostare le classi buggy!!
    private void setNumberOfDefectsFixed(ClassInfo classInfo, List<TicketInfo> ticketInfoList) {
        int numberOfDefectsFixed = 0 ;
        for (RevCommit commit : classInfo.getModifierCommitList()) {
            if (existsTicket(commit, ticketInfoList)) {
                numberOfDefectsFixed ++ ;

            }
        }
        classInfo.setNumberDefectsFixed(numberOfDefectsFixed);
    }

    private boolean existsTicket(RevCommit commit, List<TicketInfo> ticketInfoList) {
        for (TicketInfo ticketInfo : ticketInfoList) {
            for (RevCommit fixCommit : ticketInfo.getFixCommitList()) {
                if (commit.getName().compareTo(fixCommit.name()) == 0) {
                    return true ;
                }
            }
        }
        return false ;
    }

    private void setLocForClass(ClassInfo classInfo, RevCommit commit) throws IOException {
        TreeWalk treeWalk = new TreeWalk(repo) ;
        treeWalk.reset(commit.getTree().getId());
        treeWalk.setRecursive(true);
        treeWalk.setFilter(PathFilter.create(classInfo.getName()));
        treeWalk.next() ;

        ObjectId objectId = treeWalk.getObjectId(0);
        ObjectLoader loader = repo.open(objectId);
        String content = new String(loader.getBytes(), StandardCharsets.UTF_8);

        String[] lines = content.split("\n") ;
        int loc = lines.length ;

        classInfo.setLoc(loc);
    }

    private void setNumberOfRevisionMetricsForClass(ClassInfo classInfo, List<Change> changeList) {
        classInfo.setNumberOfRevisions(changeList.size());
    }

    private void setAuthorMetricsForClass(ClassInfo classInfo, List<Change> changeList) {
        Set<String> authorSet = new HashSet<>() ;
        for (Change change : changeList) {
            authorSet.add(change.getChangeAuthor()) ;
        }
        classInfo.setNumberOfAuthors(authorSet.size());
    }

    private void setChurnMetricsForClass(ClassInfo classInfo, List<Change> changeList, Integer revisionNumber) {
        if (revisionNumber == 0) {
            return ;
        }

        int totalChurn = 0 ;
        int maxChurn = 0 ;
        float avgChurn ;
        List<Integer> churnList = new ArrayList<>() ;
        for (Change change : changeList) {
            churnList.add(Math.abs(change.getAddedLoc() - change.getDeletedLoc())) ;
        }

        for (Integer churn : churnList) {
            totalChurn += churn ;
            if (maxChurn < churn) {
                maxChurn = churn ;
            }
        }

        avgChurn = ((float) totalChurn) / revisionNumber ;

        classInfo.setChurn(totalChurn);
        classInfo.setMaxChurn(maxChurn);
        classInfo.setAvgChurn(avgChurn);
    }

    private void setTouchedLocMetricsForClass(ClassInfo classInfo, List<Change> changeList) {
        int totalTouched = 0 ;
        for (Change change : changeList) {
            totalTouched = totalTouched + change.getAddedLoc() + change.getDeletedLoc() ;
        }
        classInfo.setTouchedLoc(totalTouched);
    }

    private void setAddedLocMetricsForClass(ClassInfo classInfo, List<Change> changeList, Integer revisionNumber) {
        if (revisionNumber == 0) {
            return ;
        }

        int totalAdded = 0;
        int maxAdded = 0 ;
        float avgAdded ;
        for (Change change : changeList) {
            totalAdded += change.getAddedLoc() ;
            if (maxAdded < change.getAddedLoc()) {
                maxAdded = change.getAddedLoc() ;
            }
        }
        avgAdded = ((float) totalAdded) / revisionNumber ;

        classInfo.setAddedLoc(totalAdded);
        classInfo.setMaxAddedLoc(maxAdded);
        classInfo.setAvgAddedLoc(avgAdded);
    }

    public Change computeMetricsInRevision(ClassInfo classInfo, RevCommit commit) throws IOException {
        if (commit.getParentCount() == 0) {
            return computeMetricsInFirstRevision(classInfo, commit) ;
        }
        else {
            return computeMetricsInOtherRevisions(classInfo, commit) ;
        }
    }

    private Change computeMetricsInOtherRevisions(ClassInfo classInfo, RevCommit commit) throws IOException {
        int addedLines = 0;
        int deletedLines = 0 ;
        String commitAuthor ;
        Change change = null ;

        RevCommit prevCommit = commit.getParent(0);
        DiffFormatter formatter = new DiffFormatter(DisabledOutputStream.INSTANCE) ;

        formatter.setRepository( git.getRepository() );
        formatter.setPathFilter(PathFilter.create(classInfo.getName()));
        List<DiffEntry> diffEntryList = formatter.scan(prevCommit.getTree(), commit.getTree()) ;

        for (DiffEntry diffEntry : diffEntryList) {
            if (diffEntry.getChangeType().equals(DiffEntry.ChangeType.MODIFY)) {
                // File modificato tra i due Commit
                for (Edit edit : formatter.toFileHeader(diffEntry).toEditList()) {
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

        if (!diffEntryList.isEmpty()) {
            commitAuthor = commit.getCommitterIdent().getEmailAddress() ;
            change = new Change(addedLines, deletedLines, commitAuthor) ;
        }

        formatter.close() ;
        return change ;
    }

    private Change computeMetricsInFirstRevision(ClassInfo classInfo, RevCommit commit) throws IOException {
        int addedLines = 0;
        int deletedLines = 0 ;
        String commitAuthor ;
        Change change = null ;

        TreeWalk treeWalk = new TreeWalk(repo) ;
        treeWalk.setFilter(PathFilter.create(classInfo.getName()));
        treeWalk.reset(commit.getTree().getId());
        treeWalk.setRecursive(true);

        if (treeWalk.next()) {
            // File creato al primo commit del progetto
            addedLines += classInfo.getLoc() ;
            commitAuthor = commit.getCommitterIdent().getEmailAddress() ;
            change = new Change(addedLines, deletedLines, commitAuthor) ;
        }

        treeWalk.close();

        return change ;
    }

}