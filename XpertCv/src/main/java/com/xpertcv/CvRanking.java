/**
 * File: CvRanking.java
 * Ranks CVs based on keywords, experience, and AI analysis
 */
package com.xpertcv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CvRanking {
    private DatabaseManager dbManager;
    private DeepSeekAnalyzer aiAnalyzer;
    private List<RankedCv> rankedCvs;

    public CvRanking(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.aiAnalyzer = new DeepSeekAnalyzer();
        this.rankedCvs = new ArrayList<>();
    }

    public List<RankedCv> rankCvsByKeywords(List<String> keywords, int requiredExperience, ScoringSettings settings) {
        List<RankedCv> cvs = dbManager.getAllCvs();
        rankedCvs = new ArrayList<>();

        for (RankedCv cv : cvs) {
            String cvText = dbManager.getCvTextById(cv.getCvId());
            if (cvText == null || cvText.isEmpty()) {
                continue; // Skip CVs without extracted text
            }

            int score = 0;
            cvText = cvText.toLowerCase();

            // Keyword matching
            for (String keyword : keywords) {
                if (cvText.contains(keyword.toLowerCase())) {
                    score += settings.getPointsPerKeyword();
                }
            }

            // Experience matching
            int experienceYears = extractExperienceYears(cvText);
            if (experienceYears >= requiredExperience) {
                score += settings.getBaseExperiencePoints();
                // Bonus for extra years
                score += Math.min(5, experienceYears - requiredExperience) * settings.getPointsPerExtraYear();
            }

            cv.setScore(score);
            cv.setAiFeedback("Manual scoring based on keywords and experience.");
            rankedCvs.add(cv);
        }

        Collections.sort(rankedCvs);
        return rankedCvs;
    }

    public List<RankedCv> rankAllCvs(String jobDescription) {
        List<RankedCv> cvs = dbManager.getAllCvs();
        rankedCvs = new ArrayList<>();

        for (RankedCv cv : cvs) {
            String cvText = dbManager.getCvTextById(cv.getCvId());
            if (cvText == null || cvText.isEmpty()) {
                continue; // Skip CVs without extracted text
            }

            // Use DeepSeek API to analyze CV
            Map<String, Object> analysisResult = aiAnalyzer.mockAnalyzeCv(cvText, jobDescription);
            int score = (int) analysisResult.get("score");
            String feedback = (String) analysisResult.get("feedback");

            cv.setScore(score);
            cv.setAiFeedback(feedback);
            dbManager.updateCvWithAI(cv.getCvId(), score, feedback);
            rankedCvs.add(cv);
        }

        Collections.sort(rankedCvs);
        return rankedCvs;
    }

    public List<RankedCv> getRankedCvs() {
        return rankedCvs;
    }

    public int extractExperienceYears(String cvText) {
        // Use the same method as in DeepSeekAnalyzer
        return aiAnalyzer.extractExperienceYears(cvText);
    }
}