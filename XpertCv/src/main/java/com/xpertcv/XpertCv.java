/**
 * File: XpertCv.java
 * Main application class
 */
package com.xpertcv;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class XpertCv {
    private Scanner scanner;
    private DatabaseManager dbManager;
    private UploadCv uploader;
    private CvParser parser;
    private CvRanking ranker;
    private GenerateResult reporter;
    private JobDescriptionManager jobManager;
    private List<RankedCv> rankedCvs;

    public XpertCv() {
        this.scanner = new Scanner(System.in);
        this.dbManager = new DatabaseManager();
        this.uploader = new UploadCv(dbManager);
        this.parser = new CvParser(dbManager);
        this.ranker = new CvRanking(dbManager);
        this.reporter = new GenerateResult(dbManager);
        this.jobManager = new JobDescriptionManager(dbManager, scanner);
        this.rankedCvs = new ArrayList<>();
    }

    public void run() {
        showBranding();
        showStartupAnimation();
        startMenu();
    }

    public void showBranding() {
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║                                               ║");
        System.out.println("║  ██╗  ██╗██████╗ ███████╗██████╗ ████████╗   ║");
        System.out.println("║  ╚██╗██╔╝██╔══██╗██╔════╝██╔══██╗╚══██╔══╝   ║");
        System.out.println("║   ╚███╔╝ ██████╔╝█████╗  ██████╔╝   ██║      ║");
        System.out.println("║   ██╔██╗ ██╔═══╝ ██╔══╝  ██╔══██╗   ██║      ║");
        System.out.println("║  ██╔╝ ██╗██║     ███████╗██║  ██║   ██║      ║");
        System.out.println("║  ╚═╝  ╚═╝╚═╝     ╚══════╝╚═╝  ╚═╝   ╚═╝      ║");
        System.out.println("║                                               ║");
        System.out.println("║              CV ANALYZER v1.0                 ║");
        System.out.println("║                                               ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
    }

    public void showStartupAnimation() {
        System.out.print("Initializing");
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(300);
                System.out.print(".");
            } catch (InterruptedException e) {
                // Ignore
            }
        }
        System.out.println("\nSystem ready!\n");
    }

    public void startMenu() {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n===== XPERT CV ANALYZER MENU =====");
            System.out.println("1. Upload and Process CVs");
            System.out.println("2. Add Job Description");
            System.out.println("3. Analyze & Rank CVs");
            System.out.println("4. Generate Reports");
            System.out.println("5. Export Reports");
            System.out.println("6. Delete CV");
            System.out.println("7. View CVs");
            System.out.println("8. Manage Job Descriptions");
            System.out.println("9. Search CVs");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 0:
                        exit = true;
                        break;
                    case 1:
                        uploadAndProcessCvs();
                        break;
                    case 2:
                        addJobDescription();
                        break;
                    case 3:
                        analyzeCvs();
                        break;
                    case 4:
                        generateReports();
                        break;
                    case 5:
                        exportReports();
                        break;
                    case 6:
                        deleteCvMenu();
                        break;
                    case 7:
                        viewCvs();
                        break;
                    case 8:
                        manageJobDescriptions();
                        break;
                    case 9:
                        searchCvs();
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        System.out.println("Thank you for using XpertCV Analyzer!");
        dbManager.closeConnection();
        scanner.close();
    }

    private void uploadAndProcessCvs() {
        System.out.println("\n===== UPLOAD AND PROCESS CVs =====");

        String folderPath = browseFolderCLI();

        if (folderPath == null || folderPath.trim().isEmpty()) {
            System.out.println("Operation cancelled.");
            return;
        }

        try {
            List<Integer> uploadedCvIds = uploader.uploadAllFromFolder(folderPath);

            if (uploadedCvIds != null && !uploadedCvIds.isEmpty()) {
                System.out.println("Processing uploaded CVs...");
                parser.parseAndStoreUploadedCvs(uploadedCvIds);
            } else {
                System.out.println("No CVs were uploaded. Ensure the folder contains PDF or DOCX files.");
            }
        } catch (Exception e) {
            System.err.println("An error occurred while uploading or processing CVs: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public String browseFolderCLI() {
        System.out.print("Enter the folder path containing CVs: ");
        String folderPath = scanner.nextLine().trim();

        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("The specified path does not exist or is not a directory.");
            return null;
        }

        return folderPath;
    }

    public void addJobDescription() {
        System.out.println("\n===== ADD JOB DESCRIPTION =====");
        System.out.println("Enter the job description (type 'END' on a new line to finish):");

        StringBuilder description = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).equals("END")) {
            description.append(line).append("\n");
        }

        if (description.length() > 0) {
            jobManager.addJobDescription(description.toString());
        } else {
            System.out.println("Job description cannot be empty.");
        }
    }

    public void deleteCvMenu() {
        System.out.println("\n===== DELETE CV OPTIONS =====");
        System.out.println("1. Delete by ID");
        System.out.println("2. Delete All CVs");
        System.out.println("0. Cancel");
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    deleteCv();
                    break;
                case 2:
                    deleteAllCvs();
                    break;
                case 0:
                    System.out.println("Operation cancelled.");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }


    public void deleteCv() {
        viewCvs();

        System.out.print("Enter the ID of the CV to delete: ");
        try {
            int cvId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Are you sure you want to delete this CV? (y/n): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if (confirm.equals("y") || confirm.equals("yes")) {
                boolean deleted = dbManager.deleteCv(cvId);
                if (deleted) {
                    System.out.println("CV deleted successfully.");
                } else {
                    System.out.println("Failed to delete CV: ID not found.");
                }
            } else {
                System.out.println("Deletion cancelled.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    public void deleteAllCvs() {
        System.out.print("Are you sure you want to delete ALL CVs? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("y") || confirm.equals("yes")) {
            boolean deleted = dbManager.deleteAllCvs();
            if (deleted) {
                System.out.println("All CVs have been deleted successfully.");
            } else {
                System.out.println("No CVs found to delete or an error occurred.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }


    public void viewCvs() {
        List<RankedCv> cvs = dbManager.getAllCvs();

        if (cvs.isEmpty()) {
            System.out.println("No CVs found in database.");
            return;
        }

        System.out.println("\n============= STORED CVS =============");
        System.out.println("ID | File Name | Score | Has Text");
        System.out.println("--------------------------------------");

        for (RankedCv cv : cvs) {
            String cvText = dbManager.getCvTextById(cv.getCvId());
            boolean hasText = cvText != null && !cvText.isEmpty();

            System.out.printf("%2d | %-30s | %5d | %s%n",
                    cv.getCvId(), cv.getFileName(), cv.getScore(), hasText ? "Yes" : "No");
        }

        System.out.println("======================================");
    }

    public void analyzeCvs() {
        System.out.println("\n===== ANALYZE & RANK CVS =====");

        // Check if there are CVs to analyze
        List<RankedCv> cvs = dbManager.getAllCvs();
        if (cvs.isEmpty()) {
            System.out.println("No CVs found in database. Please upload CVs first.");
            return;
        }

        // Process any CVs that haven't been parsed yet
        parser.processUnparsedCvs();

        // Select a job description to use for analysis
        int jobId = jobManager.selectJobDescription();
        if (jobId == -1) {
            return;
        }

        String jobDescription = dbManager.getJobDescriptionById(jobId);
        if (jobDescription == null) {
            System.out.println("Error: Could not retrieve job description.");
            return;
        }

        System.out.println("Analyzing CVs against job description...");
        rankedCvs = ranker.rankAllCvs(jobDescription);

        if (rankedCvs.isEmpty()) {
            System.out.println("No CVs could be analyzed. Make sure CVs have been properly processed.");
        } else {
            System.out.println("Analysis complete!");
            reporter.displayRankings(rankedCvs);
        }
    }

    public void generateReports() {
        System.out.println("\n===== GENERATE REPORTS =====");

        if (rankedCvs == null || rankedCvs.isEmpty()) {
            System.out.println("No ranked CVs available. Please analyze CVs first.");
            return;
        }

        reporter.generateDetailedReports(rankedCvs);
    }

    public void exportReports() {
        System.out.println("\n===== EXPORT REPORTS =====");

        if (rankedCvs == null || rankedCvs.isEmpty()) {
            System.out.println("No ranked CVs available. Please analyze CVs first.");
            return;
        }

        System.out.println("1. Export as TXT");
        System.out.println("2. Export as CSV");
        System.out.print("Select format: ");

        try {
            int formatChoice = Integer.parseInt(scanner.nextLine().trim());
            String format;

            switch (formatChoice) {
                case 1:
                    format = "txt";
                    break;
                case 2:
                    format = "csv";
                    break;
                default:
                    System.out.println("Invalid choice. Defaulting to TXT format.");
                    format = "txt";
            }

            System.out.print("Enter the export directory path: ");
            String directoryPath = scanner.nextLine().trim();

            reporter.exportReportsToFile(rankedCvs, directoryPath, format);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private void manageJobDescriptions() {
        boolean back = false;

        while (!back) {
            System.out.println("\n===== MANAGE JOB DESCRIPTIONS =====");
            System.out.println("1. View All Job Descriptions");
            System.out.println("2. Add New Job Description");
            System.out.println("3. Edit Job Description");
            System.out.println("4. Delete Job Description");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        jobManager.displayAllJobDescriptions();
                        break;
                    case 2:
                        addJobDescription();
                        break;
                    case 3:
                        editJobDescription();
                        break;
                    case 4:
                        deleteJobDescription();
                        break;
                    case 5:
                        back = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private void editJobDescription() {
        int jobId = jobManager.selectJobDescription();
        if (jobId == -1) {
            return;
        }

        String currentDescription = dbManager.getJobDescriptionById(jobId);
        if (currentDescription == null) {
            System.out.println("Error: Could not retrieve job description.");
            return;
        }

        System.out.println("\nCurrent job description:");
        System.out.println("--------------------------------------------");
        System.out.println(currentDescription);
        System.out.println("--------------------------------------------");

        System.out.println("Enter the new job description (type 'END' on a new line to finish):");

        StringBuilder description = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).equals("END")) {
            description.append(line).append("\n");
        }

        if (description.length() > 0) {
            jobManager.updateJobDescription(jobId, description.toString());
        } else {
            System.out.println("Job description cannot be empty. Operation cancelled.");
        }
    }

    private void deleteJobDescription() {
        int jobId = jobManager.selectJobDescription();
        if (jobId == -1) {
            return;
        }

        System.out.print("Are you sure you want to delete this job description? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("y") || confirm.equals("yes")) {
            jobManager.deleteJobDescription(jobId);
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void searchCvs() {
        System.out.println("\n===== SEARCH CVS =====");

        List<RankedCv> cvs = dbManager.getAllCvs();
        if (cvs.isEmpty()) {
            System.out.println("No CVs found in database.");
            return;
        }

        System.out.println("1. Search by Keywords");
        System.out.println("2. Search by Minimum Score");
        System.out.println("3. Search by File Name");
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    searchByKeywords();
                    break;
                case 2:
                    searchByScore();
                    break;
                case 3:
                    searchByFileName();
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private void searchByKeywords() {
        System.out.print("Enter keywords (comma-separated): ");
        String keywordsInput = scanner.nextLine().trim();

        if (keywordsInput.isEmpty()) {
            System.out.println("No keywords entered.");
            return;
        }

        String[] keywordsArray = keywordsInput.split(",");
        List<String> keywords = new ArrayList<>();
        for (String keyword : keywordsArray) {
            keywords.add(keyword.trim().toLowerCase());
        }

        System.out.print("Enter minimum years of experience required (0 for any): ");
        try {
            int requiredExperience = Integer.parseInt(scanner.nextLine().trim());

            ScoringSettings settings = new ScoringSettings();
            List<RankedCv> results = ranker.rankCvsByKeywords(keywords, requiredExperience, settings);

            if (results.isEmpty()) {
                System.out.println("No matching CVs found.");
            } else {
                reporter.displayRankings(results);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for experience. Please enter a number.");
        }
    }

    private void searchByScore() {
        System.out.print("Enter minimum score: ");
        try {
            int minScore = Integer.parseInt(scanner.nextLine().trim());

            List<RankedCv> results = new ArrayList<>();
            for (RankedCv cv : dbManager.getAllCvs()) {
                if (cv.getScore() >= minScore) {
                    results.add(cv);
                }
            }

            if (results.isEmpty()) {
                System.out.println("No CVs with score " + minScore + " or higher found.");
            } else {
                reporter.displayRankings(results);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private void searchByFileName() {
        System.out.print("Enter file name (or part of it): ");
        String searchTerm = scanner.nextLine().trim().toLowerCase();

        if (searchTerm.isEmpty()) {
            System.out.println("No search term entered.");
            return;
        }

        List<RankedCv> results = new ArrayList<>();
        for (RankedCv cv : dbManager.getAllCvs()) {
            if (cv.getFileName().toLowerCase().contains(searchTerm)) {
                results.add(cv);
            }
        }

        if (results.isEmpty()) {
            System.out.println("No matching CVs found.");
        } else {
            reporter.displayRankings(results);
        }
    }

    public static void main(String[] args) {
        XpertCv app = new XpertCv();
        app.run();
    }
}