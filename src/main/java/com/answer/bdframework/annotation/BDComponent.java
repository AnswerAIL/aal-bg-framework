package com.answer.bdframework.annotation;

import java.lang.annotation.*;

/**
 * Created by L.Answer on 2018-07-24 10:33
 */

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BDComponent {

}