package flows;

import builder.WekaClassifierListBuilder;
import model.weka.WekaClassifier;
import model.weka.WekaEvaluation;
import model.weka.WekaFilter;
import utils.PathBuilder;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import writer.EvaluationWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class WekaFlow {

    private WekaFlow() {}
    public static void weka(String projectName, int maxIndex) throws Exception {

        WekaClassifierListBuilder listBuilder = new WekaClassifierListBuilder() ;
        EvaluationWriter evaluationWriter = new EvaluationWriter(projectName) ;

        List<WekaEvaluation> wekaEvaluationList = new ArrayList<>() ;


        for (int index = 0 ; index < maxIndex ; index++) {
            Logger.getGlobal().log(Level.INFO, "{0}", "Valutazione Versione " + index + "\n");
            DataSource trainSource = new DataSource(PathBuilder.buildTrainingDataSetPath(projectName, index).toString());
            Instances trainingSet = trainSource.getDataSet() ;

            int trueNumber = trainingSet.attributeStats(trainingSet.numAttributes() - 1).nominalCounts[0] ;
            int falseNumber = trainingSet.attributeStats(trainingSet.numAttributes() - 1).nominalCounts[1] ;

            Instances testingSet = getTestingSet(projectName, index, maxIndex) ;
            if (testingSet == null) {
                break;
            }

            trainingSet.setClassIndex(trainingSet.numAttributes() - 1);
            testingSet.setClassIndex(testingSet.numAttributes() - 1);

            List<WekaClassifier> classifierList = listBuilder.buildClassifierList(trueNumber, falseNumber) ;

            for (WekaClassifier wekaClassifier : classifierList) {
                Classifier classifier = wekaClassifier.getClassifier();
                classifier.buildClassifier(trainingSet);

                WekaFilter filter = wekaClassifier.getWekaFilter() ;

                Evaluation evaluation = new Evaluation(testingSet) ;
                evaluation.evaluateModel(classifier, testingSet) ;

                wekaClassifier.setEvaluation(evaluation);

                wekaEvaluationList.add(new WekaEvaluation(wekaClassifier, index, evaluation));
            }
        }

        evaluationWriter.writeClassifiersEvaluation(projectName, wekaEvaluationList) ;
    }

    private static Instances getTestingSet(String projectName, int startIndex, int maxIndex) throws Exception {
        /*
        Come TestingSet consideriamo il primo successivo alla versione a cui siamo arrivati che presenta almeno una classe Buggy.
        Fare questo ci permette di evitare che ci siano valori NaN nei risultati
        */
        for (int index = startIndex ; index < maxIndex ; index++) {
            DataSource testSource = new DataSource(PathBuilder.buildTestingDataSetPath(projectName, index).toString());
            Instances testingSet = testSource.getDataSet() ;
            int testingTrueNumber = testingSet.attributeStats(testingSet.numAttributes() - 1).nominalCounts[0] ;
            if (testingTrueNumber != 0) {
                return testingSet ;
            }
        }
        return null ;
    }

}