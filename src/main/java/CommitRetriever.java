import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class CommitRetriever {

    public Map<String, ArrayList<String>> retrieveCommit(List<String> ticketsIDs, String repoPath) throws GitAPIException, IOException {
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        Repository repo = repositoryBuilder.setGitDir(new File(repoPath + "/.git")).build() ;
        Git git = new Git(repo) ;
        LogCommand logCommand = git.log() ;
        Iterable<RevCommit> commitIterable = logCommand.call() ;

        Map<String, ArrayList<String>> ticketMap = new HashMap<>() ;
        for (String ticketID : ticketsIDs) {
            ticketMap.put(ticketID, new ArrayList<>()) ;
        }

        for (RevCommit commit : commitIterable) {
            String commitTicket = matchTicketAndCommit(commit.getFullMessage(), ticketsIDs);
            if (commitTicket.compareTo("") != 0) {
                ticketMap.get(commitTicket).add(commit.getId().name());
            }
        }

        return ticketMap ;
    }

    private String matchTicketAndCommit(String commitMessage, List<String> ticketsIDs) {
        for (String ticketID : ticketsIDs) {
            if (commitMessage.contains(ticketID)) {
                return ticketID ;
            }
        }
        return "" ;
    }
}