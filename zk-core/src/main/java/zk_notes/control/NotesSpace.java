package zk_notes.control;

import lombok.Setter;
import mpe.core.P;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.pare.Pare;
import mpu.str.Rt;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;
import zk_com.base_ctr.Div0D;
import zk_com.core.IReRender;
import zk_com.core.IZDnd;
import zk_form.control.*;
import zk_notes.coms.SeNoteTbxm;
import zk_notes.control.tabsmode.NotesSpaceTabsView;
import zk_os.AppZos;
import zk_os.sec.Sec;
import zk_page.core.SpVM;
import zk_notes.node.NodeDir;
import zk_page.ZKCFinder;

import java.util.*;

public class NotesSpace extends Div0D implements IReRender {

	final Window window;
	final Pare<String, String> sd3pn;
	final boolean isFirstRun;

	public static void initPage(Window window) {

		//
		// Head Resources

		SeNoteTbxm.registerHeadCom();

		if (Sec.isEditorAdminOwner()) {
			window.appendChild(new RightControlForm());
		}

		window.appendChild(new NotesSpace(window, SpVM.get().sdn0()));
	}

	@Override
	public Component newCom() {
		return new NotesSpace(window, sd3pn, false);
	}

	public NotesSpace(Window window, Pare<String, String> sd3pn) {
		this(window, sd3pn, true);
	}

	public NotesSpace(Window window, Pare<String, String> sd3pn, boolean isFirstRun) {
		super();
		this.window = window;
		this.sd3pn = sd3pn;
		this.isFirstRun = isFirstRun;
	}

	public static Component rerenderFirst() {
//		ZKC.printAll();
		NotesSpace notesSpace = ZKCFinder.rerenderFirst(NotesSpace.class);
		return notesSpace;
	}

	private @Setter boolean tabMode = false;

	@Override
	protected void initDND() {
		if (tabMode == false && getPageState().isAllowedAccess_Edit()) {
			IZDnd.initDND(window, ARG.ofNN(defaultAfterUpdateClb));
		}
	}


	@Override
	protected void init() {

		if (isFirstRun) {
			window.appendChild(new BreadDiv(sd3pn));
		}

		boolean tabModActive = "tabs".equals(getPageState().get("mode", null));

		if (tabModActive) {
			initTabs();
		} else {
			initNotTabs();
		}

	}


	private void initTabs() {
		super.init();

		Map<NodeDir, Component> comMap = NodeFactory.buildPageComMap(sd3pn, this, true);

		if (L.isInfoEnabled()) {
			L.info(Rt.buildReport(comMap, "ComMap[" + sd3pn.key() + "/" + sd3pn.val() + "]").toString());
		}

		List<NodeLn> nodeComs = ARR.asAL();

		comMap.entrySet().forEach(e -> {

			Component com = e.getValue();

			if (com == null) {
				//happens error in log
				return;
			} else if (com instanceof ErrLb) {
				if (AppZos.isDebugEnable()) {
					appendChild(com);
				} else {
					P.warnBig("ErrLb:" + com);
				}
				return;
			}

			if (com instanceof NodeLn) {
				nodeComs.add((NodeLn) com);
			}

		});

		appendChild(new NotesSpaceTabsView(nodeComs));


	}

	private void initNotTabs() {

		defaultAfterUpdateDragEventClb((e) -> {
//			NotesSpace.rerenderFirst();
		});

		super.init();

		Map<NodeDir, Component> comMap = NodeFactory.buildPageComMap(sd3pn, this, true);

		if (L.isInfoEnabled()) {
			L.info(Rt.buildReport(comMap, "ComMap[" + sd3pn.key() + "/" + sd3pn.val() + "]").toString());
		}

		comMap.entrySet().forEach(e -> {

			Component com = e.getValue();

			if (com == null) {
				//happens error in log
				return;
			} else if (com instanceof ErrLb) {
				if (AppZos.isDebugEnable()) {
					appendChild(com);
				} else {
					P.warnBig("ErrLb:" + com);
				}
				return;
			}

			if (!isFirstRun && com instanceof NodeLn) {
				((NodeLn) com).checkAndOpenIfStateOpened(false);
			}


			appendChild(com);

		});
	}


}
