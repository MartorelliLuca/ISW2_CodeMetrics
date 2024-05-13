package writer;



import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;

public class ARFFWriter {

    private static final String SEPARATOR = "," ;
    private static final String RELATION = "@relation" ;
    private static final String ATTRIBUTE = "@attribute" ;
    private static final String DATA = "@data" ;

    public void writeARFF(Path filePath, String relationName, List<ARFFAttribute> attributeList, List<List<String>> lineInfoArray) throws IOException {

        Writer writer = new BufferedWriter(new FileWriter(filePath.toString())) ;
        writeHeader(writer, relationName, attributeList) ;
        writeLines(writer, lineInfoArray) ;

        writer.close();
    }

    private void writeLines(Writer writer, List<List<String>> lineInfoArray) throws IOException {
        writer.write(DATA);
        writer.write("\n");

        for(int lineIndex = 0 ; lineIndex < lineInfoArray.size() ; lineIndex++) {
            List<String> lineInfos = lineInfoArray.get(lineIndex) ;
            StringBuilder lineBuilder = new StringBuilder() ;
            for (int i = 0 ; i < lineInfos.size() ; i++) {
                String info = lineInfos.get(i) ;
                lineBuilder.append(info) ;
                if (i != lineInfos.size() - 1) {
                    lineBuilder.append(SEPARATOR) ;
                }
            }
            if (lineIndex != lineInfoArray.size() - 1) {
                lineBuilder.append("\n") ;
            }
            writer.write(lineBuilder.toString());
        }
    }

    private void writeHeader(Writer writer, String relationName, List<ARFFAttribute> attributeList) throws IOException {
        StringBuilder relationBuilder = new StringBuilder() ;
        relationBuilder.append(RELATION).append(" ").append(relationName).append("\n\n") ;
        writer.write(relationBuilder.toString());

        StringBuilder attributeBuilder = new StringBuilder() ;
        for (ARFFAttribute attribute : attributeList) {
            attributeBuilder.append(ATTRIBUTE).append(" ").append(attribute.attributeName()).append(" ").append(attribute.attributeDomain()).append("\n") ;
        }
        attributeBuilder.append("\n") ;
        writer.write(attributeBuilder.toString());
    }
}