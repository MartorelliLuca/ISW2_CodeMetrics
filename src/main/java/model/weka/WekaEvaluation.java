package model.weka;


import weka.classifiers.Evaluation;

public class WekaEvaluation {

    private Evaluation evaluation ;
    private int evaluationIndex ;
    private String classifierName ;

    public WekaEvaluation(String classifierName, int evaluationIndex, Evaluation evaluation) {
        this.classifierName = classifierName ;
        this.evaluationIndex = evaluationIndex ;
        this.evaluation = evaluation ;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public int getEvaluationIndex() {
        return evaluationIndex;
    }

    public String getClassifierInfo() {
        return classifierName;
    }

}