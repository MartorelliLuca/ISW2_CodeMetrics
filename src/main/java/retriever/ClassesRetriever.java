package retriever;

import model.retrieve.ClassInfo;
import model.retrieve.VersionInfo;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassesRetriever {

    private String projectName ;
    private Repository repo ;

    public ClassesRetriever(String projectName, Repository repo) {
        this.projectName = projectName ;
        this.repo = repo ;
    }

    public void retrieveClassesForAllVersions(List<VersionInfo> versionInfoList) throws IOException {
        Logger.getGlobal().log(Level.INFO, "{0}", "Recupero Classi per " + projectName.toUpperCase());
        for (VersionInfo versionInfo : versionInfoList) {
            List<ClassInfo> classInfoList = retrieveClassesForVersion(versionInfo);
            versionInfo.setClassInfoList(classInfoList);
            retrieveChangingCommitsForAllClasses(versionInfo) ;
        }
    }

    private void retrieveChangingCommitsForAllClasses(VersionInfo versionInfo) throws IOException {
        for (RevCommit commit : versionInfo.getVersionCommitList()) {
            List<String> changedClasses = getChangedClassesByCommit(commit, repo) ;
            for (ClassInfo classInfo : versionInfo.getClassInfoList()) {
                for (String changedClass : changedClasses) {
                    if (changedClass.compareTo(classInfo.getName()) == 0) {
                        classInfo.getModifierCommitList().add(commit);
                        break;
                    }
                }
            }
        }
    }

    private List<ClassInfo> retrieveClassesForVersion(VersionInfo versionInfo) throws IOException {
        List<RevCommit> commitList = versionInfo.getVersionCommitList() ;

        List<ClassInfo> classInfoList = new ArrayList<>() ;
        if (commitList.isEmpty()) {
            return classInfoList ;
        }

        RevCommit lastCommit = commitList.get(commitList.size() - 1) ;

        ObjectId treeId = lastCommit.getTree().getId();
        TreeWalk treeWalk = new TreeWalk(repo) ;
        treeWalk.reset(treeId);
        treeWalk.setRecursive(false);

        while (treeWalk.next()) {
            if (treeWalk.isSubtree()) {
                treeWalk.enterSubtree();
            }
            else {
                String className = treeWalk.getPathString();
                if (isValidClass(className)) {
                    ClassInfo classInfo = new ClassInfo(className) ;
                    classInfoList.add(classInfo) ;
                }
            }
        }
        treeWalk.close();
        classInfoList.sort(Comparator.comparing(ClassInfo::getName)) ;
        return classInfoList ;
    }

    private List<String> getChangedClassesByCommit(RevCommit commit, Repository repo) throws IOException {
        List<String> changedClassesList = new ArrayList<>() ;

        if (commit.getParentCount() == 0) {
            // Il commit che analizzo è il primo: devo vedere se la classe è creata in questo commit
            TreeWalk treeWalk = new TreeWalk(repo) ;
            treeWalk.reset(commit.getTree().getId());
            treeWalk.setRecursive(false);

            while (treeWalk.next()) {
                String className = treeWalk.getPathString() ;
                if (!treeWalk.isSubtree() && isValidClass(className)) {
                    changedClassesList.add(treeWalk.getPathString()) ;
                }
            }

            treeWalk.close();
        }
        else {
            RevCommit prevCommit = commit.getParent(0);

            DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE) ;
            diffFormatter.setRepository(repo) ;
            diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
            List<DiffEntry> diffEntryList = diffFormatter.scan(prevCommit.getTree(), commit.getTree()) ;

            for (DiffEntry diffEntry : diffEntryList) {
                String className = diffEntry.getNewPath() ;
                if (isValidClass(className)) {
                    changedClassesList.add(diffEntry.getNewPath());
                }
            }

            diffFormatter.close();
        }

        return changedClassesList ;
    }

    private boolean isValidClass(String className) {
        return className.endsWith(".java") && !className.contains("/test/") ;
    }
}
