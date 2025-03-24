/**
 * File: RankedCv.java
 * Model class to represent a ranked CV
 */
package com.xpertcv;

public class RankedCv implements Comparable<RankedCv> {
    private int cvId;
    private String fileName;
    private int score;
    private String aiFeedback;

    public RankedCv(int cvId, String fileName, int score, String aiFeedback) {
        this.cvId = cvId;
        this.fileName = fileName;
        this.score = score;
        this.aiFeedback = aiFeedback;
    }

    public int getCvId() {
        return cvId;
    }

    public void setCvId(int cvId) {
        this.cvId = cvId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getAiFeedback() {
        return aiFeedback;
    }

    public void setAiFeedback(String aiFeedback) {
        this.aiFeedback = aiFeedback;
    }

    @Override
    public int compareTo(RankedCv other) {
        // Sort in descending order by score
        return Integer.compare(other.score, this.score);
    }
}