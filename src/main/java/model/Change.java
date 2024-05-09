package model;

public class Change {

    private int addedLoc ;
    private int deletedLoc ;
    private String changeAuthor;

    public Change(int addedLoc, int deletedLoc, String changeAuthor) {
        this.addedLoc = addedLoc ;
        this.deletedLoc = deletedLoc ;
        this.changeAuthor = changeAuthor ;
    }

    public int getAddedLoc() {
        return addedLoc;
    }

    public void setAddedLoc(int addedLoc) {
        this.addedLoc = addedLoc;
    }

    public int getDeletedLoc() {
        return deletedLoc;
    }

    public void setDeletedLoc(int deletedLoc) {
        this.deletedLoc = deletedLoc;
    }

    public String getChangeAuthor() {
        return changeAuthor;
    }

    public void setChangeAuthor(String commitAuthor) {
        this.changeAuthor = commitAuthor;
    }
}
