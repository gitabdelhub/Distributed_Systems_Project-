package com.monitor.shared.utils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public enum Level { INFO, WARNING, ERROR }

    public static void log(Level level, String source, String message) {
        String time = LocalDateTime.now().format(FMT);
        System.out.printf("[%s] [%s] [%s] %s%n", time, level, source, message);
    }

    public static void info(String source, String msg)    { log(Level.INFO,    source, msg); }
    public static void warning(String source, String msg) { log(Level.WARNING, source, msg); }
    public static void error(String source, String msg)   { log(Level.ERROR,   source, msg); }
}