package writer;


import model.retrieve.ClassInfo;
import model.retrieve.VersionInfo;
import utils.PathBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ARFWriter {

    private static final String SEPARATOR = "," ;
    private final String projectName ;

    private static final String[] ATTRIBUTES = {
            "LinesOfCode",
            "AddedLOC",
            "MaxAddedLOC",
            "AvgAddedLOC",
            "TouchedLOC",
            "Churn",
            "MaxChurn",
            "AvgChurn",
            "NumberOfAuthors",
            "NumberOfRevisions",
            "NumberOfDefectsFixed",
            "Buggy"} ;

    public ARFWriter(String projectName) throws IOException {
        this.projectName = projectName ;
        Files.createDirectories(PathBuilder.buildDataSetDirectoryPath(projectName, true, false));
        Files.createDirectories(PathBuilder.buildDataSetDirectoryPath(projectName, false, false));
    }

    public void writeInfoAsARF(List<VersionInfo> versionInfoList, Integer index, boolean training) throws IOException {
        Path outputPath = PathBuilder.buildDataSetFilePath(projectName, training, false, index) ;

        File arfFile = new File(outputPath.toString()) ;
        try(Writer writer = new BufferedWriter(new FileWriter(arfFile))) {
            writeHeader(writer, training, index);
            writeData(writer, versionInfoList) ;
        }
    }

    private void writeData(Writer writer, List<VersionInfo> versionInfoList) throws IOException {
        writer.write("@data");
        writer.write("\n");
        for (VersionInfo versionInfo : versionInfoList) {
            writeVersionInfo(writer, versionInfo);
        }
    }

    private void writeVersionInfo(Writer writer, VersionInfo versionInfo) throws IOException {
        StringBuilder dataBuilder = new StringBuilder() ;
        for (ClassInfo classInfo : versionInfo.getClassInfoList()) {
            dataBuilder.append(getClassDataString(classInfo)).append("\n") ;
        }
        writer.write(dataBuilder.toString());
    }

    private String getClassDataString(ClassInfo classInfo) {
        return classInfo.getLoc()
                + SEPARATOR
                + classInfo.getAddedLoc()
                + SEPARATOR
                + classInfo.getMaxAddedLoc()
                + SEPARATOR
                + classInfo.getAvgAddedLoc()
                + SEPARATOR
                + classInfo.getTouchedLoc()
                + SEPARATOR
                + classInfo.getChurn()
                + SEPARATOR
                + classInfo.getMaxChurn()
                + SEPARATOR
                + classInfo.getAvgChurn()
                + SEPARATOR
                + classInfo.getNumberOfAuthors()
                + SEPARATOR
                + classInfo.getNumberOfRevisions()
                + SEPARATOR
                + classInfo.getNumberDefectsFixed()
                + SEPARATOR
                + (classInfo.isBuggy() ? "'True'" : "'False'") ;
    }

    private void writeHeader(Writer writer, boolean training, Integer index) throws IOException {
        StringBuilder headerBuilder = new StringBuilder() ;
        headerBuilder.append("@relation ").append(projectName).append("_") ;
        if (training) {
            headerBuilder.append("training_") ;
        }
        else {
            headerBuilder.append("testing_") ;
        }
        headerBuilder.append(index).append("\n\n") ;

        for (int i = 0 ; i < ATTRIBUTES.length - 1; i++) {
            headerBuilder.append("@attribute ").append(ATTRIBUTES[i]).append(" numeric").append("\n") ;
        }
        headerBuilder.append("@attribute ").append(ATTRIBUTES[ATTRIBUTES.length - 1]).append(" {'True', 'False'}").append("\n\n") ;

        writer.write(headerBuilder.toString());
    }
}
