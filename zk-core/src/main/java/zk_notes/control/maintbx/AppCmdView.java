package zk_notes.control.maintbx;

import mpc.env.APP;
import mpc.str.sym.SYMJ;
import mpc.types.tks.cmt.Cmd2;
import mpe.call_msg.core.NodeID;
import mpu.IT;
import mpu.X;
import mpu.core.ARRi;
import mpu.pare.Pare;
import mpu.str.RANDOM;
import mpu.str.TKN;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Window;
import udav_net.apis.zznote.ItemPath;
import udav_net.apis.zznote.NoteApi;
import zk_com.base.Lb;
import zk_com.base.Xml;
import zk_com.core.IZCom;
import zk_form.dirview.DirView0;
import zk_form.notify.ZKI;
import zk_notes.control.NotesPSP;
import zk_notes.control.maintbx.shconsole.ShConsolePanel;
import zk_notes.fsman.NodeFileTransferMan;
import zk_notes.node.NodeDir;
import zk_notes.node.core.NVT;
import zk_os.AppZosBashExecView;
import zk_os.AppZosCore;
import zk_os.core.Sdn;
import zk_os.sec.ROLE;
import zk_os.sec.SecMan;
import zk_page.ZKC;
import zk_page.ZKR;
import zk_page.panels.BottomHistoryPanel;
import zklogapp.logview.LogFileView;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class AppCmdView {

	@FunctionalInterface
	public interface AppCmd<R> {
		R run();

		default ROLE forRole() {
			return ROLE.OWNER;
		}

		// Статические фабричные методы с признаками
		static <R> AppCmd<R> ofNoSec(Supplier<R> supplier) {
			return new AppCmd<R>() {
				@Override
				public R run() {
					return supplier.get();
				}

				@Override
				public ROLE forRole() {
					return ROLE.ANONIM;
				}
			};
		}

		static <R> AppCmd<R> forOwnerOrAdmin(Supplier<R> supplier) {
			return new AppCmd<R>() {
				@Override
				public R run() {
					return supplier.get();
				}

				@Override
				public ROLE forRole() {
					return ROLE.ADMIN;
				}
			};
		}

		static <R> AppCmd<R> forEditor(Supplier<R> supplier) {
			return new AppCmd<R>() {
				@Override
				public R run() {
					return supplier.get();
				}

				@Override
				public ROLE forRole() {
					return ROLE.EDITOR;
				}
			};
		}

		default boolean isAllowed(Sdn sdn) {
			ROLE role = forRole();
			switch (role) {
				case OWNER:
					return SecMan.isOwner();
				case ADMIN:
					return SecMan.isOwnerOrAdmin();
				case EDITOR:
					return SecMan.isAllowedEditPlane(sdn);
				case RUNNER:
					return false;
//					return SecMan2.isAllowedNode_FORM_EDIT(sdn);
				case ANONIM:
					return false;


			}
			return false;
		}
	}

	public static void runMainCmd(Event e, Sdn sdn, String cmd, boolean shMode) throws IOException {

		AppCmd appCmd = AppCmdView.getAppCommand_NoSec(sdn, cmd);

		if (appCmd != null) {
			appCmd.run();
			return;
		}

		//find cmd

		appCmd = getAppCmd_OpenShellConsole(sdn, cmd);

		if (appCmd == null) {
			if (shMode) {
				appCmd = getShellSingleCommand(sdn, cmd);
			}
		}

		if (appCmd == null) {
			appCmd = getAppCommand(sdn, cmd);
		}

		if (appCmd == null) {

			return;
		}

		if (!appCmd.isAllowed(sdn)) {
			return;
		}
		appCmd.run();

	}

	public static AppCmd getAppCommand(Sdn sdn, String cmd) throws IOException {
		AppCmd appCmd = run_openFs(cmd);
		if (appCmd != null) {
			return appCmd;
		}
		appCmd = run_AddNode_AsDldLogfileUrl(sdn, cmd);
		if (appCmd != null) {
			return appCmd;
		}
		appCmd = run_AddNode_AsDldDataUrl(sdn, cmd);
		if (appCmd != null) {
			return appCmd;
		}
		appCmd = run_AddNode_AsString(cmd);
		if (appCmd != null) {
			return appCmd;
		}
		appCmd = run_MVU(cmd);
		if (appCmd != null) {
			return appCmd;
		}
		appCmd = run_MVD(cmd);
		if (appCmd != null) {
			return appCmd;
		}

		appCmd = run_OpenManualAdmin(cmd);
		if (appCmd != null) {
			return appCmd;
		}

		return null;

	}

	public static AppCmd run_MVD(String cmd0) {
		if (!cmd0.startsWith("~mvd")) {
			return null;
		}
		return AppCmd.forOwnerOrAdmin(() -> {
			String cmd = cmd0.substring(4).trim();
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
		});
	}

	public static AppCmd run_MVU(String cmd0) throws IOException {
		if (!cmd0.startsWith("~mvu")) {
			return null;
		}
		return AppCmd.forOwnerOrAdmin(() -> {
			String cmd = cmd0.substring(4).trim();
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
		});
	}

	private static AppCmd run_AddNode_AsString(String cmd0) {
		if (!cmd0.startsWith("!")) {
			return null;
		}
		return AppCmd.forEditor(() -> {
			String cmd = cmd0.substring(1).trim();

			String[] two = TKN.twoQk(cmd, " ");
			String name = ARRi.first(two, 0, null);
			String cnt = ARRi.first(two, 1, null);

			Sdn targetSdn = null;

			NodeFileTransferMan.addNewFormAndOpenUX(targetSdn, name, cnt);

			return true;
		});
	}

	private static AppCmd run_AddNode_AsDldDataUrl(Sdn sdn, String cmd) throws IOException {
		if (!DldUrlCCL.isValid(cmd)) {
			return null;
		}
		return AppCmd.forOwnerOrAdmin(() -> {
			NodeID iip = (NodeID) ZKC.getFirstWindow().getAttribute(NotesPSP.INDEX_ITEM_PAGE_NODE);
			if (iip != null) {
				Path targetFile = DldUrlCCL.doDownloadToDir(cmd, sdn, NodeDir.ofNodeId(iip).getSelfDir());
				ZKI.infoAfterPointerInfo("Download to node '%s' as file '%s", iip.nodeName(), targetFile.getFileName());
//				ZKR.restartPage();
				return true;
			}
			Pare<NodeDir, Window> nodeDirWindowPare = NodeFileTransferMan.addNewRandomForm(sdn);
			if (nodeDirWindowPare != null) {
				NodeDir node = nodeDirWindowPare.key();
				Path targetFile = DldUrlCCL.doDownloadToDir(cmd, sdn, node.toPath());
				ZKI.infoAfterPointerInfo("Download to NEW node '%s' as file '%s'", node.nodeName(), targetFile.getFileName());
//				ZKR.restartPage();
				return true;
			}
			return false;
		});
	}

	private static AppCmd run_AddNode_AsDldLogfileUrl(Sdn sdn, String cmd) throws IOException {
		if (!DldLogCCL.isValid(cmd)) {
			return null;
		}
		return AppCmd.forOwnerOrAdmin(() -> {

			NodeID iip = (NodeID) ZKC.getFirstWindow().getAttribute(NotesPSP.INDEX_ITEM_PAGE_NODE);
			if (iip != null) {
				Path targetFile = DldLogCCL.doDownloadToDir(cmd, sdn, NodeDir.ofNodeId(iip).getSelfDir());
				LogFileView.openSingly(targetFile.toString());
				ZKI.infoAfterPointerInfo("Download LOG to node '%s' as file '%s", iip.nodeName(), targetFile.getFileName());
				return true;
			}

			NodeFileTransferMan.AddNewForm.OptsAdd opts = NodeFileTransferMan.AddNewForm.OptsAdd.newOpts();
			opts.setNodeViewType(NVT.DIR);
			Pare<NodeDir, Window> nodeDirWindowPare = NodeFileTransferMan.addNewRandomForm(sdn, opts);
			if (nodeDirWindowPare != null) {
				NodeDir node = nodeDirWindowPare.key();
				Path targetFile = DldLogCCL.doDownloadToDir(cmd, sdn, node.toPath());
				ZKI.infoAfterPointerInfo("Download LOG to NEW node '%s' as file '%s'", node.nodeName(), targetFile.getFileName());
				LogFileView.openSingly(targetFile.toString());
				return true;
			}
			return false;
		});
	}

	private static AppCmd run_openFs(String cmd) {
		if (cmd.startsWith("fs ")) {
			return AppCmd.forOwnerOrAdmin(() -> {
				String folder = cmd.substring(3);
				IT.isDirExist(folder);
				DirView0.openWithSimpleMenu(Paths.get(folder));
				return true;
			});
		}
		return null;
	}

	private static AppCmd run_OpenManualAdmin(String cmd) {
		if (cmd.equals("??")) {
			return AppCmd.forOwnerOrAdmin(() -> {
				ZKC.getFirstWindow().appendChild(Xml.ofRsrcXml(MainTbx.MANUAL_RCRS_ADMIN)._modal()._title(SYMJ.INFO_SIMPLE + " Help for admin")._closable()._showInWindow());
				return true;
			});
		}
		return null;
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
	public static void runImplMVU(String mvu_cmd) {
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

	static AppCmd getAppCommand_NoSec(Sdn sdn, String cmd) {

		switch (cmd) {

			case "?":
				return () -> ZKC.getFirstWindow().appendChild(Xml.ofRsrcXml(MainTbx.MANUAL_RCRS)._modal()._title(SYMJ.INFO_SIMPLE + " Help")._closable()._showInWindow());

			case "+":
				return () -> {
					String tgBotId = APP.getTgBotId(null);
					if (tgBotId != null) {
						ZKR.redirectToPage("https://t.me/" + tgBotId, true);
					}
					return true;
				};

			case "++":
				return () -> {
					Integer vkBotId = APP.getVkBotId(null);
					if (vkBotId != null) {
						String address = "https://vk.com/club" + Math.abs(vkBotId);
						ZKR.openWindow800_1200(address);
//					ZKR.redirectToPage(address, true);
					}
					return true;
				};
		}

		if (cmd.startsWith("#") && cmd.length() > 4) {

			return () -> {
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
			};

		}

		return null;
	}

	static AppCmd getAppCmd_OpenShellConsole(Sdn sdn, String cmd) {

		switch (cmd) {

			case "~~":
				return AppCmd.forOwnerOrAdmin(() -> ShConsolePanel.openSimple());

			case "~~clear":
				return AppCmd.forOwnerOrAdmin(() -> {
					ShConsolePanel.clear();
					return true;
				});

			case MainTbx.MAIN_TBX_MODE_SH:
				return AppCmd.forOwnerOrAdmin(() -> {
					MainTbx.findFirst().applyMode(true);
					return true;
				});

			case "!~":
				return AppCmd.forOwnerOrAdmin(() -> { //off sh mode
					MainTbx.findFirst().applyMode(false);
					return true;
				});

			default:
				return null;
		}

	}

	static AppCmd getShellSingleCommand(Sdn sdn, String cmd) {
		return AppCmd.forOwnerOrAdmin(() -> {
			String apply = (String) AppZosBashExecView.funcExecCmdAndShowResult.apply(cmd);
			BottomHistoryPanel.addItemAsData(apply, false);
			return null;
		});
	}
}
