package zk_page.behaviours;

import mp.utl_odb.tree.AppPropDef;
import mpc.time.EDayTime;
import mpu.X;
import mpu.str.USToken;
import org.jetbrains.annotations.NotNull;
import zk_os.AppZosProps;
import zk_page.node_state.FormState;

public enum BgImg {
	NIGHT_MODE, BG_ABS_JPG, BG_ABS2_JPG, BG_ABS3_JPG, BG_SEC_PNG, BG_DARK_LIGHT_JPG, BG_ZN1_JPG, BG_KIT_JPG, BG_SHIVA_JPG, BG_MARS_JPG;

	@NotNull
	public static String getBgImageViaNigthMode(FormState formState) {
		BgImg bgImg = null;
		AppPropDef<BgImg> zosProps = AppZosProps.APD_ZOS_BGIMG;
		if (formState != null) {
			bgImg = (BgImg) formState.getAs(zosProps.name(), BgImg.class, null);
		}
		bgImg = bgImg != null ? bgImg : zosProps.getValueOrDefault(BG_SEC_PNG);
		if (bgImg == NIGHT_MODE) {
			bgImg = EDayTime.valueOf() == EDayTime.NIGHT ? BG_DARK_LIGHT_JPG : BG_SEC_PNG;
		}
		String bgUrl = X.f("url(_img/%s)", bgImg.toFileName());
		return bgUrl;
	}

	public String toFileName() {
		String[] twoGreedy = USToken.twoGreedy(name().toLowerCase(), "_");
		return twoGreedy[0] + "." + twoGreedy[1];
	}
}
