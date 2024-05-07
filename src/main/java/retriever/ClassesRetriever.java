package retriever;

import model.ClassInfo;
import model.VersionInfo;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassesRetriever {

    Git git ;
    Repository repo ;
    List<RevCommit> allCommitList;

    public ClassesRetriever(String repoPath, String projectName) throws IOException, GitAPIException {
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        this.repo = repositoryBuilder.setGitDir(new File(repoPath + "/" + projectName + "/.git")).build() ;
        this.git = new Git(repo) ;
        this.allCommitList = new ArrayList<>() ;

        Iterable<RevCommit> revCommitIterable = git.log().call() ;
        for (RevCommit revCommit : revCommitIterable) {
            this.allCommitList.add(revCommit);
        }

        this.allCommitList.sort(Comparator.comparing(o -> o.getAuthorIdent().getWhen()));
    }

    public void retrieveClassesForAllVersions(List<VersionInfo> versionInfoList) throws IOException {
        for (int i = 0 ; i < versionInfoList.size() ; i++) {
            LocalDate prevReleaseDate ;
            if (i == 0) {
                prevReleaseDate = null ;
            }
            else {
                prevReleaseDate = versionInfoList.get(i - 1).getVersionDate() ;
            }
            List<ClassInfo> classInfoList = retrieveClassesForVersion(versionInfoList.get(i), prevReleaseDate) ;
            versionInfoList.get(i).setClassInfoList(classInfoList);
        }

        for (ClassInfo classInfo : versionInfoList.get(versionInfoList.size() - 1).getClassInfoList()) {
            Logger.getGlobal().log(Level.INFO, classInfo.getName());
        }
    }

    private List<ClassInfo> retrieveClassesForVersion(VersionInfo versionInfo, LocalDate prevReleaseDate) throws IOException {
        List<RevCommit> commitList = retrieveCommitsForVersion(versionInfo, prevReleaseDate) ;
        List<ClassInfo> classInfoList = new ArrayList<>() ;

        for (RevCommit revCommit : commitList) {
            analyzeRevTree(revCommit, classInfoList) ;
        }

        return classInfoList ;
    }

    private void analyzeRevTree(RevCommit revCommit, List<ClassInfo> classInfoList) throws IOException {
        ObjectId treeId = revCommit.getTree().getId();
        TreeWalk treeWalk = new TreeWalk(repo) ;
        treeWalk.reset(treeId);
        treeWalk.setRecursive(false);

        while (treeWalk.next()) {
            if (treeWalk.isSubtree()) {
                treeWalk.enterSubtree();
            }
            else {
                String filePath = treeWalk.getPathString() ;
                if (filePath.endsWith(".java") && !filePath.contains("/src/test") && !classIsInList(filePath, classInfoList)) {
                    ClassInfo classInfo = new ClassInfo(filePath) ;
                    classInfoList.add(classInfo) ;
                }
            }
        }
    }

    private boolean classIsInList(String filePath, List<ClassInfo> classInfoList) {
        for (ClassInfo classInfo : classInfoList) {
            if (classInfo.getName().compareTo(filePath) == 0) {
                return true ;
            }
        }
        return false ;
    }

    private List<RevCommit> retrieveCommitsForVersion(VersionInfo versionInfo, LocalDate prevReleaseDate) {
        List<RevCommit> versionCommitList = new ArrayList<>() ;
        LocalDate releaseDate = versionInfo.getVersionDate() ;

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd") ;
        for (RevCommit revCommit : allCommitList) {
            LocalDate commitDate = LocalDate.parse(dateFormatter.format(revCommit.getAuthorIdent().getWhen())) ;
            if (prevReleaseDate == null) {
                if (commitDate.isBefore(releaseDate)) {
                    versionCommitList.add(revCommit) ;
                }
            }
            else {
                if (commitDate.isAfter(prevReleaseDate) && (commitDate.isBefore(releaseDate) || commitDate.isEqual(releaseDate))) {
                    versionCommitList.add(revCommit) ;
                }
            }
        }

        return versionCommitList ;
    }
}
