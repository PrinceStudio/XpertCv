package com.xpertcv;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static final String LOG_FILE = "xpertcv.log";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static boolean initialized = false;

    public static void initialize() {
        if (!initialized) {
            try {
                // Create or clear the log file
                FileWriter fileWriter = new FileWriter(LOG_FILE);
                fileWriter.write("XpertCV Log - Started on " + DATE_FORMAT.format(new Date()) + "\n");
                fileWriter.close();
                initialized = true;
            } catch (IOException e) {
                System.err.println("Error initializing log file: " + e.getMessage());
            }
        }
    }

    public static void log(String message) {
        log(LogLevel.INFO, message);
    }

    public static void log(LogLevel level, String message) {
        if (!initialized) {
            initialize();
        }

        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter out = new PrintWriter(fw)) {

            String logEntry = String.format("[%s] [%s] %s",
                    DATE_FORMAT.format(new Date()),
                    level.toString(),
                    message);
            out.println(logEntry);

            // Also print to console for ERROR level
            if (level == LogLevel.ERROR) {
                System.err.println(logEntry);
            }
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    public static void logUserAction(String username, String action) {
        log(LogLevel.USER, username + ": " + action);
    }

    public enum LogLevel {
        INFO,
        WARNING,
        ERROR,
        USER
    }
}
