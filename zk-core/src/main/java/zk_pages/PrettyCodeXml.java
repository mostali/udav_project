package zk_pages;

import zk_com.base.Xml;
import zk_com.core.IZWin;
import zk_form.head.IHeadCom;
import zk_form.head.IHeadRsrc;
import zk_form.head.StdHeadLib;
import zk_page.ZKJS;
import zk_page.ZKPage;

//https://github.com/JiHong88/suneditor/blob/master/README.md
//http://suneditor.com/sample/html/examples.html
//http://suneditor.com/sample/html/out/document-user.html#noticeOpen
//https://github.com/JiHong88/SunEditor/issues/1056
public class PrettyCodeXml extends Xml implements IZWin, IHeadCom {

	public static final IHeadRsrc[] HEAD_RSCS = {StdHeadLib.PRETTYFY_JS};

	@Override
	public IHeadRsrc[] getHeadRsrc() {
		return HEAD_RSCS;
	}

	public PrettyCodeXml(String value) {
		super(value);
	}

	@Override
	protected void init() {
		super.init();

		PrettyCodeXml tbxm = this;

		String uuid = tbxm.getUuid();
//		ZKL.log("SE:"+uuid);
//		if (super.saveble) {
//			tbxm.addEventListener(EVENT_ON_SAVE, (SerializableEventListener<Event>) event -> {
////				onSaveWtf(event, "wtf");
//
//			});
//		}
		ZKJS.bindJS(tbxm, "PR.prettyPrint()");


////								String v2="<script src=\"https://cdn.jsdelivr.net/gh/google/code-prettify@master/loader/run_prettify.js\"></script>";
////								String v2="<script src=\"https://cdn.jsdelivr.net/gh/google/code-prettify@master/loader/run_prettify.js?skin=sunburst&autorun=true;lang=java\"></script>";
////								pageCtrl.addBeforeHeadTags("<script src=\"https://cdn.jsdelivr.net/gh/google/code-prettify@master/loader/run_prettify.js\"></script>");
////								String v2 = "<script src=\"https://cdn.jsdelivr.net/gh/google/code-prettify@master/loader/run_prettify.js?autorun=true&amp;lang=java&amp;skin=sunburst\"></script>";
//		String v2 = "<script src=\"https://cdn.jsdelivr.net/gh/google/code-prettify@master/loader/run_prettify.js?autorun=true&amp;lang=kotlin&amp;skin=sunburst\"></script>";
////								String v2 = "<script src=\"https://cdn.jsdelivr.net/gh/google/code-prettify@master/loader/run_prettify.js?autorun=true&amp;skin=sunburst\"></script>";
////								https://github.com/googlearchive/code-prettify/tree/master/src
//
//		ZKC.getFirstPageCtrl().addBeforeHeadTags(v2);

//		ZKJS.bindJS(tbxm, "");uuid

	}

}
