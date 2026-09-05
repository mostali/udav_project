//package zk_form.control;
//
//import lombok.SneakyThrows;
//import mpu.core.ARG;
//import mpu.X;
//import mpc.exception.NI;
//import mpu.str.USToken;
//import mpe.state_rw.IMapStateRw;
//import mpt.TRM;
//import mpt.TrmRq;
//import mpt.TrmRsp;
//import mpt.TrmRspStr;
//import org.zkoss.zk.ui.event.Event;
//import org.zkoss.zk.ui.event.Events;
//import org.zkoss.zk.ui.event.InputEvent;
//import org.zkoss.zk.ui.event.SerializableEventListener;
//import org.zkoss.zul.Combobox;
//import org.zkoss.zul.Comboitem;
//import utl_web.UWeb;
//import zk_com.base_ext.EnumSwitcher;
//import zk_old_core.control_old.TopAdminMenu;
//import zk_old_core.AppCoreStateOld;
//import zk_com.elements.Pos6TRBL_H;
//import zk_com.base_ctr.Div0;
//import zk_os.AppCmdUI;
//import zk_form.notify.ZKI_Log;
//import zk_page.core.ISpCom;
//import zk_form.notify.ZKI_Window;
//import zk_os.sec.MatrixAccess;
//
//import java.util.*;
//
//public class QuickCmdRunner extends Div0 implements ISpCom {
//	@Override
//	public MatrixAccess getMA() {
//		return MatrixAccess.EDITOR_FULL;
//	}
//
//	static IMapStateRw getState() {
//		return AppCoreStateOld.getStateGlobal(QuickCmdRunner.class, true);
//	}
//
//	private static void applyStyle(QuickCmdRunner ctrlMenu, Pos6TRBL_H tmPos, boolean... writeState) {
//		if (ARG.isDefEqTrue(writeState)) {
//			getState().write(TopAdminMenu.TM_POS, tmPos.name());
//		}
//
//		String _WIDTH_H = UWeb.isMobile() ? "width:100%;" : TopAdminMenu._WIDTH_H;
//		String baseStyle = "position:fixed;z-index:1000;" + _WIDTH_H;
//		switch (tmPos) {
//			case TL:
//				ctrlMenu.setStyle(baseStyle + TopAdminMenu._TOP1 + "left:2rem;");
//				break;
//			case TC:
//				ctrlMenu.setStyle(baseStyle + TopAdminMenu._TOP1 + TopAdminMenu._MARGIN_H_CENTER);
//				break;
//			case TR:
//				ctrlMenu.setStyle(baseStyle + TopAdminMenu._TOP1 + "right:2rem;");
//				break;
//
//			case BR:
//				ctrlMenu.setStyle(baseStyle + TopAdminMenu._BOTTOM0 + "right:2rem;");
//				break;
//			case BC:
//				ctrlMenu.setStyle(baseStyle + TopAdminMenu._BOTTOM0 + TopAdminMenu._MARGIN_H_CENTER);
//				break;
//			case BL:
//				ctrlMenu.setStyle(baseStyle + TopAdminMenu._BOTTOM0 + "left:2rem;");
//				break;
//
//			default:
//				throw new NI(tmPos);
//		}
//	}
//
//	Pos6TRBL_H getCurrentPosition() {
//		return getState().readAs(TopAdminMenu.TM_POS, Pos6TRBL_H.class, Pos6TRBL_H.TC);
//	}
//
//	@Override
//	protected void init() {
//
//		applyStyle(this, getCurrentPosition());
//
//		appendChild(new EnumSwitcher<Pos6TRBL_H>(Pos6TRBL_H.class) {
//			@Override
//			protected void applyPosition(Pos6TRBL_H typeValue) {
//				applyStyle(QuickCmdRunner.this, typeValue, true);
//			}
//		});
//
//		Combobox cb = new Combobox();
//
//		cb.setAutocomplete(true);
//		cb.setAutodrop(true);
//
//		fillAutocompleteList(null).forEach(i -> cb.appendItem(i));
//
//		cb.addEventListener(Events.ON_OK, (SerializableEventListener<Event>) event -> {
//			try {
//				onEventCmd(cb.getValue());
//			} catch (Throwable ex) {
//				ZKI_Window.errorIQ(ex);
//			}
//		});
//		cb.addEventListener(Events.ON_CHANGING, (SerializableEventListener<Event>) event -> {
//			String org = ((InputEvent) event).getValue();
//			Collection<String> atList = fillAutocompleteList(org);
//			List<Comboitem> children = cb.getChildren();
//			children.clear();
//			atList.forEach(i -> cb.appendItem(i));
//		});
////		setStyle("position:fixed;bottom:15px;z-index:1000;");
////		cb.setWidth("600px");
//
//		QuickCmdRunner.this.appendChild(cb);
//	}
//
//
//	private static Collection<String> fillAutocompleteList(String value) {
//		Collection<String> vls = null;
//		if (X.notEmpty(value)) {
//			Map<String, ?> cmd = TRM.cmds(USToken.first(value, " ", value), null);
//			if (cmd != null) {
//				vls = cmd.keySet();
//			}
//		}
//		if (vls == null) {
//			vls = TRM.trms_keys();
//		}
//		return vls;
//	}
//
//	/**
//	 * *************************************************************
//	 * --------------------------- HANDLE --------------------------
//	 * *************************************************************
//	 */
////	public class IdGrid extends Grid implements IdSpace {
//	//no method implementation required
////	}
//	@SneakyThrows
//	private void onEventCmd(String cmd){
//
//		if (AppCmdUI.execute(cmd)) {
//			return;
//		}
//
//		TrmRsp rsp = TRM.executeCmd(null, TrmRq.fromWeb(cmd));
//		TrmRsp.Status status = rsp.status();
//		if (L.isInfoEnabled()) {
//			String msg = X.fl("Runned ({}) >> {}", status, cmd);
//			L.info(msg, rsp);
//		} else if (rsp.isFail()) {
//			if (L.isErrorEnabled()) {
//				String msg = X.fl("Runned ({}) >> {}", status, cmd);
//				L.error(msg, rsp);
//			}
//		}
//		String str = TrmRspStr.toFull_Report(rsp, 0).toString();
//		ZKI_Log.status(status, str);
//
//	}
//
//}
