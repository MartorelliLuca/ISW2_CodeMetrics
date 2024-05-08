package retriever;

import model.ClassInfo;
import model.VersionInfo;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassesRetriever {

    private final Repository repo ;

    public ClassesRetriever(String projectName, String repoPath) throws IOException {
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        this.repo = repositoryBuilder.setGitDir(new File(repoPath + projectName + "/.git")).build();
    }

    public void retrieveClassesForAllVersions(List<VersionInfo> versionInfoList) throws IOException {
        for (VersionInfo versionInfo : versionInfoList) {
            List<ClassInfo> classInfoList = retrieveClassesForVersion(versionInfo);
            versionInfo.setClassInfoList(classInfoList);
        }

        StringBuilder stringBuilder = new StringBuilder() ;
        stringBuilder.append("Numero di Classi per Versioni").append("\n") ;
        for (VersionInfo versionInfo : versionInfoList) {
            stringBuilder.append(versionInfo.getVersionName()).append(" >> ").append(versionInfo.getClassInfoList().size()).append("\n") ;
        }
        Logger.getGlobal().log(Level.INFO, "{0}", stringBuilder);
    }

    private List<ClassInfo> retrieveClassesForVersion(VersionInfo versionInfo) throws IOException {
        List<RevCommit> commitList = versionInfo.getVersionCommitList() ;

        // TODO Scegliere cosa fare con le classi che hanno zero commit
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
                if (treeWalk.getPathString().endsWith(".java") && !treeWalk.getPathString().contains("/test/")) {
                    String className = treeWalk.getPathString() ;
                    ClassInfo classInfo = new ClassInfo(className) ;
                    classInfoList.add(classInfo) ;
                }
            }
        }
        classInfoList.sort(Comparator.comparing(ClassInfo::getName));
        return classInfoList ;
    }
}
