package computer;

import model.TicketInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicketFilter {

    public List<TicketInfo> filterTicket(List<TicketInfo> ticketInfoList) {

        List<TicketInfo> filteredList = new ArrayList<>(ticketInfoList) ;

        for (TicketInfo ticketInfo : ticketInfoList) {
            if (ticketInfo.getOpeningVersion() == null || ticketInfo.getFixVersion() == null || ticketInfo.getFixCommit() == null) {
                filteredList.remove(ticketInfo) ;
                continue;
            }

            Integer openingRelease = ticketInfo.getOpeningVersion().getReleaseNumber() ;
            Integer fixRelease = ticketInfo.getFixVersion().getReleaseNumber() ;
            if (openingRelease > fixRelease) {
                filteredList.remove(ticketInfo) ;
            }

            if (ticketInfo.getInjectedVersion() != null) {
                Integer injectedRelease = ticketInfo.getInjectedVersion().getReleaseNumber() ;
                if (injectedRelease > openingRelease) {
                    filteredList.remove(ticketInfo) ;
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder("Versioni Filtrate\n") ;
        for (TicketInfo ticketInfo : filteredList) {
            stringBuilder.append("[").append(ticketInfo.getTicketId()).append(" -- ") ;
            if (ticketInfo.getInjectedVersion() != null) {
                stringBuilder.append("Inj ").append(ticketInfo.getInjectedVersion().getVersionName());
            }
            else {
                stringBuilder.append("Inj NULL") ;
            }
            stringBuilder.append(" -- ").append("Open ").append(ticketInfo.getOpeningVersion().getVersionName()).append(" -- ").append("Fix ").append(ticketInfo.getFixVersion().getVersionName()).append("]\n") ;
        }
        Logger.getGlobal().log(Level.INFO, "{0}", stringBuilder) ;

        return filteredList ;
    }
}