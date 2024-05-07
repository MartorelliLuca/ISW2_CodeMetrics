package main;

import computer.InjectedVersionsComputer;
import computer.ProportionComputer;
import exceptions.ProportionException;
import model.VersionInfo;
import computer.TicketFilter;
import model.TicketInfo;
import org.eclipse.jgit.api.errors.GitAPIException;
import retriever.ClassesRetriever;
import retriever.TicketRetriever;
import retriever.VersionRetriever;
import retriever.FixCommitRetriever;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class Main {

    private static final String PROJECT_NAME = "openjpa";
    private static final String PROJECTS_PATH_ENV = "PROJECTS_PATH";

    public static void main(String[] args) throws IOException, URISyntaxException, ProportionException, GitAPIException {
        String projectPath = System.getenv(PROJECTS_PATH_ENV);

        if (projectPath == null) {
            throw new IllegalArgumentException("Environment variable 'PROJECTS_PATH' must be set." +
                    "Please set the variable with the path of your projects.");
        }
        //faccio una lista delle versioni del progetto preso in esame
        VersionRetriever versionRetriever = new VersionRetriever(PROJECT_NAME);
        List<VersionInfo> versionInfoList = versionRetriever.retrieveVersions();

        TicketRetriever ticketRetriever = new TicketRetriever(PROJECT_NAME);
        List<TicketInfo> ticketInfoList = ticketRetriever.retrieveBugTicket(versionInfoList);

        TicketFilter filter = new TicketFilter();
        List<TicketInfo> filteredList = filter.filterTicket(ticketInfoList, versionInfoList.get(0).getVersionDate());

        ProportionComputer proportionComputer = new ProportionComputer() ;
        Float proportion = proportionComputer.computeProportion(filteredList);

        InjectedVersionsComputer injectedVersionsComputer = new InjectedVersionsComputer() ;
        injectedVersionsComputer.setInjectedVersionsForTickets(filteredList, versionInfoList, proportion);

        FixCommitRetriever fixCommitRetriever = new FixCommitRetriever(PROJECTS_PATH_ENV, PROJECT_NAME) ;
        fixCommitRetriever.retrieveFixCommitsForTickets(filteredList) ;

        ClassesRetriever classesRetriever = new ClassesRetriever(PROJECTS_PATH_ENV, PROJECT_NAME) ;
        classesRetriever.retrieveClassesForAllVersions(versionInfoList);
    }
}