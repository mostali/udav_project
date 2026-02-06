package zk_notes;

import mpc.env.APP;
import mpc.str.sym.SYMJ;
import mpe.NT;

public class AxnTheme {


	public static final String RED = "#e66771";
	public static final String BLUE = "#00bfff";
	public static final String GREEN = "#66ff00";

	public static final int FONT_SIZE_MENU = 9;

	public static final int FONT_SIZE_WIDGET_HEADER = 16;
	public static final int FONT_SIZE_APP_LINK = 22;

	//

	public static final int DEFAULT_FONT_SIZE_AUTO_DIMS = 16;

	public static final int DEFAULT_PAGE_HEIGHT_MIN = 3900;

	public static final int ZI_QVIEW_WIN = 9802;
	public static final int ZI_QVIEW = 9801;


	public static final int ZI_LOGO = 9999;

	public static final int ZI_MENU = 1008;
	public static final int MAX_FILE_SIZE = 10;

	public static final int HEADER_HEIGHT = 50;//px

	//TopFixedPanel
	public static final String[] NAV_HEADER_POS = {"10px", "10px"};
	public static final int HEADER_HEIGHT_FIXED_LEFT = 45;


	public static String getIcon() {
		switch (APP.getNetOfAppName(NT.DEF)) {
			case XN:
			case BEA:
				return SYMJ.LOTOS;
			default:
				return SYMJ.TREE;
		}
	}

}
