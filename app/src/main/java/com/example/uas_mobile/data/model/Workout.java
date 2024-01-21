package com.example.uas_mobile.data.model;

import com.google.gson.annotations.SerializedName;

public class Workout {
    private int id;
    @SerializedName("user_id")
    private int userId;
    private String type;
    private int duration;
    private String notes;
    private String date;

    public Workout(int userId, String type, int duration, String notes, String date) {
        this.userId = userId;
        this.type = type;
        this.duration = duration;
        this.notes = notes;
        this.date = date;
    }

    // Getters and setters for each field

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
