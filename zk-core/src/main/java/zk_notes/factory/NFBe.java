package zk_notes.factory;

import mpc.exception.WhatIsTypeException;
import mpu.core.ARG;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.Window;
import zk_com.core.IZCom;
import zk_notes.control.RightControlPanel;
import zk_notes.node_state.FormState;
import zk_page.ZKS;
import zk_page.events.ECtrl;

//dnd + behaviours
public class NFBe {

	public static void applyDndPosWhBehaviours(IZCom com, boolean enableDndEvent, boolean absolute, boolean applyRandOrTopLeft) {
		applyDndPosWhBehaviours(com, com.getFormName(), enableDndEvent, absolute, applyRandOrTopLeft);
	}

	public static void applyDndPosWhBehaviours(IZCom com, String name, boolean enableDndEvent, boolean absolute, boolean applyRandOrTopLeft, boolean... isPageState) {
		if (absolute) {
			com.absolute();
		}
		if (enableDndEvent) {
			ZKS.DRAG_DROP((HtmlBasedComponent) com, true, true);
		}
		if (applyRandOrTopLeft) {
			FormState formStateProps = ARG.isDefEqTrue(isPageState) ? com.getPageComState_JSON(name, false) : com.getFormState_PROPS(name, false);
			applyState_RandomOrTopLeft((HtmlBasedComponent) com, formStateProps);
		}
	}

	public static IZCom applyState_RandomOrTopLeft_ForNode(IZCom izCom, FormState... comState) {
		applyState_RandomOrTopLeft((HtmlBasedComponent) izCom, comState.length == 1 ? comState[0] : izCom.getComStateDefault());
		return izCom;
	}

	public static void applyState_RandomOrTopLeft(HtmlBasedComponent com, FormState comState) {
		if (!comState.apply_TOP_LEFT(com)) {
			ZKS.APPLY_RANDOM_TOPLEFT(com);
		}
	}

	public static void apply_BgColor(HtmlBasedComponent com, FormState formState) {
		String bgColor = formState.get(FormState.BG_COLOR, null);
		if (bgColor != null) {
			ZKS.BGCOLOR(com, bgColor);
		}
	}

	public static void apply_TopLeft_WidthHeigth_Bgc_Titles(HtmlBasedComponent comTextWin, //
															HtmlBasedComponent comTextNoteTbxm_OrComTextWin, //
															FormState stateForm, //
															boolean skipAbsolutePositionProps) { //
		if (!stateForm.existPropsFile()) {
			return;
		}
		if (!skipAbsolutePositionProps && !stateForm.apply_TOP_LEFT(comTextWin)) {
			ZKS.TOP_LEFT(comTextWin, 30.0, 30.0);
		}

		boolean fixedOrTrue = stateForm.fields().get_FIXED(true);
		if (fixedOrTrue) {
			if (!stateForm.apply_WIDTH_HEIGHT(comTextNoteTbxm_OrComTextWin)) {
				//nothing, already init
			}
		}

		stateForm.apply_TITLE(comTextNoteTbxm_OrComTextWin);
		stateForm.apply_TITLEX(comTextNoteTbxm_OrComTextWin);

		apply_BgColor(comTextWin, stateForm);

	}

	public static void addEventListenerMoveAndResize(HtmlBasedComponent comTextWin, HtmlBasedComponent resizableCom_comTextNoteTbxm_OrComTextWin, FormState state) {
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
				RightControlPanel.light("Size updated " + state.formName());
			}
		};
		resizableCom_comTextNoteTbxm_OrComTextWin.addEventListener(resizableIsWin ? Events.ON_SIZE : Events.ON_DOUBLE_CLICK, updateEvent);
		comTextWin.addEventListener(Events.ON_MOVE, updateEvent);
	}

	public static void updateMoveEvent(Event e, FormState fCom, Component... parent) {
//		MoveEvent me = (MoveEvent) e;
		//					if (me.getKeys() != 258) {
		//						return;
		//					}
		fCom.setFromCom_TOP_LEFT((HtmlBasedComponent) ARG.toDefOr(e.getTarget(), parent));
	}

	public static void updateResizableEvent(Event e, FormState fCom) {

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
