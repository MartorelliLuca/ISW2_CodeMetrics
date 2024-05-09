package computer;

import model.TicketInfo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicketFilter {

    private String projectName ;

    public TicketFilter(String projectName) {
        this.projectName = projectName.toUpperCase() ;
    }

    public List<TicketInfo> filterTicketByVersions(List<TicketInfo> ticketInfoList, LocalDate firstVersionDate) {

        List<TicketInfo> filteredList = new ArrayList<>() ;

        for (TicketInfo ticketInfo : ticketInfoList) {
            boolean isValid = hasValidVersions(ticketInfo, firstVersionDate) ;
            if (isValid) {
                filteredList.add(ticketInfo) ;
            }
        }

        StringBuilder stringBuilder = new StringBuilder() ;
        stringBuilder.append("Numero Ticket Filtrati Per ").append(projectName).append(" >> ").append(filteredList.size()).append("\n") ;
        Logger.getGlobal().log(Level.INFO, "{0}", stringBuilder);

        return filteredList ;
    }

    private Boolean hasValidVersions(TicketInfo ticketInfo, LocalDate firstVersionDate) {
        /*
        Un ticket viene scartato se ha una data di creazione precedente a quella della prima versione.
        Questi possono essere considerati dei ticket creati internamente durante lo sviluppo della prima
        versione e quindi possiamo considerare che non impattino lo sviluppo nelle versioni successive
         */
        if (ticketInfo.getCreateDate().isBefore(firstVersionDate)) {
            return false ;
        }

        /*
        Il Ticket ha openingVersion = null se è stato creato ma non è ancora stata rilasciata una Release successiva,
        quindi non possiamo sapere le informazioni di quest'ultima.
         */
        if (ticketInfo.getOpeningVersion() == null || ticketInfo.getFixVersion() == null) {
            return false ;
        }
        /*
        Ticket che hanno opening > fix vengono scartati perché significa che le informazioni di Jira
        per quel ticket sono inconsistenti: non potendo sapere quali informazioni sono errate il ticket
        viene scartato
         */
        Integer openingRelease = ticketInfo.getOpeningVersion().getReleaseNumber() ;
        Integer fixRelease = ticketInfo.getFixVersion().getReleaseNumber() ;
        if (openingRelease > fixRelease) {
            return false ;
        }
/*
        Ticket che hanno opening > fix vengono scartati perché significa che le informazioni di Jira
        per quel ticket sono inconsistenti: non potendo sapere quali informazioni sono errate il ticket
        viene scartato
         */
        if (ticketInfo.getInjectedVersion() != null) {
            Integer injectedRelease = ticketInfo.getInjectedVersion().getReleaseNumber() ;
            if (injectedRelease >= openingRelease) {
                return false ;
            }
        }
        return true ;
    }

}