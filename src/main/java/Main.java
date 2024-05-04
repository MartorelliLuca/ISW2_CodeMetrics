import model.VersionInfo;
import computer.TicketFilter;
import computer.VersionsComputer;
import model.TicketInfo;
import model.VersionInfo;
import retriever.CommitRetriever;
import retriever.TicketRetriever;
import retriever.VersionRetriever;

import org.eclipse.jgit.api.errors.GitAPIException;


import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class Main {
    private static final String PROJECT_NAME = "bookkeeper" ;
    private static final String PROJECT_PATH = "/home/lux/Documents/GitHub/" ;
    public static void main(String[] args) throws IOException, URISyntaxException, GitAPIException {

        VersionRetriever versionRetriever = new VersionRetriever(PROJECT_NAME) ;
        List<VersionInfo> versionInfoList = versionRetriever.retrieveVersions() ;

        TicketRetriever ticketRetriever = new TicketRetriever(PROJECT_NAME) ;
        List<TicketInfo> ticketInfoList = ticketRetriever.retrieveBugTicket(versionInfoList) ;

        CommitRetriever commitRetriever = new CommitRetriever(PROJECT_PATH + PROJECT_NAME) ;
        commitRetriever.retrieveFixCommitsForTickets(ticketInfoList) ;

        VersionsComputer versionsComputer = new VersionsComputer() ;
        versionsComputer.computeOpeningAndFixVersion(ticketInfoList, versionInfoList);

        TicketFilter filter = new TicketFilter() ;
        filter.filterTicket(ticketInfoList);

    }
}