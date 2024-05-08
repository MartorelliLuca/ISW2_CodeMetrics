package metrics_computer;

import model.ClassInfo;
import model.VersionInfo;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AuthorsNumberComputer {

    private final Repository repo;
    private final Git git;

    public AuthorsNumberComputer(Repository repo, Git git) {
        this.repo = repo ;
        this.git = git ;
    }

    public void computeAuthorsNumber(List<VersionInfo> versionInfoList) throws GitAPIException, IOException {
        for (VersionInfo versionInfo : versionInfoList) {
            computeAuthorsNumberForVersion(versionInfo);
        }
    }

    private void computeAuthorsNumberForVersion(VersionInfo versionInfo) throws GitAPIException, IOException {
        List<ClassInfo> classInfoList = versionInfo.getClassInfoList() ;
        List<RevCommit> commitList = versionInfo.getVersionCommitList() ;

        if (commitList.isEmpty()) {
            return ;
        }

        for (ClassInfo classInfo : classInfoList) {
            int numberOfAuthors = computeAuthorsNumberForClass(classInfo, commitList);
            classInfo.setNumberOfAuthors(numberOfAuthors);
        }
    }

    private int computeAuthorsNumberForClass(ClassInfo classInfo, List<RevCommit> commitList) throws GitAPIException, IOException {
        Set<String> authorsSet = new HashSet<>() ;
        for (RevCommit commit : commitList) {
            String commitAuthor = computeAuthorsNumberInCommit(classInfo, commit) ;
            if (commitAuthor != null) {
                authorsSet.add(commitAuthor) ;
            }
        }
        return authorsSet.size() ;
    }

    private String computeAuthorsNumberInCommit(ClassInfo classInfo, RevCommit commit) throws IOException, GitAPIException {
        String commitAuthor = null ;

        if (commit.getParentCount() == 0) {
            try(TreeWalk treeWalk = new TreeWalk(repo)) {
                treeWalk.setFilter(PathFilter.create(classInfo.getName()));
                treeWalk.reset(commit.getTree().getId());
                treeWalk.setRecursive(true);

                if (treeWalk.next()) {
                    // File creato al primo commit del progetto
                    commitAuthor = commit.getCommitterIdent().getEmailAddress() ;
                }
            }
        }
        else {
            ObjectReader reader = repo.newObjectReader() ;
            CanonicalTreeParser commitTreeParser = new CanonicalTreeParser() ;
            commitTreeParser.reset(reader, commit.getTree());

            RevCommit prevCommit = commit.getParent(0) ;
            CanonicalTreeParser prevCommitTreeParser = new CanonicalTreeParser(null, reader, prevCommit.getTree()) ;

            DiffCommand diffCommand = git.diff()
                    .setNewTree(commitTreeParser)
                    .setOldTree(prevCommitTreeParser)
                    .setShowNameAndStatusOnly(true)
                    .setPathFilter(PathFilter.create(classInfo.getName())) ;

            List<DiffEntry> diffEntryList = diffCommand.call() ;

            if (!diffEntryList.isEmpty()) {
                // Il File è stato modificato nel commit e quindi chi lo ha modificato ne è autore
                commitAuthor = commit.getCommitterIdent().getEmailAddress() ;
            }
        }
        return commitAuthor;
    }
}
