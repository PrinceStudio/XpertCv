/**
 * File: JobDescription.java
 * Model class to represent a job description
 */
package com.xpertcv;

public class JobDescription {
    private int id;
    private String description;
    private String dateAdded;

    public JobDescription(int id, String description, String dateAdded) {
        this.id = id;
        this.description = description;
        this.dateAdded = dateAdded;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getDateAdded() {
        return dateAdded;
    }
}