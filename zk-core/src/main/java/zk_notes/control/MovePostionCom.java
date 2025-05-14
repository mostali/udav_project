package zk_notes.control;

import lombok.RequiredArgsConstructor;
import mpc.exception.WhatIsTypeException;
import mpc.str.sym.SYMJ;
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
import zk_page.ZKNFinder;
import zk_page.ZKS;
import zk_notes.node.NodeDir;
import zk_notes.node_state.FormState;
import zk_pages.PrettyCodeXml;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class MovePostionCom extends Div0 {
	final NodeDir nodeDir;

	public static void show(NodeDir nodeDir) {
		Window window = new MovePostionCom(nodeDir)._showInWindow();
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

	@Override
	protected void init() {
		super.init();
//		Window.Mode popup = Window.Mode.POPUP;
		Window.Mode popup = Window.Mode.OVERLAPPED;
		_sizable()._modal(popup)._closable()._pos(WinPos.center)._sizable();
		Supplier<String> getLabel = () -> SYMJ.STAR_MOVED + " Move " + (isNodeForm.get() ? "Form" : "Com");
		Lb move = new Lb(getLabel.get());
		Cb cbIsForm = new Cb();
		tbxStep = (Tbx) new Tbx(stepPx + "").placeholder("step").width(50);
		cbIsForm.onCLICK(e -> {
			isNodeForm.set(!isNodeForm.get());
			move.setValue(getLabel.get());
		});
		_caption(Span0.of(move, cbIsForm, tbxStep));
		ZKS.WIDTH_HEIGHT(this, 220, 80);
		for (Pos value : Pos.values()) {
			appendChild(new Bt(value.nameEmoj()).onCLICK(e -> movePostition(value)));
		}
	}

	private void movePostition(Pos pos) {
		FormState state = nodeDir.state();
		boolean isForm = isNodeForm.get();
		if (!isForm) {
			state = state.stateCom();
		}
		FormState.Fields fields = state.fields();
		List<Component> allNotes = ZKNFinder.findAllNodeCom(nodeDir, isForm, isForm);
		int stepPx1 = UST.INT(tbxStep.getValue(), stepPx);
		switch (pos) {
			case UP:
			case DOWN: {
				int px = fields.get_TOP() + (pos == Pos.UP ? -stepPx1 : stepPx1);
				fields.set_TOP(px);
				allNotes.forEach(c -> {
					PrettyCodeXml com = PrettyCodeXml.findInChildren(c, null);
					(com != null ? com : (HtmlBasedComponent) c).setTop(px + ZKS.PX);
				});
				break;
			}
			case LEFT:
			case RIGHT: {
				int px = fields.get_LEFT() + (pos == Pos.LEFT ? -stepPx1 : stepPx1);
				fields.set_LEFT(px);
				allNotes.forEach(c -> {
					PrettyCodeXml com = PrettyCodeXml.findInChildren(c, null);
					(com != null ? com : (HtmlBasedComponent) c).setLeft(px + ZKS.PX);
				});
				break;
			}
			default:
				throw new WhatIsTypeException(pos);
		}
	}

}
