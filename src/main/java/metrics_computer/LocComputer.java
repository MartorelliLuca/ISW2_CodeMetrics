package metrics_computer;

import model.ClassInfo;
import model.VersionInfo;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class LocComputer {

    private Repository repo ;

    public LocComputer(Repository repository) {
        this.repo = repository ;
    }

    public void computeLinesOfCode(List<VersionInfo> versionInfoList) throws IOException {
        for (VersionInfo versionInfo : versionInfoList) {
            computeLinesOfCodeForVersion(versionInfo) ;
        }
    }

    private void computeLinesOfCodeForVersion(VersionInfo versionInfo) throws IOException {
        //Computes LOC for classes in this release
        List<ClassInfo> classInfoList = versionInfo.getClassInfoList() ;
        List<RevCommit> revCommits = versionInfo.getVersionCommitList() ;
        if (revCommits.isEmpty()) {
            return ;
        }

        RevCommit commit = revCommits.get(revCommits.size() - 1) ;
        for (ClassInfo classInfo : classInfoList) {
            int loc = computeLinesOfCodeForClass(classInfo, commit);
            classInfo.setLoc(loc);
        }

    }

    private int computeLinesOfCodeForClass(ClassInfo classInfo, RevCommit commit) throws IOException {
        int loc = 0 ;

        try(
                TreeWalk treeWalk = new TreeWalk(repo)) {

            treeWalk.reset(commit.getTree().getId());
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathFilter.create(classInfo.getName()));
            treeWalk.next() ;

            ObjectId objectId = treeWalk.getObjectId(0);
            ObjectLoader loader = repo.open(objectId);
            String content = new String(loader.getBytes(), StandardCharsets.UTF_8);

            String[] lines = content.split("\n") ;
            loc = lines.length ;
        }

        return loc ;
    }
}
