package computer;

import model.TicketInfo;
import model.VersionInfo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FixAndAffectedVersionsComputer {

    private final ProportionComputer proportionComputer ;

    public FixAndAffectedVersionsComputer() {
        this.proportionComputer = new ProportionComputer() ;
    }

    public void setInjectedAndAffectedVersionForAllTickets(List<TicketInfo> ticketInfoList, List<VersionInfo> versionInfoList) throws URISyntaxException, IOException {
        List<TicketInfo> sortedTicketList = new ArrayList<>(ticketInfoList) ;
        sortedTicketList.sort(Comparator.comparing(TicketInfo::getResolutionDate));
        for (TicketInfo ticketInfo : sortedTicketList) {
            Float proportionValue = proportionComputer.computeProportionForTicket(ticketInfo) ;
            if (proportionValue != null) {
                setInjectedVersionForTicket(ticketInfo, versionInfoList, proportionValue);
                List<VersionInfo> affectedVersionList = computeAffectedVersionsForTicket(ticketInfo, versionInfoList);
                ticketInfo.setAffectedVersionList(affectedVersionList);
            }
        }
    }

    private void setInjectedVersionForTicket(TicketInfo ticketInfo, List<VersionInfo> versionInfoList, Float proportionValue) {
        Integer proportionIndex ;
        Integer openingNumber = ticketInfo.getOpeningVersion().getReleaseNumber() ;
        Integer fixNumber = ticketInfo.getFixVersion().getReleaseNumber() ;
        if (Objects.equals(fixNumber, openingNumber)) {
            proportionIndex = (int) (fixNumber - proportionValue) ;
        }
        else {
            proportionIndex = (int) (fixNumber - (fixNumber - openingNumber) * proportionValue) ;
        }

        Integer index = Integer.max(0, proportionIndex) ;

        VersionInfo injectedVersion = versionInfoList.get(index) ;
        ticketInfo.setInjectedVersion(injectedVersion) ;
    }

    private List<VersionInfo> computeAffectedVersionsForTicket(TicketInfo ticketInfo, List<VersionInfo> versionInfoList) {
        List<VersionInfo> affectedVersionList = new ArrayList<>() ;
        for (VersionInfo versionInfo : versionInfoList) {
            int injectedNumber = ticketInfo.getInjectedVersion().getReleaseNumber() ;
            int fixNumber =  ticketInfo.getFixVersion().getReleaseNumber() ;

            if ( (injectedNumber <= versionInfo.getReleaseNumber()) && (versionInfo.getReleaseNumber() < fixNumber)) {
                affectedVersionList.add(versionInfo) ;
            }
        }

        return affectedVersionList ;
    }

}
