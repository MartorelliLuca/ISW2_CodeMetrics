package computer;

import model.TicketInfo;
import main.Main;

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