package computer;


import enums.ProjectsEnum;
import exceptions.ProportionException;
import model.TicketInfo;
import model.VersionInfo;
import retriever.TicketRetriever;
import retriever.VersionRetriever;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProportionComputer {
    private static final int THRESHOLD = 10;


    public Float computeProportion(List<TicketInfo> ticketInfoList) throws URISyntaxException, IOException, ProportionException {

        Logger.getGlobal().log(Level.INFO, "{0}", ticketInfoList.size());

        List<TicketInfo> proportionFilteredList = filterTicketList(ticketInfoList);
        if (proportionFilteredList.size() >= THRESHOLD) {
            return incrementalProportion(proportionFilteredList);
        } else {
            return coldStart();
        }
    }

    private Float incrementalProportion(List<TicketInfo> proportionFilteredList) {
        float incrementProportion = 0f;
        for (TicketInfo ticketInfo : proportionFilteredList) {
            Integer fixReleaseNumber = ticketInfo.getFixVersion().getReleaseNumber();
            Integer openingReleaseNumber = ticketInfo.getOpeningVersion().getReleaseNumber();
            Integer injectedReleaseNumber = ticketInfo.getInjectedVersion().getReleaseNumber();
            Float proportion = (((float) fixReleaseNumber) - injectedReleaseNumber) / (fixReleaseNumber - openingReleaseNumber);
            incrementProportion += proportion;
        }

        Logger.getGlobal().log(Level.INFO, "Proportion {0}\n", incrementProportion / proportionFilteredList.size());

        return incrementProportion / proportionFilteredList.size();
    }

    private Float coldStart() throws URISyntaxException, IOException, ProportionException {
        Float coldStartProportion = 0f;
        Integer projectNumber = 0;
        for (ProjectsEnum project : ProjectsEnum.values()) {
            Float projectColdStart = projectColdStartComputer(project.name().toLowerCase());
            if (projectColdStart != null) {
                coldStartProportion += projectColdStart;
                projectNumber++;
            }
        }
        if (projectNumber != 0) {
            return coldStartProportion / projectNumber;
        } else {
            throw new ProportionException();
        }
    }

    private Float projectColdStartComputer(String projectName) throws URISyntaxException, IOException {
        VersionRetriever versionRetriever = new VersionRetriever(projectName) ;
        List<VersionInfo> versionInfoList = versionRetriever.retrieveVersions() ;

        TicketRetriever ticketRetriever = new TicketRetriever(projectName) ;
        List<TicketInfo> ticketInfoList = ticketRetriever.retrieveBugTicket(versionInfoList) ;

        TicketFilter filter = new TicketFilter() ;
        List<TicketInfo> filteredList = filter.filterTicket(ticketInfoList, versionInfoList.get(0).getVersionDate());

        List<TicketInfo> proportionFilteredList = filterTicketList(filteredList);
        if (proportionFilteredList.size() >= THRESHOLD) {
            return incrementalProportion(proportionFilteredList) ;
        }

        return null ;
    }




    private List<TicketInfo> filterTicketList(List<TicketInfo> ticketInfoList) {

        List<TicketInfo> proportionFilteredList = new ArrayList<>() ;
        for (TicketInfo ticketInfo : ticketInfoList) {
            if (!ticketInfo.getOpeningVersion().getReleaseNumber().equals(ticketInfo.getFixVersion().getReleaseNumber()) && ticketInfo.getInjectedVersion() != null) {
                proportionFilteredList.add(ticketInfo) ;
            }
        }

        float incrementProportion = 0f ;
        Logger.getGlobal().log(Level.INFO, "Filtered Ticket List Size {0}", proportionFilteredList.size());
        StringBuilder stringBuilder = new StringBuilder("Ticket List for Proportion\n") ;
        for (TicketInfo ticketInfo : proportionFilteredList) {
            stringBuilder.append(ticketInfo.toString()).append("\n") ;
        }
        Logger.getGlobal().log(Level.INFO, "{0}", stringBuilder);

        return proportionFilteredList ;
    }
}