package builder;

import model.weka.WekaClassifier;
import model.weka.WekaFilter;
import model.weka.WekaSampler;
import weka.attributeSelection.BestFirst;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.SelectedTag;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;

import java.util.ArrayList;
import java.util.List;

public class WekaClassifierListBuilder {

    public List<WekaClassifier> buildClassifierList(int trueNumber, int falseNumber){

        List<Classifier> classifierList = buildBaseClassifiersList() ;
        List<WekaFilter> filterList = buildFilters() ;
        List<WekaSampler> samplerList = buildSamplers(trueNumber, falseNumber) ;

        List<WekaClassifier> wekaClassifierList = new ArrayList<>() ;

        // Classificatore da solo
        for (Classifier classifier : classifierList) {
            wekaClassifierList.add(new WekaClassifier(classifier, classifier.getClass().getSimpleName(), null, null, false));
        }

        // Classificatore con aggiunta Attribute Selection
        wekaClassifierList.addAll(combineWithAttributeSelection(filterList, classifierList)) ;

        // Classificatore con aggiunto Sampling
        wekaClassifierList.addAll(combineWithSampling(samplerList, classifierList)) ;

        // Classificatore con Attribute Selection e Sampling
        wekaClassifierList.addAll(combineAttributeSelectionAndSampling(filterList, samplerList, classifierList)) ;

        // Classificatore con Sensitive Learning
        wekaClassifierList.addAll(combineWithCostSensitive(filterList, classifierList)) ;


        return wekaClassifierList ;
    }

    private List<Classifier> buildBaseClassifiersList() {
        List<Classifier> classifierList = new ArrayList<>() ;

        RandomForest randomForest = new RandomForest() ;
        classifierList.add(randomForest);

        NaiveBayes naiveBayes = new NaiveBayes() ;
        classifierList.add(naiveBayes);

        Classifier iBk = new IBk() ;
        classifierList.add(iBk);

        return classifierList ;
    }



    private List<WekaClassifier> combineWithAttributeSelection(List<WekaFilter> filterList, List<Classifier> classifierList) {
        List<WekaClassifier> wekaClassifierList = new ArrayList<>() ;
        for (WekaFilter wekaFilter : filterList) {
            for (Classifier classifier : classifierList) {
                FilteredClassifier filteredClassifier = new FilteredClassifier() ;
                filteredClassifier.setClassifier(classifier);
                filteredClassifier.setFilter(wekaFilter.getFilter());

                wekaClassifierList.add(new WekaClassifier(filteredClassifier, classifier.getClass().getSimpleName(), wekaFilter, null, false));
            }
        }
        return wekaClassifierList ;
    }

    private List<WekaClassifier> combineWithSampling(List<WekaSampler> samplerList, List<Classifier> classifierList) {
        List<WekaClassifier> wekaClassifierList = new ArrayList<>() ;
        for (WekaSampler wekaSampler : samplerList) {
            for (Classifier classifier : classifierList) {
                FilteredClassifier filteredClassifier = new FilteredClassifier() ;
                filteredClassifier.setClassifier(classifier);
                filteredClassifier.setFilter(wekaSampler.getSampler());

                wekaClassifierList.add(new WekaClassifier(filteredClassifier, classifier.getClass().getSimpleName(),null, wekaSampler, false));
            }
        }

        return wekaClassifierList ;
    }

    private List<WekaClassifier> combineAttributeSelectionAndSampling(List<WekaFilter> filterList, List<WekaSampler> samplerList, List<Classifier> classifierList) {
        List<WekaClassifier> wekaClassifierList = new ArrayList<>() ;
        for (WekaFilter wekaFilter : filterList) {
            for (WekaSampler wekaSampler : samplerList) {
                for (Classifier classifier : classifierList) {
                    FilteredClassifier innerClassifier = new FilteredClassifier() ;
                    innerClassifier.setClassifier(classifier);
                    innerClassifier.setFilter(wekaSampler.getSampler());

                    FilteredClassifier externalClassifier = new FilteredClassifier() ;
                    externalClassifier.setFilter(wekaFilter.getFilter());
                    externalClassifier.setClassifier(innerClassifier);

                    wekaClassifierList.add(new WekaClassifier(externalClassifier, classifier.getClass().getSimpleName(), wekaFilter, wekaSampler, false));
                }
            }
        }

        return wekaClassifierList ;
    }

    private List<WekaClassifier> combineWithCostSensitive(List<WekaFilter> filterList, List<Classifier> classifierList) {
        List<WekaClassifier> wekaClassifierList = new ArrayList<>() ;
        for (Classifier classifier : classifierList) {
            List<CostSensitiveClassifier> costSensitiveClassifierList = buildCostSensitiveClassifiers() ;
            for (CostSensitiveClassifier costSensitiveClassifier : costSensitiveClassifierList) {
                costSensitiveClassifier.setClassifier(classifier);
                wekaClassifierList.add(new WekaClassifier(costSensitiveClassifier, classifier.getClass().getSimpleName(), null, null, true));
            }
        }

        for (Classifier classifier : classifierList) {
            List<CostSensitiveClassifier> costSensitiveClassifierList = buildCostSensitiveClassifiers() ;
            for (CostSensitiveClassifier costSensitiveClassifier : costSensitiveClassifierList) {
                for (WekaFilter wekaFilter : filterList) {
                    FilteredClassifier externalClassifier = new FilteredClassifier() ;
                    externalClassifier.setClassifier(costSensitiveClassifier);
                    externalClassifier.setFilter(wekaFilter.getFilter());

                    costSensitiveClassifier.setClassifier(classifier);

                    wekaClassifierList.add(new WekaClassifier(externalClassifier, classifier.getClass().getSimpleName(), wekaFilter, null, true));
                }
            }
        }

        return wekaClassifierList ;
    }

    private List<WekaFilter> buildFilters() {
        List<WekaFilter> filterList = new ArrayList<>() ;

        for (int i = 0 ; i < 3 ; i++) {
            AttributeSelection attributeSelection = new AttributeSelection() ;

            BestFirst search = new BestFirst() ;
            SelectedTag directionTag = new SelectedTag(i, search.getDirection().getTags()) ;

            attributeSelection.setSearch(search);

            String directionString = directionTag.getSelectedTag().getReadable() ;

            WekaFilter wekaFilter = new WekaFilter(attributeSelection, search.getClass().getSimpleName(), directionString) ;
            filterList.add(wekaFilter) ;
        }

        return filterList ;
    }

    private List<WekaSampler> buildSamplers(int trueNumber, int falseNumber) {
        List<WekaSampler> samplerList = new ArrayList<>() ;

        // Oversampling
        Resample resample = new Resample() ;
        resample.setNoReplacement(false);
        resample.setBiasToUniformClass(1.0);

        double resamplePercentage = (((double) falseNumber) / (falseNumber + trueNumber)) * 100 ;

        resample.setSampleSizePercent(2 * resamplePercentage);
        samplerList.add(new WekaSampler(resample)) ;

        //Undersampling
        SpreadSubsample spreadSubsample = new SpreadSubsample() ;
        spreadSubsample.setDistributionSpread(1.0);
        samplerList.add(new WekaSampler(spreadSubsample));

        //SMOTE
        SMOTE smote = new SMOTE() ;
        double smotePercentage;
        if (trueNumber == 0) {
            smotePercentage = 0 ;
        }
        else {
            smotePercentage = ((falseNumber - trueNumber) / ((double) trueNumber)) * 100.0 ;
        }
        smote.setPercentage(smotePercentage);
        smote.setClassValue("1");
        samplerList.add(new WekaSampler(smote));

        return samplerList ;
    }


    private List<CostSensitiveClassifier> buildCostSensitiveClassifiers() {
        CostSensitiveClassifier costSensitiveClassifier = new CostSensitiveClassifier() ;
        // Sensitive Learning
        costSensitiveClassifier.setMinimizeExpectedCost(false) ;

        costSensitiveClassifier.setCostMatrix(buildCostMatrix(1.0, 10.0));

        return new ArrayList<>(List.of(costSensitiveClassifier)) ;
    }

    private CostMatrix buildCostMatrix(double costFalsePositive, double costFalseNegative) {
        // ricontrolla matrice
        CostMatrix costMatrix = new CostMatrix(2) ;
        costMatrix.setCell(0,0,0.0);
        costMatrix.setCell(1,1,0.0);
        costMatrix.setCell(0,1, costFalseNegative);
        costMatrix.setCell(1,0, costFalsePositive);

        return costMatrix ;
    }

}