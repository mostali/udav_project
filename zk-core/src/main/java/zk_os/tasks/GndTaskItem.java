package zk_os.tasks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mp.utl_odb.tree.CtxtDb;
import mp.utl_odb.tree.UTree;
import mpc.env.APP;
import mpc.log.LogTailReaderThread0;
import mpc.str.sym.SYMJ;
import mpe.rt.Thread0;
import mpu.IT;
import mpu.Sys;
import mpu.X;
import mpu.core.ARR;
import mpu.str.JOIN;
import mpu.str.STR;
import mpu.str.TKN;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import zk_com.base.Bt;
import zk_com.base_ctr.Menupopup0;
import zk_com.base_ctr.Span0;
import zk_com.core.IZStyle;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Messagebox;
import zk_page.ZKColor;
import zk_page.ZKM_Editor;

import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class GndTaskItem extends Span0 {
	private final @Getter String name;
	private final String threadKey;

	private final @Getter String title;

	final CtxtDb.CtxTimeModel model;

	protected String getColor() {
		return ZKColor.YELLOW.nextColor();
	}

	public GndTaskItem(CtxtDb.CtxTimeModel model, String thread) {
		super();
		this.threadKey = thread;
		this.name = model.getKey();
		this.title = model.getValue();
		this.model = model;
	}

	@Override
	protected void init() {
		super.init();

		title(title);

		IZStyle bt = new Bt(SYMJ.THINK + STR.substr(name, 2), getColor(), ZKColor.BLACK.nextColor()).border_radius("10px");
		bt.title(name);
		appendChild((Component) bt);

		Menupopup0 menu = getOrCreateMenupopup((HtmlBasedComponent) getParent());
		Supplier<String> threadNameForRT = () -> TKN.firstGreedy(threadKey, "#", threadKey);
//		Supplier<String> threadNameOrig = () -> thread;

		Supplier<Boolean> threadActiveGet = () -> Sys.isThreadActive(threadNameForRT.get());

		Thread thread1 = Thread0.byName(threadNameForRT.get(), null);
		if (thread1 instanceof LogTailReaderThread0) {
			LogTailReaderThread0 logThread = (LogTailReaderThread0) thread1;
			menu.addMenuItem("Show current log", e -> {
				ZKM_Editor.openEditorText("StackTrace", JOIN.objsByNL(logThread.getLogTailReader().getCollector()));
			});
		} else {

			//wth why here thread instead thread.get()#
			CtxtDb.CtxTimeModel foundTask = UTree.tree(APP.GNDD_TREE()).getCtxTimeModelByKey(threadKey);

			if (foundTask != null) {
				menu.addMenuItem("Show persist data", e -> {
					ZKM_Editor.openEditorText("StackTrace", JOIN.objsByNL(foundTask.getKey(), "--", foundTask.getKey(), "--", foundTask.getExt(), "--", foundTask.getTimeAsQDate()));
				});

			}
		}

		if (threadActiveGet.get()) {

			menu.addMenuItem("Get StackTrace..", e -> {
				IT.state(threadActiveGet.get(), "thread is not active");
				Thread thread = Thread0.byName(threadNameForRT.get());
				StackTraceElement[] data = Thread.getAllStackTraces().get(thread);
				ZKM_Editor.openEditorText("StackTrace", JOIN.objsByNL(data));
			});

			menu.addMenuItem("Interrupt trhead..", e -> {
				ZKI_Messagebox.showMessageBoxBlueYN("Interrupt", "Interrupt", y -> {
					if (y) {
						IT.state(threadActiveGet.get(), "thread is not active");
						List<Thread> threads = Sys.getThreads(threadNameForRT.get(), ARR.EMPTY_LIST);
						threads.forEach(t -> t.interrupt());
						ZKI.infoAfterPointer("Interrupt:" + X.sizeOf(threads), ZKI.Level.INFO);
					}
				});
			});

		}

	}

}
