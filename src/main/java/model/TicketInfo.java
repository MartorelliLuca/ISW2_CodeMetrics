package model;

import org.eclipse.jgit.revwalk.RevCommit;

import java.time.LocalDate;
import java.util.List;

public class TicketInfo {

    private String ticketId ;

    private VersionInfo fixVersion ;
    private RevCommit fixCommit ;
    private LocalDate createDate ;
    private List<VersionInfo> affectedVersionList ;
    private VersionInfo openingVersion ;
    private LocalDate resolutionDate ;
    private VersionInfo injectedVersion ;


    public TicketInfo(String ticketId) {
        this.ticketId = ticketId ;
    }


    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }


    public VersionInfo getOpeningVersion() {
        return openingVersion;
    }

    public void setOpeningVersion(VersionInfo openingVersion) {
        this.openingVersion = openingVersion;
    }

    public RevCommit getFixCommit() {
        return fixCommit;
    }

    public void setFixCommit(RevCommit fixCommit) {
        this.fixCommit = fixCommit;
    }

    public VersionInfo getFixVersion() {
        return fixVersion;
    }

    public void setFixVersion(VersionInfo fixVersion) {
        this.fixVersion = fixVersion;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public List<VersionInfo> getAffectedVersionList() {
        return affectedVersionList;
    }

    public void setAffectedVersionList(List<VersionInfo> affectedVersionList) {
        this.affectedVersionList = affectedVersionList;
    }

    public VersionInfo getInjectedVersion() {
        return injectedVersion;
    }

    public void setInjectedVersion(VersionInfo injectedVersion) {
        this.injectedVersion = injectedVersion;
    }

    public LocalDate getResolutionDate() {
        return resolutionDate;
    }

    public void setResolutionDate(LocalDate resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder() ;
        stringBuilder.append(ticketId).append(" // ").append("Created ").append(createDate.toString()).append(" // ").append("Resolution ").append(resolutionDate.toString()).append(" // ");

        stringBuilder.append("Injected ") ;
        if (injectedVersion != null) {
            stringBuilder.append(injectedVersion.getVersionName()) ;
        }
        else {
            stringBuilder.append("NULL") ;
        }
        stringBuilder.append(" // " );

        stringBuilder.append("Opening ") ;
        if (openingVersion != null) {
            stringBuilder.append(openingVersion.getVersionName()) ;
        }
        else {
            stringBuilder.append("NULL") ;
        }
        stringBuilder.append(" // " );

        stringBuilder.append("Fix ") ;
        if (fixVersion != null) {
            stringBuilder.append(fixVersion.getVersionName()) ;
        }
        else {
            stringBuilder.append("NULL") ;
        }

        return stringBuilder.toString() ;
    }
}