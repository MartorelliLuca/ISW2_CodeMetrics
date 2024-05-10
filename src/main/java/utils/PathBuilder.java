package utils;

import java.nio.file.Path;

public class PathBuilder {

    private PathBuilder() {}

    private static Path buildParentDirectoryPath(String projectName) {
        return Path.of("./output", projectName.toUpperCase()) ;
    }

    public static Path buildLogPath(String projectName) {
        return Path.of(buildParentDirectoryPath(projectName).toString(), "Log") ;
    }

    public static Path buildDataSetDirectoryPath(String projectName, boolean isTraining, boolean isCsv) {
        String typePart = isTraining ? "Train" : "Test" ;
        String formatPart = isCsv ? "csv" : "arff" ;
        Path parentDirectory = buildParentDirectoryPath(projectName) ;
        return Path.of(parentDirectory.toString(), "DataSet", typePart, formatPart) ;
    }

    public static Path buildDataSetFilePath(String projectName, boolean isTraining, boolean isCsv, int index) {
        String extensionPart = isCsv ? "csv" : "arff" ;
        String typePart = isTraining ? "train" : "test" ;
        Path directory = buildDataSetDirectoryPath(projectName, isTraining, isCsv) ;
        return Path.of(directory.toString(), projectName.toUpperCase() + "_" + index + "_" + typePart + "." + extensionPart) ;
    }

    public static Path buildTrainingDataSetPath(String projectName, int index) {
        return buildDataSetFilePath(projectName, true, false, index) ;
    }

    public static Path buildTestingDataSetPath(String projectName, int index) {
        return buildDataSetFilePath(projectName, false, false, index) ;
    }


    public static Path buildEvaluationDirectoryPath(String projectName) {
        Path parentDirectory = buildParentDirectoryPath(projectName) ;
        return Path.of(parentDirectory.toString(), "Evaluation") ;
    }

    public static Path buildEvaluationFilePath(String projectName) {
        return  Path.of(buildEvaluationDirectoryPath(projectName).toString(), projectName.toUpperCase() + "_Evaluation.csv") ;
    }


}
