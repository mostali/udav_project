package utl_rest;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Утилитарный класс для построения шаблонов Velocity
 * <p>
 * Документация
 * https://click.apache.org/docs/velocity/developer-guide.html
 * <p>
 * Загрузка из jar
 * https://docs.atlassian.com/DAC/javadoc/velocity/1.4-atlassian-9/reference/org/apache/velocity/runtime/resource/loader/JarResourceLoader.html
 * <p>
 * Snipets
 * https://packagecontrol.io/packages/Java%20Velocity
 *
 */
public class UVelocity {
	/**
	 * Custom Velocity Engine Properties
	 */
	public static String toFileStringPatternCustom(String fileTemplate, Properties engineProperties, VelocityContext context) {
		VelocityEngine vEngine = new VelocityEngine();
		vEngine.init(engineProperties);
		return toFileStringPattern(fileTemplate, vEngine, context);
	}

	/**
	 * Template as Absolute File
	 */
	public static String toAbsoluteFileStringPattern(String rootDir, String fileTemplate, VelocityContext context) {
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
		ve.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, rootDir);
		return toFileStringPattern(fileTemplate, ve, context);
	}

	/**
	 * Package Template from Jar with class context
	 * Использование: template = UVelocity.toJarFileStringPattern("./dir/app.jar", Somesrc.class, STRATEGY_USE_VELOCITY_TEMPLATE_FILENAME, createVelocityContext(this.params));
	 */
	public static String toJarFileStringPattern(String jarPath, Class clazz, String packageFileTemplate, VelocityContext context) {
		String relativePath = clazz.getPackage().getName().replace(".", "/") + "/";
		return toJarFileStringPattern(jarPath, relativePath + packageFileTemplate, context);
	}

	/**
	 * Package Template from Jar
	 */
	public static String toJarFileStringPattern(String jarPath, String packageFileTemplate, VelocityContext context) {
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "jar");
		ve.setProperty("jar.resource.loader.class", "org.apache.velocity.runtime.resource.loader.JarResourceLoader");
		ve.setProperty("jar.resource.loader.path", "jar:file:" + jarPath);
		ve.init();
		return toFileStringPattern(packageFileTemplate, ve, context);
	}

	/**
	 * Package Template
	 * Для стандартного случая упаковки проекта.
	 */
	public static String toPackageFileStringPattern(String packageFileTemplate, VelocityContext context) {
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		ve.init();
		return toFileStringPattern(packageFileTemplate, ve, context);
	}

	//	/**
	//	 * https://stackoverflow.com/questions/21730335/how-to-include-a-file-from-a-velocity-template-using-classpathresourceloader
	//	 */
	//	@Deprecated
	//	public static String toPackageFileStringPattern_WNW(String packageFileTemplate, VelocityContext context) {
	//		VelocityEngine ve = new VelocityEngine();
	//		ve.setProperty(RuntimeConstants.EVENTHANDLER_INCLUDE, IncludeRelativePath.class.getName());
	//		return toFileStringPattern(packageFileTemplate, ve, context);
	//	}


	private static String toFileStringPattern(String fileTemplate, VelocityEngine vEngine, VelocityContext context) {
		Template template = vEngine.getTemplate(fileTemplate, Charset.defaultCharset().toString());
		StringWriter swOut = new StringWriter();
		template.merge(context, swOut);
		String result = swOut.toString();
		return result;
	}

}
