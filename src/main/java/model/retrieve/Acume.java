package model.retrieve;


public class Acume{

    private int index;
    private int size;
    private double probabilityOfBuggyness;
    private String isBuggy;

    public Acume(int index, int size, double probabilityOfBuggyness, String isBuggy) {
        this.index = index;
        this.size = size;
        this.probabilityOfBuggyness = probabilityOfBuggyness;
        this.isBuggy = isBuggy;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getProbabilityOfBuggyness() {
        return probabilityOfBuggyness;
    }

    public void setProbabilityOfBuggyness(double probabilityOfBuggyness) {
        this.probabilityOfBuggyness = probabilityOfBuggyness;
    }

    public String isBuggy() {
        return isBuggy;
    }

    public void setBuggy(String buggy) {
        isBuggy = buggy;
    }

}
