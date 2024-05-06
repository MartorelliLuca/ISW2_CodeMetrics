package computer;


import model.TicketInfo;
import model.VersionInfo;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InjectedVersionsComputer {
    public void setInjectedVersionsForTickets(List<TicketInfo> ticketInfoList, List<VersionInfo> versionInfoList, Float proportion) {

        Logger.getGlobal().log(Level.INFO, "Computing Injected Versions With Proportion {0}",  proportion);

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
        Logger.getGlobal().log(Level.INFO, "{0}", stringBuilder);
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
}