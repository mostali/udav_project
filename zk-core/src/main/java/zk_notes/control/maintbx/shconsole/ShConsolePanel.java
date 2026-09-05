package zk_notes.control.maintbx.shconsole;

import mp.utl_odb.netapp.AppCore;
import mp.utl_odb.tree.UTree;
import mp.utl_odb.tree.ctxdb.Ctx3Db;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpc.str.sym.SYMJ;
import mpe.str.StringWalkBuilder;
import mpu.SysExec;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.pare.Pare3;
import mpu.str.STR;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.Page;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IReRender;
import zk_com.core.IZDrop;
import zk_page.ZKC;
import zk_page.ZKCFinderExt;
import zk_page.ZKS;

import java.util.List;
import java.util.function.Supplier;

public class ShConsolePanel extends AbstractConsolePanel implements IReRender, IZDrop {

	private static final long serialVersionUID = 1L;

	//
	//

	public static ShConsolePanel findFirst(ShConsolePanel... defRq) {
		return ZKCFinderExt.findFirst_InPage(ShConsolePanel.class, true, defRq);
	}

	public static ShConsolePanel openSimple() {
		ShConsolePanel shConsole = new ShConsolePanel();
		ZKS.HEIGHT(shConsole, 600);
//		shConsole.setSizable(true);
//		shConsole.setMovable(true);
//		ZKS.DRAG_DROP(shConsole, true);
		ZKC.getFirstWindow().appendChild(shConsole);
		return shConsole;
	}

	public static ShConsolePanel rerenderFirst(ShConsolePanel... defRq) {
		ShConsolePanel first = removeMeFirst(defRq);
		if (first != null) {
			first.rerender(true);
		}
		return first;
	}

	public static ShConsolePanel removeMeFirst(ShConsolePanel... defRq) {
		return ZKC.removeMeFirst(ShConsolePanel.class, true, defRq);
	}


	private static @NotNull String wrapSpaceNl(String val, boolean... checkHas) {
		if (ARG.isDefEqTrue(checkHas)) {
			return ARR.contains(val, STR.NL) ? STR.NL + val + STR.NL : val;
		}
		return STR.NL + val + STR.NL;
	}

	@Override
	protected void onClickButton_HISTORY() {
		UTree tree = treeHist();
		StringWalkBuilder<Ctx3Db.CtxModelCtr> msgBld = StringWalkBuilder.of(m -> {
//			String val = m.getKey() + STR.NL + "rslt:" + STR.substrQk(m.getValue(), 50) + " " + getSpace();
			String val = wrapSpaceNl(m.getKey(), true);
			return val;
		});
		StringBuilder out = msgBld.buildSbAll(tree.getModels());
		setOut("found:" + tree.getCount(-1L) + "", out.toString());
	}

	@Override
	protected void fillHistoryMenu(Menupopup0 menu) {
		menu.addMI(SYMJ.CLEAR + " Clear history", e -> clear());
		menu.addMI(SYMJ.CLEAR + " Clear all, keep only uniq", e -> clearKeepOnlyUniq());
	}

	/**
	 * Run code
	 */
	@Override
	protected void onClickButton_RUN() {

		String cmd = getComIn().getText();

		Supplier onImpl = () -> SysExec.execByMode(cmd, SysExec.MODE1);

		Pare3<Integer, String, String> rslt = onAddHistory(cmd, onImpl);

		if (cmd == null || cmd.trim().isEmpty()) {
			return;
		}


		if (rslt == null) {
			return;
		}

		renderResultStdOut(rslt);

	}

	public static void clear() {
		treeHist().removeDb();
	}


	public static void clearKeepOnlyUniq() {
		UTree uTree = treeHist();
		List<Ctx3Db.CtxModelCtr> models = uTree.getModels();
		clear();
		models.forEach(m -> uTree.put(m.getKey(), m.getValue(), m.getExt()));
	}

	private static @NotNull Pare3<Integer, String, String> onAddHistory(String cmd, Supplier<Pare3<Integer, String, String>> onImpl) {
		UTree tree = treeHist();
		Pare3<Integer, String, String> rslt = onImpl.get();
		tree.put(cmd, rslt.val(), rslt.key() + (rslt.hasExt() ? "\n" + rslt.ext() : ""));
		return rslt;
	}

	private static UTree treeHist() {
		UTree tree = (UTree) AppCore.of().tree("history-shell").withUpdateMode(ICtxDb.UpdateMode.ADD);
		return tree;
	}


	private void renderResultStdIn(Pare3<Integer, String, String> rslt) {
		super.setOut(rslt.key() + "", rslt.val());
	}

	private void renderResultStdOut(Pare3<Integer, String, String> rslt) {

		if (!rslt.hasExt()) {
			//ok
			super.setOut(rslt.key() + "", rslt.val() + "");
			return;
		}

		super.setOut(rslt.key() + "", rslt.val() + wrapSpaceNl(rslt.ext()));

	}


}
