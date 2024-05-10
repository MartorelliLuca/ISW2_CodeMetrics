package main;

import utils.PathBuilder;

import java.util.List;
import weka.classifiers.Classifier;
import weka.classifiers.rules.ZeroR;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class WekaFlow {

    private static final List<Classifier> classifierList = List.of(new ZeroR()) ;

    private WekaFlow() {}
    public static void weka(String projectName, int maxIndex) throws Exception {

        for (int i = 0 ; i < maxIndex ; i++) {
            DataSource trainSource = new DataSource(PathBuilder.buildTrainingDataSetPath(projectName, i).toString());
            Instances trainingSet = trainSource.getDataSet() ;
            trainingSet.setClassIndex(trainingSet.numAttributes() - 1);
            DataSource testSource = new DataSource(PathBuilder.buildTestingDataSetPath(projectName, i).toString());
            Instances testingSet = testSource.getDataSet() ;
        }
    }


}