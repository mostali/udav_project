package zk_page.behaviours;

import mpu.str.USToken;

public enum BgImg {
	AUTO, BG_SEC_PNG, BG_DARK_LIGHT_JPG, BG_ZN1_JPG, BG_KIT_JPG, BG_SHIVA_JPG, BG_MARS_JPG;

	public String toFileName() {
		String[] twoGreedy = USToken.twoGreedy(name().toLowerCase(), "_");
		return twoGreedy[0] + "." + twoGreedy[1];
	}
}
