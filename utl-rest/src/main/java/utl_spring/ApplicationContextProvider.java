//package utl_spring;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.BeansException;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.util.Assert;
//
////@Component
//public class ApplicationContextProvider implements ApplicationContextAware {
//	private static final Logger log = LoggerFactory.getLogger(ApplicationContextProvider.class);
//	private static ApplicationContext applicationContext;
//
//	private ApplicationContextProvider() {
//	}
//
//	public static ApplicationContext getApplicationContext() {
//		Assert.state(applicationContext != null, "Application context holder is not initialized");
//		return applicationContext;
//	}
//
//	@Override
//	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//		if (ApplicationContextProvider.applicationContext != null) {
//			log.warn("Application context holder was already initialized at least one time");
//		} else {
//			while (applicationContext.getParent() != null) {
//				applicationContext = applicationContext.getParent();
//			}
//
//			ApplicationContextProvider.applicationContext = applicationContext;
//		}
//	}
//
//	public  <T> T getBean(String name) {
//		Object bean = applicationContext.getBean(name);
//		if (bean == null) {
////			log.error("Bean with name {} not found", name);
//			return null;
//		} else {
//			try {
//				return (T) bean;
//			} catch (ClassCastException var4) {
////				log.error("Error on bean casting ", var4);
//				return null;
//			}
//		}
//	}
//}