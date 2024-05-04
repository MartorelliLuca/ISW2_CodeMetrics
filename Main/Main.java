package Main;

import retrivers.CommitRetriever;
import retrivers.TicketRetriever;

public class Main {

    public static void main(String[] args) {
        String projName ="BOOKKEEPER";
        String issueType = "Bug";
        String status = "closed";
        String resolution = "fixed";

        CommitRetriever commitRetriever = new CommitRetriever("/home/andrea/Documenti/bookkeeper");

        TicketRetriever bookkeeperRetriever = new TicketRetriever(projName, issueType, status, resolution);

        TicketRetriever syncopeRetriever = new TicketRetriever("SYNCOPE", issueType, status, resolution);

        TicketRetriever copeRetriever = new TicketRetriever("TAJO", issueType, status, resolution);

    }

}