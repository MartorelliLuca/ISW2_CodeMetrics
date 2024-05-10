package model.retrieve;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

public class ClassInfo {

    private String name ;
    private boolean buggy ;
    private int loc ;
    private int addedLoc ;
    private int maxAddedLoc ;
    private float avgAddedLoc ;
    private int touchedLoc ;
    private int churn ;
    private int maxChurn ;
    private float avgChurn ;
    private int numberOfAuthors ;
    private List<RevCommit> modifierCommitList ;
    private int numberOfRevisions ;
    private int numberDefectsFixed ;


    public ClassInfo(String name) {
        this.name = name ;
        this.modifierCommitList = new ArrayList<>() ;
        this.buggy = false ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBuggy(boolean buggy) {
        this.buggy = buggy;
    }

    public boolean isBuggy() {
        return buggy;
    }

    public int getLoc() {
        return loc;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }

    public int getAddedLoc() {
        return addedLoc;
    }

    public void setAddedLoc(int addedLoc) {
        this.addedLoc = addedLoc;
    }

    public int getMaxAddedLoc() {
        return maxAddedLoc;
    }

    public void setMaxAddedLoc(int maxAddedLoc) {
        this.maxAddedLoc = maxAddedLoc;
    }

    public float getAvgAddedLoc() {
        return avgAddedLoc;
    }

    public void setAvgAddedLoc(float avgAddedLoc) {
        this.avgAddedLoc = avgAddedLoc;
    }

    public int getNumberOfAuthors() {
        return numberOfAuthors;
    }

    public void setNumberOfAuthors(int numberOfAuthors) {
        this.numberOfAuthors = numberOfAuthors;
    }

    public int getTouchedLoc() {
        return touchedLoc;
    }

    public void setTouchedLoc(int touchedLoc) {
        this.touchedLoc = touchedLoc;
    }

    public int getChurn() {
        return churn;
    }

    public void setChurn(int churn) {
        this.churn = churn;
    }

    public int getMaxChurn() {
        return maxChurn;
    }

    public void setMaxChurn(int maxChurn) {
        this.maxChurn = maxChurn;
    }

    public float getAvgChurn() {
        return avgChurn;
    }

    public void setAvgChurn(float avgChurn) {
        this.avgChurn = avgChurn;
    }
    public int getNumberOfRevisions() {
        return numberOfRevisions;
    }

    public void setNumberOfRevisions(int numberOfRevisions) {
        this.numberOfRevisions = numberOfRevisions;
    }

    public List<RevCommit> getModifierCommitList() {
        return modifierCommitList;
    }

    public void setModifierCommitList(List<RevCommit> modifierCommitList) {
        this.modifierCommitList = modifierCommitList;
    }

    public int getNumberDefectsFixed() {
        return numberDefectsFixed;
    }

    public void setNumberDefectsFixed(int numberDefectsFixed) {
        this.numberDefectsFixed = numberDefectsFixed;
    }
}
