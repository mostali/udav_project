package zk_pages.zznsi_pages.jira_tasks;

import mp.utl_odb.tree.ctxdb.Ctx10Db;
import mpc.str.sym.SYMJ;
import mpc.url.UUrl;
import mpu.X;
import zk_form.notify.ZKI;
import zk_os.AppCoreZos;

import java.util.Map;

public class JtApp {

	public static final String TASK_ICON = SYMJ.GLOB_GRID;

	public static void show(String msg, ZKI.Level... level) {
		ZKI.infoAfterPointer(msg, level);
	}

	public static void log(String msg, Object... args) {
		ZKI.log(msg, args);
	}

	public static String toLinkIssue(String s, String key) {
		return UUrl.normUrl(s, "browse", key);
	}

	public static Ctx10Db RECOVERY_STATE_TREE() {
		return AppCoreZos.APP_CORE.tree10("env/jtapp", "recovery-state-issues");
	}

	public static void showInfo(String msg, Object... args) {
		show(X.f_(msg, args), ZKI.Level.INFO);
	}

	public static void showWarn(String msg, Object... args) {
		show(X.f_(msg, args), ZKI.Level.WARN);
	}
}
