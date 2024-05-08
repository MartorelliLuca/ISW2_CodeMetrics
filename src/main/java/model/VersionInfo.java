package model;

import java.time.LocalDate;
import java.util.List;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

public class VersionInfo {

    private String versionName ;
    private LocalDate versionDate ;
    private String versionId ;
    private List<ClassInfo> classInfoList ;
    private Integer releaseNumber ;
    private List<RevCommit> versionCommitList ;


    public VersionInfo(String versionName, LocalDate versionDate, String versionId) {
        this.versionName = versionName ;
        this.versionDate = versionDate ;
        this.versionId = versionId ;
    }

    public List<ClassInfo> getClassInfoList() {
        return classInfoList;
    }

    public void setClassInfoList(List<ClassInfo> classInfoList) {
        this.classInfoList = classInfoList;
    }

    public LocalDate getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(LocalDate versionDate) {
        this.versionDate = versionDate;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public List<RevCommit> getVersionCommitList() {
        return versionCommitList;
    }

    public void setVersionCommitList(List<RevCommit> versionCommitList) {
        this.versionCommitList = versionCommitList;
    }
    public Integer getReleaseNumber() {
        return releaseNumber;
    }

    public void setReleaseNumber(Integer releaseNumber) {
        this.releaseNumber = releaseNumber;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder() ;

        stringBuilder.append("Number ").append(releaseNumber).append(" // ").append("Name ").append(versionName).
                append(" // ").append("Date ").append(versionDate.toString());

        return stringBuilder.toString() ;
    }
}