package writer;

import model.retrieve.ClassInfo;
import model.retrieve.VersionInfo;
import utils.CSVUtils;
import utils.PathBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CSVWriter {

    private static final String SEPARATOR = "," ;
    private final String projectName ;

    private static final String[] HEADER_ARRAY = {
            "Version",     //
            "ClassName",   //Nome della classe.
            "LinesOfCode", //Indica il numero totale di linee di codice in un determinato progetto o file, e quanto il codice è stato modificato nel tempo.
            "AddedLOC",    //La somma delle linee di codice aggiunte attraverso tutte le revisioni.
            "MaxAddedLOC", //Il massimo delle linee di codice aggiunte in una singola revisione.
            "AvgAddedLOC", //La media delle linee di codice aggiunte per revisione.
            "TouchedLOC",  //Totale delle linee di codice che sono state modificate.
            "Churn",       //La somma delle linee di codice aggiunte e cancellate attraverso tutte le revisioni. Indice di instabilità dei cambiamenti.
            "MaxChurn",    //Il massimo del churn in una singola revisione.
            "AvgChurn",    //La media del churn per revisione.
            "NumberOfAuthors", //Numero di autori, ossia il numero di persone che hanno contribuito al codice.
            "NumberOfRevisions",
            "NumberOfDefectsFixed",
            "Buggy"} ;

    public CSVWriter(String projectName) throws IOException {
        this.projectName = projectName ;
        Files.createDirectories(PathBuilder.buildDataSetDirectoryPath(projectName, true, true));
        Files.createDirectories(PathBuilder.buildDataSetDirectoryPath(projectName, false, true));
        }

    public void writeInfoAsCSV(List<VersionInfo> versionInfoList, Integer index, boolean training) throws IOException {
        Path outputPath = PathBuilder.buildDataSetFilePath(projectName, training, true, index) ;

        File csvFile = new File(outputPath.toString()) ;
        try(Writer writer = new BufferedWriter(new FileWriter(csvFile))) {
            CSVUtils.writeHeader(writer, HEADER_ARRAY, SEPARATOR);
            for (VersionInfo versionInfo : versionInfoList) {
                writeVersionInfo(writer, versionInfo);
            }
        }
    }

    private void writeVersionInfo(Writer writer, VersionInfo versionInfo) throws IOException {
        List<ClassInfo> classInfoList = versionInfo.getClassInfoList() ;
        StringBuilder stringBuilder = new StringBuilder() ;
        for (ClassInfo classInfo : classInfoList) {
            stringBuilder.append(versionInfo.getReleaseNumber()).append(SEPARATOR) ;

            String classString = buildClassString(classInfo) ;
            stringBuilder.append(classString).append("\n") ;

        }
        writer.write(stringBuilder.toString());
    }

    private String buildClassString(ClassInfo classInfo) {

        return classInfo.getName()
                + SEPARATOR
                + classInfo.getLoc()
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
                + (classInfo.isBuggy() ? "True" : "False");
    }
}