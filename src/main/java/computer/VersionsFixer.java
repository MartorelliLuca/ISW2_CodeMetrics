package computer;

import model.retrieve.TicketInfo;
import model.retrieve.VersionInfo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class VersionsFixer {

    private final ProportionComputer proportionComputer ;

    private final String projectName ;

    public VersionsFixer(String projectName) {
            this.projectName = projectName ;
        this.proportionComputer = new ProportionComputer() ;
    }

    public void fixInjectedAndAffectedVersions(List<TicketInfo> ticketInfoList, List<VersionInfo> versionInfoList) throws URISyntaxException, IOException {
        List<TicketInfo> sortedTicketList = new ArrayList<>(ticketInfoList) ;
        sortedTicketList.sort(Comparator.comparing(TicketInfo::getResolutionDate));
        for (TicketInfo ticketInfo : sortedTicketList) {
            Float proportionValue = proportionComputer.computeProportionForTicket(ticketInfo) ;
            if (proportionValue != null) {
                // Se il valore di proportion non è null, allora il ticket non ha injectedVersion associata e la calcoliamo con proportion
                setInjectedVersionForTicket(ticketInfo, versionInfoList, proportionValue);
            }
            /*
            Essendo il campo affected versions di Jira non obbligatorio, è possibile che delle versioni manchino:
            calcoliamo le affected versions non solo per i ticket di cui abbiamo calcolato la injected con proportion,
            ma per tutti i ticket
             */
            List<VersionInfo> affectedVersionList = computeAffectedVersionsForTicket(ticketInfo, versionInfoList);
            ticketInfo.setAffectedVersionList(affectedVersionList);
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
        affectedVersionList.sort(Comparator.comparing(VersionInfo::getReleaseNumber));
        return affectedVersionList ;
    }

}
