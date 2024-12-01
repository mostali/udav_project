package zk_form.notify;

import mpc.str.sym.SYMJ;
import mpu.X;
import mpc.fs.ext.EXT;
import mpc.json.UGson;
import mpu.str.JOIN;
import mpu.pare.Pare;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.XulElement;
import zk_com.base.Tbxm;
import zk_page.ZKME;

import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public class ZKI {

	public static void alert(CharSequence message, Object... args) {
		ZKI_Log.alert(message, args);
	}

	public static void alert(Exception ex) {
		ZKI_Log.alert(ex);
	}

	public static void alert(Exception ex, CharSequence message, Object... args) {
		ZKI_Log.alert(ex, message, args);
	}

	public static void infoSingleLine(CharSequence message, Object... args) {
		Clients.showNotification(X.f(message, args));
	}

	public static void errorSingleLine(CharSequence message, Object... args) {
		Clients.showNotification(X.f(message, args), "error", null, "center", 5000);
	}

	public static void infoMultiLine(CharSequence msg, Object... args) {
		ZKI_Toast.info(msg, args);
	}

//	public static Pare<Window, Tbxm> infoEditorBw(Path file, boolean... json) {
//		return ZKME.openEditor(file, ARG.isDefEqTrue(json), true);
//	}

	public static Pare<Window, Tbxm> infoEditorBw(Path file) {
		return infoEditorBw(file, UGson.isGsonContent(file) ? EXT.JSON : EXT.TXT);
	}

	public static Pare<Window, Tbxm> infoEditorBw(Path file, EXT type) {
		return ZKME.openEditor(file, EXT.JSON == type, true);
	}

	public static Pare<Window, Tbxm> infoEditorBw(Collection<String> lines) {
		return infoEditorBw(JOIN.allByNL(lines));
	}

	public static XulElement infoEditorJsonBw(Collection jsons) {
		return infoEditorJsonBw(JOIN.allByNL(jsons), true);
	}

	public static XulElement infoEditorJsonBw(Object singleJson, boolean linent) {
		Supplier<String> stringSupplier = () -> linent ? UGson.toStringPrettyLinent(singleJson.toString()) : UGson.toStringPretty(singleJson.toString());
//		Function<String, Boolean> saveCallback = s -> true;
		Function<String, Boolean> saveCallback = null;
		return ZKME.openEditorWithContent(stringSupplier, saveCallback, null, true);
	}

	public static Pare<Window, Tbxm> errorEditorBw(CharSequence msg, Object... args) {
		return ZKME.openEditorWithContent(SYMJ.WARN + " Error", X.f(msg, args), true);
	}

	public static Pare<Window, Tbxm> infoEditorBw(CharSequence msg, Object... args) {
		return ZKME.openEditorWithContent(SYMJ.INFO_SIMPLE + " Info", X.f(msg, args), true);
	}

	public static Pare<Window, Tbxm> infoEditorBw(Object title_cap_com, CharSequence msg, Object... args) {
		return ZKME.openEditorWithContent(title_cap_com, X.f(msg, args), true);
	}

	public static void infoBottomRightFast(CharSequence msg, Object... args) {
		ZKI_Toast.infoBottomRightFast(msg, args);
	}
}
