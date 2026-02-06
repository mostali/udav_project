package zk_form.head;

import lombok.RequiredArgsConstructor;
import zk_notes.node.core.NVT;

@RequiredArgsConstructor
public enum StdHeadLib implements IHeadRsrc {
	JQUERY_3_1_1(StdHeadLibTYPE.JS, "https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"),//
	DND_SIMPLE(StdHeadLibTYPE.JS, "/_com/_mouse-event/dnd-simple.js"),//
	CLICK_INFO(StdHeadLibTYPE.JS, "/_com/_mouse-event/simple-click-info.js"),//
	//
	//
	BXSLIDER_JS(StdHeadLibTYPE.JS, "https://cdn.jsdelivr.net/bxslider/4.2.12/jquery.bxslider.min.js"),//
	BXSLIDER_CSS(StdHeadLibTYPE.CSS, "https://cdn.jsdelivr.net/bxslider/4.2.12/jquery.bxslider.css"),//
	//
	//https://github.com/JiHong88/SunEditor/blob/master/README.md
	//
	SUNEDITOR_CSS(StdHeadLibTYPE.CSS, "https://cdn.jsdelivr.net/npm/suneditor@latest/dist/css/suneditor.min.css"),//
	SUNEDITOR_JS(StdHeadLibTYPE.JS, "https://cdn.jsdelivr.net/npm/suneditor@latest/dist/suneditor.min.js"),//
	SUNEDITOR_EN_JS(StdHeadLibTYPE.JS, "https://cdn.jsdelivr.net/npm/suneditor@latest/src/lang/en.js"),//

	PRETTYFY_JS(StdHeadLibTYPE.JS, "https://cdn.jsdelivr.net/gh/google/code-prettify@master/loader/run_prettify.js?autorun=true&amp;lang=kotlin&amp;skin=sunburst"),//
	CHARTS_JS(StdHeadLibTYPE.JS, "https://cdn.jsdelivr.net/npm/chart.js"),//


	//
	//Native JS
	//
	PAGE_JS(StdHeadLibTYPE.JS, "/_js/zkos.js"),//
	//
	//
	PAGE_JQ_FILEUPLOAD(StdHeadLibTYPE.JS, "/js/zkc_afu/jslibs/jquery.fileupload.js"),//
	PAGE_JQ_WIDGET(StdHeadLibTYPE.JS, "/js/zkc_afu/jslibs/jquery.ui.widget.js"),//
	PAGE_JQ_TRANSP(StdHeadLibTYPE.JS, "/js/zkc_afu/jslibs/jquery.iframe-transport.js"),//
	PAGE_JQ_TOAST(StdHeadLibTYPE.JS, "/js/zkc_afu/jslibs/jquery.toastmessage.js"),//
	JQUERY_1_9_1(StdHeadLibTYPE.JS, "https://cdnjs.cloudflare.com/ajax/libs/jquery/1.9.1/jquery.min.js"),//
	//
	//
	JS_CODEMIRROR_6_65_7(StdHeadLibTYPE.JS, "https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.65.7/codemirror.min.js"),//
	JS_CODEMIRROR_6_65_7_PERL(StdHeadLibTYPE.JS, "https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.65.7/mode/perl/perl.min.js"),//
	CSS_CODEMIRROR_6_65_7(StdHeadLibTYPE.CSS, "https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.65.7/codemirror.min.css"),//
	CSS_CODEMIRROR_6_65_7_THEME_ABBOTT(StdHeadLibTYPE.CSS, "https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.65.7/theme/abbott.min.css"),//
	//
	//
	;

	private final StdHeadLibTYPE headRsrcType;
	private final String file;

	public static void initAndAddRsrcToPage(NVT nvt) {
		switch (nvt) {
			case CODE:
				PRETTYFY_JS.addToPage();
				break;
			case WYSIWYG:
				SUNEDITOR_JS.addToPage();
				SUNEDITOR_CSS.addToPage();
				break;
//			case WYSIWYG:
//				StdHeadLib.SUNEDITOR_JS.addToPage();
//				StdHeadLib.SUNEDITOR_CSS.addToPage();
//				break;
		}
	}

	public IHeadRsrc toRsrc() {
		return headRsrcType.of(file, this);
	}

	@Override
	public String rsrc() {
		return file;
	}

	@Override
	public StdHeadLibTYPE type() {
		return headRsrcType;
	}

	@Override
	public StdHeadLib getStdHeadLib() {
		return this;
	}

}
