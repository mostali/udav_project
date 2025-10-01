package zk_form.notify;

import mpc.exception.WhatIsTypeException;
import mpu.str.SPLIT;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base_ctr.Div0;
import zk_com.win.HideBy;
import zk_form.ZkTheme;
import zk_page.ZKC;
import zk_page.ZKJS;
import zk_page.ZKS;

import java.util.List;


@Deprecated //old way, but used
class NotifyCustom extends Div0 {

	public enum ViewPosition {
		DEFAULT, CENTER, TOP_CENTER, BOTTOM_RIGHT;

		public NotifyCustom show(String lines, HideBy hideBy) {
			return show(lines, hideBy, null);
		}

		public NotifyCustom show(String lines, HideBy hideBy, ZKI.Level ntfLevel) {
			return NotifyCustom.show(ZKC.getFirstWindow(), SPLIT.allByNL(lines), this, hideBy, ntfLevel);
		}

		public NotifyCustom show(List<String> lines, HideBy hideBy) {
			return NotifyCustom.show(ZKC.getFirstWindow(), lines, this, hideBy);
		}

	}

	public static NotifyCustom show(Component parent, List<String> lines, ViewPosition view, HideBy hideBy) {
		return show(parent, Div0.buildMultilineDiv(lines), view, hideBy, null);
	}

	public static NotifyCustom show(Component parent, List<String> lines, ViewPosition view, HideBy hideBy, ZKI.Level level) {
		return show(parent, Div0.buildMultilineDiv(lines), view, hideBy, level);
	}

	public static NotifyCustom show(Component parent, HtmlBasedComponent container, ViewPosition view, HideBy hideBy, ZKI.Level level) {

		if (level == null) {
			level = ZKI.Level.INFO;
		}

		ZKI.Level finalLevel = level;
		NotifyCustom ntfPanel = new NotifyCustom() {
			@Override
			protected void init() {
				super.init();

				NotifyCustom ntfPanel = this;
				ntfPanel.appendBt((SerializableEventListener) event -> ZKC.removeMeCheckWindowParentReturnParent(ntfPanel), "X");

				//		String clasZKS = ZKS.classRnd(ntfPanel, "ntf_pnl_", 5);
				//		ntfPanel.setClass(clasZKS + " " + ZkTheme.getClassStyle(level));
				ntfPanel.setClass(ZkTheme.getClassStyle(finalLevel));

				ntfPanel.appendChild(container);

				container.setClass(ZkTheme.DIV_NOTIFY_CHILD);

				switch (view) {
					case TOP_CENTER:
						ZKJS.setAction_ShowEffect(ntfPanel, 500);
						ZKS.of(ntfPanel).left("20%").top("2rem").width("80%").zindex(ZkTheme.NOTIFY).abs();//.border(multiline, "1px solid red")
						ZKS.of(container).width("80%");//.border(multiline, "1px solid red")
						break;
					case CENTER:
						ZKJS.setAction_ShowEffect(ntfPanel, 500);
						ZKS.of(ntfPanel).left("20%").top("20%").width("80%").zindex(ZkTheme.NOTIFY).abs();//.border(multiline, "1px solid red")
						ZKS.of(container).width("80%");//.border(multiline, "1px solid red")
						break;
					case BOTTOM_RIGHT:
						ZKJS.setAction_ShowEffect(ntfPanel, 1000);
						ZKS.of(ntfPanel).right("2rem").bottom("2rem").zindex(ZkTheme.NOTIFY).abs();
						ZKS.of(container).width("100%");
						break;
					case DEFAULT:
						break;
					default:
						throw new WhatIsTypeException(view);
				}

				hideBy.apply(ntfPanel);

//		switch (hideBy) {
//			case DBL_CLICK:
//				ntfPanel.addEventListener(Events.ON_DOUBLE_CLICK, (SerializableEventListener<Event>) event -> ZKJS.bindOutViaClass(clasZKS));
//				break;
//			case TIMEOUT_SLOW:
//			case TIMEOUT_FAST:
////				String javascript3 = "setTimeout(function(){ document.querySelector(\"." + clasZKS + "\").remove(); } , 3600);";
//				int ms = hideBy == HideBy.TIMEOUT_SLOW ? 7200 : 3600;
//				String javascript3 = "setTimeout(function(){ jq(\"." + clasZKS + "\").fadeOut(); } , " + ms + ");";
//				ZKJS.eval(javascript3);
//				break;
//			case DEFAULT:
//				break;
//			default:
//				throw new WhatIsTypeException(hideBy);
//		}

			}
		};


		parent.appendChild(ntfPanel);

		return ntfPanel;
	}

}
