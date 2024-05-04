package retriever;

import model.TicketInfo;
import model.VersionInfo;
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

    public void retrieveFixCommitsForTickets(List<TicketInfo> ticketInfoList) throws GitAPIException {
        LogCommand logCommand = git.log() ;
        Iterable<RevCommit> commitIterable = logCommand.call() ;

        List<RevCommit> commitList = new ArrayList<>() ;
        for (RevCommit commit : commitIterable) {
            commitList.add(commit) ;
        }

        for (TicketInfo ticketInfo : ticketInfoList) {
            RevCommit fixCommit = findFixCommitForTicket(ticketInfo, commitList) ;
            ticketInfo.setFixCommit(fixCommit);
        }
    }

    private RevCommit findFixCommitForTicket(TicketInfo ticketInfo, List<RevCommit> commitList) {
        List<RevCommit> fixCommitList = new ArrayList<>() ;
        for (RevCommit commit : commitList) {
            String commitMessage = commit.getFullMessage() ;
            if (commitMessage.contains(ticketInfo.getTicketId())) {
                fixCommitList.add(commit) ;
            }
        }
        fixCommitList.sort(Comparator.comparing(o -> o.getAuthorIdent().getWhen()));

        if (!fixCommitList.isEmpty()) {
            return fixCommitList.get(fixCommitList.size() - 1);
        }
        else {
            return null ;
        }
    }

}