package zk_com.base;


import java.nio.file.Path;

public class Tbxm extends Tbx {

	public static Tbxm of(String placeholder) {
		Tbxm tbx = new Tbxm();
		tbx.setPlaceholder(placeholder);
		return tbx;
	}

	public static Tbxm of(String placeholder, String value) {
		Tbxm tbx = new Tbxm(value);
		tbx.setPlaceholder(placeholder);
		return tbx;
	}

	public Tbxm(DIMS... dims) {
		super(dims);
	}

	public Tbxm(String value, DIMS... dims) {
		this(value, null, null, dims);
	}

	public Tbxm(String value, String placeholder, String tooltip, DIMS... dims) {
		super(value, placeholder, tooltip, dims);
	}

	public Tbxm(Path filePath, DIMS... dims) {
		super(filePath, dims);
	}

	public Tbxm(Path filePath, Object width_px_pct, Object height_px_pct) {
		super(filePath);
		width(width_px_pct);
		height(height_px_pct);
	}

	public Tbxm(int rows, DIMS... dims) {
		super();
		setRows(rows);
		dims(dims);
	}

}
