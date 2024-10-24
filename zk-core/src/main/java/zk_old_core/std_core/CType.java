package zk_old_core.std_core;

import mpu.core.ENUM;
import mpc.exception.WhatIsTypeException;
import mpc.fs.ext.EXT;
import mpc.fs.LS_SORT;
import mpc.fs.ext.MapExt;
import zk_old_core.std.*;

import java.nio.file.Path;
import java.util.*;

/**
 * @author dav 14.05.2022
 */
public enum CType {
	IMGW, IMG, VIDEO, MP3, HTML, ZUL, CSS, JS, SH, UNDEFINED, HTML_DOCX, HTML_MD,//
	GALLERY;

	public static final Comparator<CType> COMPARATOR = Comparator.comparingInt(ENUM::indexOf);

	public static Set<CType> defineCTypeIndex(Path formDir) {
		return defineCTypeIndex(MapExt.getMapExt(formDir, null, LS_SORT.NATURAL));
	}

	public static Set<CType> defineCTypeIndex(Map<Path, EXT> mapExt) {
		Set<CType> treeSet = new TreeSet<>(CType.COMPARATOR);
		Collection<EXT> exts = mapExt.values();
		for (EXT ext : exts) {
			if (ext == null) {//it dir
				continue;
			}
			CType ctype = CType.of(ext);
			treeSet.add(ctype);
		}
		return treeSet;
	}


	public static CType of(Path path) {
		return of(EXT.of(path));
	}

	public static CType of(EXT ext) {
		switch (ext) {
			case JPG:
			case PNG:
			case BMP:
			case JPEG:
				return CType.IMG;
			case HTML:
				return CType.HTML;
			case CSS:
				return CType.CSS;
			case JS:
				return CType.JS;
			case ZUL:
				return CType.ZUL;
			case DOCX:
				return CType.HTML_DOCX;
			case MD:
				return CType.HTML_MD;
			default:
				return UNDEFINED;
		}
	}

	public Class<? extends AbsVF> type() {
		switch (this) {
			case UNDEFINED: {
				throw new IllegalStateException("Call type from undefined");
			}
			case IMG:
			case IMGW:
				return ImgVF.class;
			case HTML:
				return HtmlVF.class;
			case MP3:
			case VIDEO:
			case ZUL:
			case JS:
			case CSS:
				return AbsRsrcVF.class;
			case HTML_DOCX:
				return HtmlDocxVF.class;
			case HTML_MD:
				return HtmlMdVF.class;
			case GALLERY:
				return GalleryVF.class;
			default:
				throw new WhatIsTypeException(this);

		}
	}
}
