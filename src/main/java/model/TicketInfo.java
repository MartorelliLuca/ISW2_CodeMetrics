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


}