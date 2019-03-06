package com.answer.bdframework.common;

import com.answer.bdframework.entity.LogLevel;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by L.Answer on 2018-07-30 16:36
 */
public class LoggerCommon {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static final int DEBUG_LEVEL = 1;
    public static final int INFO_LEVEL = 2;
    public static final int WARN_LEVEL = 3;
    public static final int ERROR_LEVEL = 4;

    public static void debug(StackTraceElement stackTraceElement, String log) {
        logger(LogLevel.DEBUG, stackTraceElement, log);
    }

    public static void info(StackTraceElement stackTraceElement, String log) {
        logger(LogLevel.INFO, stackTraceElement, log);
    }

    public static void warn(StackTraceElement stackTraceElement, String log) {
        logger(LogLevel.WARN, stackTraceElement, log);
    }

    public static void error(StackTraceElement stackTraceElement, String log) {
        logger(LogLevel.ERROR, stackTraceElement, log);
    }

    private static void logger(LogLevel logLevel, StackTraceElement stackTraceElement, String log) {
        System.out.println(logPrefix(logLevel) + stackTraceElement.getClassName() + ":[" + stackTraceElement.getLineNumber() + "] " + log);
    }

    private static String logPrefix(LogLevel logLevel) {
        return "["+ logLevel.getName() +"] " + SDF.format(new Date()) + " ";
    }

    private static String logPrefix(Class clz) {
        return "[INFO] " + SDF.format(new Date()) + " " + clz.getName() + " ";
    }

    private static String logPrefix(Class clz, LogLevel logLevel) {
        return "["+ logLevel.getName() +"] " + SDF.format(new Date()) + " " + clz.getName() + " ";
    }
}