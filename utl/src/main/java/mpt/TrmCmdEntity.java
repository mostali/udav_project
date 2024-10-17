package mpt;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TrmCmdEntity {
	String[] value() default "";
	//	SimpleStringConditionEq eq() default SimpleStringConditionEq.EQ;
	//	int z() default 0;
	//	String alias() default "";
}
