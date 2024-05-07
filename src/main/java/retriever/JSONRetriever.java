package retriever;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class JSONRetriever {

    public String getJsonString(URL url) throws IOException{
        //method retrieves data from a given URL, reads it character
        // by character, and returns the accumulated data as a single string.
        try (InputStream urlInput = url.openStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlInput)) ;
            StringBuilder builder = new StringBuilder() ;

            int c ;
            while ( (c = reader.read()) != -1) {
                builder.append((char) c) ;
            }
            return builder.toString() ;
        }
    }

}