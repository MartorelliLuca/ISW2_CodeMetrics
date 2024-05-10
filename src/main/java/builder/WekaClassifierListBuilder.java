package builder;

import model.weka.WekaClassifier;
import model.weka.WekaFilter;
import model.weka.WekaSampler;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.ClassBalancer;
import weka.filters.supervised.instance.SpreadSubsample;

import java.util.ArrayList;
import java.util.List;

public class WekaClassifierListBuilder {

    public List<WekaClassifier> buildClassifierList() {

        List<Classifier> classifierList = new ArrayList<>(List.of(new RandomForest(), new NaiveBayes(), new IBk())) ;
        List<WekaFilter> filterList = buildFilters() ;
        List<WekaSampler> samplerList = buildSamplers() ;

        List<WekaClassifier> wekaClassifierList = new ArrayList<>() ;

        for (Classifier classifier : classifierList) {
            wekaClassifierList.add(new WekaClassifier(classifier, classifier.getClass().getSimpleName(), null, null));
        }

        for (WekaFilter wekaFilter : filterList) {
            for (Classifier classifier : classifierList) {
                FilteredClassifier filteredClassifier = new FilteredClassifier() ;
                filteredClassifier.setClassifier(classifier);
                filteredClassifier.setFilter(wekaFilter.getFilter());

                wekaClassifierList.add(new WekaClassifier(filteredClassifier, classifier.getClass().getSimpleName(), wekaFilter, null));
            }
        }

        for (WekaSampler wekaSampler : samplerList) {
            for (Classifier classifier : classifierList) {
                FilteredClassifier filteredClassifier = new FilteredClassifier() ;
                filteredClassifier.setClassifier(classifier);
                filteredClassifier.setFilter(wekaSampler.getSampler());

                wekaClassifierList.add(new WekaClassifier(filteredClassifier, classifier.getClass().getSimpleName(),null, wekaSampler));
            }
        }

        for (WekaFilter wekaFilter : filterList) {
            for (WekaSampler wekaSampler : samplerList) {
                for (Classifier classifier : classifierList) {
                    FilteredClassifier innerClassifier = new FilteredClassifier() ;
                    innerClassifier.setClassifier(classifier);
                    innerClassifier.setFilter(wekaSampler.getSampler());

                    FilteredClassifier externalClassifier = new FilteredClassifier() ;
                    externalClassifier.setFilter(wekaFilter.getFilter());
                    externalClassifier.setClassifier(innerClassifier);

                    wekaClassifierList.add(new WekaClassifier(externalClassifier, classifier.getClass().getSimpleName(), wekaFilter, wekaSampler));
                }
            }
        }


        return wekaClassifierList ;
    }

    private List<WekaFilter> buildFilters() {
        List<WekaFilter> filterList = new ArrayList<>() ;

        for (int i = 0 ; i < 2 ; i++) {
            AttributeSelection attributeSelection = new AttributeSelection() ;
            GreedyStepwise greedyStepwise = new GreedyStepwise() ;
            greedyStepwise.setSearchBackwards(i != 0);

            attributeSelection.setSearch(greedyStepwise);

            WekaFilter wekaFilter = new WekaFilter(attributeSelection, attributeSelection.getClass().getSimpleName(), greedyStepwise.getClass().getSimpleName(), i != 0) ;
            filterList.add(wekaFilter) ;
        }

        return filterList ;
    }

    private List<WekaSampler> buildSamplers() {
        List<WekaSampler> samplerList = new ArrayList<>() ;

        ClassBalancer classBalancer = new ClassBalancer() ;
        samplerList.add(new WekaSampler(classBalancer, classBalancer.getClass().getSimpleName())) ;

        SpreadSubsample spreadSubsample = new SpreadSubsample() ;
        spreadSubsample.setDistributionSpread(1.0);
        samplerList.add(new WekaSampler(spreadSubsample, spreadSubsample.getClass().getSimpleName()));

        return samplerList ;
    }
}