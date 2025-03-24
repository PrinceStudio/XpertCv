/**
 * File: GenerateResult.java
 * Handles generation and display of reports
 */
package com.xpertcv;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GenerateResult {
    private DatabaseManager dbManager;

    public GenerateResult(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void displayRankings(List<RankedCv> rankedCvs) {
        if (rankedCvs == null || rankedCvs.isEmpty()) {
            System.out.println("No ranked CVs to display.");
            return;
        }

        System.out.println("\n============= CV RANKINGS =============");
        System.out.println("Rank | CV ID | File Name | Score | Feedback");
        System.out.println("----------------------------------------");

        int rank = 1;
        for (RankedCv cv : rankedCvs) {
            String feedbackPreview = cv.getAiFeedback();
            if (feedbackPreview != null && feedbackPreview.length() > 30) {
                feedbackPreview = feedbackPreview.substring(0, 30) + "...";
            }

            System.out.printf("%4d | %5d | %-30s | %5d | %s%n",
                    rank++, cv.getCvId(), truncateString(cv.getFileName(), 30), cv.getScore(), feedbackPreview);
        }

        System.out.println("========================================");
        System.out.println("Use 'Generate Detailed Reports' option to see complete feedback.");
    }

    public void generateDetailedReports(List<RankedCv> rankedCvs) {
        if (rankedCvs == null || rankedCvs.isEmpty()) {
            System.out.println("No ranked CVs to report on.");
            return;
        }

        System.out.println("\n========== DETAILED CV REPORTS ==========");

        int rank = 1;
        for (RankedCv cv : rankedCvs) {
            System.out.println("\nRank #" + rank++ + " - CV ID: " + cv.getCvId());
            System.out.println("File Name: " + cv.getFileName());
            System.out.println("Score: " + cv.getScore() + "/100");
            System.out.println("--------------------------------------------");
            System.out.println("AI Feedback:");
            System.out.println(cv.getAiFeedback());
            System.out.println("--------------------------------------------");
        }
    }

    public boolean exportReportsToFile(List<RankedCv> rankedCvs, String filePath, String format) {
        if (rankedCvs == null || rankedCvs.isEmpty()) {
            System.out.println("No ranked CVs to export.");
            return false;
        }

        if (format == null) {
            format = "txt"; // Default format
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = filePath + "/cv_report_" + timestamp + "." + format.toLowerCase();

        try {
            // Create directory if it doesn't exist
            Files.createDirectories(Paths.get(filePath));

            switch (format.toLowerCase()) {
                case "csv":
                    exportToCSV(rankedCvs, fileName);
                    break;
                case "txt":
                default:
                    exportToTXT(rankedCvs, fileName);
                    break;
            }

            System.out.println("Report exported successfully to: " + fileName);
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting report: " + e.getMessage());
            return false;
        }
    }

    private void exportToCSV(List<RankedCv> rankedCvs, String fileName) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Write header
            writer.write("Rank,CV ID,File Name,Score,Feedback\n");

            // Write data
            int rank = 1;
            for (RankedCv cv : rankedCvs) {
                String escapedFeedback = "";
                if (cv.getAiFeedback() != null) {
                    // Escape quotes and replace newlines for CSV
                    escapedFeedback = cv.getAiFeedback()
                            .replace("\"", "\"\"")
                            .replace("\n", " ");
                }

                writer.write(String.format("%d,%d,\"%s\",%d,\"%s\"%n",
                        rank++, cv.getCvId(), cv.getFileName(), cv.getScore(), escapedFeedback));
            }
        }
    }

    private void exportToTXT(List<RankedCv> rankedCvs, String fileName) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Write header
            writer.write("============= CV RANKINGS =============\n");
            writer.write("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\n");

            // Write data
            int rank = 1;
            for (RankedCv cv : rankedCvs) {
                writer.write("Rank #" + rank++ + " - CV ID: " + cv.getCvId() + "\n");
                writer.write("File Name: " + cv.getFileName() + "\n");
                writer.write("Score: " + cv.getScore() + "/100\n");
                writer.write("--------------------------------------------\n");
                writer.write("AI Feedback:\n");
                writer.write(cv.getAiFeedback() + "\n");
                writer.write("============================================\n\n");
            }
        }
    }

    private String truncateString(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}