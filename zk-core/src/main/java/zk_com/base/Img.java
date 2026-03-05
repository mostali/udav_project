package zk_com.base;


import lombok.SneakyThrows;
import mpc.exception.WhatIsTypeException;
import mpc.json.GsonMap;
import mpe.str.CN;
import mpu.IT;
import mpc.fs.UF;
import org.zkoss.image.AImage;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import zk_com.core.IZCom;
import zk_notes.factory.BeType;
import zk_notes.factory.NFStyle;
import zk_page.ZKC;
import zk_notes.node_state.ObjState;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;


public class Img extends Image implements IZCom {

	private String pathStr = null;

	public static Img of(Object pathFileHref) {
		if (pathFileHref instanceof CharSequence) {
			return new Img(pathFileHref.toString());
		} else if (pathFileHref instanceof Path) {
			return new Img((Path) pathFileHref);
		} else if (pathFileHref instanceof File) {
			return new Img((File) pathFileHref);
		}
		throw new WhatIsTypeException(pathFileHref + "");
	}

	@Override
	public String getComName() {
		if (pathStr() != null) {
			return UF.clearFileNameRU_RemoveSlash(pathStr());
		}
		return IZCom.super.getComName();
	}

	public String pathStr() {
		return pathStr;
	}

	public Img(String src) {
		super(src);
	}

	public Img(Path file) {
		this(file.toFile());
	}

	@SneakyThrows
	public Img(File file) {
//		if (EXT.WEBP.is(file.getName())) {
//			file = UImg.convert(file, EXT.PNG).toFile();
//		}
		if (!file.exists()) {
			L.error("Component Img except exist file with image '{}'", file);
		}
		setContent(new AImage(file));

		this.pathStr = file.toString();
	}

	public static Div wrapDiv(Img img) {
		return ZKC.newDiv(img);
	}

	public static Div wrapDiv(Path img) {
		return ZKC.newDiv(new Img(img));
	}

	public static Div wrapDiv(String src) {
//		Image child = new Image(src);
		return ZKC.newDiv(new Img(src));
	}

	@Override
	public ObjState getComStateDefault() {
		return getComState(UF.fn(IT.NE(pathStr())));
	}

	public void applyInnerProps_Width(ObjState formState) {
		GsonMap imgGm = formState.getAsGsonMap(CN.IMG, null);
		if (imgGm != null) {
			NFStyle.applyState_Width(this, formState, CN.IMG);
		}

	}

	public Map<BeType, Boolean> applyInnerProps(GsonMap stateImgCom) {

		Map<BeType, Boolean> rsp = BeType.applyProp(this, stateImgCom, BeType.values());
return rsp;
//		stateImgCom.applyChild(this, CN.IMG, "width", "opacity", "height", "position", "display", "titlex");
//		ZKS.applyStylePropertyPxPct()
//		if(stateImgCom.get(""))

//		NFStyle.applyComDefaultStyle(this, stateImgCom);
//		width(96.0);
//		absolute();
	}
}
