package utl_spring;

import mpc.rfl.R;
import mpu.str.Sb;
import mpc.str.condition.StringConditionPattern;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class AppContext implements ApplicationContextAware {

	public static ApplicationContext CONTEXT;

	/**
	 * Returns the Spring managed bean instance of the given class type if it exists.
	 * Returns null otherwise.
	 */
	public static <T extends Object> T getBean(Class<T> beanClass) {
		return CONTEXT.getBean(beanClass);
	}

	public static <T extends Object> T getBean(String bean) {
		return (T) CONTEXT.getBean(bean);
	}

	public static Sb buildReport(int tabLevel, StringConditionPattern conditionBeanName, StringConditionPattern conditionClass) {
		Sb sb = new Sb();
		String[] beans = AppContext.CONTEXT.getBeanDefinitionNames();
		Arrays.sort(beans);
		sb.TAB(tabLevel, "Beans:").NL();
		for (String bean : beans) {
			String cn = R.cn((Object) AppContext.getBean(bean), "NULL");
			if (conditionBeanName != null || conditionClass != null) {
				if (conditionBeanName != null && !conditionBeanName.matches(cn)) {
					continue;
				}
				if (conditionClass != null && !conditionClass.matches(cn)) {
					continue;
				}
			}
			sb.TAB(tabLevel + 1, bean + "=" + cn).NL();
		}
		sb.deleteLastChar();
		return sb;
	}

	@Override
	@Autowired
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		// store ApplicationContext reference to access required beans later on
		setContext(context);
	}

	/**
	 * Private method context setting (better practice for setting a static field in a bean
	 * instance - see comments of this article for more info).
	 */
	private static synchronized void setContext(ApplicationContext context) {
		AppContext.CONTEXT = context;
	}
}