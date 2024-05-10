package writer;


import model.ClassInfo;
import model.VersionInfo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ARFWriter {

    private static final String SEPARATOR = "," ;
    private final String projectName ;
    private final Path testPath ;
    private final Path trainPath ;

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
        this.trainPath = Path.of(projectName.toUpperCase(), "ARF", "Train");
        this.testPath = Path.of(projectName.toUpperCase(), "ARF", "Test");

        Files.createDirectories(trainPath);
        Files.createDirectories(testPath);
    }

    public void writeInfoAsARF(List<VersionInfo> versionInfoList, Integer index, boolean training) throws IOException {
        Path outputPath ;
        if (training) {
            outputPath = Path.of(trainPath.toString(), projectName.toUpperCase() + "_" + index + "_training.arf") ;
        }
        else {
            outputPath = Path.of(testPath.toString(), projectName.toUpperCase() + "_" + index + "_testing.arf") ;
        }

        File arfFile = new File(outputPath.toString()) ;
        try(Writer writer = new BufferedWriter(new FileWriter(arfFile))) {
            writeHeader(writer, training, index);
            writeData(writer, versionInfoList) ;
            for (VersionInfo versionInfo : versionInfoList) {
                writeVersionInfo(writer, versionInfo);
            }
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
                + (classInfo.isBuggy() ? "True" : "False") ;
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
        headerBuilder.append("@attribute ").append(ATTRIBUTES[ATTRIBUTES.length - 1]).append(" {True, False}").append("\n\n") ;

        writer.write(headerBuilder.toString());
    }
}
