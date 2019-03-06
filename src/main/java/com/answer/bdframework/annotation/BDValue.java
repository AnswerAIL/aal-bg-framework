package com.answer.bdframework.annotation;

import java.lang.annotation.*;

/**
 * Created by L.Answer on 2018-07-24 10:14
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BDValue {

    String name() default "";

    /** apply to type is String'property {@link com.answer.bdframework.entity.BDType#String} set up default value */
    String defaultVal() default "";

}