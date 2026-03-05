package zk_page.core;

import mpc.str.condition.StringConditionType;
import zk_os.sec.ROLE;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface  PageRoute {
	String sd3() default "";

	String pagename() default "";

	ROLE role() default ROLE.ANONIM;

	StringConditionType eqt() default StringConditionType.EQ;

	StringConditionType sd3_eqt() default StringConditionType.EQ;

}
