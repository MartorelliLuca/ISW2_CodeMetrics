package main;

import computer.ProportionComputer;
import model.VersionInfo;
import computer.TicketFilter;
import model.TicketInfo;
import retriever.CommitRetriever;
import retriever.TicketRetriever;
import retriever.VersionRetriever;

import org.eclipse.jgit.api.errors.GitAPIException;


import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class Main {

    private static final String PROJECT_NAME1 = "openjpa";
    private static final String PROJECT_NAME2 = "bookkeeper";
    private static final String PROJECTS_PATH_ENV = "PROJECTS_PATH";

    public static void main(String[] args) throws IOException, URISyntaxException, GitAPIException {
        String projectPath = System.getenv(PROJECTS_PATH_ENV);

        if (projectPath == null) {
            throw new IllegalArgumentException("Environment variable 'PROJECTS_PATH' must be set." +
                    "Please set the variable with the path of your projects.");
        }

        VersionRetriever versionRetriever = new VersionRetriever(PROJECT_NAME1);
        List<VersionInfo> versionInfoList = versionRetriever.retrieveVersions();

        TicketRetriever ticketRetriever = new TicketRetriever(PROJECT_NAME1);
        List<TicketInfo> ticketInfoList = ticketRetriever.retrieveBugTicket(versionInfoList);

        CommitRetriever commitRetriever = new CommitRetriever(projectPath + PROJECT_NAME1);
        commitRetriever.retrieveFixCommitsForTickets(ticketInfoList);

        TicketFilter filter = new TicketFilter();
        List<TicketInfo> filteredList = filter.filterTicket(ticketInfoList);

        ProportionComputer proportionComputer = new ProportionComputer() ;
        proportionComputer.computeProportion(filteredList) ;
    }
}