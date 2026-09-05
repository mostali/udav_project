package zk_com.sun_editor;

import mpc.fs.fd.RES;
import org.zkoss.zk.ui.event.Event;
import zk_com.base.Tbxm;
import zk_form.head.IHeadCom;
import zk_form.head.IHeadRsrc;
import zk_form.head.StdHeadLib;
import zk_page.ZKC;
import zk_page.ZKJS;
import zk_page.ZKPage;

import java.nio.file.Path;
import java.util.Map;

//https://github.com/JiHong88/suneditor/blob/master/README.md
//http://suneditor.com/sample/html/examples.html
//http://suneditor.com/sample/html/out/document-user.html#noticeOpen
//https://github.com/JiHong88/SunEditor/issues/1056
public class SeTbxm extends Tbxm implements IHeadCom {

	public static final String EVENT_ON_SAVE = "onSave";

	public static final IHeadRsrc[] HEAD_RSCS = {StdHeadLib.SUNEDITOR_CSS, StdHeadLib.SUNEDITOR_JS};//, RsrcName.SUNEDITOR_EN_JS

	@Override
	public IHeadRsrc[] getHeadRsrc() {
		return HEAD_RSCS;
	}

	public static void registerHeadCom() {
		ZKPage.renderHeadRsrcs(ZKC.getFirstPageCtrl(), HEAD_RSCS);
	}

	public SeTbxm() {
	}

	public SeTbxm(Path filePath, DIMS... dims) {
		super(filePath, dims);
	}

	public SeTbxm(Path filePath, Object width_px_pct, Object height_px_pct) {
		super(filePath, width_px_pct, height_px_pct);
	}

	public SeTbxm(String value) {
		super(value, DIMS.WH100);
	}

	//EventListener
	public void onSave(Event event) {
		Map<String, Object> data = (Map) event.getData();
		//U.p("save:" + data.get("html"));
//		Sys.say("save");
		setValue((String) data.get("html"));
		if (isPersist()) {
			onSubmitTextValue(event);
		}
	}

	@Override
	protected void init() {
		super.init();

		SeTbxm tbxm = this;


		String uuid = tbxm.getUuid();
//		ZKL.log("SE:"+uuid);
//		if (super.saveble) {
//			tbxm.addEventListener(EVENT_ON_SAVE, (SerializableEventListener<Event>) event -> {
////				onSaveWtf(event, "wtf");
//
//			});
//		}
		//			ZKJS.bindJS(tbxm, U.f(SunEditorVF.ON_BIND_PATTERN, uuid, uuid, "{}"));

		String initJs = RES.readString(getClass(), "/_com/_sun-editor/init0.js").replace("{{se_id}}", uuid);
		ZKJS.bindJS(tbxm, initJs);

	}


	public void renderHead() {
		ZKPage.renderHeadRsrc(this);
	}
}
