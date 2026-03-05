package zk_page.behaviours;

import lombok.SneakyThrows;
import mp.utl_odb.tree.AppPropDef;
import mpc.exception.FIllegalStateException;
import mpc.fs.ext.GEXT;
import mpc.fs.fd.EFT;
import mpc.fs.fd.RES;
import mpu.X;
import mpu.str.TKN;
import org.jetbrains.annotations.NotNull;
import zk_os.AppZosProps;
import zk_notes.node_state.ObjState;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

//https://wallpaperscraft.ru/
public enum BgImg {
	AUTO_NIGHT_MODE, //
	BG_SEC_PNG, //
	BG_L_ABS_JPG, //
	BG_YELLOW_ABS_JPG, BG_YELLOW_ABS2_JPG, BG_Y_SHIVA_JPG,//
	BG_B_KC_PNG, //
	BG_B_LIGHT_JPG, BG_B_STARS_JPG, //
	BG_B_KIT_JPG, //
	BG_B_MARS_JPG;

	public static final String BGIMG_DEFAULT = "bg_l_sec.png";

	//	@NotNull
//	public static String getBgImageViaNigthMode(FormState formState) {
//		BgImg bgImg = null;
//		AppPropDef<BgImg> zosProps = AppZosProps.APD_ZOS_BGIMG;
//		if (formState != null) {
//			bgImg = (BgImg) formState.getAs(zosProps.key(), BgImg.class, null);
//		}
//		bgImg = bgImg != null ? bgImg : zosProps.getValueOrDefault(BG_SEC_PNG);
//		if (bgImg == AUTO_NIGHT_MODE) {
//			bgImg = EDayTime.valueOf() == EDayTime.NIGHT ? BG_B_LIGHT_JPG : BG_SEC_PNG;
//		}
//		String bgUrl = X.f("url(_img/%s)", bgImg.toFileName());
//		return bgUrl;
//	}

	@NotNull
	public static String getBgImageRelPath(ObjState formState) {
		AppPropDef<List<String>> appPropDef = AppZosProps.APD_ZOS_BGIMG;
		List<String> stateVls = (List<String>) formState.getAs(appPropDef.getPropName(), List.class, null);
		String vl;
		if (X.notEmpty(stateVls)) {
			vl = stateVls.get(0);
		} else {
			List<String> valueOrDefault = appPropDef.getValueOrDefault(null);
			vl = X.empty(valueOrDefault) ? BGIMG_DEFAULT : valueOrDefault.get(0);
		}
		return getBgImageRelPathAsCssBgProp(vl);
	}

	@NotNull
	public static String getBgImageRelPathAsCssBgProp(String imgPath) {
		return "url(" + getBgImageRelPath(imgPath) + ")";
	}

	@NotNull
	public static String getBgImageRelPath(String imgName) {
		return "_bg_img/" + imgName;
	}

	@SneakyThrows
	@NotNull
	public static List<String> getAllDefaultBgImages() {
		List ls = RES.of("/web/_bg_img/").ls();
//		L.info("Found all Bg Images:" + ls);
		Object o = ls.get(0);
		Predicate<String> filterImg = fn -> fn.startsWith("bg_") && GEXT.of(fn) == GEXT.IMG;
		if (o instanceof Path) {
			ls = (List) ls.stream().map((p) -> ((Path) p).getFileName().toString()).filter(filterImg).collect(Collectors.toList());
		} else if (o instanceof RES) {
			ls = (List) ls.stream().filter(r -> ((RES) r).fType(null) == EFT.FILE).map((p) -> ((RES) p).fName()).filter(filterImg).collect(Collectors.toList());
		} else {
			throw new FIllegalStateException("Unknown bg types:" + ls);
		}
		Collections.sort(ls);
		return ls;
	}

	@SneakyThrows
	@NotNull
	public static List<String> getAllDefaultBgImagesSrcs() {
		List ls = RES.of("/web/_bg_img/").ls();
//		L.info("Found all Bg Images:" + ls);
		Object o = ls.get(0);
		Predicate<String> filterImg = fn -> GEXT.of(fn) == GEXT.IMG;
		if (o instanceof Path) {
			ls = (List) ls.stream().map((p) -> ((Path) p).getFileName().toString()).filter(filterImg).collect(Collectors.toList());
		} else if (o instanceof RES) {
			ls = (List) ls.stream().filter(r -> ((RES) r).fType(null) == EFT.FILE).map((p) -> ((RES) p).fName()).filter(filterImg).collect(Collectors.toList());
		} else {
			throw new FIllegalStateException("Unknown bg types:" + ls);
		}
		Collections.sort(ls);
		return ls;
	}

	public String toFileName() {
		String[] twoGreedy = TKN.twoGreedy(name().toLowerCase(), "_");
		return twoGreedy[0] + "." + twoGreedy[1];
	}
}
