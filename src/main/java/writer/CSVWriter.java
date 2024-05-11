package writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;

public class CSVWriter {

    private final static String SEPARATOR = "," ;

    public void writeCSV(Path filePath, List<String> headerArray, List<List<String>> lineInfoArray) throws IOException {
        Writer writer = new BufferedWriter(new FileWriter(filePath.toString())) ;
        writeHeader(writer, headerArray) ;
        writeLines(writer, lineInfoArray) ;

        writer.close();
    }

    private void writeLines(Writer writer, List<List<String>> lineInfoArray) throws IOException {

        for (List<String> lineInfos : lineInfoArray) {
            StringBuilder lineBuilder = new StringBuilder() ;
            for (int i = 0 ; i < lineInfos.size() ; i++) {
                String info = lineInfos.get(i) ;
                lineBuilder.append(info) ;
                if (i != lineInfos.size() - 1) {
                    lineBuilder.append(SEPARATOR) ;
                }
            }
            lineBuilder.append("\n") ;

            writer.write(lineBuilder.toString());
        }
    }

    private void writeHeader(Writer writer, List<String> headerList) throws IOException {
        StringBuilder headerBuilder = new StringBuilder() ;
        for (int i = 0 ; i < headerList.size() ; i++) {
            String headerElem = headerList.get(i) ;
            headerBuilder.append(headerElem) ;
            if (i != headerList.size() - 1) {
                headerBuilder.append(SEPARATOR) ;
            }
        }
        headerBuilder.append("\n") ;
        writer.write(headerBuilder.toString());
    }
}