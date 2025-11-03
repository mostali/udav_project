package zk_notes.control.maintbx;

import mpc.env.APP;
import mpc.str.sym.SYMJ;
import mpc.types.tks.cmt.Cmd2;
import mpu.IT;
import mpu.X;
import mpu.core.ARRi;
import mpu.pare.Pare;
import mpu.str.RANDOM;
import mpu.str.TKN;
import org.zkoss.zul.Window;
import udav_net.apis.zznote.ItemPath;
import udav_net.apis.zznote.NoteApi;
import zk_com.base.Lb;
import zk_com.base.Xml;
import zk_com.core.IZCom;
import zk_form.dirview.DirView0;
import zk_form.notify.ZKI;
import zk_notes.control.maintbx.shconsole.ShConsolePanel;
import zk_notes.fsman.NodeFileTransferMan;
import zk_notes.node.NodeDir;
import zk_os.AppZosBashExecView;
import zk_os.AppZosCore;
import zk_os.core.Sdn;
import zk_os.sec.SecMan;
import zk_page.ZKC;
import zk_page.ZKR;
import zk_page.panels.BottomHistoryPanel;

import java.io.IOException;
import java.nio.file.Paths;

public class AppCmdView {

	public static void runMainCmd(Sdn sdn, String cmd, boolean shMode) throws IOException {

		if (AppCmdView.runAppCommand_NoSec(sdn, cmd)) {
			return;
		}

		if (!SecMan.isAdminOrOwner() && !SecMan.isAllowedEdit()) {
			return;
		}

		AppCmdView.runSec(sdn, cmd, shMode);
	}

	public static boolean runAppCommand(Sdn sdn, String cmd) throws IOException {
		if (run_openFs(cmd)) {
			return true;
		}
		if (run_AddNode_AsDldUrl(sdn, cmd)) {
			return true;
		}
		if (run_AddNode_AsString(cmd)) {
			return true;
		}
		if (run_MVU(cmd)) {
			return true;
		}
		if (run_MVD(cmd)) {
			return true;
		}
		return false;

	}

	public static boolean run_MVD(String cmd) {
		if (!cmd.startsWith("~mvd")) {
			return false;
		}
		cmd = cmd.substring(4).trim();
		if (X.empty(cmd)) {
			ZKI.infoEditorDark("~mvd /s/p/i zn:/s/p/i");
			return true;
		}

		runImplMVD(cmd);

		String infoMsg = X.f("Moved %s", cmd);
		IZCom.L.info(infoMsg);
		ZKI.showMsgBottomRightFast_INFO(infoMsg);
		ZKR.restartPage();

		return true;

	}

	public static boolean run_MVU(String cmd) throws IOException {

		if (!cmd.startsWith("~mvu")) {
			return false;
		}
		cmd = cmd.substring(4).trim();
		if (X.empty(cmd)) {
			ZKI.infoEditorDark("~mvu /s/p/i zn:/s/p/i");
			return true;
		}
		runImplMVU(cmd);

		String infoMsg = X.f("Moved %s", cmd);
		IZCom.L.info(infoMsg);
		ZKI.showMsgBottomRightFast_INFO(infoMsg);

		ZKR.restartPage();

		return true;
	}

	private static boolean run_AddNode_AsString(String cmd) {
		if (!cmd.startsWith("!")) {
			return false;
		}

		cmd = cmd.substring(1).trim();

		String[] two = TKN.twoQk(cmd, " ");
		String name = ARRi.first(two, 0, null);
		String cnt = ARRi.first(two, 1, null);

		//wth targ?
//					String[] indexPath = IndexTabsPSP.getIndexPathForQuery();
		Sdn targetSdn = null;
//					if (X.emptyAll(indexPath)) {
//						targetSdn = null;
//					} else if (indexPath[0] != null) {
//						targetSdn = Sdn.of(indexPath[0], indexPath[1]);
//					} else {
//						targetSdn = null;
//					}

		NodeFileTransferMan.addNewFormAndOpenUX(targetSdn, name, cnt);

		return true;
	}

	private static boolean run_AddNode_AsDldUrl(Sdn sdn, String cmd) throws IOException {
		if (UMainTbx.DldCallLine.isValid(cmd)) {

			Pare<NodeDir, Window> nodeDirWindowPare = NodeFileTransferMan.addNewRandomForm(sdn);
			if (nodeDirWindowPare != null) {
				UMainTbx.doDownloadToDir(cmd, sdn, nodeDirWindowPare.key().toPath());
				return true;
			}

		}
		return false;
	}

	private static boolean run_openFs(String cmd) {
		if (cmd.startsWith("fs ")) {
			String folder = cmd.substring(3);
			IT.isDirExist(folder);
			DirView0.openWithSimpleMenu(Paths.get(folder));
			return true;
		}
		return false;
	}

	//download
	public static void runImplMVD(String mvu_cmd) {
		Cmd2<Object, Object> c2 = Cmd2.of2(mvu_cmd).throwIsNotWhole();
		ItemPath src = ItemPath.of(c2.keyStr());
		String itemPathStr = c2.valStr();

		String hst = TKN.first(itemPathStr, ":");
		ItemPath targetItmPath = ItemPath.of(TKN.lastGreedy(itemPathStr, ":"));

		IT.state(src.mode == ItemPath.State.ALL);
		IT.state(targetItmPath.mode == ItemPath.State.ALL);

		NoteApi znApi = AppZosCore.createNoteApiByAlias(hst);

		String vl = znApi.GET_item(targetItmPath);
		NodeDir nodeDir = NodeDir.ofNodeName(src.sdn(), src.nodeName());
		nodeDir.state().writeFcDataWithClean(vl);
		if (IZCom.L.isInfoEnabled()) {
			IZCom.L.info("Mvd item with put to : " + mvu_cmd);
		}

		znApi.DELETE_item(targetItmPath);

	}

	//upload
	public static void runImplMVU(String mvu_cmd) throws IOException {
		Cmd2<Object, Object> c2 = Cmd2.of2(mvu_cmd).throwIsNotWhole();
		ItemPath src = ItemPath.of(c2.keyStr());
		String itemPathStr = c2.valStr();

		String hst = TKN.first(itemPathStr, ":");
		ItemPath dst = ItemPath.of(TKN.lastGreedy(itemPathStr, ":"));

		IT.state(src.mode == ItemPath.State.ALL);
		IT.state(dst.mode == ItemPath.State.ALL);

		NoteApi noteApi = AppZosCore.createNoteApiByAlias(hst);

		NodeDir nodeDir = NodeDir.ofNodeName(src.sdn(), src.nodeName());
//		IT.isFileExist(nodeDir.ex)
		String bodyLines = nodeDir.state().readFcData(null);
		IT.notEmpty(bodyLines, "empty item data %s", src);

//		bodyLines = bodyLines.replace("\n", "&nbsp;");
		noteApi.PUT_item(dst, bodyLines, true);
		if (IZCom.L.isInfoEnabled()) {
			IZCom.L.info("Mvu item with put to : " + mvu_cmd);
		}

		NodeFileTransferMan.deleteItem(nodeDir);

	}

	public static void runDELETE(String rm_cmd) throws IOException {

		String hst = TKN.first(rm_cmd, ":");
		ItemPath targetItemPath = ItemPath.of(TKN.lastGreedy(rm_cmd, ":"));

		IT.state(targetItemPath.mode == ItemPath.State.ALL);

		NoteApi noteApi = AppZosCore.createNoteApiByAlias(hst);

		noteApi.DELETE_item(targetItemPath);
		if (IZCom.L.isInfoEnabled()) {
			IZCom.L.info("Deleted item via : " + rm_cmd);
		}


	}

	static boolean runAppCommand_NoSec(Sdn sdn, String cmd) {

		switch (cmd) {

			case "?":
				ZKC.getFirstWindow().appendChild(Xml.ofRsrcXml(MainTbx.MANUAL_RCRS)._modal()._title(SYMJ.INFO_SIMPLE + " Help")._closable()._showInWindow());
				return true;

			case "+":
				String tgBotId = APP.getTgBotId(null);
				if (tgBotId != null) {
					ZKR.redirectToPage("https://t.me/" + tgBotId, true);
				}
				return true;

			case "++":
				Integer vkBotId = APP.getVkBotId(null);
				if (vkBotId != null) {
					String address = "https://vk.com/club" + Math.abs(vkBotId);
					ZKR.openWindow800_1200(address);
//					ZKR.redirectToPage(address, true);
				}
				return true;

		}

		if (cmd.startsWith("#") && cmd.length() > 4) {

			String topPos = RANDOM.range(10, 60) + "%";
			String leftPos = RANDOM.range(10, 60) + "%";
			String targetStyle = X.f_("display:block;width:30%;height:30%;position:absolute;top:%s;left:%s;z-index:10000;background-color:%s", topPos, leftPos, cmd);
			Lb lb = (Lb) Lb.of(cmd).addSTYLE(targetStyle);
			lb.onDBLCLICK(e -> {
				Thread.sleep(3000);
				e.getTarget().detach();
			});
			ZKC.getFirstWindow().appendChild(lb);

			return true;

		}

		return false;
	}

	static boolean runSec(Sdn sdn, String cmd, boolean shMode) throws IOException {

		if (runShellConsole(sdn, cmd)) {
			return true;
		}

		if (shMode) {
			runShellCommand(sdn, cmd);
			return true;

		}

		return runAppCommand(sdn, cmd);

	}

	static boolean runShellConsole(Sdn sdn, String cmd) {

		switch (cmd) {

			case "~~": {
				ShConsolePanel.openSimple();
				return true;
			}

			case "~~clear": {
				ShConsolePanel.clear();
				return true;
			}

			case MainTbx.MAIN_TBX_MODE_SH: {
				MainTbx.findFirst().applyMode(true);
				return true;
			}

			case "!~": { //off sh mode
				MainTbx.findFirst().applyMode(false);
				return true;
			}

			default:
				return false;
		}

	}

	static void runShellCommand(Sdn sdn, String cmd) {
		String apply = (String) AppZosBashExecView.funcExecCmdAndShowResult.apply(cmd);
		BottomHistoryPanel.addItemAsData(apply, false);
	}
}
