/**
 * File: ScoringSettings.java
 * Contains settings for CV scoring
 */
package com.xpertcv;

public class ScoringSettings {
    private int pointsPerKeyword;
    private int baseExperiencePoints;
    private int pointsPerExtraYear;

    public ScoringSettings() {
        // Default settings
        this.pointsPerKeyword = 10;
        this.baseExperiencePoints = 20;
        this.pointsPerExtraYear = 5;
    }

    public ScoringSettings(int pointsPerKeyword, int baseExperiencePoints, int pointsPerExtraYear) {
        this.pointsPerKeyword = pointsPerKeyword;
        this.baseExperiencePoints = baseExperiencePoints;
        this.pointsPerExtraYear = pointsPerExtraYear;
    }

    public int getPointsPerKeyword() {
        return pointsPerKeyword;
    }

    public int getBaseExperiencePoints() {
        return baseExperiencePoints;
    }

    public int getPointsPerExtraYear() {
        return pointsPerExtraYear;
    }
}
