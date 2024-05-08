package retriever;

import main.Main;
import model.TicketInfo;
import model.VersionInfo;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import utils.DateUtils;
import utils.Log;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FixCommitRetriever {

    Git git ;
    Repository repo ;

    public FixCommitRetriever(String repoPath, String projectName) throws IOException {
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        this.repo = repositoryBuilder.setGitDir(new File(repoPath + projectName + "/.git")).build() ;
        this.git = new Git(repo) ;
    }

    public void retrieveFixCommitsForTickets(List<TicketInfo> ticketInfoList, VersionInfo firstVersion, VersionInfo lastVersion) throws GitAPIException {
        LogCommand logCommand = git.log() ;
        Iterable<RevCommit> commitIterable = logCommand.call() ;
        List<RevCommit> commitList = new ArrayList<>() ;
        for (RevCommit commit : commitIterable) {
            commitList.add(commit) ;
        }

        //TODO ritornare lista filtrata anziché filtrare in-place

        // Mettere Log per controllare i Ticket che non hanno commit Associato

        Integer commitNumber = 0 ;
        for (TicketInfo ticketInfo : ticketInfoList) {
            List<RevCommit> fixCommitList = findFixCommitListForTicket(ticketInfo, commitList, firstVersion, lastVersion) ;
            commitNumber += fixCommitList.size() ;
            ticketInfo.setFixCommitList(fixCommitList);
        }

        Log.logTicketList(ticketInfoList, "Recupero Fix Commit");
        StringBuilder stringBuilder = new StringBuilder() ;
        stringBuilder.append("Numero di Commit ").append(commitNumber).append("\n");
        Logger.getGlobal().log(Level.INFO, "{0}", stringBuilder);
    }

    private List<RevCommit> findFixCommitListForTicket(TicketInfo ticketInfo, List<RevCommit> commitList, VersionInfo firstVersion, VersionInfo lastVersion) {
        List<RevCommit> fixCommitList = new ArrayList<>() ;
        Pattern pattern = Pattern.compile(ticketInfo.getTicketId() + "+[^0-9]") ;
        for (RevCommit commit : commitList) {
            LocalDate commitDate = DateUtils.dateToLocalDate(commit.getAuthorIdent().getWhen());

            boolean doesMatch = commitMatchesTicket(commit, pattern) ;
            boolean compliantDates = lastVersion.getVersionDate().isAfter(commitDate) && firstVersion.getVersionDate().isBefore(commitDate) ;
            if (doesMatch && compliantDates) {
                fixCommitList.add(commit);
            }
        }
        fixCommitList.sort(Comparator.comparing(o -> o.getAuthorIdent().getWhen()));

        return fixCommitList ;
    }

    private boolean commitMatchesTicket(RevCommit commit, Pattern pattern) {
        String commitMessage = commit.getFullMessage() ;
        String projectName = Main.PROJECT_NAME.toUpperCase() ;

        Matcher matcher = pattern.matcher(commitMessage) ;
        boolean matchFound = matcher.find() ;

        int firstIndex = commitMessage.indexOf(projectName + "-") ;
        if (firstIndex != -1 && matchFound) {
            int matchIndex = matcher.start() ;
            return matchIndex == firstIndex ;
        }
        return false ;
    }

}