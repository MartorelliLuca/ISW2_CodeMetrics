import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CommitRetriever {

    Git git ;
    Repository repo ;

    public CommitRetriever(String repoPath) throws IOException {
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        this.repo = repositoryBuilder.setGitDir(new File(repoPath + "/.git")).build() ;
        this.git = new Git(repo) ;
    }

    public List<RevCommit> retrieveAllCommitsInfo() throws GitAPIException {

        LogCommand logCommand = git.log() ;
        Iterable<RevCommit> commitIterable = logCommand.call() ;

        List<RevCommit> revCommitList = new ArrayList<>() ;
        for (RevCommit commit : commitIterable) {
            revCommitList.add(commit) ;
        }
        revCommitList.sort(Comparator.comparingLong(o -> o.getAuthorIdent().getWhen().getTime()));
        return revCommitList ;
    }

    public List<Ref> retrieveCommitForVersion() throws IOException {
        List<String> commitList = new ArrayList<>() ;

        RefDatabase refDatabase = repo.getRefDatabase();
        List<Ref> refList = refDatabase.getRefs() ;
        List<Ref> filteredRefList = new ArrayList<>() ;
        for (Ref ref: refList) {
            if(ref.getName().contains("tags")) {
                System.out.println(ref.getObjectId().getName() + " " + ref.getName());
                filteredRefList.add(ref) ;
            }
        }

        return filteredRefList ;
    }


    public Map<String, ArrayList<String>> retrieveCommitFromTickets(List<String> ticketsIDs) throws GitAPIException {
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