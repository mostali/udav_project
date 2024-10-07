package zk_os;

import mpc.env.Env;
import mpc.time.EDayTime;
import mpu.X;
import mpu.str.SPLIT;
import mpu.str.Sb;
import mpc.str.condition.LogGetterDate;
import mpt.TRM;
import mpu.str.UST;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import zk_old_core.sd.core.SdMan;
import zk_page.behaviours.BgImg;

import java.util.concurrent.atomic.AtomicReference;

@Service

public class AppZos {

	public static final Logger L = LoggerFactory.getLogger(AppZos.class);

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


	public static void restart() {
		org.springframework.boot.devtools.restart.Restarter.getInstance().restart();
	}

	public static Sb buildReport(boolean env, boolean sd, boolean trm) {
		Sb appInfo = new Sb();
		if (env) {
			appInfo.NL("Env");
			appInfo.TABNL(1, "App [" + Env.getAppName() + "] ready..");
			appInfo.TABNL(1, "App RPA:" + Env.RPA);
			appInfo.TABNL(1, "App MasterRepo:" + AppZosCore.getMasterRepo());
			appInfo.TABNL(1, "App MasterPage:" + AppZosCore.getMasterPage());
		}
		if (sd) {
//			appInfo.NL("Sd");
			Sb sdRp = SdMan.buildReport(0);
			appInfo.append(sdRp);
		}
		if (trm) {
//			appInfo.NL("Sd");
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
		String valueOrDefault = AppZosProps.APR_LOG_LINE_MAPPING.getValueOrDefault();
		return SPLIT.allByComma(valueOrDefault).stream().map(UST::INT).mapToInt(i -> i).toArray();
	}

	@NotNull
	public static String getBgImageViaNigthMode() {
		String bgUrl;

		BgImg val = AppZosProps.APD_ZOS_NIGHTTHEME_DISABLE.getValueOrDefault();
		if (val == BgImg.AUTO) {
			val = EDayTime.valueOf() == EDayTime.NIGHT ? BgImg.BG_DARK_LIGHT_JPG : BgImg.BG_SEC_PNG;
		}
		bgUrl = X.f("url(_img/%s)", val.toFileName());

		return bgUrl;
	}

	@Override
	public String toString() {
		return "BaeApp{" + "env=" + ENV + '}';
	}

}
