package model;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public class VersionInfo {
    String id;
    int index;
    String name;
    LocalDate date;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public VersionInfo(String id, String name, LocalDate date) {
        this.id = id;
        this.name = name;
        this.date = date;
    }
}