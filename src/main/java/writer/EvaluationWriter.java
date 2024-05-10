package writer;


import model.weka.WekaEvaluation;
import utils.PathBuilder;
import utils.CSVUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class EvaluationWriter {

    private final String projectName ;

    public static final String SEPARATOR = "," ;

    public static final String[] HEADER_ARRAY = {
            "TrainingRelease",
            "Classifier",
            "Precision",
            "Recall",
            "AUC",
            "Kappa"} ;

    public EvaluationWriter(String projectName) throws IOException {
        this.projectName = projectName ;
        Files.createDirectories(PathBuilder.buildEvaluationDirectoryPath(projectName));
    }

    public void writeClassifiersEvaluation(String projectName, List<WekaEvaluation> wekaEvaluationList) throws IOException {
        Path outputPath = PathBuilder.buildEvaluationFilePath(projectName) ;

        File csvFile = new File(outputPath.toString()) ;
        try(Writer writer = new BufferedWriter(new FileWriter(csvFile))) {
            CSVUtils.writeHeader(writer, HEADER_ARRAY, SEPARATOR);
            for (WekaEvaluation wekaEvaluation : wekaEvaluationList) {
                writeEvaluationInfo(writer, wekaEvaluation);
            }
        }
    }

    private void writeEvaluationInfo(Writer writer, WekaEvaluation wekaEvaluation) throws IOException {
        String evaluationString = wekaEvaluation.getEvaluationIndex() +
                SEPARATOR +
                wekaEvaluation.getClassifierInfo() +
                SEPARATOR +
                wekaEvaluation.getEvaluation().precision(0) +
                SEPARATOR +
                wekaEvaluation.getEvaluation().recall(0) +
                SEPARATOR +
                wekaEvaluation.getEvaluation().areaUnderROC(0) +
                SEPARATOR +
                wekaEvaluation.getEvaluation().kappa() +
                "\n";

        writer.write(evaluationString);
    }

}
