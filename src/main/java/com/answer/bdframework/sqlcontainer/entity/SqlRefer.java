package com.answer.bdframework.sqlcontainer.entity;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * Created by L.Answer on 2018-07-14 11:43
 */
@XmlRootElement(name = "sql")
public class SqlRefer {
    private String id;
    private String sqlRefer;

    @XmlAttribute(required = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlValue
    public String getSqlRefer() {
        return sqlRefer;
    }

    public void setSqlRefer(String sqlRefer) {
        this.sqlRefer = sqlRefer;
    }
}