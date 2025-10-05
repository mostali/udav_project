package zk_notes.control.maintbx;

import mpc.env.APP;
import mpc.rfl.RFL;
import mpc.types.tks.cmt.Cmd2;
import mpu.IT;
import mpu.X;
import mpu.core.ARRi;
import mpu.pare.Pare;
import mpu.str.TKN;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Window;
import udav_net.apis.zznote.ItemPath;
import udav_net.apis.zznote.NoteApi;
import zk_com.base.Tbx;
import zk_com.base_ctr.Div0;
import zk_form.notify.ZKI;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.fsman.NodeFileTransferMan;
import zk_os.AppZosCore;
import zk_os.AppZosView;
import zk_os.core.Sdn;
import zk_os.db.net.WebUsr;
import zk_os.sec.SecMan;
import zk_page.ZKCFinderExt;
import zk_page.ZKR;
import zk_page.ZKS;
import zk_page.index.tabs.IndexTabsPSP;
import zk_page.panels.BottomHistoryPanel;

import java.io.IOException;

public class MainTbx extends Div0 {

	public static final String MAIN_TBX_MODE = "mode";
	public static final String MAIN_TBX_MODE_SH = "~";
	public static final int WIDTH_SH_MODE = 196;
	public static final int WIDTH_DEF_MODE = 96;
	final Tbx tbxIn = new Tbx();
//	final Tbxm tbxmOut = new Tbxm();

	public static MainTbx findFirst(MainTbx... defRq) {
		return ZKCFinderExt.findFirst_inPage0(MainTbx.class, true, defRq);
	}

	@Override
	protected void init() {
		super.init();

		setCLASS(RFL.scn(MainTbx.class));

		{
			ZKS.FIXED(tbxIn);
			ZKS.RIGHT(tbxIn, "50pt");
			ZKS.TOP(tbxIn, "5pt");
			ZKS.OPACITY(tbxIn, 0.8);
			ZKS.TEXT_ALIGN(tbxIn, 1);
			ZKS.FONT_SIZE(tbxIn, "14pt");
			ZKS.HEIGHT(tbxIn, 24);
			tbxIn.setCLASS(RFL.scn(MainTbx.class) + "In");

		}
		{
//			ZKS.FIXED(tbxmOut);
//			ZKS.RIGHT(tbxmOut, "50pt");
//			ZKS.TOP(tbxmOut, "15pt");
//			ZKS.OPACITY(tbxmOut, 0.0);
//			ZKS.TEXT_ALIGN(tbxmOut, 1);
//			ZKS.FONT_SIZE(tbxmOut, "14pt");
//			ZKS.HEIGHT(tbxmOut, 24);
//			ZKS.WIDTH(tbxmOut, 96);


//			tbxmOut.addSTYLE(" --full-height: 220px;height:2px;width:100px;overflow:hidden;transition: height 0.4s ease-in-out;position:fixed;right:50pt;top:15pt;");
//			ZKJS.bindJS(tbxIn, "var panel = document.querySelector('.MainTbx');var hint = panel.querySelector('.MainTbxIn');panel.onmouseenter=function() {this.style.height = '220px';hint.style.opacity = '1';};panel.onmouseleave = function() {this.style.height = '60px';hint.style.opacity = '0';};");
//			tbxmOut.setCLASS(RFL.scn(MainTbx.class) + "Out");
		}

		applyMode(false);

		tbxIn.onOK((Event e) -> {

			if (!SecMan.isAdminOrOwner() && !SecMan.isAllowedEdit()) {
				return;
			}

			run();

		});

		tbxIn.onChangingAutoWidth(WIDTH_DEF_MODE);

		appendChilds(tbxIn);//tbxmOut
	}

	private void run() throws IOException {
		String value = tbxIn.getValue().trim();
		switch (value) {
			case MAIN_TBX_MODE_SH: {
				findFirst().applyMode(true);
				break;
			}
			case "!~": {
				findFirst().applyMode(false);
				break;
			}
			default:
				boolean isShMode = MainTbx.this.attr_get(MAIN_TBX_MODE, "").equals(MAIN_TBX_MODE_SH);
				if (isShMode) {
					runShMode(sdnRq(), value);
				} else {
					runAppMode(sdnRq(), value);
				}
				break;
		}

	}

	private void applyMode(boolean isShMode) {
		tbxIn.setValue("");
		if (isShMode) {
			ZKS.WIDTH(tbxIn, WIDTH_SH_MODE);
			tbxIn.placeholder(MAIN_TBX_MODE_SH);
			attr_put(MAIN_TBX_MODE, MAIN_TBX_MODE_SH);
		} else {
			ZKS.WIDTH(tbxIn, WIDTH_DEF_MODE);
			WebUsr webUsr = WebUsr.get(null);
			if (webUsr != null) {
				tbxIn.placeholder(webUsr.getLogin());
			}
			attr_rm(MAIN_TBX_MODE);
		}

	}

	private static void runShMode(Sdn sdn, String cmd) {
		String apply = (String) AppZosView.funcExecCmdAndShowResult.apply(cmd);
		BottomHistoryPanel.addItemAsData(apply, false);
	}

	private static void runAppMode(Sdn sdn, String cmd) throws IOException {
		switch (cmd) {
			case "+":
				String tgBotId = APP.getTgBotId(null);
				if (tgBotId != null) {
					ZKR.redirectToPage("https://t.me/" + tgBotId, true);
				}
				break;

			default:

				if (UMainTbx.DldCallLine.isValid(cmd)) {
					Pare<NodeDir, Window> nodeDirWindowPare = NodeFileTransferMan.addNewRandomForm(sdn);
					if (nodeDirWindowPare != null) {
						UMainTbx.doDownloadToDir(cmd, sdn, nodeDirWindowPare.key().toPath());
					}
				} else if (cmd.startsWith("!")) {
					cmd = cmd.substring(1).trim();
					String[] two = TKN.twoQk(cmd, " ");
					String name = ARRi.first(two, 0, null);
					String cnt = ARRi.first(two, 1, null);

					String[] indexPath = IndexTabsPSP.getIndexPathForQuery();
					Sdn targetSdn;
					if (X.emptyAll(indexPath)) {
						targetSdn = null;
					} else if (indexPath[0] != null) {
						targetSdn = Sdn.of(indexPath[0], indexPath[1]);
					} else {
						targetSdn = null;
					}

					NodeFileTransferMan.addNewFormAndOpenUX(targetSdn, name, cnt);

				} else if (cmd.startsWith("~mvu")) {
					cmd = cmd.substring(4).trim();
					if (X.empty(cmd)) {
						ZKI.infoEditorDark("~mvu /s/p/i zn:/s/p/i");
						return;
					}
					runMVU(cmd);

					{
						String infoMsg = X.f("Moved %s", cmd);
						L.info(infoMsg);
						ZKI.showMsgBottomRightFast_INFO(infoMsg);

						ZKR.restartPage();
					}
				} else if (cmd.startsWith("~mvd")) {
					cmd = cmd.substring(4).trim();
					if (X.empty(cmd)) {
						ZKI.infoEditorDark("~mvd /s/p/i zn:/s/p/i");
						return;
					}

					runMVD(cmd);

					{
						String infoMsg = X.f("Moved %s", cmd);
						L.info(infoMsg);
						ZKI.showMsgBottomRightFast_INFO(infoMsg);
						ZKR.restartPage();
					}
				}

		}

	}


	public static void runMVD(String mvu_cmd) {
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
		if (L.isInfoEnabled()) {
			L.info("Mvd item with put to : " + mvu_cmd);
		}

		znApi.DELETE_item(targetItmPath);

	}

	public static void runMVU(String mvu_cmd) throws IOException {
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
		if (L.isInfoEnabled()) {
			L.info("Mvu item with put to : " + mvu_cmd);
		}

		NodeFileTransferMan.deleteItem(nodeDir);

	}

	public static void runDELETE(String rm_cmd) throws IOException {

		String hst = TKN.first(rm_cmd, ":");
		ItemPath targetItemPath = ItemPath.of(TKN.lastGreedy(rm_cmd, ":"));

		IT.state(targetItemPath.mode == ItemPath.State.ALL);

		NoteApi noteApi = AppZosCore.createNoteApiByAlias(hst);

		noteApi.DELETE_item(targetItemPath);
		if (L.isInfoEnabled()) {
			L.info("Deleted item via : " + rm_cmd);
		}


	}

}
