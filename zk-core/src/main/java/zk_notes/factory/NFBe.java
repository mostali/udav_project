package zk_notes.factory;

import mpc.exception.WhatIsTypeException;
import mpu.core.ARG;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.Window;
import zk_com.core.IZCom;
import zk_notes.control.RightSpacePanel;
import zk_notes.node_state.ObjState;
import zk_page.ZKS;
import zk_page.events.ECtrl;

//dnd + behaviours
public class NFBe {

//	public static void applyDnd_PosWh_Behaviours(IZCom com, boolean enableDndEvent, boolean absolute, boolean applyRandOrTopLeft) {
//		applyDnd_PosWh_Behaviours(com, com.getFormName(), enableDndEvent, absolute, applyRandOrTopLeft);
//	}

	public static void applyDnd_PosWh_Behaviours(IZCom com, String name, boolean enableDndEvent, boolean applyRandOrTopLeft, boolean... isPageComState) {
		if (enableDndEvent) {
			ZKS.DRAG_DROP((HtmlBasedComponent) com, true, true);
		}
		if (applyRandOrTopLeft) {
			ObjState formStateProps = ARG.isDefEqTrue(isPageComState) ? com.getPagecomState(name) : com.getFormState(name);
			NFStyle.applyState_RandomOrTopLeft((HtmlBasedComponent) com, formStateProps);
		}
	}

	public static void addEventListenerPersistMoveAndResize(HtmlBasedComponent comTextWin, HtmlBasedComponent resizableCom_comTextNoteTbxm_OrComTextWin, ObjState state) {
		Class aClass = resizableCom_comTextNoteTbxm_OrComTextWin.getClass();
		boolean resizableIsWin = resizableCom_comTextNoteTbxm_OrComTextWin instanceof Window;

		EventListener updateEvent = e -> {
			if (e instanceof MoveEvent) {
				updateMoveEvent(e, state, comTextWin);
			} else if (
//					!Events.ON_CLICK.equals(e.getName()) && //
					(resizableIsWin ?//
							e instanceof SizeEvent ://
							e instanceof MouseEvent && aClass.isAssignableFrom(e.getTarget().getClass())))//
			{
				updateResizableEvent(e, state);
				RightSpacePanel.light("Size updated " + state.objName());
			}
		};
		resizableCom_comTextNoteTbxm_OrComTextWin.addEventListener(resizableIsWin ? Events.ON_SIZE : Events.ON_DOUBLE_CLICK, updateEvent);
		comTextWin.addEventListener(Events.ON_MOVE, updateEvent);
	}

	public static void updateMoveEvent(Event e, ObjState fCom, Component... parent) {
//		MoveEvent me = (MoveEvent) e;
		//					if (me.getKeys() != 258) {
		//						return;
		//					}
		fCom.setFromCom_TOP_LEFT((HtmlBasedComponent) ARG.toDefOr(e.getTarget(), parent));
	}

	public static void updateResizableEvent(Event e, ObjState fCom) {

		String w;
		String h;

		if (e instanceof MouseEvent) {
			MouseEvent me = (MouseEvent) e;


			boolean isCtrl = me.getKeys() == ECtrl.ZKE_ALT_CODE;
			if (!isCtrl) {
				return;
			}

			//			int oldX = fCom.getPX("width", 0);
			//			int oldY = fCom.getPX("height", 0);

			int newX = me.getX();
			int newY = me.getY();

			//			boolean isBigDiff = oldX > 10 && oldX > 10 && Math.abs(oldX - newX) > 30 || Math.abs(oldY - newY) > 30;
			//			if (isBigDiff) {
			//				return;
			//			}
			w = newX + ZKS.PX;
			h = newY + ZKS.PX;
		} else if (e instanceof SizeEvent) {
			SizeEvent se = (SizeEvent) e;
//			boolean isCtrl = se.getKeys() == 258;
//			if (!isCtrl) {
//				return;
//			}
			w = se.getWidth();
			h = se.getHeight();
		} else {
			throw new WhatIsTypeException(e.getClass());
		}
		fCom.set("width", w);
		fCom.set("height", h);
	}

}
