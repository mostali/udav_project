package zk_os.tasks;

import lombok.SneakyThrows;
import mp.utl_odb.query_core.QP;
import mp.utl_odb.tree.ctxdb.CKey;
import mp.utl_odb.tree.ctxdb.Ctx10Db;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpe.str.CN;
import mpu.SysThreads;
import mpu.X;
import mpu.core.ARR;
import mpu.core.QDate;
import mpu.func.FunctionV1;
import mpu.str.STR;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Lb;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IReRender;
import zk_form.notify.ZKI;
import zk_notes.AppNotesProps;
import zk_notes.events.AppEventsFD;
import zk_os.quartz.QzApiEE;
import zk_page.ZKC;
import zk_page.ZKJS;
import zk_page.ZKS;
import zk_page.panels.BottomHistoryPanel;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class TaskPanel extends Div0 implements IReRender {

	public TaskPanel() {
		super();

		dbAsync().checkLazyCreateDb();

	}

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

	public static void openSimple() {
		ZKC.getFirstWindow().appendChild(new TaskPanel());
	}

	public static TaskPanel removeMeFirst(TaskPanel... defRq) {
		return ZKC.removeMeFirst(TaskPanel.class, true, defRq);
	}

	public static TaskPanel findMeFirst(TaskPanel... defRq) {
		return ZKC.findMeFirst(TaskPanel.class, true, defRq);
	}

	public static void applyTaskPanelStyle(Div0 taskPanel) {

		ZKS.FIXED(taskPanel);
		ZKS.HEIGHT_MIN(taskPanel, 50);
		ZKS.WIDTH(taskPanel, 100.0);

		ZKS.BORDER(taskPanel, "1px dashed silver");

		ZKS.LEFT(taskPanel, "5pt");
		ZKS.BOTTOM(taskPanel, 0);

	}

	@Override
	protected void init() {
		super.init();

		TasksRow rowTasksNodes = new TasksRow(true);
//		RowTasks rowTasksDaemons = new RowTasks(false);

		{
			Div0 bottomDiv = Div0.of(rowTasksNodes);
			appendChild(bottomDiv);

//			ZKS.ABSOLUTE(bottomDiv);
//			ZKS.FIXED(bottomDiv);
		}

//		ZKS.BGCOLOR(this, UColorTheme.BLACK[0]);
//		ZKS.ABSOLUTE(this);
//		ZKS.RIGHT(this, "150pt");
//		ZKS.LEFT(this, "5pt");

		applyTaskPanelStyle(this);


		//
		//

//		String f = ZKJS.js_addClass(selectorCom, IZComFadeIO.CLASS_NOIN) +//
//				ZKJS.js_setTimeout(ZKJS.func_zauSend(selectorCom, "onDoCycle", "{}"), 3000);
//		ZKJS.eval(f);
		Integer updateEveryMs = AppNotesProps.APP_TASKS_PANEL_UPDATE_SEC.getValueOrDefault(-1) * 1000;
		if (updateEveryMs > 0) {

			String selectorCom = "#" + getUuid();
			ZKJS.bindJS(this, ZKJS.js_setInterval(ZKJS.func_zauSend(selectorCom, "onPulseCycle", "{}"), updateEveryMs));

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
//				appendChild((Component) new Lb("T*%s", X.sizeOf(Thread.getAllStackTraces())).title("Total Thread's"));
				appendChild((Component) new Lb("T*%s", STR.noNL(ARR.as(SysThreads.getAllThreadCount()).toString())).title("Total Thread's, Daemon's, Max)"));
				appendChild((Component) Lb.of("%sQ*%s", delPfx, X.sizeOf(QzApiEE.getAllJobKeys())).title("Quartz Jobs"));
				Lb lbDaemons = (Lb) Lb.of("%sD*%s", delPfx, dbAsync().getCount(0L)).title("Runned Task's");
				Lb child = (Lb) lbDaemons.onCLICK(e -> AppEventsFD.applyEvent_OPENTREE(e.getTarget(), dbAsync().getDbFilePath()));
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


}
