package model.weka;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;

public class WekaClassifier {

    private Classifier classifier ;
    private String classifierName ;
    private WekaFilter wekaFilter ;
    private WekaSampler wekaSampler ;
    private Evaluation evaluation ;

    private Integer index ;

    public WekaClassifier(Classifier classifier, String classifierName, WekaFilter wekaFilter, WekaSampler wekaSampler) {
        this.classifier = classifier ;
        this.classifierName = classifierName ;
        this.wekaFilter = wekaFilter ;
        this.wekaSampler = wekaSampler ;
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public String getFilterName() {
        if (wekaFilter == null) {
            return "NULL" ;
        }
        if (wekaFilter.getBackward() == null) {
            return wekaFilter.getFilterName() + "_" + wekaFilter.getSearchMethod();
        }
        return wekaFilter.getFilterName() + "_" + wekaFilter.getSearchMethod() + (wekaFilter.getBackward() ? "Backward" : "Forward") ;
    }

    public String getSamplerName() {
        if (wekaSampler == null) {
            return "NULL" ;
        }
        return wekaSampler.getSamplerName();
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public String getClassifierName() {
        return classifierName + "_" + getFilterName() + "_" + getSamplerName();
    }

    public WekaFilter getWekaFilter() {
        return wekaFilter;
    }

    public void setWekaFilter(WekaFilter wekaFilter) {
        this.wekaFilter = wekaFilter;
    }

    public WekaSampler getWekaSampler() {
        return wekaSampler;
    }

    public void setWekaSampler(WekaSampler wekaSampler) {
        this.wekaSampler = wekaSampler;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }
}