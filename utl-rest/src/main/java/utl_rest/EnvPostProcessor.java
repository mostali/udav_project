package utl_rest;

import mpc.env.Env;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import mpc.log.L;
import utl_spring.USpring;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Path;

public class EnvPostProcessor implements EnvironmentPostProcessor {
	public static final String APP_PROPS_RL = "application.rl.properties";
	public static final String DARG_APP_PROPS_EXTERNAL = "application.external.properties";

	public Path getRootPathApp() {
		return Env.RPA;
	}

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		if (L.isInfoEnabled()) {
			L.info("INFO: App dir:" + getRootPathApp());
		}

		boolean rslt;
//		//пробуем подгрузить RPA
//		boolean isLoadedDefaultConfig = loadConfigFromFile(environment, APP_PROPS_RPA, getAppStoreEnvPath().resolve(APP_PROPS_RPA));

		//пробуем подгрузить from RL
		rslt = loadConfigFromFile(environment, APP_PROPS_RL, Env.RUN_LOCATION.resolve(APP_PROPS_RL));

//		rslt = loadConfigFromFile(environment, Env.FILE_APPLICATION_PROPERTIES, Env.getAppDataDir(NT.DEF.name().toLowerCase()));

		//подгружаем 'external.config' из RUN_ARGS на ПРОМЕ
		//подгружаем тут, чтобы этот конфиг был мастером
		rslt = loadConfigFromSystemProperty(environment, DARG_APP_PROPS_EXTERNAL);

	}

	private static boolean loadConfigFromFile(ConfigurableEnvironment environment, String nameGlobalConfig, Path fileWithGlobalProperties) {
		boolean rslt = false;
		if (fileWithGlobalProperties != null && fileWithGlobalProperties.toFile().isFile()) {
			USpring.postProcessEnvironment_AddProperties(environment, nameGlobalConfig, fileWithGlobalProperties.toFile(), USpring.AppPropsPosition.FIRST);
			rslt = true;
		}
		L.info("INFO: Config '{}' from '{}' is loading -> {}", nameGlobalConfig, fileWithGlobalProperties, rslt);
		return rslt;
	}

	private static boolean loadConfigFromSystemProperty(ConfigurableEnvironment environment, String nameExternalConfig) {
		final String externalProperty = System.getProperty(nameExternalConfig);
		File propertyFile = StringUtils.isNotBlank(externalProperty) ? new File(externalProperty) : null;
		boolean rslt = false;
		if (propertyFile != null && propertyFile.exists() && propertyFile.isFile()) {
			USpring.postProcessEnvironment_AddProperties(environment, nameExternalConfig, propertyFile, USpring.AppPropsPosition.FIRST);
			rslt = true;
		}
		L.info("INFO: External config '-D{}' is loading -> {}", DARG_APP_PROPS_EXTERNAL, rslt);
		return rslt;
	}

}