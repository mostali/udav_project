package zk_notes.control.maintbx;

import mpc.env.APP;
import mpc.types.tks.cmt.Cmd2;
import mpu.IT;
import mpu.X;
import mpu.core.ARRi;
import mpu.pare.Pare;
import mpu.str.TKN;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Window;
import zk_com.base.Tbx;
import zk_form.notify.ZKI;
import zk_os.AppZosCore;
import udav_net.apis.zznote.NoteApi;
import udav_net.apis.zznote.ItemPath;
import zk_os.core.Sdn;
import zk_os.db.net.WebUsr;
import zk_os.sec.SecMan;
import zk_page.ZKCFinderExt;
import zk_page.ZKR;
import zk_page.ZKS;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.fsman.NodeFileTransferMan;
import zk_page.index.tabs.IndexTabsPSP;

import java.io.IOException;

public class MainTbx extends Tbx {

//	public static Function<String, Pare3<Boolean, String, String>> authFunc = (tk) -> Pare3.of(false, "ni", "{}");

	public static MainTbx findFirst(MainTbx... defRq) {
		return ZKCFinderExt.findFirst_inPage0(MainTbx.class, true, defRq);
	}

	@Override
	protected void init() {
		super.init();

		ZKS.ABSOLUTE(this);
		ZKS.RIGHT(this, 80);
		ZKS.TOP(this, 5);
		ZKS.OPACITY(this, 0.8);
		ZKS.TEXT_ALIGN(this, 1);
		ZKS.FONT_SIZE(this, "14pt");
		ZKS.HEIGHT(this, 24);
		ZKS.WIDTH(this, 96);

		onOK((Event e) -> {

			if (SecMan.isAdminOrOwner() || SecMan.isAllowedEdit()) {
				String value = getValue().trim();
				if (value.equals("+")) {
					String tgBotId = APP.getTgBotId(null);
					if (tgBotId != null) {
						ZKR.redirectToPage("https://t.me/" + tgBotId);
					}
				} else if (UMainTbx.DldCallLine.isValid(value)) {
					Pare<NodeDir, Window> nodeDirWindowPare = NodeFileTransferMan.addNewRandomForm(sdn());
					if (nodeDirWindowPare != null) {
						UMainTbx.doDownloadToDir(value, sdn(), nodeDirWindowPare.key().toPath());
					}
					return;
				} else if (value.startsWith("!")) {
					value = value.substring(1).trim();
					String[] two = TKN.twoQk(value, " ");
					String name = ARRi.first(two, 0, null);
					String cnt = ARRi.first(two, 1, null);

					String[] indexPath = IndexTabsPSP.getIndexPathForQuery();
					Sdn sdn = null;
					if (X.emptyAll(indexPath)) {
						sdn = null;
					} else if (indexPath[0] != null) {
						sdn = Sdn.of(indexPath[0], indexPath[1]);
					}

					NodeFileTransferMan.addNewFormAndOpenUX(sdn, name, cnt);

					return;
				} else if (value.startsWith("~mvu")) {
					value = value.substring(4).trim();
					if (X.empty(value)) {
						ZKI.infoEditorBw("~mvu /s/p/i zn:/s/p/i");
						return;
					}
					runMVU(value);

					{
						String infoMsg = X.f("Moved %s", value);
						L.info(infoMsg);
						ZKI.showMsgBottomRightFast_INFO(infoMsg);

						ZKR.restartPage();
					}
				} else if (value.startsWith("~mvd")) {
					value = value.substring(4).trim();
					if (X.empty(value)) {
						ZKI.infoEditorBw("~mvd /s/p/i zn:/s/p/i");
						return;
					}

					runMVD(value);

					{
						String infoMsg = X.f("Moved %s", value);
						L.info(infoMsg);
						ZKI.showMsgBottomRightFast_INFO(infoMsg);
						ZKR.restartPage();
					}
				}

			}
		});

		WebUsr webUsr = WebUsr.get(null);

		if (webUsr != null) {
			placeholder(webUsr.getLogin());
		}

	}


	public static void runMVD(String mvu_cmd) throws IOException {
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
