package utl_spring;

import mpc.fs.UF;
import mpc.fs.fd.RES;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.rfl.RFL;
import org.springframework.aop.framework.Advised;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Properties;


//https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#server.error.include-binding-errors
//https://tedblob.com/spring-register-bean-programmatically/#:~:text=You%20can%20implement%20the%20ApplicationContextInitializer,and%20register%20the%20bean%20programmatically.
public class USpring {

	public static Class getGenericProxyClass(Object proxy, int i, Class... defRq) {
		Class<?> proxiedInterface = ((Advised) proxy).getProxiedInterfaces()[0];
		Type[] genericInterfaces = proxiedInterface.getGenericInterfaces();
		for (Type genericInterface : genericInterfaces) {
			if (genericInterface instanceof ParameterizedType) {
				Type[] genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
				for (Type genericType : genericTypes) {
					if (++i == i) {
						return RFL.clazz(genericType.getTypeName(), defRq);
					}
				}
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Generic not found from proxy class '%s'", proxiedInterface);
	}

	public static Class getProxyClass(Object proxy) {
		return getProxyClass(proxy, 0);
	}

	public static Class getProxyClass(Object proxy, int i) {
		return ((Advised) proxy).getProxiedInterfaces()[i];
	}

	public static String readRsrcContent(Class from, String pathRsrc) {

//		if (true) {
		return RES.of(from, UF.normUnixRootDir(pathRsrc)).cat();

//		} else {
//			try {
//				return RES.of(from, pathRsrc).cat();
//			} catch (Exception e) {
//				try {
//					File file = ResourceUtils.getFile("classpath:" + pathRsrc);
//					return new String(Files.readAllBytes(file.toPath()));
//				} catch (Exception e2) {
//					//wth//
//					return RES.of(from, "/" + pathRsrc).cat();
//				}
//			}
//		}


	}

	public class AppPropsPosition {
		public static final int FIRST = 1;
		public static final int LAST = 2;
		public static final int BEFORE = 3;
		public static final int AFTER = 4;
	}

	public static void postProcessEnvironment_AddProperties(ConfigurableEnvironment environment, String name, File fileWithProperties, int simpleConstant, String... relativePropertySourceName) {
		FileSystemResource resource = new FileSystemResource(fileWithProperties);
		try {
			Properties properties = PropertiesLoaderUtils.loadProperties(resource);
			postProcessEnvironment_AddProperties(environment, name, properties, simpleConstant);
		} catch (IOException e) {
			throw new RuntimeException("load fileWithProperties", e);
		}
	}

	/**
	 * public class AiEnvironmentPostProcessor implements EnvironmentPostProcessor{...}
	 */
	public static void postProcessEnvironment_AddProperties(ConfigurableEnvironment environment, String name, Properties properties, int position, String... relativePropertySourceName) {
		switch (position) {
			case AppPropsPosition.FIRST:
				environment.getPropertySources().addFirst(new PropertiesPropertySource(name, properties));
				break;
			case AppPropsPosition.LAST:
				environment.getPropertySources().addLast(new PropertiesPropertySource(name, properties));
				break;
			case AppPropsPosition.AFTER:
				environment.getPropertySources().addAfter(ARRi.first(relativePropertySourceName), new PropertiesPropertySource(name, properties));
				break;
			case AppPropsPosition.BEFORE:
				environment.getPropertySources().addBefore(ARRi.first(relativePropertySourceName), new PropertiesPropertySource(name, properties));
				break;
			default:
				throw new WhatIsTypeException("Wrong AppPropsPosition '%s'", position);

		}
	}

}
