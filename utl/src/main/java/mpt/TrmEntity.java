package mpt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TrmEntity {
	String[] value();// default "";
	//	SimpleStringConditionEq eq() default SimpleStringConditionEq.EQ;
	//	int z() default 0;
	//	String alias() default "";
}
