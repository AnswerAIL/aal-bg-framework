package com.answer.bdframework.entity;

import static com.answer.bdframework.common.LoggerCommon.*;

/**
 * Created by L.Answer on 2018-07-30 16:47
 */
public enum LogLevel {
    DEBUG(DEBUG_LEVEL, "DEBUG"),
    INFO(INFO_LEVEL, "INFO"),
    WARN(WARN_LEVEL, "WARN"),
    ERROR(ERROR_LEVEL, "ERROR");

    private  int level;
    private String name;

    LogLevel(int level, String name) {
        this.level = level;
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

}