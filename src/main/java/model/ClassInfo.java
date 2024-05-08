package model;

public class ClassInfo {

    private String name ;

    private boolean buggy ;

    public ClassInfo(String name) {
        this.name = name ;
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
}
