package com.khaiminh.ecolocator.Models;

import com.google.firebase.firestore.GeoPoint;
import java.util.Date;
import java.util.List;

public class Site {
    private String id;
    private String name;
    private String description;
    private String additionalInfo;
    private String admin;
    private GeoPoint coordinates;
    private Date dateTime;
    private List<String> participants;

    // No-argument constructor
    public Site() {
    }

    // Constructor with arguments
    public Site(String id, String name, String description, String additionalInfo, String admin, GeoPoint coordinates, Date dateTime, List<String> participants) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.additionalInfo = additionalInfo;
        this.admin = admin;
        this.coordinates = coordinates;
        this.dateTime = dateTime;
        this.participants = participants;
    }

    // Getters and setters for all fields
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public GeoPoint getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(GeoPoint coordinates) {
        this.coordinates = coordinates;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
}
