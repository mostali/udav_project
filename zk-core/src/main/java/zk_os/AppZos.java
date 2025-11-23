package zk_os;

import mpc.env.APP;
import mpc.env.Env;
import mpu.str.SPLIT;
import mpu.str.Sb;
import mpc.str.condition.LogGetterDate;
import mpt.TRM;
import mpu.str.UST;
import nett.appb.TgApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service

public class AppZos {

	public static final Logger L = LoggerFactory.getLogger(AppZos.class);

	public static volatile TgApp TgApp;

	@Autowired
	public Environment ENV;

	private final static AtomicReference<AppZos> INSTANCE = new AtomicReference<AppZos>();

	private AppZos() {
		final AppZos previous = INSTANCE.getAndSet(this);
		if (previous != null) {
			L.warn("AiApp singleton " + this + " created after " + previous);
		}
	}

	public static AppZos get() {
		return INSTANCE.get();
	}

	public static boolean isDebugEnable() {
//		return APP.IS_DEBUG_ENABLE;
		return AppZosProps.APD_IS_DEBUG_ENABLE.getValueOrDefault(false);
	}

	public static boolean isPromEnable() {
		return AppZosProps.APD_IS_PROM_ENABLE.getValueOrDefault(false);
	}

	public static boolean isDevEnable() {
		return AppZosProps.APD_IS_DEV_ENABLE.getValueOrDefault(false);
	}


	public static void restart() {
		org.springframework.boot.devtools.restart.Restarter.getInstance().restart();
	}

	public static Sb buildReport(boolean env, boolean trm) {
		Sb appInfo = new Sb();
		if (env) {
			appInfo.NL("Env");
			appInfo.TABNL(1, "App [" + Env.getAppName() + "] ready..");
			appInfo.TABNL(1, "App RPA:" + Env.RPA);
		}
		if (trm) {
			Sb sdRp = TRM.buildReport(0);
			appInfo.append(sdRp);
		}
		return appInfo;
	}

	public static LogGetterDate getLogGetterDate() {
		String valueOrDefault = AppZosProps.APR_LOG_DATE_FORMAT.getValueOrDefault();
		return LogGetterDate.buildByFormat(valueOrDefault);
	}

	public static int[] getLogLineMapping() {
		String valueOrDefault = AppZosProps.APD_LOG_LINE_MAPPING.getValueOrDefault();
		return SPLIT.allByComma(valueOrDefault).stream().map(UST::INT).mapToInt(i -> i).toArray();
	}

	@Override
	public String toString() {
		return "BeaApp{" + "env=" + ENV + '}';
	}

}
