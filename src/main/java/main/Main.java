package main;

import computer.BuggyClassesComputer;
import computer.FixAndAffectedVersionsComputer;
import exceptions.ProportionException;
import model.VersionInfo;
import computer.TicketFilter;
import model.TicketInfo;
import org.eclipse.jgit.api.errors.GitAPIException;
import retriever.ClassesRetriever;
import retriever.TicketRetriever;
import retriever.VersionRetriever;
import retriever.CommitRetriever;
import writer.CsvWriter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static final String PROJECT_NAME = "bookkeeper";
    private static final String PROJECT_PATH = "/home/lux/Documents/GitHub/";

    public static void main(String[] args) throws IOException, URISyntaxException, GitAPIException {

        if (PROJECT_PATH == null) {
            throw new IllegalArgumentException("Environment variable 'PROJECTS_PATH' must be set." +
                    "Please set the variable with the path of your projects.");
        }
        //faccio una lista delle versioni del progetto preso in esame
        VersionRetriever versionRetriever = new VersionRetriever(PROJECT_NAME);
        List<VersionInfo> versionInfoList = versionRetriever.retrieveVersions();

        VersionInfo firstVersion = versionInfoList.get(0) ;
        VersionInfo lastVersion = versionInfoList.get(versionInfoList.size() - 1) ;

        TicketRetriever ticketRetriever = new TicketRetriever(PROJECT_NAME);
        List<TicketInfo> ticketInfoList = ticketRetriever.retrieveBugTicket(versionInfoList);

        TicketFilter filter = new TicketFilter(PROJECT_NAME) ;
        List<TicketInfo> filteredList = filter.filterTicketByVersions(ticketInfoList, firstVersion.getVersionDate());

        FixAndAffectedVersionsComputer versionsComputer = new FixAndAffectedVersionsComputer() ;
        versionsComputer.setInjectedAndAffectedVersionForAllTickets(filteredList, versionInfoList);

        CommitRetriever commitRetriever = new CommitRetriever(PROJECT_PATH, PROJECT_NAME, lastVersion.getVersionDate()) ;
        commitRetriever.retrieveCommitListForAllVersions(versionInfoList) ;

        List<TicketInfo> completeTicketList = commitRetriever.retrieveFixCommitListForAllTickets(filteredList, firstVersion, lastVersion) ;

        ClassesRetriever classesRetriever = new ClassesRetriever(PROJECT_NAME, PROJECT_PATH) ;
        classesRetriever.retrieveClassesForAllVersions(versionInfoList);

        BuggyClassesComputer buggyClassesComputer = new BuggyClassesComputer(PROJECT_NAME, PROJECT_PATH) ;
        buggyClassesComputer.computeBuggyClassesForAllVersions(completeTicketList, versionInfoList);

        CsvWriter csvWriter = new CsvWriter(PROJECT_NAME) ;
        csvWriter.writeAllVersionInfo(versionInfoList);
    }
}