package com.answer.bdframework.context;

import java.util.Map;

/**
 * Created by L.Answer on 2018-07-30 14:29
 */
public interface BDApplicationContext {

    Map<String, Object> getBeans();

    Object getBean(Class clz);

    void close();

}