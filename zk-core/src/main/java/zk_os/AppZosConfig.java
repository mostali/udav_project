package zk_os;

import lombok.SneakyThrows;
import mpc.env.APP;
import mpc.env.AutoInitClassProperty;
import mpe.core.U;
import mpc.env.AP;
import mpc.env.AutoInitValue;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import utl_web.UWeb;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class AppZosConfig {

	public static final String APK_BEA_TRM_ENABLE = "bea.trm.enable";
	public static final String APK_SPACE_PATH = "space.path";
	public static final String APK_SD3_INDEX = "sd3.index";

	@AutoInitValue(prop = APK_BEA_TRM_ENABLE,  def = "false")
	public static Boolean TRM_ENABLE = null;//AP.getAs(APK_BEA_TRM_ENABLE, Boolean.class, false);

	@AutoInitValue(prop = APP.APK_SUPER_KEY, def = U.__NULL__)
	public static String SUPER_KEY = null;//AP.getAs(APK_SUPER_KEY, String.class, null);

//	@AutoInitValue(prop = APP.APK_IS_DEBUG,  def = "false")
//	public static Boolean IS_DEBUG = null;

	@AutoInitValue(prop = APK_SD3_INDEX,  def = "2")
	public static Integer SD3_INDEX = null;


	@SneakyThrows
	public static void initProperty(String property, String value) {
		AutoInitClassProperty.setValueObject(AppZosConfig.class, property, value);
	}

	public static int getCookieAuthTimeout() {
		return AP.getAs("web.session.timeout.bycookie.sec", Integer.class, (int) TimeUnit.DAYS.toSeconds(30));
	}

	public static @NotNull Charset getCharset() {
		return Charset.forName(AppZosProps.CHARSET.getValueOrDefault());
	}

	public static String getSd3() {
		Execution execution = Executions.getCurrent();
		HttpServletRequest servletRequest = (HttpServletRequest) execution.getNativeRequest();
		return getSd3(servletRequest);
	}

	public static String getSd3(HttpServletRequest servletRequest) {
		return UWeb.getSubDomian_part3(servletRequest, SD3_INDEX, "");
	}
}
