package retriever;

import model.retrieve.TicketInfo;
import model.retrieve.VersionInfo;
import utils.DateUtils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

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

    public void retrieveFixCommitListForAllTickets(List<TicketInfo> ticketInfoList, VersionInfo firstVersion, VersionInfo lastVersion) {

        Logger.getGlobal().log(Level.INFO, "{0}", "Recupero Fix Commit per " + projectName.toUpperCase());
        List<TicketInfo> filteredByCommitList = new ArrayList<>() ;
        Integer fixCommitNumber = 0 ;
        for (TicketInfo ticketInfo : ticketInfoList) {
            List<RevCommit> fixCommitList = retrieveFixCommitListForTicket(ticketInfo, firstVersion, lastVersion) ;
            ticketInfo.setFixCommitList(fixCommitList);

            fixCommitNumber = fixCommitNumber + fixCommitList.size() ;
        }

        ticketInfoList.removeIf(ticketInfo -> ticketInfo.getFixCommitList().isEmpty()) ;
    }

    private List<RevCommit> retrieveFixCommitListForTicket(TicketInfo ticketInfo, VersionInfo firstVersion, VersionInfo lastVersion) {
        List<RevCommit> fixCommitList = new ArrayList<>() ;
        //Pattern pattern = Pattern.compile(ticketInfo.getTicketId() + "+[^0-9]");
        Pattern pattern = Pattern.compile(ticketInfo.getTicketId() + "\\b");
        for (RevCommit commit : this.commitList) {
            LocalDate commitDate = DateUtils.dateToLocalDate(commit.getCommitterIdent().getWhen());
            // Aggiungere condizione per cui il commit non può essere successivo alla resolution date del ticket ??
            boolean doesMatch = commitMatchesTicket(commit, pattern) ;
            boolean compliantVersionDates = lastVersion.getVersionDate().isAfter(commitDate) && firstVersion.getVersionDate().isBefore(commitDate);
            boolean compliantTicketDates = !commitDate.isBefore(ticketInfo.getCreateDate()) && !commitDate.isAfter(ticketInfo.getResolutionDate()) ;
            if (doesMatch && compliantVersionDates && compliantTicketDates) {
                fixCommitList.add(commit);

            }
        }
        fixCommitList.sort(Comparator.comparing(o -> o.getCommitterIdent().getWhen()));

        return fixCommitList ;
    }

    private boolean commitMatchesTicket(RevCommit commit, Pattern pattern) {
        String commitMessage = commit.getFullMessage() ;

        Matcher matcher = pattern.matcher(commitMessage) ;
        return matcher.find();
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

        // Se non ci sono commit associati alla versione allora rimuoviamo la versione
        // Associamo poi gli indici alle versioni: queste sono tutte le versioni che manteniamo

        versionInfoList.removeIf(versionInfo -> versionInfo.getVersionCommitList().isEmpty()) ;
        for (int i = 0 ; i < versionInfoList.size() ; i++) {
            versionInfoList.get(i).setReleaseNumber(i);
        }
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
        } else {
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