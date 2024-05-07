package retriever;

import model.TicketInfo;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FixCommitRetriever {

    Git git ;
    Repository repo ;

    public FixCommitRetriever(String repoPath, String projectName) throws IOException {
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        this.repo = repositoryBuilder.setGitDir(new File(repoPath + projectName + "/.git")).build() ;
        this.git = new Git(repo) ;
    }

    public void retrieveFixCommitsForTickets(List<TicketInfo> ticketInfoList) throws GitAPIException {
        LogCommand logCommand = git.log() ;
        Iterable<RevCommit> commitIterable = logCommand.call() ;
        List<RevCommit> commitList = new ArrayList<>() ;
        for (RevCommit commit : commitIterable) {
            commitList.add(commit) ;
        }

        for (TicketInfo ticketInfo : ticketInfoList) {
            List<RevCommit> fixCommitList = findFixCommitListForTicket(ticketInfo, commitList) ;
            ticketInfo.setFixCommitList(fixCommitList);
        }

        StringBuilder stringBuilder = new StringBuilder("Ticket Fix Commits\n") ;
        for (TicketInfo ticketInfo : ticketInfoList) {
            stringBuilder.append(ticketInfo.toString()).append("\n") ;
        }
        Logger.getGlobal().log(Level.INFO, "{0}", stringBuilder);
    }

    private List<RevCommit> findFixCommitListForTicket(TicketInfo ticketInfo, List<RevCommit> commitList) {
        List<RevCommit> fixCommitList = new ArrayList<>() ;
        for (RevCommit commit : commitList) {
            String commitMessage = commit.getFullMessage() ;
            if (commitMessage.contains(ticketInfo.getTicketId())) {
                fixCommitList.add(commit) ;
            }
        }
        fixCommitList.sort(Comparator.comparing(o -> o.getAuthorIdent().getWhen()));

        return fixCommitList ;
    }

}