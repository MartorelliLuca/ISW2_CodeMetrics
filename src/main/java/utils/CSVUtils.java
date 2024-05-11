package utils;


import java.io.IOException;
import java.io.Writer;

public class CSVUtils {

    private CSVUtils() {}

    public static void writeHeader(Writer writer, String[] headerArray, String separator) throws IOException {
        StringBuilder stringBuilder = new StringBuilder() ;
        for (int i = 0 ; i < headerArray.length ; i++) {
            stringBuilder.append(headerArray[i]) ;
            if (i != headerArray.length - 1) {
                stringBuilder.append(separator) ;
            }
        }
        stringBuilder.append("\n") ;
        writer.write(stringBuilder.toString());
    }
}