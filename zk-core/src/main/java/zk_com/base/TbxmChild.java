package zk_com.base;


import mpu.core.ARRi;
import zk_com.core.IZState;

import java.nio.file.Path;

public class TbxmChild extends Tbxm {

	final int index;

	@Override
	public String getFormName() {
		return ((IZState) super.getParent()).getFormName();
	}

	public TbxmChild(Path path, int index) {
		super(path);
		this.index = index;
	}

	public static TbxmChild of(String placeholder) {
		TbxmChild tbx = new TbxmChild();
		tbx.setPlaceholder(placeholder);
		return tbx;
	}

	public static TbxmChild of(String placeholder, String value) {
		TbxmChild tbx = new TbxmChild(value);
		tbx.setPlaceholder(placeholder);
		return tbx;
	}

	public TbxmChild(DIMS... dims) {
		super(dims);
		this.index = -1;
	}

	public TbxmChild(String value, DIMS... dims) {
		this(value, null, null, dims);
	}

	public TbxmChild(String value, String placeholder, String tooltip, DIMS... dims) {
		super(value, placeholder, tooltip, dims);
		this.index = -1;
	}

	public TbxmChild(Path filePath, DIMS... dims) {
		super(filePath, dims);
		this.index = -1;
	}

	public TbxmChild(Path filePath, Object width_px_pct, Object height_px_pct) {
		super(filePath);
		width(width_px_pct);
		height(height_px_pct);
		this.index = -1;
	}

	public TbxmChild(int rows, DIMS... dims) {
		super();
		setRows(rows);
		dims(dims);
		this.index = -1;
	}

	public TbxmChild getMasterChild(TbxmChild... defRq) {
		return ARRi.first(getParent().getChildren(), defRq);
	}
}
