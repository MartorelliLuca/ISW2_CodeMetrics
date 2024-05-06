package computer;


import model.TicketInfo;
import model.VersionInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProportionComputer {

    public void setInjectedVersionsForTickets(List<TicketInfo> ticketInfoList, List<VersionInfo> versionInfoList) {
        Float proportion = computeProportion(ticketInfoList) ;

        for (TicketInfo ticketInfo : ticketInfoList) {
            if (ticketInfo.getInjectedVersion() == null) {
                VersionInfo injectedVersion = computeInjectedVersion(ticketInfo, versionInfoList, proportion) ;
                ticketInfo.setInjectedVersion(injectedVersion);
            }
        }
        StringBuilder stringBuilder = new StringBuilder("Computed Injected Versions\n") ;
        for (TicketInfo ticketInfo : ticketInfoList) {
            stringBuilder.append(ticketInfo.toString()).append("\n") ;
        }
        Logger.getGlobal().log(Level.INFO, "{0}", stringBuilder.toString());
    }

    private VersionInfo computeInjectedVersion(TicketInfo ticketInfo, List<VersionInfo> versionInfoList, Float proportion) {
        int fixReleaseNumber = ticketInfo.getFixVersion().getReleaseNumber() ;
        int openingReleaseNumber = ticketInfo.getOpeningVersion().getReleaseNumber() ;

        int proportionInjectedVersion ;
        if (fixReleaseNumber == openingReleaseNumber) {
            proportionInjectedVersion = (int) (fixReleaseNumber - proportion);
        }
        else {
            proportionInjectedVersion = (int) (fixReleaseNumber - (fixReleaseNumber - openingReleaseNumber) * proportion);
        }
        int injectedVersionIndex = Integer.max(0, proportionInjectedVersion) ;

        return versionInfoList.get(injectedVersionIndex) ;
    }

    public Float computeProportion(List<TicketInfo> ticketInfoList) {

        Logger.getGlobal().log(Level.INFO, "{0}", ticketInfoList.size());

        List<TicketInfo> proportionFilteredList = new ArrayList<>() ;
        for (TicketInfo ticketInfo : ticketInfoList) {
            if (!ticketInfo.getOpeningVersion().getReleaseNumber().equals(ticketInfo.getFixVersion().getReleaseNumber()) && ticketInfo.getInjectedVersion() != null) {
                proportionFilteredList.add(ticketInfo) ;
            }
        }

        float incrementProportion = 0f ;
        for (TicketInfo ticketInfo : proportionFilteredList) {
            Integer fixReleaseNumber = ticketInfo.getFixVersion().getReleaseNumber() ;
            Integer openingReleaseNumber = ticketInfo.getOpeningVersion().getReleaseNumber() ;
            Integer injectedReleaseNumber = ticketInfo.getInjectedVersion().getReleaseNumber() ;
            float proportion = (((float) fixReleaseNumber) - injectedReleaseNumber) / (fixReleaseNumber - openingReleaseNumber);
            incrementProportion += proportion ;
        }
        StringBuilder stringBuilder = new StringBuilder("Ticket List for Proportion\n") ;
        for (TicketInfo ticketInfo : proportionFilteredList) {
            stringBuilder.append(ticketInfo.toString()).append("\n") ;
        }

        Logger.getGlobal().log(Level.INFO, "{0}", stringBuilder.toString());
        Logger.getGlobal().log(Level.INFO, "Filtered Ticket List Size {0}", proportionFilteredList.size());
        Logger.getGlobal().log(Level.INFO, "Proportion {0}", incrementProportion / proportionFilteredList.size());

        return incrementProportion / proportionFilteredList.size() ;
    }
}