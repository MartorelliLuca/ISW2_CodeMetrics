import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JiraRetriever {

    public List<String> retrieveBugTicket(String projectName) throws IOException, URISyntaxException {
        URLBuilder urlBuilder = new URLBuilder() ;
        String urlFirstPart = urlBuilder.buildUrl(projectName) ;
        int startPoint = 0 ;
        int maxAmount = 500 ;
        int issuesNumber ;

        ArrayList<String> issuesKeys = new ArrayList<>() ;
        do {
            String urlString = urlBuilder.completeUrl(startPoint, maxAmount, urlFirstPart) ;
            Logger.getGlobal().log(Level.INFO, urlString);
            URI uri = new URI(urlString) ;
            URL url = uri.toURL() ;

            String jsonString = getJsonString(url);
            JSONObject jsonObject = new JSONObject(jsonString) ;
            JSONArray jsonIssueArray = jsonObject.getJSONArray("issues") ;

            parseIssuesArray(issuesKeys, jsonIssueArray) ;

            issuesNumber = jsonIssueArray.length() ;
            startPoint = startPoint + maxAmount ;
        } while (issuesNumber != 0) ;

        return issuesKeys ;
    }

    public List<VersionInfo> retrieveVersions(String projectName) throws URISyntaxException, IOException {
        String urlString = "https://issues.apache.org/jira/rest/api/2/project/" + projectName.toUpperCase();
        URI uri = new URI(urlString);
        URL url = uri.toURL();

        String jsonString = getJsonString(url);
        JSONObject jsonObject = new JSONObject((jsonString));
        JSONArray jsonVersionArray = jsonObject.getJSONArray("versions");

        List<VersionInfo> versionInfoList = new ArrayList<>() ;
        for (int i = 0; i < jsonVersionArray.length(); i++) {
            String versionName = "";
            String dateString = "";
            String versionId = "" ;
            if (jsonVersionArray.getJSONObject(i).has("releaseDate") && jsonVersionArray.getJSONObject(i).has("name") && jsonVersionArray.getJSONObject(i).has("id")) {
                versionName = jsonVersionArray.getJSONObject(i).get("name").toString();
                dateString = jsonVersionArray.getJSONObject(i).get("releaseDate").toString();
                versionId = jsonVersionArray.getJSONObject(i).get("id").toString() ;

                LocalDate versionDate = LocalDate.parse(dateString) ;
                VersionInfo versionInfo = new VersionInfo(versionName, versionDate, versionId) ;
                versionInfoList.add(versionInfo) ;
            }
        }

        versionInfoList.sort(Comparator.comparing(VersionInfo::getVersionDate));

        for (VersionInfo info : versionInfoList) {
            Logger.getGlobal().log(Level.INFO, "Version >> " + info.getVersionName());
        }

        return versionInfoList ;
    }

    private String getJsonString(URL url) throws IOException {
        try (InputStream urlInput = url.openStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlInput)) ;
            StringBuilder builder = new StringBuilder() ;

            int c ;
            while ( (c = reader.read()) != -1) {
                builder.append((char) c) ;
            }

            return builder.toString();
        }
    }

    private void parseIssuesArray(ArrayList<String> issuesKeys, JSONArray jsonArray) {
        for (int i = 0 ; i < jsonArray.length() ; i++) {
            issuesKeys.add(jsonArray.getJSONObject(i).get("key").toString()) ;
        }
    }

}