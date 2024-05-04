package computer;

import model.TicketInfo;
import model.VersionInfo;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class VersionsComputer {

    public void computeOpeningAndFixVersion(List<TicketInfo> ticketInfoList, List<VersionInfo> versionInfoList) {
        computeOpeningVersions(ticketInfoList, versionInfoList);
        computeFixVersions(ticketInfoList, versionInfoList);
    }

    private void computeOpeningVersions(List<TicketInfo> ticketInfoList, List<VersionInfo> versionInfoList) {
        for (TicketInfo ticketInfo : ticketInfoList) {
            for (VersionInfo versionInfo : versionInfoList) {
                if (versionInfo.getVersionDate().isAfter(ticketInfo.getCreateDate())) {
                    ticketInfo.setOpeningVersion(versionInfo);
                    break;
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder("Opening Versions Log") ;
        stringBuilder.append("\n") ;
        for (TicketInfo ticketInfo : ticketInfoList) {
            stringBuilder.append("[").append(ticketInfo.getTicketId()).append(" -- ") ;

            String openingVersionString ;
            if (ticketInfo.getOpeningVersion() != null) {
                openingVersionString = ticketInfo.getOpeningVersion().getReleaseNumber().toString() ;
            }
            else {
                openingVersionString = "NULL" ;
            }
            stringBuilder.append(openingVersionString).append("]\n") ;
        }

        Logger.getGlobal().log(Level.INFO, "{0}", stringBuilder);
    }


    private void computeFixVersions(List<TicketInfo> ticketInfoList, List<VersionInfo> versionInfoList) {
        for (TicketInfo ticketInfo : ticketInfoList) {
            if (ticketInfo.getFixCommit() == null) {
                continue;
            }
            for (VersionInfo versionInfo : versionInfoList) {
                LocalDate fixDate = ticketInfo.getFixCommit().getAuthorIdent().getWhenAsInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate versionDate = versionInfo.getVersionDate() ;
                if (versionDate.isAfter(fixDate)) {
                    ticketInfo.setFixVersion(versionInfo);
                    break ;
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder("Fix Versions") ;
        stringBuilder.append("\n") ;
        for (TicketInfo ticketInfo : ticketInfoList) {
            if (ticketInfo.getInjectedVersion() != null && ticketInfo.getFixVersion() != null && ticketInfo.getOpeningVersion() != null) {
                stringBuilder.append("[").append(ticketInfo.getTicketId()).append(" -- ").append(ticketInfo.getInjectedVersion().getReleaseNumber()).append(" -- ").append(ticketInfo.getOpeningVersion().getReleaseNumber()).append(" -- ").append(ticketInfo.getFixVersion().getReleaseNumber()).append("]\n");
            }
        }
        Logger.getGlobal().log(Level.INFO, "{0}", stringBuilder);
    }

}