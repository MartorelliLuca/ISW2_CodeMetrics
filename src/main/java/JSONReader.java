
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class JSONReader {

    public String getJsonString(URL url) throws IOException {
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