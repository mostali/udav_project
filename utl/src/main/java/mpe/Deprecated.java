package mpe;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/**
 * Аннотация означает, что функционал не должен использоваться (устарел, вскоре будет удален и т.д.)
 *
 * @author sergey2020 16.07.2021
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {CONSTRUCTOR, FIELD, LOCAL_VARIABLE, METHOD, PACKAGE, PARAMETER, TYPE})
public @interface Deprecated {
	/**
	 * С какой версии/даты оставлен для совместимости и не должен использоваться в новом функционале
	 */
	String since() default "";

	/**
	 * С какой версии/даты будет удален
	 */
	String removalSince() default "";

	/**
	 * Что использовать вместо этого функционала
	 */
	String use() default "";

	/**
	 * Произвольный комментарий
	 */
	String comment() default "";
}
