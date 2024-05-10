package main;

import builder.WekaClassifierListBuilder;
import model.weka.WekaClassifier;
import model.weka.WekaEvaluation;
import utils.PathBuilder;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import writer.EvaluationWriter;

import java.util.ArrayList;
import java.util.List;


public class WekaFlow {

    private WekaFlow() {}
    public static void weka(String projectName, int maxIndex) throws Exception {

        WekaClassifierListBuilder listBuilder = new WekaClassifierListBuilder() ;
        EvaluationWriter evaluationWriter = new EvaluationWriter(projectName) ;

        List<WekaEvaluation> wekaEvaluationList = new ArrayList<>() ;

        for (int index = 0 ; index < maxIndex ; index++) {
            DataSource trainSource = new DataSource(PathBuilder.buildTrainingDataSetPath(projectName, index).toString());
            Instances trainingSet = trainSource.getDataSet() ;
            DataSource testSource = new DataSource(PathBuilder.buildTestingDataSetPath(projectName, index).toString());
            Instances testingSet = testSource.getDataSet() ;

            trainingSet.setClassIndex(trainingSet.numAttributes() - 1);
            testingSet.setClassIndex(testingSet.numAttributes() - 1);

            List<WekaClassifier> classifierList = listBuilder.buildClassifierList() ;

            for (WekaClassifier wekaClassifier : classifierList) {
                Classifier classifier = wekaClassifier.getClassifier();
                classifier.buildClassifier(trainingSet);
                Evaluation evaluation = new Evaluation(testingSet) ;
                evaluation.evaluateModel(classifier, testingSet) ;

                wekaClassifier.setEvaluation(evaluation);

                wekaEvaluationList.add(new WekaEvaluation(wekaClassifier.getClassifierName(), index, evaluation));
            }
        }

        evaluationWriter.writeClassifiersEvaluation(projectName, wekaEvaluationList) ;
    }


}