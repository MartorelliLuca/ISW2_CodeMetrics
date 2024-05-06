package model;

import java.time.LocalDate;
import org.eclipse.jgit.lib.Ref;

public class VersionInfo {

    private String versionName ;
    private LocalDate versionDate ;
    private String versionId ;
    private Integer releaseNumber ;
    private Ref ref ;


    public VersionInfo(String versionName, LocalDate versionDate, String versionId) {
        this.versionName = versionName ;
        this.versionDate = versionDate ;
        this.versionId = versionId ;
        this.ref = null ;
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

    public Ref getRef() {
        return ref;
    }

    public void setRef(Ref ref) {
        this.ref = ref;
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