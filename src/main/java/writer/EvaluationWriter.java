package writer;


import model.weka.WekaEvaluation;
import utils.PathBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class EvaluationWriter {

    private static final List<String> HEADER_ARRAY = new ArrayList<>(List.of(
            "TrainingRelease",
            "ClassifierName",
            "FilterName",
            "SamplerName",
            "SensitiveLearning",
            "Precision",
            "Recall",
            "ROC_AUC",
            "Kappa",
            "TruePositive",
            "FalsePositive",
            "TrueNegative",
            "FalseNegative"
    )) ;

    public EvaluationWriter(String projectName) throws IOException {
        Files.createDirectories(PathBuilder.buildEvaluationDirectoryPath(projectName));
    }

    public void writeClassifiersEvaluation(String projectName, List<WekaEvaluation> wekaEvaluationList) throws IOException {
        Path outputPath = PathBuilder.buildEvaluationFilePath(projectName) ;
        CSVWriter csvWriter = new CSVWriter() ;

        List<List<String>> rowsInfoList = buildEvaluationRowList(wekaEvaluationList) ;

        csvWriter.writeCSV(outputPath, HEADER_ARRAY, rowsInfoList);
    }

    private List<List<String>> buildEvaluationRowList(List<WekaEvaluation> wekaEvaluationList) {
        List<List<String>> rowsInfoList = new ArrayList<>() ;
        for (WekaEvaluation wekaEvaluation : wekaEvaluationList) {
            rowsInfoList.add(buildWekaEvaluationListRepresentation(wekaEvaluation));
        }

        return rowsInfoList ;
    }

    private List<String> buildWekaEvaluationListRepresentation(WekaEvaluation wekaEvaluation) {

        return new ArrayList<>(List.of(
                Integer.toString(wekaEvaluation.getEvaluationIndex()),
                wekaEvaluation.getClassifierName(),
                wekaEvaluation.getFilterName() ,
                wekaEvaluation.getSamplingType(),
                Boolean.toString(wekaEvaluation.isCostSensitive()),
                Double.toString(wekaEvaluation.getPrecision()),
                Double.toString(wekaEvaluation.getRecall()),
                Double.toString(wekaEvaluation.getRoc()),
                Double.toString(wekaEvaluation.getKappa()),
                Double.toString(wekaEvaluation.getTruePositive()),
                Double.toString(wekaEvaluation.getFalsePositive()),
                Double.toString(wekaEvaluation.getTrueNegative()),
                Double.toString(wekaEvaluation.getFalseNegative())
        ));
    }

}