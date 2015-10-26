package com.suh.itboy.tracker.Model;

/**
 * Created by itboy on 10/8/2015.
 */
public class GeofenceListItemModel {
    private long id;
    private String title;
    private String enterString;
    private String exitString;
    private int radius;
    private double latitude;
    private double longitude;
    private String requestId;
    private int transitionType;
    private long expirationDuration;
    private String updateTime;

    public GeofenceListItemModel(String title, double latitude, double longitude) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getTransitionType() {
        return transitionType;
    }

    public void setTransitionType(int transitionType) {
        this.transitionType = transitionType;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public long getExpirationDuration() {
        return expirationDuration;
    }

    public void setExpirationDuration(long expirationDuration) {
        this.expirationDuration = expirationDuration;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getEnterString() {
        return enterString;
    }

    public void setEnterString(String enterString) {
        this.enterString = enterString;
    }

    public String getExitString() {
        return exitString;
    }

    public void setExitString(String exitString) {
        this.exitString = exitString;
    }
}
