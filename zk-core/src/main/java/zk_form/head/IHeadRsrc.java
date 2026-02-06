package zk_form.head;

import mpc.exception.WhatIsTypeException;
import mpu.core.RW;
import mpc.fs.fd.RES;
import mpu.str.STR;
import zk_page.ZKJS;
import zk_page.ZKPage;

import java.nio.file.Path;

public interface IHeadRsrc<R> {
	default StdHeadLib getStdHeadLib() {
		return null;
	}

	R rsrc();

	StdHeadLibTYPE type();

	default Class getRsrsType() {
		R rsrc = rsrc();
		return rsrc == null ? null : rsrc.getClass();
	}

	default String toRsrcContent() {
		Class type = getRsrsType();
		if (Path.class.isAssignableFrom(type)) {
			return RW.readString((Path) rsrc());
		} else if (type == RES.class) {
			return ((RES) rsrc()).cat();
		} else if (CharSequence.class.isAssignableFrom(type)) {
			return rsrc().toString();
		}
		throw new WhatIsTypeException(type);
	}

	default String toHeadContent() {
		return toHeadContent(this);
	}

	static String LINK_JS(String linkJs) {
		return "<script src=\"" + linkJs + "\"></script>";
	}

	static String LINK_CSS(String linkCss) {
		return "<link rel=\"stylesheet\" href=\"" + linkCss + "\">";
	}

	static String DATA_CSS(String dataCss) {
		return STR.wrapTag(dataCss, "style", "rel=\"stylesheet\"");
	}

	static String DATA_JS(String dataJs) {
		return STR.wrapTag(dataJs, "script", ZKJS.TAG_ATTR);
	}

	static String toHeadContent(IHeadRsrc rsrc) {
		StdHeadLibTYPE headType = rsrc.type();
		String rsrcData = rsrc.toRsrcContent();
		switch (headType) {
			case JS:
				return IHeadRsrc.LINK_JS(rsrcData);
			case CSS:
				return IHeadRsrc.LINK_CSS(rsrcData);
			case DATA_CSS:
				return IHeadRsrc.DATA_CSS(rsrcData);
			case DATA_JS:
				return IHeadRsrc.DATA_JS(rsrcData);
			case DATA:
				return rsrcData;
			default:
				throw new WhatIsTypeException(headType);
		}
	}

	default void addToPage() {
		ZKPage.renderHeadRsrc(this);
	}

//	default void addToPage0() {
//		IHeadRsrc rsrc = StdHeadLib.PRETTYFY_JS.toRsrc();
//		ZKPage.renderHeadRsrc(rsrc);
//	}
}
