import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        JiraRetriever jiraRetriever = new JiraRetriever() ;
        CommitRetriever commitRetriever = new CommitRetriever() ;
        try {
            ArrayList<String> issuesList = jiraRetriever.retrieve();

            java.util.Map<String, ArrayList<String>> ticketMap = commitRetriever.retrieveCommit(issuesList);

            for (String ticketId : ticketMap.keySet()) {
                System.out.println("Ticket >> " + ticketId);
                System.out.println("Commit >> " + ticketMap.get(ticketId));
                System.out.println();
            }

        } catch (URISyntaxException | GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}