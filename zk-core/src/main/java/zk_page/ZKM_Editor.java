package zk_page;

import mpc.str.sym.SYMJ;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.XulElement;
import zk_com.base.Bt;
import zk_com.base.Tbx;
import zk_com.base.Tbxm;
import zk_com.base_ctr.Div0;
import zk_com.sun_editor.SeTbxm;

import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.Supplier;

public class ZKM_Editor {

	public static Pare<Window, Tbxm> openEditorJson(Path pathFile, boolean... darkTheme) {
		return openEditorText(pathFile, true, darkTheme);
	}

	public static XulElement openEditorWithBtSave(Supplier<String> supplierContent, Function<String, Boolean> saveCallback) {
		return openEditorWithBtSave(supplierContent, saveCallback, true);
	}

	public static Window openEditorWithBtSave(Path fileJson, Function<String, Boolean> saveCallback) {
		Tbxm tbxm = (Tbxm) new Tbxm(fileJson, Tbx.DIMS.WH100).prettyjson().saveOnShortCut();
		return ZKM.showModal(new Bt(SYMJ.SAVE).onCLICK(tbxm.getEventSave(saveCallback)), tbxm);
	}

	public static Pare<Window, Tbxm> openEditorText(Path pathFile, boolean json, boolean... darkTheme) {
		Tbxm tbxm = (Tbxm) new Tbxm(pathFile, Tbx.DIMS.WH100).prettyjson(json).saveOnShortCut();
		Window window = ZKM.showModal(new Bt(SYMJ.SAVE).onCLICK(tbxm.getEventSave()), tbxm, darkTheme);
		return Pare.of(window, tbxm);
	}

	public static XulElement openEditorWithBtSave(Supplier<String> supplierContent, Function<String, Boolean> saveCallback, boolean json) {
		return openEditorWithBtSave(supplierContent, saveCallback, null, json);
	}

	public static XulElement openEditorWithBtSave(Supplier<String> supplierContent, Function<String, Boolean> saveCallback, Component addiComCap, boolean json) {
		Tbxm tbxm = (Tbxm) new Tbxm(supplierContent.get(), Tbx.DIMS.WH100).prettyjson(json);
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

	public static Pare<Window, Tbxm> openEditorJson(Object title, String content, boolean... darkTheme) {
//		content = true ? UGson.toStringPrettyLinent(content.toString()) : UGson.toStringPretty(content.toString());
		Tbxm modalCom = (Tbxm) new Tbxm(content, Tbx.DIMS.WH100).prettyjson();
		return Pare.of(ZKM.showModal(title, modalCom, ZKC.getFirstWindow(), darkTheme), modalCom);
	}

	public static Pare<Window, Tbxm> openEditorText(Object title, String content, boolean... darkTheme) {
		Tbxm modalCom = new Tbxm(content, Tbx.DIMS.WH100);
		return Pare.of(ZKM.showModal(title, modalCom, ZKC.getFirstWindow(), darkTheme), modalCom);
	}

	public static Pare<Window, Tbxm> openEditorJSON(String title, Path content, boolean... darkTheme) {
		Tbxm modalCom = (Tbxm) new Tbxm(content, Tbx.DIMS.WH100).prettyjson(true).saveOnShortCut();
		return Pare.of(ZKM.showModal(title, modalCom, ZKC.getFirstWindow(), new String[]{"100%", "100%"}, darkTheme), modalCom);
	}

	public static Pare<Window, Tbxm> openEditorTEXT(String title, Path content, boolean... darkTheme) {
		Tbxm modalCom = (Tbxm) new Tbxm(content, Tbx.DIMS.WH100).saveOnShortCut();
		return Pare.of(ZKM.showModal(title, modalCom, ZKC.getFirstWindow(), new String[]{"100%", "100%"}, darkTheme), modalCom);
	}

	public static Pare<Window, SeTbxm> openEditorHTML(String title, Path content) {
		SeTbxm modalCom = (SeTbxm) new SeTbxm(content).saveOnShortCut();
		modalCom.renderHead();
		modalCom.setHeight("777px");
		modalCom.setWidth("100%");
//		modalCom.setVflex("min");
		return Pare.of(ZKM.showModal(title, modalCom, ZKC.getFirstWindow(), new String[]{"100%", "100%"}), modalCom);
	}

	public static Pare<Window, SeTbxm> openEditorHTML(String title, String content) {
		SeTbxm modalCom = new SeTbxm(content);
		modalCom.renderHead();
		return Pare.of(ZKM.showModal(title, modalCom, ZKC.getFirstWindow(), new String[]{"100%", "100%"}), modalCom);
	}

}
