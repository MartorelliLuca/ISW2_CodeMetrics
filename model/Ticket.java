package model;

import org.jetbrains.annotations.NotNull;
import retrivers.VersionRetriever;

import java.time.LocalDate;
import java.util.ArrayList;

public class Ticket {

    String key;
    LocalDate creationDate;
    LocalDate resolutionDate;
    ArrayList<VersionInfo> affectedReleases;
    VersionInfo openingRelease;
    VersionInfo fixedRelease;
    VersionInfo injectedRelease;
    VersionRetriever versionRetriever;

    public Ticket(@NotNull String creationDate, @NotNull String resolutionDate, String key, ArrayList<VersionInfo> affectedReleases, @NotNull VersionRetriever versionRetriever) {
        this.creationDate = LocalDate.parse(creationDate.substring(0, 10));
        this.resolutionDate = LocalDate.parse(resolutionDate.substring(0, 10));
        this.key = key;
        setVersionRetriever(versionRetriever);
        setInjectedRelease(affectedReleases);
    }

    public VersionRetriever getVersionRetriever() {
        return versionRetriever;
    }

    public void setVersionRetriever(VersionRetriever versionRetriever) {
        if(versionRetriever == null) {
            throw new RuntimeException();
        }
        this.versionRetriever = versionRetriever;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public LocalDate getResolutionDate() {
        return resolutionDate;
    }

    public String getKey() {
        return key;
    }

    public ArrayList<VersionInfo> getAffectedReleases() {
        return affectedReleases;
    }

    public VersionInfo getOpeningRelease() {
        return openingRelease;
    }

    public void setOpeningRelease(VersionInfo openingRelease) {
        this.openingRelease = openingRelease;
    }

    public VersionInfo getFixedRelease() {
        return fixedRelease;
    }

    public void setFixedRelease(VersionInfo fixedRelease) {
        this.fixedRelease = fixedRelease;
        computeAffectedRelease();
    }

    public VersionInfo getInjectedRelease() {
        return injectedRelease;
    }

    public void setInjectedRelease(VersionInfo release) {
        this.injectedRelease = release;
        computeAffectedRelease();
    }

    private void setInjectedRelease(ArrayList<VersionInfo> affectedReleases) {
        if(!affectedReleases.isEmpty()) {
            this.injectedRelease = affectedReleases.get(0);
            computeAffectedRelease();
        } else {
            this.injectedRelease = null;
        }
    }

    public void computeAffectedRelease() {
        // Execute the method only if the ticket has fixed and injected release
        if(this.injectedRelease == null || this.fixedRelease == null) return;

        ArrayList<VersionInfo> affectedReleases = new ArrayList<>();
        for (VersionInfo versionInfo : versionRetriever.projVersions) {
            if ((versionInfo.getIndex() >= this.injectedRelease.getIndex()) && (versionInfo.getIndex() <= this.fixedRelease.getIndex())) {
                affectedReleases.add(versionInfo);
            }
        }

        this.affectedReleases = affectedReleases;
    }
}