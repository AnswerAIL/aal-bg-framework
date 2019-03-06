package com.answer.bdframework.annotation;

import com.answer.bdframework.algorithm.Algorithum3DES;
import com.answer.bdframework.algorithm.AlgorithmAbs;
import com.answer.bdframework.entity.LogLevel;

import java.lang.annotation.*;

/**
 * Created by L.Answer on 2018-07-30 10:56
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BigDataBootApplication {

    String[] scanSQLXmlPath();

    /**
     * jarFile:     jarPath#class package path
     *      for example: /data/deploy/risk-control-rask.jar#com.answer.task
     * classFile:   class package path
     *      for example: com.answer.task
     * */
    String[] scanJavaPath();

    /** default file path: resources/bigdata-settings.xml  */
    String settingPath() default "bigdata-settings.xml";

    LogLevel logLevel() default LogLevel.INFO;

    /** apply to type is <code> java.lang.String </code>{@link com.answer.bdframework.entity.BDType#String} parameter's encrypt */
    Class<? extends AlgorithmAbs> encry() default Algorithum3DES.class;

    boolean jarFile() default false;
}