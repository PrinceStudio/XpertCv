/**
 * File: JobDescriptionManager.java
 * Manages job descriptions
 */
package com.xpertcv;

import java.util.List;
import java.util.Scanner;

public class JobDescriptionManager {
    private DatabaseManager dbManager;
    private Scanner scanner;

    public JobDescriptionManager(DatabaseManager dbManager, Scanner scanner) {
        this.dbManager = dbManager;
        this.scanner = scanner;
    }

    public int addJobDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            System.out.println("Job description cannot be empty.");
            return -1;
        }

        int jobId = dbManager.addJobDescription(description);
        if (jobId != -1) {
            System.out.println("Job description added successfully with ID: " + jobId);
        } else {
            System.out.println("Failed to add job description.");
        }

        return jobId;
    }

    public List<JobDescription> getAllJobDescriptions() {
        return dbManager.getAllJobDescriptions();
    }

    public boolean deleteJobDescription(int jobId) {
        boolean success = dbManager.deleteJobDescription(jobId);
        if (success) {
            System.out.println("Job description deleted successfully.");
        } else {
            System.out.println("Failed to delete job description: ID not found.");
        }

        return success;
    }

    public boolean updateJobDescription(int jobId, String newDescription) {
        if (newDescription == null || newDescription.trim().isEmpty()) {
            System.out.println("Job description cannot be empty.");
            return false;
        }

        boolean success = dbManager.updateJobDescription(jobId, newDescription);
        if (success) {
            System.out.println("Job description updated successfully.");
        } else {
            System.out.println("Failed to update job description: ID not found.");
        }

        return success;
    }

    public void displayAllJobDescriptions() {
        List<JobDescription> jobDescriptions = getAllJobDescriptions();

        if (jobDescriptions.isEmpty()) {
            System.out.println("No job descriptions found.");
            return;
        }

        System.out.println("\n============= JOB DESCRIPTIONS =============");
        System.out.println("ID | Date Added | Description");
        System.out.println("--------------------------------------------");

        for (JobDescription job : jobDescriptions) {
            String descriptionPreview = job.getDescription();
            if (descriptionPreview.length() > 50) {
                descriptionPreview = descriptionPreview.substring(0, 50) + "...";
            }

            System.out.printf("%2d | %s | %s%n",
                    job.getId(), job.getDateAdded(), descriptionPreview);
        }

        System.out.println("==============================================");
    }

    public int selectJobDescription() {
        List<JobDescription> jobDescriptions = getAllJobDescriptions();

        if (jobDescriptions.isEmpty()) {
            System.out.println("No job descriptions found. Please add one first.");
            return -1;
        }

        displayAllJobDescriptions();

        System.out.print("Enter the ID of the job description to use: ");
        try {
            int jobId = Integer.parseInt(scanner.nextLine().trim());

            // Verify the job ID exists
            boolean found = false;
            for (JobDescription job : jobDescriptions) {
                if (job.getId() == jobId) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("Invalid job ID selected.");
                return -1;
            }

            return jobId;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return -1;
        }
    }
}
