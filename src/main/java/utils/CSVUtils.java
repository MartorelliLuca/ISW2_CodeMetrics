package utils;


import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CSVUtils {

    private CSVUtils() {}

    public static void writeHeader(Writer writer, List<String> headerArray, String separator) throws IOException {
        StringBuilder stringBuilder = new StringBuilder() ;
        for (int i = 0 ; i < headerArray.size() ; i++) {
            stringBuilder.append(headerArray.get(i)) ;
            if (i != headerArray.size() - 1) {
                stringBuilder.append(separator) ;
            }
        }
        stringBuilder.append("\n") ;
        writer.write(stringBuilder.toString());
    }
}