package zk_os.tasks.v1;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mp.utl_odb.tree.ctxdb.Ctx3Db;
import mp.utl_odb.tree.UTree;
import mpc.env.APP;
import mpc.log.LogTailReaderThread0;
import mpc.str.sym.SYMJ;
import mpe.rt.Thread0;
import mpu.IT;
import mpu.SysThreads;
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
import zk_form.notify.ZKI_Quest;
import zk_page.ZKColor;
import zk_page.ZKME;

import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class GndTaskItem_V1 extends Span0 {
	private final @Getter String name;
	private final String threadKey;

	private final @Getter String title;

	final Ctx3Db.CtxModelCtr model;

	protected String getColor() {
		return ZKColor.YELLOW.nextColor();
	}

	public GndTaskItem_V1(Ctx3Db.CtxModelCtr model, String thread) {
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

		IZStyle bt = new Bt(SYMJ.THINK + STR.substrQk(name, 2), getColor(), ZKColor.BLACK.nextColor()).border_radius("10px");
		bt.title(name);
		appendChild((Component) bt);

		Menupopup0 menu = getOrCreateMenupopup((HtmlBasedComponent) getParent());
		Supplier<String> threadNameForRT = () -> TKN.firstGreedy(threadKey, "#", threadKey);
//		Supplier<String> threadNameOrig = () -> thread;

		Supplier<Boolean> threadActiveGet = () -> SysThreads.isThreadActive(threadNameForRT.get());

		Thread thread1 = Thread0.byName(threadNameForRT.get(), null);
		if (thread1 instanceof LogTailReaderThread0) {
			LogTailReaderThread0 logThread = (LogTailReaderThread0) thread1;
			menu.addMI("Show current log", e -> {
				ZKME.textReadonly("StackTrace", JOIN.objsByNL(logThread.getLogTailReader().getCollector()));
			});
		} else {

			//wth why here thread instead thread.get()#
			Ctx3Db.CtxModelCtr foundTask = UTree.tree(APP.TREE_GNDD_V1()).getModelByKey(threadKey);

			if (foundTask != null) {
				menu.addMI("Show persist data", e -> {
					ZKME.textReadonly("StackTrace", JOIN.objsByNL(foundTask.getKey(), "--", foundTask.getKey(), "--", foundTask.getExt(), "--", foundTask.getTimeAsQDate()));
				});

			}
		}

		if (threadActiveGet.get()) {

			menu.addMI("Get StackTrace..", e -> {
				IT.state(threadActiveGet.get(), "thread is not active");
				Thread thread = Thread0.byName(threadNameForRT.get());
				StackTraceElement[] data = Thread.getAllStackTraces().get(thread);
				ZKME.textReadonly("StackTrace", JOIN.objsByNL(data));
			});

			menu.addMI("Interrupt trhead..", e -> {
				ZKI_Quest.showMessageBoxBlueYN("Interrupt", "Interrupt", y -> {
					if (y) {
						IT.state(threadActiveGet.get(), "thread is not active");
						List<Thread> threads = SysThreads.getThreads(threadNameForRT.get(), ARR.EMPTY_LIST);
						threads.forEach(t -> t.interrupt());
						ZKI.infoAfterPointer("Interrupt:" + X.sizeOf(threads), ZKI.Level.INFO);
					}
				});
			});

		}

	}

}
