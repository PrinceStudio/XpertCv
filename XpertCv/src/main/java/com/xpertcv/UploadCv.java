/**
 * File: UploadCv.java
 * Handles CV upload operations
 */
package com.xpertcv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UploadCv {
    private DatabaseManager dbManager;
    private final List<Integer> uploadedCvIds;

    public UploadCv(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.uploadedCvIds = new ArrayList<>();
    }

    public List<Integer> uploadAllFromFolder(String folderPath) {
        uploadedCvIds.clear();
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("The specified folder does not exist or is not a directory.");
            return uploadedCvIds;
        }

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("The folder is empty.");
            return uploadedCvIds;
        }

        int successCount = 0;
        for (File file : files) {
            if (file.isFile() && isAllowedFormat(file)) {
                int cvId = uploadCv(file);
                if (cvId != -1) {
                    uploadedCvIds.add(cvId);
                    successCount++;
                }
            }
        }

        System.out.println("Successfully uploaded " + successCount + " CV files.");
        return uploadedCvIds;
    }

    public int uploadCv(File file) {
        String fileName = file.getName();
        String filePath = file.getAbsolutePath();

        if (!isAllowedFormat(file)) {
            System.out.println("Skipping " + fileName + ": Format not supported. Only PDF and DOCX files are allowed.");
            return -1;
        }

        if (dbManager.isCvAlreadyUploaded(fileName)) {
            System.out.println("Skipping " + fileName + ": File already exists in the database.");
            return -1;
        }

        // Insert CV with empty text for now
        int cvId = dbManager.insertCv(fileName, filePath, "");

        if (cvId != -1) {
            System.out.println("Uploaded: " + fileName);
        } else {
            System.out.println("Failed to upload: " + fileName);
        }

        return cvId;
    }

    public boolean isAllowedFormat(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".pdf") || fileName.endsWith(".docx");
    }

    public List<Integer> getUploadedCvIds() {
        return uploadedCvIds;
    }
}