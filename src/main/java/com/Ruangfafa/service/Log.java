package com.Ruangfafa.service;

import com.Ruangfafa.common.Constants.LogJava;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {

    public static void log(String message, Exception error, String source, boolean doPrint) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(LogJava.TIME_FORMAT));
        String logMessage;

        if (error != null) {
            logMessage = String.format(LogJava.LOG_FORMAT_WITH_ERROR, timestamp, source, message, error.toString());
        } else {
            logMessage = String.format(LogJava.LOG_FORMAT, timestamp, source, message);
        }

        if (doPrint) {
            System.out.println(logMessage);
        }
    }
}
