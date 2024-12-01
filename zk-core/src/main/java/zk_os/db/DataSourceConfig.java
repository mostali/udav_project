package zk_os.db;

import mpc.env.Env;
import mpc.env.boot.AppBoot;
import mpc.fs.UF;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import utl_spring.AppContext;
import zk_os.db.WebUsrService;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

	private static final String APP_SQLITE = "app.sqlite";

	@Bean
	public DataSource dataSource() {
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.sqlite.JDBC");
		dataSource.setUrl("jdbc:sqlite:" + getAppDbFile());
		dataSource.setUsername("sa");
		dataSource.setPassword("sa");
		return dataSource;
	}

	public static String getAppDbFile() {
		return Env.RPA.resolve(APP_SQLITE).toString();
	}

}
