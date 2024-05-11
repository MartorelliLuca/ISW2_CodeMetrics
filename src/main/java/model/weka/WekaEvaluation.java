package model.weka;


import weka.classifiers.Evaluation;

public class WekaEvaluation {

    private Evaluation evaluation ;
    private int evaluationIndex ;
    private WekaClassifier wekaClassifier ;

    private static final int INDEX = 0;

    public WekaEvaluation(WekaClassifier wekaClassifier, int evaluationIndex, Evaluation evaluation) {
        this.wekaClassifier = wekaClassifier ;
        this.evaluationIndex = evaluationIndex ;
        this.evaluation = evaluation ;
    }

    public int getEvaluationIndex() {
        return evaluationIndex;
    }

    public String getClassifierName() {
        return wekaClassifier.getClassifierName();
    }

    public String getFilterName() {
        return wekaClassifier.getFilterName();
    }

    public String getSamplerName() {
        return wekaClassifier.getSamplerName();
    }

    public boolean isCostSensitive() {
        return wekaClassifier.getIsCostSensitive() ;
    }

    public double getPrecision() {
        int precisionDen = (int) (evaluation.numTruePositives(INDEX) + evaluation.numFalsePositives(INDEX));
        if (precisionDen == 0) {
            return Double.NaN ;
        }
        return evaluation.precision(INDEX) ;
    }

    public double getRecall() {
        int recallDen = (int) (evaluation.numTruePositives(INDEX) + evaluation.numFalseNegatives(INDEX)) ;
        if (recallDen == 0) {
            return Double.NaN ;
        }
        return evaluation.recall(INDEX) ;
    }

    public double getRoc() {
        return evaluation.areaUnderROC(INDEX) ;
    }

    public double getKappa() {
        return evaluation.kappa() ;
    }

    public double getTruePositive() {
        return evaluation.numTruePositives(INDEX) ;
    }

    public double getFalsePositive() {
        return evaluation.numFalsePositives(INDEX) ;
    }

    public double getTrueNegative() {
        return evaluation.numTrueNegatives(INDEX) ;
    }

    public double getFalseNegative() {
        return evaluation.numFalseNegatives(INDEX) ;
    }

}