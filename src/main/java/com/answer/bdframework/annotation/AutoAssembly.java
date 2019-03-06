package com.answer.bdframework.annotation;

import java.lang.annotation.*;

/**
 * Created by L.Answer on 2018-08-02 11:09
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoAssembly {
}