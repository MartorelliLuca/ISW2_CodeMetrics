package utils;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GitUtils {

    private GitUtils() {}

    public static List<String> getChangedClassesByCommit(RevCommit commit, Repository repo) throws IOException {
        List<String> changedClassesList = new ArrayList<>() ;

        if (commit.getParentCount() == 0) {
            // Il commit che analizzo è il primo: devo vedere se la classe è creata in questo commit
            TreeWalk treeWalk = new TreeWalk(repo) ;
            treeWalk.reset(commit.getTree().getId());
            treeWalk.setRecursive(false);

            while (treeWalk.next()) {
                if (!treeWalk.isSubtree() && treeWalk.getPathString().contains(".java") && !treeWalk.getPathString().contains("/test/")) {
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
                changedClassesList.add(diffEntry.getNewPath()) ;
            }

            diffFormatter.close();
        }

        return changedClassesList ;
    }
}