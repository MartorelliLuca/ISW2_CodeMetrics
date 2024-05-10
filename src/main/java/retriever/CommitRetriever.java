package retriever;

import main.Main;
import model.TicketInfo;
import model.VersionInfo;
import utils.DateUtils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommitRetriever {

    private final String projectName ;
    private final List<RevCommit> commitList ;

    public CommitRetriever(String projectName, Git git, LocalDate lastVersionDate) throws IOException, GitAPIException {
        this.projectName = projectName.toUpperCase();
        this.commitList = new ArrayList<>();

        Iterable<RevCommit> commitIterable = git.log().call() ;
        for (RevCommit commit : commitIterable) {
            LocalDate commitDate = DateUtils.dateToLocalDate(commit.getCommitterIdent().getWhen()) ;
            if (commitDate.isBefore(lastVersionDate)) {
                this.commitList.add(commit) ;
            }
        }
        this.commitList.sort(Comparator.comparing(o -> o.getCommitterIdent().getWhen()));
    }

    public List<TicketInfo> retrieveFixCommitListForAllTickets(List<TicketInfo> ticketInfoList, VersionInfo firstVersion, VersionInfo lastVersion) {

        // Mettere Log per controllare i Ticket che non hanno commit Associato
        List<TicketInfo> filteredByCommitList = new ArrayList<>() ;
        Integer fixCommitNumber = 0 ;
        for (TicketInfo ticketInfo : ticketInfoList) {
            List<RevCommit> fixCommitList = retrieveFixCommitListForTicket(ticketInfo, firstVersion, lastVersion) ;
            ticketInfo.setFixCommitList(fixCommitList);

            fixCommitNumber = fixCommitNumber + fixCommitList.size() ;
            if (!fixCommitList.isEmpty()) {
                filteredByCommitList.add(ticketInfo) ;
            }
        }

        StringBuilder stringBuilder = new StringBuilder() ;
        stringBuilder.append("Recupero Fix Commit per ").append(projectName.toUpperCase()).append("\n") ;
        stringBuilder.append("Numero di Commit di Fix >> ").append(fixCommitNumber).append("\n") ;
        stringBuilder.append("Ticket Con Commit Associato >> ").append(filteredByCommitList.size()) ;
        Logger.getGlobal().log(Level.INFO, "{0}", stringBuilder);

        return filteredByCommitList ;
    }

    private List<RevCommit> retrieveFixCommitListForTicket(TicketInfo ticketInfo, VersionInfo firstVersion, VersionInfo lastVersion) {
        List<RevCommit> fixCommitList = new ArrayList<>() ;
        Pattern pattern = Pattern.compile(ticketInfo.getTicketId() + "+[^0-9]") ;
        for (RevCommit commit : this.commitList) {
            LocalDate commitDate = DateUtils.dateToLocalDate(commit.getCommitterIdent().getWhen());
            // TODO Aggiungere condizione per cui il commit non può essere successivo alla resolution date del ticket ??
            boolean doesMatch = commitMatchesTicket(commit, pattern) ;
            boolean compliantDates = lastVersion.getVersionDate().isAfter(commitDate) && firstVersion.getVersionDate().isBefore(commitDate) ;
            if (doesMatch && compliantDates) {
                fixCommitList.add(commit);

            }
        }
        fixCommitList.sort(Comparator.comparing(o -> o.getCommitterIdent().getWhen()));

        return fixCommitList ;
    }

    private boolean commitMatchesTicket(RevCommit commit, Pattern pattern) {
        String commitMessage = commit.getFullMessage() ;

        Matcher matcher = pattern.matcher(commitMessage) ;
        boolean matchFound = matcher.find() ;

        int firstIndex = commitMessage.indexOf(projectName + "-") ;
        if (firstIndex != -1 && matchFound) {
            int matchIndex = matcher.start() ;
            return matchIndex == firstIndex ;
        }
        return false ;
    }

    public void retrieveCommitListForAllVersions(List<VersionInfo> versionInfoList) {
        Logger.getGlobal().log(Level.INFO, "{0}", "Recupero commit per Versioni di " + projectName.toUpperCase());
        for (int i = 0 ; i < versionInfoList.size() ; i++) {
            VersionInfo versionInfo = versionInfoList.get(i) ;

            LocalDate versionDate = versionInfoList.get(i).getVersionDate() ;
            LocalDate prevVersionDate ;
            if (i == 0) {
                prevVersionDate = null ;
            }
            else {
                prevVersionDate = versionInfoList.get(i - 1).getVersionDate() ;
            }
            List<RevCommit> revCommits = retrieveCommitListForVersion(versionDate, prevVersionDate);
            versionInfo.setVersionCommitList(revCommits);
        }

        versionInfoList.removeIf(versionInfo -> versionInfo.getVersionCommitList().isEmpty()) ;
        for (int i = 0 ; i < versionInfoList.size() ; i++) {
            versionInfoList.get(i).setReleaseNumber(i);
        }

        StringBuilder stringBuilder = new StringBuilder() ;
        stringBuilder.append("\n").append("Commit Totali >> ").append(this.commitList.size()) ;
        stringBuilder.append("\n").append("Commit Per Versione").append("\n") ;
        for (VersionInfo versionInfo : versionInfoList) {
            stringBuilder.append(versionInfo.getReleaseNumber()).append(" >> ").append(versionInfo.getVersionDate()).append(" >> ").append(versionInfo.getVersionName()).append(" >> ").append(versionInfo.getVersionCommitList().size()).append("\n") ;
        }
        Logger.getGlobal().log(Level.INFO, "{0}", stringBuilder);
    }

    private List<RevCommit> retrieveCommitListForVersion(LocalDate versionDate, LocalDate prevVersionDate) {
        List<RevCommit> versionCommitList = new ArrayList<>() ;

        if (prevVersionDate == null) {
            for (RevCommit commit : this.commitList) {
                LocalDate commitDate = DateUtils.dateToLocalDate(commit.getCommitterIdent().getWhen()) ;

                if (commitDate.isBefore(versionDate)) {
                    versionCommitList.add(commit) ;
                }
            }
        }
        else {
            for (RevCommit commit : this.commitList) {
                LocalDate commitDate = DateUtils.dateToLocalDate(commit.getCommitterIdent().getWhen()) ;

                if ((commitDate.isBefore(versionDate) || commitDate.isEqual(versionDate)) && commitDate.isAfter(prevVersionDate)) {
                    versionCommitList.add(commit) ;
                }
            }
        }

        versionCommitList.sort(Comparator.comparing(o -> o.getCommitterIdent().getWhen())) ;

        return versionCommitList ;
    }

}