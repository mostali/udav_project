package zk_form.control;

import mpc.env.APP;
import mpc.ui.UColorTheme;
import mpe.core.UErr;
import mpu.X;
import mpu.str.RANDOM;
import zk_com.base.Xml;
import zk_com.base_ctr.Div0;
import zk_page.ZKS;

public class ErrLb extends Div0 {
	final String msg;
	final Throwable ex;

	public ErrLb(String msg) {
		this(msg, null);
	}

	public ErrLb(String msg, Throwable ex) {
		super();

		this.msg = msg;
		this.ex = ex;

		if (APP.isDebugEnable()) {
			String rndRed = RANDOM.ARRAY_ITEM(UColorTheme.RED);
			if (L.isErrorEnabled()) {
				if (ex == null) {
					L.error(msg + "/ERR:NULL");
				} else {
					L.error(msg, ex);
				}
			}
			if (ex == null) {
				appendLb(msg + "/ERR:NULL");
			} else {
				Xml child = Xml.DETAILS_INLINE(X.f("<b style='color:%s'>%s %s<b>", rndRed, msg, UErr.getMessageOr(ex, "causeNull")), UErr.getStackTrace(ex).replace("\n", "</br>"));
				appendChild(child);
			}
		}

		ZKS.OPACITY(this, 0.9);
//			absolute();
//			ZKS.BOTTOM_RIGHT(this, 2.0, RANDOM.RANGE(200, 500));
	}

	@Override
	public String toString() {
		return msg + " > " + (ex != null ? ex.getMessage() : "NULL");
	}
}
