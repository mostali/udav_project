package utl_spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;


@Service
public class BeanContext implements BeanFactoryAware {
	private static final Logger log = LoggerFactory.getLogger(BeanContext.class);

	public static BeanFactory CONTEXT;

	public BeanContext() {
	}

	public static Environment getEnv() throws BeansException {
		return getBean(Environment.class);
	}

	public static Object getBean(String s) throws BeansException {
		return CONTEXT.getBean(s);
	}

	public static <T> T getBean(String s, Class<T> tClass) throws BeansException {
		return CONTEXT.getBean(s, tClass);
	}


	public static <T> T getBean(Class<T> tClass) throws BeansException {
		return CONTEXT.getBean(tClass);
	}

	public static Object getBean(String s, Object... objects) throws BeansException {
		return CONTEXT.getBean(s, objects);
	}

	public static boolean containsBean(String s) {
		return CONTEXT.containsBean(s);
	}

	@Override
	public void setBeanFactory(BeanFactory applicationContext) throws BeansException {
		if (CONTEXT == null) {
			log.warn("BeanFactory CONTEXT is not null. Double Spring context creation?");
		}
		CONTEXT = applicationContext;
	}
}