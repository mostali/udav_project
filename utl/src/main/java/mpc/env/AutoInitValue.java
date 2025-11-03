package mpc.env;

import mpe.core.U;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * AutoValueInit
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface AutoInitValue {
	String prop();

//	Class type() default String.class;

	String def() default U.__NULL__;

	String bash_call() default U.__NULL__;
}
