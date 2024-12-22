package zk_notes.control;

import mpe.core.P;
import mpu.core.ARG;
import mpu.pare.Pare;
import mpu.str.Rt;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;
import zk_com.base_ctr.Div0D;
import zk_com.core.IReRender;
import zk_com.core.IZDnd;
import zk_form.control.*;
import zk_notes.coms.SeNoteTbxm;
import zk_os.sec.Sec;
import zk_page.core.SpVM;
import zk_page.node.NodeDir;
import zk_page.ZKCFinder;
import zk_old_core.coms.RightControlForm;

import java.util.Map;

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

		window.appendChild(new NotesSpace(window, SpVM.get().sdn()));
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
		return ZKCFinder.rerenderFirst(NotesSpace.class);
	}

	@Override
	protected void initDND() {
		if (Sec.isEditorAdminOwner()) {
			IZDnd.initDND(window, ARG.ofNN(defaultAfterUpdateClb));
		}
	}


	@Override
	protected void init() {

		if (isFirstRun) {
			window.appendChild(new BreadDiv(sd3pn));
		}

//		defaultAfterUpdateDragEventClb((e) -> {
//			NotesSpace.rerenderFirst();
//		});

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
				P.warnBig("ErrLb:" + com);
				return;
			}

			if (!isFirstRun && com instanceof NodeLn) {
				((NodeLn) com).checkAndOpenIfStateOpened(false);
			}

			appendChild(com);

		});

	}


}
