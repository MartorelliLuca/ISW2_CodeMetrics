import org.eclipse.jgit.api.errors.GitAPIException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final String PROJECT_NAME = "openjpa" ;
    public static void main(String[] args) throws IOException, URISyntaxException, GitAPIException {
        String repoPath = "/Home/Documents/Github/" ;

        JiraRetriever jiraRetriever = new JiraRetriever() ;
        CommitRetriever commitRetriever = new CommitRetriever() ;
        List<RevCommit> commitList = commitRetriever.retrieveAllCommitsInfo(repoPath + PROJECT_NAME);

    }
}