package computer;

import model.TicketInfo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicketFilter {

    public List<TicketInfo> filterTicket(List<TicketInfo> ticketInfoList, LocalDate firstVersionDate) {

        List<TicketInfo> filteredList = new ArrayList<>() ;

        for (TicketInfo ticketInfo : ticketInfoList) {
            if (isValidTicket(ticketInfo, firstVersionDate)) {
                filteredList.add(ticketInfo) ;
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

    private Boolean isValidTicket(TicketInfo ticketInfo, LocalDate firstVersionDate) {

        // TODO: CI SONO DEI TICKET CREATI PRIMA DELLA PRIMA VERSIONE: ESCLUDERLI O NO??
        // TODO: Rimuovere i Ticket che hanno data di creazione precedente alla prima release ??
        if (ticketInfo.getCreateDate().isBefore(firstVersionDate)) {
            return false ;
        }


        if (ticketInfo.getOpeningVersion() == null || ticketInfo.getFixVersion() == null) {
            return false ;
        }

        Integer openingRelease = ticketInfo.getOpeningVersion().getReleaseNumber() ;
        Integer fixRelease = ticketInfo.getFixVersion().getReleaseNumber() ;
        if (openingRelease > fixRelease) {
            return false ;
        }

        if (ticketInfo.getInjectedVersion() != null) {
            Integer injectedRelease = ticketInfo.getInjectedVersion().getReleaseNumber() ;
            if (injectedRelease >= openingRelease) {
                return false ;
            }
        }
        return true ;
    }

}