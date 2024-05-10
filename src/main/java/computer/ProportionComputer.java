package computer;


import enums.ProjectEnum;
import model.retrieve.TicketInfo;
import model.retrieve.VersionInfo;
import retriever.TicketRetriever;
import retriever.VersionRetriever;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProportionComputer {
    private static final int THRESHOLD = 5;

    private final ArrayList<TicketInfo> proportionTicketList ;
    private Float coldStartProportion;
    private List<Float> coldStartArray ;

    public ProportionComputer() {
        this.proportionTicketList = new ArrayList<>() ;
    }


    public Float computeProportionForTicket(TicketInfo ticketInfo) throws URISyntaxException, IOException {
        boolean isValidForProportion = isValidTicket(ticketInfo) ;
        if (isValidForProportion) {
            proportionTicketList.add(ticketInfo) ;
            return null ;
        }
        else {
            return applyProportion() ;
        }
    }

    private Float applyProportion() throws URISyntaxException, IOException {
        Float proportionValue ;
        if (proportionTicketList.size() >= THRESHOLD) {
            proportionValue = computeIncrementalProportion(this.proportionTicketList) ;
        }
        else {
            proportionValue = computeColdStartProportion() ;
        }
        return proportionValue ;
    }

    private Float computeIncrementalProportion(List<TicketInfo> proportionTicketList) {
        // TODO Ragionare su impatto su precision e recall calcolando proportion in questo modo
        float incrementProportion = 0f;
        Integer size = proportionTicketList.size() ;
        for (TicketInfo ticketInfo : proportionTicketList) {
            Integer fixReleaseNumber = ticketInfo.getFixVersion().getReleaseNumber();
            Integer openingReleaseNumber = ticketInfo.getOpeningVersion().getReleaseNumber();
            Integer injectedReleaseNumber = ticketInfo.getInjectedVersion().getReleaseNumber();
            float proportion = (((float) fixReleaseNumber) - injectedReleaseNumber) / (fixReleaseNumber - openingReleaseNumber);
            incrementProportion += proportion;
        }

        return incrementProportion / size ;
    }

    private Float computeColdStartProportion() throws URISyntaxException, IOException {
        if (coldStartProportion != null) {
            return coldStartProportion ;
        }
        List<Float> projectsProportionList = new ArrayList<>() ;
        for (ProjectEnum project : ProjectEnum.values()) {
            Float projectColdStart = computeColdStartForProject(project.name().toLowerCase()) ;
            if (projectColdStart != null) {
                projectsProportionList.add(projectColdStart);
            }
        }
        Float coldStartValue ;
        projectsProportionList.sort(Comparator.naturalOrder());
        if (projectsProportionList.size() % 2 != 0) {
            coldStartValue = projectsProportionList.get((projectsProportionList.size() - 1) / 2) ;
        } else {
            Float firstValue = projectsProportionList.get((projectsProportionList.size() / 2) - 1) ;
            Float secondValue = projectsProportionList.get((projectsProportionList.size()) / 2) ;
            coldStartValue = (0.5f) * (firstValue + secondValue) ;
        }

        this.coldStartProportion = coldStartValue ;
        this.coldStartArray = projectsProportionList ;
        return this.coldStartProportion;
    }

    private Float computeColdStartForProject(String projectName) throws URISyntaxException, IOException {
        VersionRetriever versionRetriever = new VersionRetriever(projectName) ;
        List<VersionInfo> versionInfoList = versionRetriever.retrieveVersions() ;

        TicketRetriever ticketRetriever = new TicketRetriever(projectName) ;
        List<TicketInfo> ticketInfoList = ticketRetriever.retrieveBugTicket(versionInfoList) ;

        List<TicketInfo> projectProportionTicketList = filterTicketListForProportion(ticketInfoList) ;
        if (projectProportionTicketList.size() < THRESHOLD) {
            return null ;
        }
        else {
            return computeIncrementalProportion(projectProportionTicketList);
        }
    }

    private List<TicketInfo> filterTicketListForProportion(List<TicketInfo> ticketInfoList) {

        List<TicketInfo> proportionFilteredList = new ArrayList<>() ;
        for (TicketInfo ticketInfo : ticketInfoList) {
            if (isValidTicket(ticketInfo)) {
                proportionFilteredList.add(ticketInfo) ;
            }
        }

        return proportionFilteredList ;
    }

    private boolean isValidTicket(TicketInfo ticketInfo) {
                /*
        Consideriamo un ticket valido per fare Proportion se
        * Ha Injected Version
        * Opening e Fix Version sono diverse per evitare che il denominatore sia nullo
         */
        // TODO AGGIUNGERE CONDIZIONE DI RIMOZIONE OPENING != FIX ?? SE TOGLI AGGIUNGI CONTROLLI DENOMINATORE DIVERSO DA ZERO
        return !ticketInfo.getOpeningVersion().getReleaseNumber().equals(ticketInfo.getFixVersion().getReleaseNumber()) && ticketInfo.getInjectedVersion() != null;
    }

    public float getColdStartProportionValue() {
        return this.coldStartProportion ;
    }

    public List<Float> getColdStartArray() {
        return this.coldStartArray ;
    }

}