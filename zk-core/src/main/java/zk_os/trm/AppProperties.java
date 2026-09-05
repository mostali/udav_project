package zk_os.trm;

import mpu.core.ARR;
import mpu.X;
import mpu.str.SPLIT;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Configuration
public class AppProperties implements InitializingBean {

//	public static String _SS_API_BASE_URI = "http://superset.sppr-dev.d.exportcenter.ru/";
//	public static String[] _SS_API_LP = {"dss", "Vw6lbtTJzi"};
//	public static int _JOB_UPDATE_DURATION_MS = 5_000;//for test

	@Override
	public void afterPropertiesSet() throws Exception {
//		_SS_API_BASE_URI = ssProps.getBaseUri();
//		_SS_API_LP = new String[]{ssProps.getLogin(), ssProps.getPass()};
//		_JOB_UPDATE_DURATION_MS = ssProps.getJob_update_duration_ms();
	}

	@Autowired
	private SecProps secProps;

	public SecProps getSecProps() {
		return secProps;
	}

	@Component
	public static class SecProps {
//		@Value("${security.apps}")
		private String apps;

		private Map<String[], Set<String>> appIps = null;

		public Map<String[], Set<String>> getAppIps() {
			if (appIps != null) {
				return appIps;
			} else if (X.empty(apps)) {
				return appIps = Collections.EMPTY_MAP;
			}
			Map<String[], Set<String>> map = new HashMap<>();
			Stream.of(SPLIT.argsBy(apps, ";")).forEach(tk ->
			{
				String[] name_code_ips = SPLIT.argsBy(tk, ":");
				String app = name_code_ips[0];
				String code = name_code_ips[1];
				String[] srcs = SPLIT.argsBy(name_code_ips[2], ",");
				String[] ips0 = SPLIT.argsBy(name_code_ips[3], ",");
				String[] key = ARR.merge(new String[]{app, code}, srcs);
				map.put(key, ARR.asHSET(ips0));
			});
			return appIps = map;
		}

	}



}
