package com.answer.bdframework.entity;

/**
 * Created by L.Answer on 2018-07-30 15:34
 */
public enum BDType {
    /** String reflect to java.lang.String */
    String("java.lang.String"),
    /** List reflect to java.util.List */
    List("java.util.List"),
    /** Map reflect to */
    Map("java.util.Map"),
    /** Integer reflect to java.util.Map */
    Integer("java.lang.Integer"),
    /** Long reflect to java.lang.Long */
    Long("java.lang.Long"),
    /** Float reflect to java.lang.Float */
    Float("java.lang.Float"),
    /** Double reflect to java.lang.Double */
    Double("java.lang.Double");

    private String type;

    BDType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}