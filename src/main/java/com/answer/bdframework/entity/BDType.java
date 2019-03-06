package com.answer.bdframework.entity;

/**
 * Created by L.Answer on 2018-07-30 15:34
 */
public enum BDType {
    String("java.lang.String"),
    List("java.util.List"),
    Map("java.util.Map"),
    Integer("java.lang.Integer"),
    Long("java.lang.Long"),
    Float("java.lang.Float"),
    Double("java.lang.Double");

    private String type;

    BDType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}