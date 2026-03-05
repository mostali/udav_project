package botcore;

import mpc.str.condition.StringConditionType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RouteAno {
	String key() default "";

	RDM mode() default RDM.ON;

	StringConditionType eq() default StringConditionType.EQ;

	int z() default 0;

	int rx_grp() default 1;

	String alias() default "";

}
