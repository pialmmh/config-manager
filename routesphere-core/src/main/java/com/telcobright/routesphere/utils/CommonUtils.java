package com.telcobright.routesphere.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Common utility methods shared across all RouteSphere modules
 */
public class CommonUtils {
    
    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Generate a unique ID
     */
    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Generate a unique ID with prefix
     */
    public static String generateUniqueId(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString();
    }
    
    /**
     * Get current timestamp as formatted string
     */
    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DEFAULT_DATE_FORMATTER);
    }
    
    /**
     * Format a LocalDateTime with default format
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DEFAULT_DATE_FORMATTER) : "";
    }
    
    /**
     * Check if a string is null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Check if a string is not null and not empty
     */
    public static boolean isNotEmpty(String str) {
        return !isNullOrEmpty(str);
    }
    
    /**
     * Generate a random number between min and max (inclusive)
     */
    public static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
    
    /**
     * Generate a random delay in milliseconds
     */
    public static long randomDelay(long minMs, long maxMs) {
        return ThreadLocalRandom.current().nextLong(minMs, maxMs + 1);
    }
    
    /**
     * Sleep for specified milliseconds, handling interruption
     */
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Get stack trace as string
     */
    public static String getStackTraceAsString(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.toString()).append("\n");
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        return sb.toString();
    }
    
    /**
     * Truncate string to specified length
     */
    public static String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }
    
    /**
     * Convert bytes to human readable format
     */
    public static String humanReadableByteCount(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    private CommonUtils() {
        // Private constructor to prevent instantiation
    }
}