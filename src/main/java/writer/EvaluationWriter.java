package writer;


import model.weka.WekaEvaluation;
import utils.PathBuilder;
import utils.CSVUtils;
import weka.classifiers.Evaluation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class EvaluationWriter {

    private static final String SEPARATOR = "," ;

    private static final String[] HEADER_ARRAY = {
            "TrainingRelease",
            "ClassifierName",
            "FilterName",
            "SamplerName",
            "CostSensitive",
            "Precision",
            "Recall",
            "ROC_AUC",
            "Kappa",
            "TruePositive",
            "FalsePositive",
            "TrueNegative",
            "FalseNegative",
    } ;

    public EvaluationWriter(String projectName) throws IOException {
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
                wekaEvaluation.getClassifierName() +
                SEPARATOR +
                wekaEvaluation.getFilterName() +
                SEPARATOR +
                wekaEvaluation.getSamplerName() +
                SEPARATOR +
                wekaEvaluation.isCostSensitive() +
                SEPARATOR +
                wekaEvaluation.getPrecision() +
                SEPARATOR +
                wekaEvaluation.getRecall() +
                SEPARATOR +
                wekaEvaluation.getRoc() +
                SEPARATOR +
                wekaEvaluation.getKappa() +
                SEPARATOR +
                wekaEvaluation.getTruePositive() +
                SEPARATOR +
                wekaEvaluation.getFalsePositive() +
                SEPARATOR +
                wekaEvaluation.getTrueNegative() +
                SEPARATOR +
                wekaEvaluation.getFalseNegative() +
                "\n";

        writer.write(evaluationString);
    }

}