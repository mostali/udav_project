package zk_page;

import mpc.str.condition.LogGetterDate;
import mpc.str.sym.SYMJ;
import mpu.pare.Pare;
import mpu.str.SPLIT;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.XulElement;
import zk_com.base.Bt;
import zk_com.base.Tbx;
import zk_com.base.Tbxm;
import zk_com.base_ctr.Div0;
import zk_com.sun_editor.SeTbxm;
import zk_form.ext.LogHtmlCom;
import zk_os.AppZosProps;

import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.Supplier;

public class ZKME {

	public static Pare<Window, Tbxm> jsonSaveable(Path pathFile, boolean... darkTheme) {
		return anyWithBtSave(pathFile, true, darkTheme);
	}

	public static XulElement jsonPrettyWithBtSave(Supplier<String> supplierContent, Function<String, Boolean> saveCallback) {
		return jsonPrettyWithBtSave(supplierContent, saveCallback, true);
	}

	public static Window jsonPrettyWithBtSave(Path fileJson, Function<String, Boolean> saveCallback) {
		Tbxm tbxm = (Tbxm) new Tbxm(fileJson, Tbx.DIMS.WH100).prettyjson().saveOnShortCut();
		return ZKM.showModal(new Bt(SYMJ.SAVE).onCLICK(tbxm.getEventSave(saveCallback)), tbxm);
	}

	public static XulElement jsonPrettyWithBtSave(Supplier<String> supplierContent, Function<String, Boolean> saveCallback, boolean json) {
		return anyWithBtSave(supplierContent, saveCallback, null, json);
	}

	public static XulElement anyWithBtSave(Supplier<String> supplierContent, Function<String, Boolean> saveCallback, Component addiComCap, boolean jsonPretty) {
		Tbxm tbxm = (Tbxm) new Tbxm(supplierContent.get(), Tbx.DIMS.WH100).prettyjson(jsonPretty);
//		if (saveCallback != null) {
//			tbxm.saveOnShortCut();
//		}
		Component top = null;
		if (saveCallback != null) {
			top = new Bt(SYMJ.SAVE).onCLICK(tbxm.getEventSave(saveCallback));
		}
		if (addiComCap != null) {
			top = top == null ? addiComCap : Div0.of(top, addiComCap);
		}
		ZKM.showModal(top, tbxm);
		return tbxm;
	}

	public static Pare<Window, Tbxm> jsonSaveable(Object title, Path content, boolean... darkTheme) {
		Tbxm modalCom = (Tbxm) new Tbxm(content, Tbx.DIMS.WH100).saveOnShortCut().prettyjson();
		return Pare.of(ZKM.showModal(title, modalCom, ZKC.getFirstWindow(), darkTheme), modalCom);
	}

	public static Pare<Window, Tbxm> jsonSaveable(Object title, String content, boolean... darkTheme) {
		Tbxm modalCom = (Tbxm) new Tbxm(content, Tbx.DIMS.WH100).saveOnShortCut().prettyjson();
		return Pare.of(ZKM.showModal(title, modalCom, ZKC.getFirstWindow(), darkTheme), modalCom);
	}

	public static Pare<Window, Tbxm> textReadonly(Object title, String content, boolean... darkTheme) {
		return text(title, content, false, darkTheme);
	}

	public static Pare<Window, Tbxm> text(Object title, String content, boolean saveable, boolean... darkTheme) {
		Tbxm modalCom = new Tbxm(content, Tbx.DIMS.WH100);
		if (saveable) {
			modalCom.saveOnShortCut();
		}
		return Pare.of(ZKM.showModal(title, modalCom, ZKC.getFirstWindow(), darkTheme), modalCom);
	}

	public static Pare<Window, Tbxm> anyWithBtSave(Path pathFile, boolean json, boolean... darkTheme) {
		Tbxm tbxm = (Tbxm) new Tbxm(pathFile, Tbx.DIMS.WH100).prettyjson(json).saveOnShortCut();
		Window window = ZKM.showModal(new Bt(SYMJ.SAVE).onCLICK(tbxm.getEventSave()), tbxm, darkTheme);
		return Pare.of(window, tbxm);
	}

	public static Pare<Window, Tbxm> textSaveable(String title, Path content, boolean... darkTheme) {
		return text(title, content, true, darkTheme);
	}

	public static Pare<Window, Tbxm> text(String title, Path content, boolean saveable, boolean... darkTheme) {
		Tbxm modalCom = new Tbxm(content, Tbx.DIMS.WH100);
		if (saveable) {
			modalCom.saveOnShortCut();
		}
		return Pare.of(ZKM.showModal(title, modalCom, ZKC.getFirstWindow(), ZKM.WH100, darkTheme), modalCom);
	}

	public static Pare<Window, SeTbxm> html(String title, Path content, boolean saveable) {
		SeTbxm modalCom = (SeTbxm) new SeTbxm(content);
		if (saveable) {
			modalCom.saveOnShortCut();
		}
		modalCom.renderHead();
		modalCom.setHeight("777px");
		modalCom.setWidth("100%");
		return Pare.of(ZKM.showModal(title, modalCom, ZKC.getFirstWindow(), ZKM.WH100), modalCom);
	}

	public static Pare<Window, SeTbxm> htmlReadonly(String title, String content) {
		SeTbxm modalCom = new SeTbxm(content);
		modalCom.renderHead();
		modalCom.setHeight("777px");
		modalCom.setWidth("100%");
		//		modalCom.setVflex("min");
		return Pare.of(ZKM.showModal(title, modalCom, ZKC.getFirstWindow(), ZKM.WH100), modalCom);
	}

	public static Window openEditorLog(Object title, String rsltOut) {
		LogHtmlCom logHtmlCom = LogHtmlCom.openWithLines(LogGetterDate.buildByFormat(AppZosProps.APR_LOG_DATE_FORMAT.getValueOrDefault()), SPLIT.allByNL(rsltOut));
		return ZKM.showModal(title, logHtmlCom);
	}
}
