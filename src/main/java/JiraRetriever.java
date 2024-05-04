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
import java.util.ArrayList;

public class JiraRetriever {

    private static final String url_part_1 = "https://issues.apache.org/jira/rest/api/2/search?jql=" ;
    private static final String PROJECT_NAME = "openjpa" ;
    private final String[] ISSUE_TYPE_LIST = new String[] {"Bug"};
    private final String[] STATUS_LIST = new String[] {"Closed", "Resolved"} ;
    private final String[] RESOLUTION_LIST = new String[] {"Fixed"} ;

    private final String[] PRIORITY_LIST = new String[] {} ;
    private final String[] FIELDS_LIST = new String[] {"key", "resolutiondate", "versions", "created"};


    public ArrayList<String> retrieve() throws MalformedURLException, URISyntaxException {
        String urlFirstPart = buildUrl() ;
        int startPoint = 0 ;
        int maxAmount = 500 ;
        int issuesNumber ;

        ArrayList<String> issuesKeys = new ArrayList<>() ;
        do {
            String urlString = completeUrl(startPoint, maxAmount, urlFirstPart) ;
            System.out.println(urlString);
            URI uri = new URI(urlString) ;
            URL url = uri.toURL() ;

            String jsonString = getJsonString(url) ;
            JSONObject jsonObject = new JSONObject(jsonString) ;
            JSONArray jsonIssueArray = jsonObject.getJSONArray("issues") ;

            parseIssuesArray(issuesKeys, jsonIssueArray) ;

            issuesNumber = jsonIssueArray.length() ;
            startPoint = startPoint + maxAmount ;
        } while (issuesNumber != 0) ;

        return issuesKeys ;
    }

    private void parseIssuesArray(ArrayList<String> issuesKeys, JSONArray jsonArray) {
        for (int i = 0 ; i < jsonArray.length() ; i++) {
            issuesKeys.add(jsonArray.getJSONObject(i).get("key").toString()) ;
        }
    }

    private String getJsonString(URL url) {
        try (InputStream urlInput = url.openStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlInput)) ;
            StringBuilder builder = new StringBuilder() ;

            int c ;
            while ( (c = reader.read()) != -1) {
                builder.append((char) c) ;
            }

            return builder.toString() ;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String completeUrl(Integer startPoint, Integer maxAmount, String urlFirstPart) {
        return urlFirstPart + "&startAt=" + startPoint.toString() + "&maxResults=" + maxAmount.toString() ;
    }

    private String buildUrl() {
        StringBuilder urlString = new StringBuilder(url_part_1 + "project=%22" + PROJECT_NAME + "%22");

        ArrayList<String> urlPartsList = new ArrayList<>() ;
        urlPartsList.add(buildUrlPart("issueType", ISSUE_TYPE_LIST)) ;
        urlPartsList.add(buildUrlPart("status", STATUS_LIST)) ;
        urlPartsList.add(buildUrlPart("resolution", RESOLUTION_LIST)) ;
        urlPartsList.add(buildUrlPart("priority", PRIORITY_LIST)) ;

        StringBuilder fieldsPart = new StringBuilder();
        if (FIELDS_LIST.length > 0) {
            fieldsPart.append("fields=") ;
            for (int i = 0; i < FIELDS_LIST.length; i++) {
                fieldsPart.append(FIELDS_LIST[i]);
                if (i < FIELDS_LIST.length - 1) {
                    fieldsPart.append(",");
                }
            }
        }

        for (String urlPart : urlPartsList) {
            if (urlPart.length() > 0) {
                urlString.append("AND").append(urlPart);
            }
        }
        return urlString + "&" + fieldsPart;
    }

    private String buildUrlPart(String filterName, String[] filterList) {
        StringBuilder urlPart = new StringBuilder();

        if (filterList.length > 0) {
            urlPart.append("(");
            for (int i = 0; i < filterList.length ; i++) {
                urlPart.append("%22").append(filterName).append("%22=").append("%22").append(filterList[i]).append("%22");
                if (i < filterList.length - 1) {
                    urlPart.append("OR");
                }
            }
            urlPart.append(")");
        }

        return urlPart.toString() ;
    }

}