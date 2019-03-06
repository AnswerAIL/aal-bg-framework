package com.answer.bdframework.sqlcontainer.entity;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by L.Answer on 2018-07-13 16:06
 */
@XmlRootElement(name = "sqlTexts")
public class SqlTexts {
    private String name;
    private List<SqlRefer> sqlRefers;
    private List<SqlText> sqlTexts;

    @XmlAttribute(required = true, name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElements(value = {@XmlElement(name = "sql", type = SqlRefer.class)})
    public List<SqlRefer> getSqlRefers() {
        return sqlRefers;
    }

    public void setSqlRefers(List<SqlRefer> sqlRefers) {
        this.sqlRefers = sqlRefers;
    }

    @XmlElements(value = {@XmlElement(name = "sqlText", type = SqlText.class)})
    public List<SqlText> getSqlTexts() {
        return sqlTexts;
    }

    public void setSqlTexts(List<SqlText> sqlTexts) {
        this.sqlTexts = sqlTexts;
    }
}