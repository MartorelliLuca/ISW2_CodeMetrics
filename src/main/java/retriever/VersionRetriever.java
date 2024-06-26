package retriever;

import model.retrieve.VersionInfo;
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


public class VersionRetriever {

    private final String projectName ;

    public VersionRetriever(String projectName) {
        this.projectName = projectName ;
    }

    public List<VersionInfo> retrieveVersions() throws URISyntaxException, IOException {
        Logger.getGlobal().log(Level.INFO, "{0}", "Recupero Versioni per " + projectName.toUpperCase());
        //This method retrieves version information from the URL and converts it into a list of VersionInfo objects.
        String urlString = "https://issues.apache.org/jira/rest/api/2/project/" + projectName.toUpperCase();
        URI uri = new URI(urlString);
        URL url = uri.toURL();

        JSONRetriever jsonReader = new JSONRetriever() ;
        String jsonString = jsonReader.getJsonString(url);
        JSONObject jsonObject = new JSONObject((jsonString));
        JSONArray jsonVersionArray = jsonObject.getJSONArray("versions");

        List<VersionInfo> versionInfoList = parseVersionArray(jsonVersionArray) ;
        versionInfoList.sort(Comparator.comparing(VersionInfo::getVersionDate));
        //Indici provvisori assegnati: vengono modificati successivamente quando scartiamo le versioni senza commit associati
        for (int i = 0 ; i < versionInfoList.size() ; i++) {
            versionInfoList.get(i).setReleaseNumber(i);
        }

        return versionInfoList ;
    }

    private List<VersionInfo> parseVersionArray(JSONArray versionJsonArray) {
        //This method takes a JSONArray as an input and returns a list of VersionInfo objects.
        List<VersionInfo> versionInfoList = new ArrayList<>() ;
        for (int i = 0; i < versionJsonArray.length(); i++) {
            VersionInfo versionInfo = parseVersionInfo(versionJsonArray.getJSONObject(i)) ;
            if (versionInfo != null) {
                versionInfoList.add(versionInfo) ;
            }
        }
        return versionInfoList ;
    }

    private VersionInfo parseVersionInfo(JSONObject vesionJsonObject) {
        // Assumiamo valide solo le relaese che hanno released = true.
        if (vesionJsonObject.has("releaseDate") && vesionJsonObject.has("name") && vesionJsonObject.has("id")) {
            String versionName = vesionJsonObject.get("name").toString();
            String dateString = vesionJsonObject.get("releaseDate").toString();
            String versionId = vesionJsonObject.get("id").toString();

            LocalDate versionDate = LocalDate.parse(dateString) ;
            return new VersionInfo(versionName, versionDate, versionId);
        }
        return null ;
    }

    //Proportion nell'approccio incrementale può essere calcolato solo con i bug fino alla Versione in cui mi sto ponendo, MAI DOPO
}