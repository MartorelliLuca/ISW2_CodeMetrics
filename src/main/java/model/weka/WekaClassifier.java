package model.weka;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;


public class WekaClassifier {

    private final String classifierName;
    private Classifier classifier ;
    private WekaFilter wekaFilter ;
    private WekaSampler wekaSampler ;
    private Evaluation evaluation ;

    private final boolean isCostSensitive ;

    public WekaClassifier(Classifier classifier, String classifierName, WekaFilter wekaFilter, WekaSampler wekaSampler, boolean isCostSensitive) {
        this.classifier = classifier ;
        this.classifierName = classifierName ;
        this.wekaFilter = wekaFilter ;
        this.wekaSampler = wekaSampler ;
        this.isCostSensitive = isCostSensitive ;
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public String getFilterName() {
        return String.valueOf(wekaFilter != null);
    }

    public String getSamplingType() {
        if (wekaSampler == null) {
            return "NotSet" ;
        }
        return wekaSampler.getSamplingType();
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public String getClassifierName() {
        return classifierName ;
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

    public boolean getIsCostSensitive() {
        return isCostSensitive ;
    }
}