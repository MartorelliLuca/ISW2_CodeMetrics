package computer;

import metrics_computer.AuthorsNumberComputer;
import metrics_computer.ChangedLocComputer;
import metrics_computer.LocComputer;
import model.VersionInfo;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MetricsComputer {
    private final String projectName ;
    private final String repoPath ;
    private final List<VersionInfo> versionInfoList ;

    public MetricsComputer(String projectName, String repoPath, List<VersionInfo> versionInfoList) {
        this.projectName = projectName ;
        this.repoPath = repoPath ;
        this.versionInfoList = versionInfoList ;
    }

    public void computeMetrics() throws IOException, GitAPIException {
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        try (Repository repo = repositoryBuilder.setGitDir(new File(repoPath + projectName + "/.git")).build() ;
             Git git = new Git(repo)) {

            LocComputer locComputer = new LocComputer(repo) ;
            locComputer.computeLinesOfCode(versionInfoList);

            ChangedLocComputer touchedLocComputer = new ChangedLocComputer(repo, git) ;
            touchedLocComputer.computeChangedLocMetrics(versionInfoList) ;

            AuthorsNumberComputer authorsNumberComputer = new AuthorsNumberComputer(repo, git) ;
            authorsNumberComputer.computeAuthorsNumber(versionInfoList);

        }
    }
}
