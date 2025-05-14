package zk_os.tasks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mp.utl_odb.tree.ctxdb.Ctx10Db;
import mpe.ftypes.core.FDate;
import mpu.func.FunctionV;
import mpu.str.JOIN;
import mpu.str.STR;
import org.zkoss.zk.ui.HtmlBasedComponent;
import zk_com.base.Bt;
import zk_com.base_ctr.Menupopup0;
import zk_com.base_ctr.Span0;
import zk_form.notify.ZKI;
import zk_page.ZKColor;
import zk_page.ZKM_Editor;

@RequiredArgsConstructor
public class TaskItem extends Span0 {
	private final @Getter String name;
//	private final String threadKey;

//	private final @Getter String title;

	final TaskItemModel itemModel;

	protected String getColor() {
		return ZKColor.YELLOW.nextColor();
	}

	public static Ctx10Db dbAsync() {
		return TaskManager.dbAsync();
	}

	public TaskItem(TaskItemModel model) {
		super();
//		this.threadKey = thread;
		this.name = model.getName();
//		this.title = model.getValue();
		this.itemModel = model;
	}

	@Override
	protected void init() {
		super.init();

		title(itemModel.model.getTimeAsQDate().f(FDate.YYYY_DB_ISO_STANDART));

		Bt bt = (Bt) new Bt(itemModel.getStatusEmoj() + STR.substr(name, 21), getColor(), ZKColor.BLACK.nextColor()).border_radius("10px");
		bt.title(name);

		appendChild(bt);

		Menupopup0 menu = getOrCreateMenupopup((HtmlBasedComponent) getParent());
//		Supplier<String> threadNameForRT = () -> TKN.firstGreedy(threadKey, "#", threadKey);
//		Supplier<String> threadNameOrig = () -> thread;

//		Supplier<Boolean> threadActiveGet = () -> Sys.isThreadActive(threadNameForRT.get());

//		Thread thread1 = Thread0.byName(threadNameForRT.get(), null);
//		if (thread1 instanceof LogTailReaderThread0) {
//			LogTailReaderThread0 logThread = (LogTailReaderThread0) thread1;
//			menu.addMenuItem("Show current log", e -> {
//				ZKM_Editor.openEditorText("StackTrace", JOIN.objsByNL(logThread.getLogTailReader().getCollector()));
//			});
//		} else {

		//wth why here thread instead thread.get()#

//		Ctx10Db.CtxModel10 foundTask = dbAsync().getModelByKey(threadKey);

//		TaskItemModel.load(threadKey, null);

		FunctionV showData = () -> {
			Boolean jobState = itemModel.getJobState();
			ZKM_Editor.openEditorText("Log..", JOIN.objsByNL(itemModel.getName(), "--", itemModel.model.getTimeAsQDate(), "--", jobState == null || jobState ? itemModel.getValue() : itemModel.getErrorValue()));
		};

//		if (foundTask != null) {
		bt.onCLICK(e -> showData.apply());
		menu.addMI("Show log", e -> showData.apply());
//		} else {
//			ZKI.alert("Except task item '%s'", foundTask.getKey());
//		}

		menu.add_______();

		TaskPanel.fillDbMenu(menu, (TaskPanel.TasksRow) getParent());
//		}

//		if (threadActiveGet.get()) {
//
//			menu.addMenuItem("Get StackTrace..", e -> {
//				IT.state(threadActiveGet.get(), "thread is not active");
//				Thread thread = Thread0.byName(threadNameForRT.get());
//				StackTraceElement[] data = Thread.getAllStackTraces().get(thread);
//				ZKM_Editor.openEditorText("StackTrace", JOIN.objsByNL(data));
//			});
//
//			menu.addMenuItem("Interrupt trhead..", e -> {
//				ZKI_Messagebox.showMessageBoxBlueYN("Interrupt", "Interrupt", y -> {
//					if (y) {
//						IT.state(threadActiveGet.get(), "thread is not active");
//						List<Thread> threads = Sys.getThreads(threadNameForRT.get(), ARR.EMPTY_LIST);
//						threads.forEach(t -> t.interrupt());
//						ZKI.infoAfterPointer("Interrupt:" + X.sizeOf(threads), ZKI.Level.INFO);
//					}
//				});
//			});
//
//		}

	}


}
