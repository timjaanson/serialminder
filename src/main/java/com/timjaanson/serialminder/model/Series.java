package com.timjaanson.serialminder.model;

import java.sql.Timestamp;

public class Series {
    private String id;
    private String name;
    private Integer availableSeasons;
    private Timestamp lastChecked;
    private boolean allowedForCheck;


    public Series() {}

    public Series(String id, String name, Integer availableSeasons, Timestamp lastChecked) {
        this.id = id;
        this.name = name;
        this.availableSeasons = availableSeasons;
        this.lastChecked = lastChecked;
    }

    public Series(String id, String name, Integer availableSeasons, Timestamp lastChecked, boolean allowedForCheck) {
        this.id = id;
        this.name = name;
        this.availableSeasons = availableSeasons;
        this.lastChecked = lastChecked;
        this.allowedForCheck = allowedForCheck;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAvailableSeasons() {
        return availableSeasons;
    }

    public void setAvailableSeasons(Integer availableSeasons) {
        this.availableSeasons = availableSeasons;
    }

    public Timestamp getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(Timestamp lastChecked) {
        this.lastChecked = lastChecked;
    }

    public boolean isAllowedForCheck() {
        return allowedForCheck;
    }

    public void setAllowedForCheck(boolean allowedForCheck) {
        this.allowedForCheck = allowedForCheck;
    }
}
