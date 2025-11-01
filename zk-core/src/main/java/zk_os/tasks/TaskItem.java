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
import zk_page.ZKColor;
import zk_page.ZKME;

@RequiredArgsConstructor
public class TaskItem extends Span0 {
	private final @Getter String name;

	final TaskItemModel itemModel;

	protected String getColor() {
		return ZKColor.YELLOW.nextColor();
	}

	public static Ctx10Db dbAsync() {
		return TaskManager.dbAsync();
	}

	public TaskItem(TaskItemModel model) {
		super();
		this.name = model.getName();
		this.itemModel = model;
	}

	@Override
	protected void init() {
		super.init();

		title(itemModel.model.getTimeAsQDate().f(FDate.YYYY_DB_ISO_STANDART));

		Bt bt = (Bt) new Bt(itemModel.getStatusEmoj() + STR.substrQk(name, 21), getColor(), ZKColor.BLACK.nextColor()).border_radius("10px");
		bt.title(name);

		appendChild(bt);

		Menupopup0 menu = getOrCreateMenupopup((HtmlBasedComponent) getParent());

		FunctionV showData = () -> {
			Boolean jobState = itemModel.getJobState();
			ZKME.textReadonly("Log..", JOIN.objsByNL(itemModel.getName(), "--", itemModel.model.getTimeAsQDate(), "--", jobState == null || jobState ? itemModel.getValue() : itemModel.getErrorValue()));
		};

		bt.onCLICK(e -> showData.apply());
		menu.addMI("Show log", e -> showData.apply());

		menu.add_______();

		TaskPanel.fillDbMenu(menu, (TaskPanel.TasksRow) getParent());

	}


}
