package zk_notes.control;

import mp.utl_odb.tree.ctxdb.Ctx10Db;
import mpc.html.STYLE;
import zk_com.base.Xml;
import zk_com.win.Win0;
import zk_form.notify.ZKI;
import zk_os.notify.NotifySrv;
import zk_page.*;
import zk_page.core.ISpCom;

import java.util.List;

public class RightNotifyPanel extends Win0 implements ISpCom {

	public static final int MARGIN_TOP_NOTETYPE = 10;
	public static final int MARGIN_TOP_CONTROL = 25;

	public static RightNotifyPanel findFirst(RightNotifyPanel... defRq) {
		return ZKCFinderExt.findFirst_inPage0(RightNotifyPanel.class, true, defRq);
	}

	public static RightNotifyPanel light(String msg) {
		RightNotifyPanel first = findFirst(null);
		if (first == null) {
			return null;
		}
//		ZKI.infoBottomCenter(msg);
		ZKJS.eval("highlightElement('%s',1000)", first.getUuid());
		return first;
	}

	@Override
	protected void init() {
		super.init();

//		appendChild(Xml.ofJs("function lightRCP(){ highlightElement()alert ('ok:'+ %s);return %s; }", getUuid(), getUuid()));

		ZKS.WC_PADDING(this, 5, 0);
		fixed();
		width(400);

		addSTYLE("background-color: rgba(255, 255, 255, 0.5)");

		setContentStyle(STYLE.addStyle(getContentStyle(), "background-color: rgba(255, 255, 255, 0.3)"));

		bottom_rigth(1.0, 5);

		ZKS.BORDER_RADIUS(this, "10px");

		ZKS.OVERFLOW(this, 3);

		ZKS.HEIGHT_MIN(this, 90.0);

		List<Ctx10Db.CtxModel10> all = NotifySrv.getAll();

		for (Ctx10Db.CtxModel10 ctxModel10 : all) {
			Xml xml = Xml.ofDetailsInline(ctxModel10.getKey() + " - " + ctxModel10.getValue(), ctxModel10.getExt());
			appendChild(xml);
//			ZKI.ViewType.INFO_BOTTOM_RIGHT.showView(ctxModel10.getKey() + " - " + ctxModel10.getValue() + "\n" + ctxModel10.getExt());
		}

	}


}
