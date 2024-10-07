package zk_form.head;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum StdHeadLib implements IHeadRsrc {
	JQUERY(StdHeadLibTYPE.JS, "https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"),//
	DND_SIMPLE(StdHeadLibTYPE.JS, "/_com/mouse-event/dnd-simple.js"),//
	CLICK_INFO(StdHeadLibTYPE.JS, "/_com/mouse-event/simple-click-info.js"),//
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

	//
	//Native JS
	//
	PAGE_JS(StdHeadLibTYPE.JS, "/_js/zkos.js")//
	;

	private final StdHeadLibTYPE headRsrcType;
	private final String file;

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
