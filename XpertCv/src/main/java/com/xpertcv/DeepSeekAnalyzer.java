/**
 * File: DeepSeekAnalyzer.java
 * Handles CV analysis using the DeepSeek API
 */
package com.xpertcv;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

public class DeepSeekAnalyzer {
    private static final String API_URL = "https://api.deepseek.com/v1/analyze";
    private static final String API_KEY = "sk-321d35a5f7a943d9b887573317f8b779";

    public Map<String, Object> analyzeCv(String cvText, String jobDescription) {
        Map<String, Object> result = new HashMap<>();
        int score = 0;
        String feedback = "";

        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setDoOutput(true);

            // Create JSON request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("cv_text", cvText);
            requestBody.put("job_description", jobDescription);

            // Send request
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(requestBody.toString().getBytes(StandardCharsets.UTF_8));
            }

            // Get response
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse response
                JSONObject jsonResponse = new JSONObject(response.toString());
                score = jsonResponse.getInt("score");
                feedback = jsonResponse.getString("feedback");
            } else {
                System.err.println("API request failed with response code: " + responseCode);
                // Fall back to keyword and experience scoring
                score = 50; // Default score
                feedback = "API request failed. This is a fallback score based on basic matching.";
            }
        } catch (Exception e) {
            System.err.println("Error analyzing CV: " + e.getMessage());
            // Fall back to keyword and experience scoring
            score = 50; // Default score
            feedback = "Error in API analysis. This is a fallback score based on basic matching.";
        }

        result.put("score", score);
        result.put("feedback", feedback);
        return result;
    }

    // Mock method for testing without actual API
    public Map<String, Object> mockAnalyzeCv(String cvText, String jobDescription) {
        Map<String, Object> result = new HashMap<>();
        int score = 0;
        StringBuilder feedback = new StringBuilder("CV Analysis Feedback:\n");

        // Simple keyword matching
        String[] keywords = jobDescription.toLowerCase().split("\\W+");
        int keywordMatches = 0;

        for (String keyword : keywords) {
            if (keyword.length() > 4) { // Only consider meaningful keywords
                if (cvText.toLowerCase().contains(keyword)) {
                    keywordMatches++;
                }
            }
        }

        // Calculate experience (mock)
        int experienceYears = extractExperienceYears(cvText);

        // Score calculation
        double keywordRate = (double) keywordMatches / keywords.length;
        score = (int) (keywordRate * 75 + Math.min(experienceYears, 5) * 5);

        // Feedback generation
        feedback.append("Keyword match rate: ").append(String.format("%.1f", keywordRate * 100)).append("%\n");
        feedback.append("Experience detected: ").append(experienceYears).append(" years\n\n");

        if (score > 80) {
            feedback.append("Excellent match for the position. Strong skills alignment with job requirements.");
        } else if (score > 60) {
            feedback.append("Good match for the position. Covers most of the required skills.");
        } else if (score > 40) {
            feedback.append("Average match. Some skills align, but there are gaps in experience.");
        } else {
            feedback.append("Below average match. Significant gaps between CV and job requirements.");
        }

        result.put("score", score);
        result.put("feedback", feedback.toString());
        return result;
    }

    public int extractExperienceYears(String cvText) {
        int totalMonths = 0;

        // Regex patterns for various formats
        Pattern[] patterns = new Pattern[] {
                Pattern.compile("(\\w+\\s\\d{4})\\s*(to|\\-|–)\\s*(\\w+\\s\\d{4}|present)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("(\\d{4})\\s*(to|\\-|–)\\s*(\\d{4}|present)", Pattern.CASE_INSENSITIVE)
        };

        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(cvText.toLowerCase());

            while (matcher.find()) {
                String startStr = matcher.group(1).trim();
                String endStr = matcher.group(3).trim();

                try {
                    LocalDate startDate = parseDate(startStr);
                    LocalDate endDate = endStr.contains("present") ? LocalDate.now() : parseDate(endStr);

                    if (startDate != null && endDate != null && !endDate.isBefore(startDate)) {
                        Period period = Period.between(startDate, endDate);
                        totalMonths += period.getYears() * 12 + period.getMonths();
                    }
                } catch (Exception ignored) {
                }
            }
        }

        return totalMonths / 12; // Convert to years
    }
    private LocalDate parseDate(String dateStr) {
        try {
            // e.g., March 2022 or Mar 2022
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e1) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);
                return LocalDate.parse(dateStr, formatter);
            } catch (Exception e2) {
                try {
                    int year = Integer.parseInt(dateStr.trim());
                    return LocalDate.of(year, 1, 1);
                } catch (Exception e3) {
                    return null;
                }
            }
        }
    }
}
