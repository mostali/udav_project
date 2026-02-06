package zk_os.db;

import mpc.env.boot.AppBoot;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabaseConfiguration implements BeanFactoryAware {

	@Autowired
	WebUsrService webUsrService;

	@Bean
	public CommandLineRunner initDatabase() {//EmployeeRepository repository
		String first = webUsrService.initDB();
		return args -> {
			//AppBoot.L.info(first);
			System.out.println("InitWebUsrDatabase:" + first);
		};
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		webUsrService = (WebUsrService) beanFactory.getBean("userDetailsService");
	}

}
