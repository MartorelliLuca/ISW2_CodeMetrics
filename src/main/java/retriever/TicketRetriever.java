package retriever;

import builder.URLBuilder;
import model.TicketInfo;
import model.VersionInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicketRetriever {
    String projectName ;

    public TicketRetriever(String projectName) {
        this.projectName = projectName ;
    }

    public List<TicketInfo> retrieveBugTicket(List<VersionInfo> versionInfoList) throws IOException, URISyntaxException {

        List<TicketInfo> ticketInfoList = retrieveFromJson(versionInfoList) ;

        VersionInfo firstVersion = versionInfoList.get(0) ;
        ticketInfoList.removeIf(ticketInfo -> !hasValidVersions(ticketInfo, firstVersion.getVersionDate())) ;

        StringBuilder stringBuilder = new StringBuilder() ;
        stringBuilder.append("Ticket Filtrati per ").append(projectName.toUpperCase()).append(" >> ").append(ticketInfoList.size()).append("\n") ;
        Logger.getGlobal().log(Level.INFO, "{0}", stringBuilder);

        return ticketInfoList ;
    }

    private List<TicketInfo> retrieveFromJson(List<VersionInfo> versionInfoList) throws IOException, URISyntaxException {

        URLBuilder urlBuilder = new URLBuilder() ;
        String urlFirstPart = urlBuilder.buildUrl(projectName) ;
        int startPoint = 0 ;
        int maxAmount = 500 ;
        int issuesNumber ;

        List<TicketInfo> ticketInfoList = new ArrayList<>() ;

        JSONRetriever jsonRetriever = new JSONRetriever() ;
        do {
            String urlString = urlBuilder.completeUrl(startPoint, maxAmount, urlFirstPart) ;
            URI uri = new URI(urlString) ;
            URL url = uri.toURL() ;

            String jsonString = jsonRetriever.getJsonString(url) ;
            JSONObject jsonObject = new JSONObject(jsonString) ;
            JSONArray jsonIssueArray = jsonObject.getJSONArray("issues") ;

            ticketInfoList.addAll(parseIssuesArray(jsonIssueArray, versionInfoList)) ;

            issuesNumber = jsonIssueArray.length() ;
            startPoint = startPoint + maxAmount ;
        } while (issuesNumber != 0) ;

        StringBuilder stringBuilder = new StringBuilder() ;
        stringBuilder.append("Ticket Totali per ").append(projectName.toUpperCase()).append(" >> ").append(ticketInfoList.size()).append("\n") ;
        Logger.getGlobal().log(Level.INFO, "{0}", stringBuilder);

        return ticketInfoList ;
    }

    private List<TicketInfo> parseIssuesArray(JSONArray jsonArray, List<VersionInfo> versionInfoList) {

        List<TicketInfo> ticketInfoList = new ArrayList<>() ;
        for (int i = 0 ; i < jsonArray.length() ; i++) {
            JSONObject jsonIssue = jsonArray.getJSONObject(i) ;
            TicketInfo ticketInfo = parseIssue(jsonIssue, versionInfoList) ;
            ticketInfoList.add(ticketInfo);
        }
        return ticketInfoList ;
    }

    private TicketInfo parseIssue(JSONObject jsonIssue, List<VersionInfo> versionInfoList) {
        String ticketId = jsonIssue.get("key").toString() ;

        JSONObject jsonIssueFieldsObject = jsonIssue.getJSONObject("fields") ;

        TicketInfo ticketInfo = new TicketInfo(ticketId) ;
        if (jsonIssueFieldsObject.has("created")) {
            String stringDate = jsonIssueFieldsObject.getString("created") ;
            LocalDate createdDate = LocalDate.parse(stringDate.substring(0,10)) ;
            // Consideriamo la Opening Version come la prima versione dopo la data di creazione del Ticket
            ticketInfo.setCreateDate(createdDate);

            VersionInfo openingVersion = computeVersionAfterDate(createdDate, versionInfoList) ;
            ticketInfo.setOpeningVersion(openingVersion);
        }

        if (jsonIssueFieldsObject.has("versions")) {
            JSONArray affectedVersionJsonArray = jsonIssueFieldsObject.getJSONArray("versions") ;
            List<VersionInfo> affectedVersionsList = parseAffectedVersions(affectedVersionJsonArray, versionInfoList) ;
            // Consideriamo la Injected Version come la prima delle Affected Versions riportate su Jira
            if (!affectedVersionsList.isEmpty()) {
                ticketInfo.setAffectedVersionList(affectedVersionsList);
                ticketInfo.setInjectedVersion(affectedVersionsList.get(0));
            }
        }
        // Assumiamo che la data effettiva di chiusura del Ticket sia la resolutiondate riportata su Jira
        if (jsonIssueFieldsObject.has("resolutiondate")) {
            String stringDate = jsonIssueFieldsObject.getString("resolutiondate") ;
            LocalDate resolutionDate = LocalDate.parse(stringDate.substring(0,10)) ;

            ticketInfo.setResolutionDate(resolutionDate);
            VersionInfo fixVersion = computeVersionAfterDate(resolutionDate, versionInfoList) ;
            ticketInfo.setFixVersion(fixVersion);
        }

        return ticketInfo ;
    }

    private List<VersionInfo> parseAffectedVersions(JSONArray affectedVersionJsonArray, List<VersionInfo> versionInfoList) {
        List<VersionInfo> affectedVersionList = new ArrayList<>() ;
        for (int i = 0 ; i < affectedVersionJsonArray.length() ; i++) {
            JSONObject versionJsonObject = affectedVersionJsonArray.getJSONObject(i) ;
            for (VersionInfo versionInfo : versionInfoList) {
                if (versionInfo.getVersionId().compareTo(versionJsonObject.getString("id")) == 0) {
                    affectedVersionList.add(versionInfo) ;
                    break ;
                }
            }
        }
        affectedVersionList.sort(Comparator.comparing(VersionInfo::getVersionDate));
        return affectedVersionList;
    }

    private VersionInfo computeVersionAfterDate(LocalDate date, List<VersionInfo> versionInfoList) {
        for (VersionInfo versionInfo : versionInfoList) {
            if (versionInfo.getVersionDate().isAfter(date) || versionInfo.getVersionDate().isEqual(date)) {
                return versionInfo ;
            }
        }
        return null ;
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