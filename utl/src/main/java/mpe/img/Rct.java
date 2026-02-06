package mpe.img;

import mpu.X;
import mpc.exception.NI;
import mpe.checks.UCi;
import mpu.core.ARG;
import mpu.IT;

import java.awt.*;
import java.util.List;

public class Rct extends Rectangle {

	public Rct(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	public static Rct of(int x, int y, int width, int height) {
		return new Rct(x, y, width, height);
	}

	public static Rct ofXY(int x, int y, int x2, int y2) {
		return new Rct(x, y, x2 - x, y2 - y);
	}

	public static Rct of(int... xy_wh) {
		int[] xyWh = UCi.isLength(xy_wh, 4);
		return new Rct(xyWh[0], xyWh[1], xyWh[2], xyWh[3]);
	}

	public static Rct of(List<Rct> occurence, boolean max) {
		if (max) {
			int x = findXY(true, occurence, false);
			int y = findXY(false, occurence, false);
			int x2 = findXY2(true, occurence, true);
			int y2 = findXY2(false, occurence, true);
			return Rct.of(x, y, x2 - x, y2 - y);
		}
		throw new NI();
	}

	public static int findXY2(boolean x_y, List<Rct> occurence, boolean mnx) {
		int vl = -1;
		for (Rct rct : occurence) {
			int xy = x_y ? rct.x2() : rct.y2();
			if (vl == -1) {
				vl = xy;
			} else if (mnx) {
				if (xy > vl) {
					vl = xy;
				}
			} else {
				if (xy < vl) {
					vl = xy;
				}
			}
		}
		return IT.isPosOrZero(vl);
	}

	public static int findXY(boolean x_y, List<Rct> occurence, boolean mnx) {
		int vl = -1;
		for (Rectangle rct : occurence) {
			int xy = x_y ? rct.x : rct.y;
			if (vl == -1) {
				vl = xy;
			} else if (mnx) {
				if (xy > vl) {
					vl = xy;
				}
			} else {
				if (xy < vl) {
					vl = xy;
				}
			}
		}
		return IT.isPosOrZero(vl);
	}

	public static int findWH(boolean w_h, List<Rct> occurence, boolean mnx) {
		int vl = -1;
		for (Rectangle rct : occurence) {
			int wh = w_h ? rct.width : rct.height;
			if (vl == -1) {
				vl = wh;
			} else if (mnx) {
				if (wh > vl) {
					vl = wh;
				}
			} else {
				if (wh < vl) {
					vl = wh;
				}
			}
		}
		return IT.isPosOrZero(vl);
	}

	public static Rct increase(Rectangle rct, int x, int y, int w, int h) {
//		if (L.isDebugEnabled()) {
//			L.debug("Before:" + AR.as(x, y, w, h) + ":" + rct);
//		}
		int x0 = rct.x + x;
		x0 = x0 < 0 ? 0 : x0;

		int y0 = rct.y + y;
		y0 = y0 < 0 ? 0 : y0;

		int w0 = rct.width + w;
		int h0 = rct.height + h;

		Rct after = Rct.of(x0, y0, w0, h0);
//		if (L.isDebugEnabled()) {
//			L.debug("After:" + AR.as(x, y, w0, h0) + ":" + after);
//		}
		return after;
	}

	public static String toFilename(Rectangle cell, String... ext) {
		return X.f("rct-%sx%s-X%sY%s.", cell.width, cell.height, cell.x, cell.y) + ARG.toDefOr("", ext);
	}

	@Override
	public String toString() {
		return X.f("Rct: %s x %s [x:%s,y:%s - x2:%s,y2:%s]  ", width, height, x, y, x2(), y2());
	}

	private int y2() {
		return height + y;
	}

	private int x2() {
		return width + x;
	}

	public int bY() {
		return y + height;
	}

	public int tY() {
		return y;
	}
}
