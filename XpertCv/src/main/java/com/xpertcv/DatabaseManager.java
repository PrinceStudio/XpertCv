package com.xpertcv;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:xpertcv.db";
    private Connection connection;

    public DatabaseManager() {
        initializeDatabase();
    }

    public void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            Statement statement = connection.createStatement();

            // Create CV Storage table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS cv_storage (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "file_name TEXT NOT NULL, " +
                            "file_path TEXT NOT NULL, " +
                            "upload_date TEXT NOT NULL, " +
                            "cv_text TEXT, " +
                            "ai_score INTEGER, " +
                            "ai_feedback TEXT)"
            );

            // Create Job Descriptions table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS job_descriptions (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "description TEXT NOT NULL, " +
                            "date_added TEXT NOT NULL)"
            );

            statement.close();
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    public int insertCv(String fileName, String filePath, String cvText) {
        int newId = -1;
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO cv_storage (file_name, file_path, upload_date, cv_text) VALUES (?, ?, datetime('now'), ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            pstmt.setString(1, fileName);
            pstmt.setString(2, filePath);
            pstmt.setString(3, cvText);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                newId = rs.getInt(1);
            }

            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error inserting CV: " + e.getMessage());
        }
        return newId;
    }

    public void updateCvWithAI(int cvId, int aiScore, String aiFeedback) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "UPDATE cv_storage SET ai_score = ?, ai_feedback = ? WHERE id = ?"
            );
            pstmt.setInt(1, aiScore);
            pstmt.setString(2, aiFeedback);
            pstmt.setInt(3, cvId);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error updating CV with AI data: " + e.getMessage());
        }
    }

    public void updateCvText(int cvId, String cvText) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "UPDATE cv_storage SET cv_text = ? WHERE id = ?"
            );
            pstmt.setString(1, cvText);
            pstmt.setInt(2, cvId);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error updating CV text: " + e.getMessage());
        }
    }

    public boolean deleteCv(int cvId) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "DELETE FROM cv_storage WHERE id = ?"
            );
            pstmt.setInt(1, cvId);
            int affectedRows = pstmt.executeUpdate();
            pstmt.close();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting CV: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteAllCvs() {
        try {
            // Step 1: Delete all CV records
            PreparedStatement deleteStmt = connection.prepareStatement("DELETE FROM cv_storage");
            deleteStmt.executeUpdate();
            deleteStmt.close();

            // Step 2: Reset auto-increment ID counter for cv_storage
            PreparedStatement resetStmt = connection.prepareStatement("DELETE FROM sqlite_sequence WHERE name='cv_storage'");
            resetStmt.executeUpdate();
            resetStmt.close();

            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting all CVs: " + e.getMessage());
            return false;
        }
    }




    public List<RankedCv> getAllCvs() {
        List<RankedCv> cvs = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, file_name, ai_score, ai_feedback FROM cv_storage");

            while (rs.next()) {
                RankedCv cv = new RankedCv(
                        rs.getInt("id"),
                        rs.getString("file_name"),
                        rs.getInt("ai_score"),
                        rs.getString("ai_feedback")
                );
                cvs.add(cv);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error retrieving CVs: " + e.getMessage());
        }
        return cvs;
    }

    public RankedCv getCvById(int cvId) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "SELECT id, file_name, ai_score, ai_feedback FROM cv_storage WHERE id = ?"
            );
            pstmt.setInt(1, cvId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                RankedCv cv = new RankedCv(
                        rs.getInt("id"),
                        rs.getString("file_name"),
                        rs.getInt("ai_score"),
                        rs.getString("ai_feedback")
                );
                rs.close();
                pstmt.close();
                return cv;
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error retrieving CV by ID: " + e.getMessage());
        }
        return null;
    }

    public String getCvTextById(int cvId) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "SELECT cv_text FROM cv_storage WHERE id = ?"
            );
            pstmt.setInt(1, cvId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String cvText = rs.getString("cv_text");
                rs.close();
                pstmt.close();
                return cvText;
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error retrieving CV text by ID: " + e.getMessage());
        }
        return null;
    }

    public String getFilePathById(int cvId) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "SELECT file_path FROM cv_storage WHERE id = ?"
            );
            pstmt.setInt(1, cvId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String filePath = rs.getString("file_path");
                rs.close();
                pstmt.close();
                return filePath;
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error retrieving file path by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean isCvAlreadyUploaded(String fileName) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "SELECT COUNT(*) FROM cv_storage WHERE file_name = ?"
            );
            pstmt.setString(1, fileName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                boolean exists = rs.getInt(1) > 0;
                rs.close();
                pstmt.close();
                return exists;
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error checking if CV exists: " + e.getMessage());
        }
        return false;
    }

    public int addJobDescription(String description) {
        int newId = -1;
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO job_descriptions (description, date_added) VALUES (?, datetime('now'))",
                    Statement.RETURN_GENERATED_KEYS
            );
            pstmt.setString(1, description);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                newId = rs.getInt(1);
            }

            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error adding job description: " + e.getMessage());
        }
        return newId;
    }

    public List<JobDescription> getAllJobDescriptions() {
        List<JobDescription> descriptions = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, description, date_added FROM job_descriptions");

            while (rs.next()) {
                JobDescription job = new JobDescription(
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getString("date_added")
                );
                descriptions.add(job);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error retrieving job descriptions: " + e.getMessage());
        }
        return descriptions;
    }

    public String getJobDescriptionById(int jobId) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "SELECT description FROM job_descriptions WHERE id = ?"
            );
            pstmt.setInt(1, jobId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String description = rs.getString("description");
                rs.close();
                pstmt.close();
                return description;
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error retrieving job description by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean updateJobDescription(int jobId, String newDescription) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "UPDATE job_descriptions SET description = ?, date_added = datetime('now') WHERE id = ?"
            );
            pstmt.setString(1, newDescription);
            pstmt.setInt(2, jobId);
            int affectedRows = pstmt.executeUpdate();
            pstmt.close();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating job description: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteJobDescription(int jobId) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "DELETE FROM job_descriptions WHERE id = ?"
            );
            pstmt.setInt(1, jobId);
            int affectedRows = pstmt.executeUpdate();
            pstmt.close();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting job description: " + e.getMessage());
            return false;
        }
    }

    public List<RankedCv> getCvsWithoutText() {
        List<RankedCv> cvs = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT id, file_name FROM cv_storage WHERE cv_text IS NULL OR cv_text = ''"
            );

            while (rs.next()) {
                RankedCv cv = new RankedCv(
                        rs.getInt("id"),
                        rs.getString("file_name"),
                        0,
                        null
                );
                cvs.add(cv);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error retrieving CVs without text: " + e.getMessage());
        }
        return cvs;
    }
}