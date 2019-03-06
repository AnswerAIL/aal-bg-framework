package com.answer.bdframework.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by L.Answer on 2018-07-30 14:47
 */
@XmlRootElement(name = "settings")
public class STS {
    private List<ST> sts;

    @XmlElements(value = {@XmlElement(name = "setting", type = ST.class)})
    public List<ST> getSts() {
        return sts;
    }

    public void setSts(List<ST> sts) {
        this.sts = sts;
    }
}