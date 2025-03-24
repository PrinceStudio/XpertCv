/**
 * File: CvParser.java
 * Parses CV files and extracts text
 */
package com.xpertcv;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CvParser {
    private DatabaseManager dbManager;

    public CvParser(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public String extractText(String filePath) {
        try {
            File file = new File(filePath);
            Tika tika = new Tika();
            return tika.parseToString(file);
        } catch (IOException | TikaException e) {
            System.err.println("Error extracting text from file: " + e.getMessage());
            return "";
        }
    }

    public boolean parseAndStoreCv(int cvId, String filePath) {
        try {
            String extractedText = extractText(filePath);
            if (extractedText.isEmpty()) {
                System.out.println("Failed to extract text from CV.");
                return false;
            }

            dbManager.updateCvText(cvId, extractedText);
            return true;
        } catch (Exception e) {
            System.err.println("Error parsing CV: " + e.getMessage());
            return false;
        }
    }

    public void parseAndStoreUploadedCvs(List<Integer> cvIds) {
        int successCount = 0;
        int failCount = 0;

        for (int cvId : cvIds) {
            String filePath = dbManager.getFilePathById(cvId);
            if (filePath != null) {
                if (parseAndStoreCv(cvId, filePath)) {
                    successCount++;
                } else {
                    failCount++;
                }
            } else {
                System.out.println("CV ID " + cvId + " not found in database.");
                failCount++;
            }
        }

        System.out.println("Parse results: " + successCount + " successful, " + failCount + " failed.");
    }

    public void processUnparsedCvs() {
        List<RankedCv> unparsedCvs = dbManager.getCvsWithoutText();

        if (unparsedCvs.isEmpty()) {
            System.out.println("No unparsed CVs found in the database.");
            return;
        }

        System.out.println("Processing " + unparsedCvs.size() + " unparsed CVs...");
        int successCount = 0;

        for (RankedCv cv : unparsedCvs) {
            String filePath = dbManager.getFilePathById(cv.getCvId());
            if (filePath != null) {
                if (parseAndStoreCv(cv.getCvId(), filePath)) {
                    successCount++;
                    System.out.println("Processed CV: " + cv.getFileName());
                } else {
                    System.out.println("Failed to process CV: " + cv.getFileName());
                }
            }
        }

        System.out.println("Successfully processed " + successCount + " of " + unparsedCvs.size() + " CVs.");
    }
}