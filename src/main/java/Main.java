import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Main {
    private static final String PROJECT_NAME = "openjpa" ;
    public static void main(String[] args) throws IOException, URISyntaxException, GitAPIException {
        String repoPath = "/Home/Documents/Github/ISW2/Projects/" ;

        JiraRetriever jiraRetriever = new JiraRetriever() ;
        CommitRetriever commitRetriever = new CommitRetriever() ;
        List<String> issuesList = jiraRetriever.retrieve(PROJECT_NAME);

        java.util.Map<String, ArrayList<String>> ticketMap = commitRetriever.retrieveCommit(issuesList, repoPath + PROJECT_NAME);

        for (Map.Entry<String, ArrayList<String>> ticketIdMap : ticketMap.entrySet()) {
            String logString = String.format("Ticket >> %s%nCommit >> %s%n", ticketIdMap.getKey(), ticketMap.get(ticketIdMap.getKey()));
            Logger.getGlobal().log(Level.INFO, logString);
        }
    }
}