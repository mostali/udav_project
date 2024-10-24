package zk_old_core.mdl;

import mpu.core.ARG;
import mpc.fs.ext.EXT;

public enum PagePostion {
	HEAD, BODY, HEADER, FOOTER;

	public static PagePostion ofFilename(String filename, PagePostion... defRq) {
		return ofFilenameWoExt(EXT.getExtFromFilename(filename, filename), defRq);
	}

	public static PagePostion ofFilenameWoExt(String filename, PagePostion... defRq) {
		switch (filename) {
			case "header":
				return PagePostion.HEADER;
			case "body":
				return PagePostion.BODY;
			case "footer":
				return PagePostion.FOOTER;
			case "head":
				return PagePostion.HEAD;
			default:
				return ARG.toDefRq(defRq);
		}
	}
}
