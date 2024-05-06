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
            if (ticketInfo.getOpeningVersion() == null || ticketInfo.getFixVersion() == null) {
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
                if (injectedRelease >= openingRelease) {
                    filteredList.remove(ticketInfo) ;
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder("Ticket Filtrati\n") ;
        stringBuilder.append("Numero di Ticket ").append(filteredList.size()).append("\n") ;
        for (TicketInfo ticketInfo : filteredList) {
            stringBuilder.append(ticketInfo.toString()).append("\n") ;
        }
        Logger.getGlobal().log(Level.INFO, "{0}", stringBuilder) ;

        return filteredList ;
    }
}