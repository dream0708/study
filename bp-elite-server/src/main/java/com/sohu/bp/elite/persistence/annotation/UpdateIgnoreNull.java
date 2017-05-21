package com.sohu.bp.elite.persistence.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * create time: 2015年12月14日 下午8:49:42
 * @auther dexingyang
 */

@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface UpdateIgnoreNull {

}
