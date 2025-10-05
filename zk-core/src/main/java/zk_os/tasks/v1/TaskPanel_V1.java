package zk_os.tasks.v1;

import lombok.SneakyThrows;
import mp.utl_odb.query_core.QP;
import mp.utl_odb.tree.ctxdb.Ctx3Db;
import mp.utl_odb.tree.UTree;
import mpc.env.APP;
import mpc.exception.FIllegalStateException;
import mpc.log.LogTailReaderThread0;
import mpe.rt.Thread0;
import mpu.X;
import mpu.func.FunctionV1;
import mpu.str.JOIN;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Lb;
import zk_com.base_ctr.Div0;
import zk_com.core.IReRender;
import zk_notes.AppNotesProps;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.TrackMap;
import zk_os.quartz.QzApiEE;
import zk_page.ZKColor;
import zk_page.ZKJS;
import zk_page.ZKS;

import java.util.List;


public class TaskPanel_V1 extends Div0 implements IReRender {

	public static final String RUNNED = "RUNNED";
	public static final String NOALIVE = "NOALIVE";

	public static final Integer[] AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND = new Integer[]{0, 10, 10, 25, 10, 0};

	public static UTree dbt() {
		return (UTree) UTree.tree(APP.TREE_GND_TASKS_V1()).withAutoCleanCfg(AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND);
	}

	public static UTree dbd() {
		return (UTree) UTree.tree(APP.TREE_GNDD_V1()).withAutoCleanCfg(AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND);
	}

	//
	//

	public static void addTask(Thread thread, TrackMap.TrackId track) {
		dbt().put(Thread0.getNameWithId(thread), track.nodeData.nodeDir.nodeId(), RUNNED);
	}

	public static void stopTask(Thread thread) {
		dbd().removeByKeyIfExist(Thread0.getNameWithId(thread));
	}

	public static void addTaskD(NodeDir node, Thread thread) {
		if (!AppNotesProps.APP_TASKS_V1_PANEL_ENABLE.getValueOrDefault(false)) {
			return;
		}
		String nameId = Thread0.getNameWithId(thread);
		dbd().put(nameId, node.nodeId(), RUNNED);
	}

	public static void stopTaskLoggedD(Thread0 thread) {
		if (!AppNotesProps.APP_TASKS_V1_PANEL_ENABLE.getValueOrDefault(false)) {
			return;
		}
		String key = Thread0.getNameWithId(thread);

		Ctx3Db.CtxModelCtr model = dbd().getModelByKey(key);
		if (model == null) {
			L.error("Except model", new FIllegalStateException("not found task by:" + key));
			return;
		}

		String val = "stopped";
		if (thread instanceof LogTailReaderThread0) {
			val = JOIN.allByNL(((LogTailReaderThread0) thread).getLogTailReader().getCollector());
		}

		dbd().put(key, val, NOALIVE);

	}

	@Override
	protected void init() {
		super.init();

		RowTasks rowTasksNodes = new RowTasks(true);
//		RowTasks rowTasksDaemons = new RowTasks(false);

		{
			Div0 bottomDiv = Div0.of(rowTasksNodes);

			appendChild(bottomDiv);

//			ZKS.ABSOLUTE(bottomDiv);
			ZKS.FIXED(bottomDiv);
			ZKS.BOTTOM(bottomDiv, 0);
			ZKS.RIGHT(bottomDiv, "5pt");

		}

//		ZKS.BGCOLOR(this, UColorTheme.BLACK[0]);
//		ZKS.ABSOLUTE(this);
		ZKS.POSITION(this, "fixed");
		ZKS.HEIGHT_MIN(this, 50);
		//
		//

//		String f = ZKJS.js_addClass(selectorCom, IZComFadeIO.CLASS_NOIN) +//
//				ZKJS.js_setTimeout(ZKJS.func_zauSend(selectorCom, "onDoCycle", "{}"), 3000);
//		ZKJS.eval(f);
		String selectorCom = "#" + getUuid();
		ZKJS.bindJS(this, ZKJS.js_setInterval(ZKJS.func_zauSend(selectorCom, "onPulseCycle", "{}"), 6000));

		addEventListener("onPulseCycle", (SerializableEventListener<Event>) event -> {

			X.p("onPulseCycle:" + dbt().getCount() + "/" + dbd().getCount());

			rowTasksNodes.resetView();
//			rowTasksDaemons.reset();

		});


	}


	static class RowTasks extends Div0 {

		final boolean root;

		public RowTasks(boolean root) {
			super();
			this.root = root;
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

			if (root) {
				appendChild(new Lb("T:" + X.sizeOf(Thread.getAllStackTraces())));
				appendChild(new Lb("Q:" + X.sizeOf(QzApiEE.getAllJobKeys())));
			}

			showDBD_RUNNED(parent);

			showDBD_NOALIVE(parent);

		}

		private void showDBD_RUNNED(Div0 parent) {

			List<Ctx3Db.CtxModelCtr> models = dbd().getModels(QP.pEQ("ext", RUNNED));

			for (Ctx3Db.CtxModelCtr key : models) {
				parent.appendChild(new DaemonTask(key));
			}

		}


		private void showDBD_NOALIVE(Div0 parent) {

			List<Ctx3Db.CtxModelCtr> models = dbd().getModels(QP.pEQ("ext", NOALIVE));

			for (Ctx3Db.CtxModelCtr key : models) {

				parent.appendChild(new NoaliveTask(key));

			}
		}

	}

	static class NoaliveTask extends GndTaskItem_V1 {

		public NoaliveTask(Ctx3Db.CtxModelCtr model) {
			super(model, model.getKey());
		}

		@Override
		protected String getColor() {
			return ZKColor.BLUE.nextColor();
		}

		@Override
		public String getTitle() {
			return "noalive";
		}
	}

	static class DaemonTask extends GndTaskItem_V1 {
		public DaemonTask(Ctx3Db.CtxModelCtr model) {
			super(model, model.getKey());
		}

	}

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
