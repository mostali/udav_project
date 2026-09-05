package zk_notes.control;

import lombok.RequiredArgsConstructor;
import mpc.exception.WhatIsTypeException;
import mpc.str.sym.SYMJ;
import mpu.IT;
import mpu.core.ARG;
import mpu.str.JOIN;
import mpu.str.UST;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Window;
import zk_com.base.Bt;
import zk_com.base.Cb;
import zk_com.base.Lb;
import zk_com.base.Tbx;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Span0;
import zk_com.win.WinPos;
import zk_notes.node_state.EntityState;
import zk_page.ZKNFinder;
import zk_page.ZKS;
import zk_notes.node.NodeDir;
import zk_notes.node_state.ObjState;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class MovePostionCom extends Div0 {
	final NodeDir nodeDir;
	final boolean onlyForm;

	public static void show(NodeDir nodeDir, boolean... onlyForm) {
		Window window = new MovePostionCom(nodeDir, ARG.isDefEqTrue(onlyForm))._showInWindow();
	}

	public static final int stepPx = 10;

	@RequiredArgsConstructor
	enum Pos {
		UP(SYMJ.ARROW_UP), DOWN(SYMJ.ARROW_DOWN), LEFT(SYMJ.ARROW_LEFT), RIGHT(SYMJ.ARROW_RIGHT);
		final String emoj;

		public String nameEmoj() {
			return emoj;
		}
	}

	private final AtomicBoolean isNodeForm = new AtomicBoolean(false);

	private Tbx tbxStep;

	private final Cb cbIsForm = (Cb) new Cb().title("Use for Form or NodeLink?");

	private final Cb cbIsWidth = (Cb) new Cb().moldToggle().title("Change Height/Width or Position?");

	@Override
	protected void init() {
		super.init();
		Window.Mode popup = Window.Mode.POPUP;
//		Window.Mode popup = Window.Mode.OVERLAPPED;

		_sizable()._modal(popup)._closable()._pos(WinPos.center)._sizable();

		if (onlyForm || !(Boolean) nodeDir.stateCom().getAs(EntityState.LINK_VISIBLE, Boolean.class, true)) {
			cbIsForm.setVisible(false);
			isNodeForm.set(true);
		}

		Supplier<String> getLabel = () -> SYMJ.STAR_MOVED + " Move " + (isNodeForm.get() ? "Form" : "Com");
		Lb move = new Lb(getLabel.get());

		tbxStep = (Tbx) new Tbx(stepPx + "").placeholder("step").width(50);
		cbIsForm.onCLICK(e -> {
			isNodeForm.set(!isNodeForm.get());
			move.setValue(getLabel.get());
		});
		_caption(Span0.of(move, cbIsForm, cbIsWidth, tbxStep));
		ZKS.WIDTH_HEIGHT(this, 220, 80);
		for (Pos value : Pos.values()) {
			appendChild(new Bt(value.nameEmoj()).onCLICK(e -> movePostition(value)));
		}
	}

	private void movePostition(Pos pos) {
		ObjState state = nodeDir.state();
		boolean isForm = isNodeForm.get();
		if (!isForm) {
			state = state.stateCom();
		}
		ObjState.Fields fields = state.fields();
		List<Component> allNotes = ZKNFinder.findAllNodeCom(nodeDir, isForm, isForm);
		int stepPx1 = UST.INT(tbxStep.getValue(), stepPx);
		if (L.isDebugEnabled()) {
			L.debug("Found all items for moving:\n" + JOIN.allByNL(allNotes));
		}
//		if(X.notEmpty(allNotes))

		boolean needWidth = cbIsWidth.isChecked();
		switch (pos) {
			case UP:
			case DOWN: {
				if (!needWidth) {
					int px = fields.get_TOP(0) + (pos == Pos.UP ? -stepPx1 : stepPx1);
					fields.set_TOP(px);
					allNotes.forEach(c -> ((HtmlBasedComponent) c).setTop(px + ZKS.PX));
				} else {
					int px = fields.get_HEIGHT(0) + (pos == Pos.UP ? -stepPx1 : stepPx1);
					IT.isPosOrZero(px);
					fields.set_HEIGHT(px);
					allNotes.forEach(c -> ((HtmlBasedComponent) c).setHeight(px + ZKS.PX));
				}
//				allNotes.get(0) (0 HtmlBasedComponent)c).setTop(px + ZKS.PX);
//				allNotes.forEach(c -> {
//					PrettyCodeXml com = PrettyCodeXml.findInChildren_PrettyCodeXml(c, null);
//					(com != null ? com : (HtmlBasedComponent) c).setTop(px + ZKS.PX);
//				});
				break;
			}
			case LEFT:
			case RIGHT: {
				if (!needWidth) {
					int px = fields.get_LEFT(0) + (pos == Pos.LEFT ? -stepPx1 : stepPx1);
					fields.set_LEFT(px);
					allNotes.forEach(c -> ((HtmlBasedComponent) c).setLeft(px + ZKS.PX));
				} else {
					int px = fields.get_WIDTH(0) + (pos == Pos.LEFT ? -stepPx1 : stepPx1);
					IT.isPosOrZero(px);
					fields.set_WIDTH(px);
					allNotes.forEach(c -> ((HtmlBasedComponent) c).setWidth(px + ZKS.PX));
				}
//				allNotes.forEach(c -> {
//					PrettyCodeXml com = PrettyCodeXml.findInChildren_PrettyCodeXml(c, null);
//					(com != null ? com : (HtmlBasedComponent) c).setLeft(px + ZKS.PX);
//				});
				break;
			}
			default:
				throw new WhatIsTypeException(pos);
		}
	}

}
