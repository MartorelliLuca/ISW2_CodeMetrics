package flows;

import builder.WekaClassifierListBuilder;
import model.retrieve.Acume;
import model.weka.WekaClassifier;
import model.weka.WekaEvaluation;
import utils.PathBuilder;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import writer.AcumeWriter;
import writer.EvaluationWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static writer.AcumeWriter.createANewFileAcume;


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

            trainingSet.setClassIndex(trainingSet.numAttributes() - 1);
            testingSet.setClassIndex(testingSet.numAttributes() - 1);

            List<WekaClassifier> classifierList = listBuilder.buildClassifierList(trueNumber, falseNumber) ;

            for (WekaClassifier wekaClassifier : classifierList) {
                //per ogni classificatore weka generato sopra fai l'evaluator
                Classifier classifier = wekaClassifier.getClassifier();
                classifier.buildClassifier(trainingSet);
                //gli do in pasto il testing set e mi crea l'oggetto evaluator con il testing set inizializzato
                Evaluation evaluation = new Evaluation(testingSet) ;
                //qui me lo valuta il testing set a seconda dei classificatori
                evaluation.evaluateModel(classifier, testingSet) ;


                //mi salvo il risultato dell'evaulation nella classe wekaClassifier
                wekaClassifier.setEvaluation(evaluation);

                wekaEvaluationList.add(new WekaEvaluation(wekaClassifier, index, evaluation));

                //todo fai cose acume
                computeAcumeFile(testingSet, wekaClassifier, index, classifier, projectName);
            }
        }

        evaluationWriter.writeClassifiersEvaluation(projectName, wekaEvaluationList) ;
    }

    private static void computeAcumeFile(Instances testingSet, WekaClassifier weka, int index, Classifier classifier, String projectName) throws Exception {
        String size = "LinesOfCode";
        String isBuggy = "Buggy";

        List<Acume> acumeUtilsList = new ArrayList<>();

        int sizeIndex = testingSet.attribute(size).index();
        int isBuggyIndex = testingSet.attribute(isBuggy).index();

        int trueClassifierIndex = testingSet.classAttribute().indexOfValue("True");

        if(trueClassifierIndex != -1){
            for (int i = 0; i < testingSet.numInstances(); i++) {
                int sizeValue = (int) testingSet.instance(i).value(sizeIndex);
                int valueIndex = (int) testingSet.instance(i).value(isBuggyIndex);
                String buggyness =  testingSet.attribute(isBuggyIndex).value(valueIndex);
                String buggy;
                buggy = writeBuggy(buggyness);
                double[] distribution = classifier.distributionForInstance(testingSet.instance(i));
                Acume acumeUtils = new Acume(i, sizeValue, distribution[trueClassifierIndex], buggy);
                acumeUtilsList.add(acumeUtils);
            }
        }
        AcumeWriter.createANewFileAcume(projectName, weka, index, acumeUtilsList);
    }

    private static String writeBuggy(String buggy){
        if(buggy.equals("True")){
            return "YES";
        } else {
            return "NO";
        }
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
        ArrayList<Attribute> list = new ArrayList<>();
        return new Instances("", list, 0);
    }
}