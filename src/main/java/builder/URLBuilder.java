package builder;


import java.util.ArrayList;

public class URLBuilder {

    private static final String BUG_URL = "https://issues.apache.org/jira/rest/api/2/search?jql=" ;

    private final String[] issueTypeList = new String[] {"Bug"};
    private final String[] statusList = new String[] {"Closed", "Resolved"} ;
    private final String[] resolutionList = new String[] {"Fixed"} ;

    private final String[] priorityList = new String[] {} ;

    private final String[] fieldsList = new String[] {"key", "created", "versions"};




    public String completeUrl(Integer startPoint, Integer maxAmount, String urlFirstPart) {
        return urlFirstPart + "&startAt=" + startPoint.toString() + "&maxResults=" + maxAmount.toString() ;
    }

    public String buildUrl(String projectName) {
        StringBuilder urlString = new StringBuilder(BUG_URL + "project=%22" + projectName + "%22");

        ArrayList<String> urlPartsList = new ArrayList<>() ;
        urlPartsList.add(buildUrlPart("issueType", issueTypeList)) ;
        urlPartsList.add(buildUrlPart("status", statusList)) ;
        urlPartsList.add(buildUrlPart("resolution", resolutionList)) ;
        urlPartsList.add(buildUrlPart("priority", priorityList)) ;

        StringBuilder fieldsPart = new StringBuilder();
        if (fieldsList.length > 0) {
            fieldsPart.append("fields=") ;
            for (int i = 0; i < fieldsList.length; i++) {
                fieldsPart.append(fieldsList[i]);
                if (i < fieldsList.length - 1) {
                    fieldsPart.append(",");
                }
            }
        }

        for (String urlPart : urlPartsList) {
            if (urlPart.length() > 0) {
                urlString.append("AND").append(urlPart);
            }
        }
        if (fieldsPart.length() > 0) {
            urlString.append("&").append(fieldsPart) ;
        }
        return urlString.toString() ;
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