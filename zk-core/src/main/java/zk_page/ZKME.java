package zk_page;

import mpc.fs.ext.GEXT;
import mpc.str.sym.SYMJ;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.XulElement;
import zk_com.base.Bt;
import zk_com.base.Img;
import zk_com.base.Tbx;
import zk_com.base.Tbxm;
import zk_com.base_ctr.Div0;

import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.Supplier;

public class ZKME {
	@Deprecated
	public static XulElement openEditorImgOrText(Path pathFile) {
		return openEditorImgOrText(pathFile, false);
	}

	@Deprecated
	public static XulElement openEditorImgOrText(Path pathFile, boolean json, boolean... darkTheme) {
		XulElement com;
		if (GEXT.IMG.hasIn(pathFile)) {
			com = new Img(pathFile);
			ZKM.showModal("Image " + pathFile.getFileName().toString(), com);
		} else {
			Pare<Window, Tbxm> windowTbxmPare = openEditor(pathFile, json, darkTheme);
			com = windowTbxmPare.val();
		}
		return com;
	}

	public static Pare<Window, Tbxm> openEditorJson(Path pathFile, boolean... darkTheme) {
		return openEditor(pathFile, true, darkTheme);
	}

	public static Pare<Window, Tbxm> openEditor(Path pathFile, boolean json, boolean... darkTheme) {
		Tbxm tbxm = (Tbxm) new Tbxm(pathFile, Tbx.DIMS.WH100).prettyjson(json).saveOnShortCut();
		Window window = ZKM.showModal(new Bt(SYMJ.SAVE).onCLICK(tbxm.getEventSave()), tbxm, darkTheme);
		return Pare.of(window, tbxm);
	}

	public static XulElement openEditorWithJson(Supplier<String> supplierContent, Function<String, Boolean> saveCallback) {
		return openEditorWithContent(supplierContent, saveCallback, true);
	}

	public static XulElement openEditorWithContent(Supplier<String> supplierContent, Function<String, Boolean> saveCallback, boolean json) {
		return openEditorWithContent(supplierContent, saveCallback, null, json);
	}

	public static XulElement openEditorWithContent(Supplier<String> supplierContent, Function<String, Boolean> saveCallback, Component additional, boolean json) {
		Tbxm tbxm = (Tbxm) new Tbxm(supplierContent.get(), Tbx.DIMS.WH100).prettyjson(json).saveOnShortCut();
		Bt btSave = new Bt(SYMJ.SAVE).onCLICK(tbxm.getEventSave(saveCallback));
		Component top = additional == null ? btSave : Div0.of(btSave, additional);
		ZKM.showModal(top, tbxm);
		return tbxm;
	}

	public static Pare<Window, Tbxm> openEditorWithContent(Object title_cap_com, String content, boolean... darkTheme) {
		return ZKM.showModalEditor(title_cap_com, content, darkTheme);
	}

	public static Window openEditorWithJson(Path fileJson, Function<String, Boolean> saveCallback) {
		Tbxm tbxm = (Tbxm) new Tbxm(fileJson, Tbx.DIMS.WH100).prettyjson().saveOnShortCut();
		return ZKM.showModal(new Bt(SYMJ.SAVE).onCLICK(tbxm.getEventSave(saveCallback)), tbxm);
	}
}
