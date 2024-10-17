package zk_old_core.mdl.pageset;

import mpu.core.ARG;
import mpc.exception.WhatIsTypeException;
import mpc.fs.ext.EXT;
import zk_form.head.IHeadRsrc;

import java.nio.file.Path;

public enum HeadFileType {
	HTML, JS, CSS, UNDEFINED;

	public static HeadFileType of(Path e, HeadFileType... defRq) {
		return of(EXT.of(e), defRq);
	}

	public static HeadFileType of(EXT e, HeadFileType... defRq) {
		switch (e) {
			case JS:
				return HeadFileType.JS;
			case CSS:
				return HeadFileType.CSS;
			case HTML:
				return HeadFileType.HTML;
			default:
				return ARG.toDef(defRq);
		}
	}

	public String toHeadData(String file_data) {
		switch (this) {
			case CSS:
				return IHeadRsrc.DATA_CSS(file_data);
			case JS:
				return IHeadRsrc.DATA_JS(file_data);
			case HTML:
				return file_data;
			default:
				throw new WhatIsTypeException(this);
		}
	}
}
