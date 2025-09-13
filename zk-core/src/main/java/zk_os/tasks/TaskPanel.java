package zk_os.tasks;

import lombok.SneakyThrows;
import mp.utl_odb.query_core.QP;
import mp.utl_odb.tree.ctxdb.CKey;
import mp.utl_odb.tree.ctxdb.Ctx10Db;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpe.str.CN;
import mpu.X;
import mpu.core.QDate;
import mpu.func.FunctionV1;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.impl.XulElement;
import zk_com.base.Lb;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IReRender;
import zk_form.notify.ZKI;
import zk_notes.events.AppEventsFD;
import zk_os.AppZosProps;
import zk_os.quartz.QzApiEE;
import zk_page.ZKC;
import zk_page.ZKJS;
import zk_page.ZKS;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class TaskPanel extends Div0 implements IReRender {

	public TaskPanel() {
		super();

		dbAsync().checkLazyCreateDb();

	}

//	public static final String RUNNED = "RUNNED";
//	public static final String NOALIVE = "NOALIVE";


	public static TaskItemModel addTaskAsync(String taskName) {
		Ctx10Db db = TaskManager.dbAsync();
		db.removeByKeyIfExist(taskName);
		Ctx10Db.CtxModel10 model = db.put(taskName, CKey.O10.of(TaskManager.TaskType.ASYNC.name()));
//		return CtxModelUpd.of(db, model);
		return TaskItemModel.of(model);
	}

	public static void fillDbMenu(Menupopup0 menu, TasksRow parent) {
		menu.addMI("Clear db", e -> {
			TaskItem.dbAsync().truncateTable();
			ZKI.infoAfterPointer("Db table was cleaned");
			parent.resetView();
		});
	}

//	public static UTree dbd() {
//		return (UTree) UTree.tree(APP.GNDD_TREE()).withAutoCleanCfg(AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND);
//	}

	//
	//

//	public static void addTask(Thread thread, TrackMap.TrackId track) {
//		dbt().put(Thread0.getNameWithId(thread), track.nodeData.nodeDir.nodeId(), RUNNED);
//	}

//	public static void stopTask(Thread thread) {
//		dbd().removeByKeyIfExist(Thread0.getNameWithId(thread));
//	}

//	public static void addTaskD(NodeDir node, Thread thread) {
//		if (!AppZosProps.APP_TASK_PANEL_ENABLE.getValueOrDefault(false)) {
//			return;
//		}
//		String nameId = Thread0.getNameWithId(thread);
//		dbd().put(nameId, node.nodeId(), RUNNED);
//	}

//	public static void stopTaskLoggedD(Thread0 thread) {
//		if (!AppZosProps.APP_TASK_PANEL_ENABLE.getValueOrDefault(false)) {
//			return;
//		}
//		String key = Thread0.getNameWithId(thread);
//
//		Ctx3Db.CtxModelCtr model = dbd().getModelByKey(key);
//		if (model == null) {
//			L.error("Except model", new FIllegalStateException("not found task by:" + key));
//			return;
//		}
//
//		String val = "stopped";
//		if (thread instanceof LogTailReaderThread0) {
//			val = JOIN.allByNL(((LogTailReaderThread0) thread).getLogTailReader().getCollector());
//		}
//
//		dbd().put(key, val, NOALIVE);
//
//	}

	@Override
	protected void init() {
		super.init();

		TasksRow rowTasksNodes = new TasksRow(true);
//		RowTasks rowTasksDaemons = new RowTasks(false);

		{
			Div0 bottomDiv = Div0.of(rowTasksNodes);

			appendChild(bottomDiv);

			ZKS.ABSOLUTE(bottomDiv);
			ZKS.FIXED(bottomDiv);
			ZKS.BOTTOM(bottomDiv, 0);
		}

//		ZKS.BGCOLOR(this, UColorTheme.BLACK[0]);
		ZKS.ABSOLUTE(this);
		ZKS.POSITION(this, "fixed");
		ZKS.HEIGHT_MIN(this, 50);
		//
		//

//		String f = ZKJS.js_addClass(selectorCom, IZComFadeIO.CLASS_NOIN) +//
//				ZKJS.js_setTimeout(ZKJS.func_zauSend(selectorCom, "onDoCycle", "{}"), 3000);
//		ZKJS.eval(f);
		Integer valueOrDefault = AppZosProps.APP_TASKS_PANEL_UPDATE_SEC.getValueOrDefault(-1);
		if (valueOrDefault > 0) {

			String selectorCom = "#" + getUuid();
			ZKJS.bindJS(this, ZKJS.js_setInterval(ZKJS.func_zauSend(selectorCom, "onPulseCycle", "{}"), (int) TimeUnit.SECONDS.toMillis(valueOrDefault)));

			addEventListener("onPulseCycle", (SerializableEventListener<Event>) event -> {

				X.p("onPulseCycle Task's:" + dbAsync().getCount() + "/" + dbAsync().getCount());

				rowTasksNodes.resetView();
//			rowTasksDaemons.reset();

			});
		}
	}

	private static ICtxDb<Ctx10Db.CtxModel10> dbAsync() {
		return TaskManager.dbAsync();
	}


	static class TasksRow extends Div0 {

		final boolean isRootRow;

		public TasksRow(boolean isRootRow) {
			super();
			this.isRootRow = isRootRow;
		}

		private FunctionV1<Div0> constructor;

		@Override
		protected void init() {
			super.init();

			constructor = this::fill;
			constructor.apply(this);

		}

		public void resetView() {
			constructor.apply(this);
		}

		@SneakyThrows
		private void fill(Div0 parent) {

			parent.getChildren().clear();

			if (isRootRow) {
//				String delPfx = STR.ARR_DEL_POINT;
				String delPfx = "/";
				appendChild(new Lb("%sT*%s", delPfx, X.sizeOf(Thread.getAllStackTraces())));
				appendChild(Lb.of("%sQ*%s", delPfx, X.sizeOf(QzApiEE.getAllJobKeys())));
				Lb child = (Lb) Lb.of("%sD*%s", delPfx, dbAsync().getCount(0L)).onCLICK(e -> AppEventsFD.applyEvent_OPENTREE(e.getTarget(), dbAsync().getDbFilePath()));
				fillDbMenu(child.getOrCreateMenupopup(ZKC.getFirstWindow()), TasksRow.this);
				appendChild(child);
			}

			showDBD_RUNNED(parent);

//			showDBD_NOALIVE(parent);

		}

		private void showDBD_RUNNED(Div0 parent) {

//			List<Ctx3Db.CtxModelCtr> models = db().getModels(QP.pEQ("ext", RUNNED));
			List<Ctx10Db.CtxModel10> models = dbAsync().getModels(QP.afterOrBeforeOrEq(true, CN.TIME, QDate.now().addMinutes(-10).ms()));

			for (Ctx10Db.CtxModel10 key : models) {
				parent.appendChild(new TaskItem(TaskItemModel.of(key)));
			}

		}


//		private void showDBD_NOALIVE(Div0 parent) {
//
//			List<Ctx3Db.CtxModelCtr> models = dbd().getModels(QP.pEQ("ext", NOALIVE));
//
//			for (Ctx3Db.CtxModelCtr key : models) {
//
//				parent.appendChild(new NoaliveTask(key));
//
//			}
//		}

	}

//	static class NoaliveTask extends GndTaskItem_V1 {
//
//		public NoaliveTask(Ctx3Db.CtxModelCtr model) {
//			super(model, model.getKey());
//		}
//
//		@Override
//		protected String getColor() {
//			return ZKColor.BLUE.nextColor();
//		}
//
//		@Override
//		public String getTitle() {
//			return "noalive";
//		}
//	}

//	static class DaemonTask extends TaskItem {
//		public DaemonTask(Ctx10Db.CtxModel10 model) {
//			super(model, model.getKey());
//		}
//	}

//	static class CallMsgTask extends GndTaskItem {
//
//		@Override
//		protected String getColor() {
//			return ZKColor.ORANGE.nextColor();
//		}
//
//		@Override
//		public String getTitle() {
//			return NodeID.of(model.getValue()).item();
//		}
//
//		public CallMsgTask(Ctx3Db.CtxModelCtr model) {
//			super(model, model.getKey());
//		}
//
//	}


}
