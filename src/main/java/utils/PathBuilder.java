package utils;

import java.nio.file.Path;

public class PathBuilder {

    private PathBuilder() {}

    public static Path buildDataSetDirectoryPath(String projectName, boolean isTraining, boolean isCsv) {
        String typePart = isTraining ? "Train" : "Test" ;
        String formatPart = isCsv ? "csv" : "arff" ;
        return Path.of("./output", projectName.toUpperCase(), "DataSet", typePart, formatPart) ;
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

}
