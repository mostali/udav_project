package zk_notes.control;

import mpc.types.tks.cmt.Cmd2;
import mpu.IT;
import mpu.X;
import mpu.core.ARRi;
import mpu.str.USToken;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.event.Event;
import zk_com.base.Tbx;
import zk_form.notify.ZKI;
import zk_os.AppZosCore;
import zk_os.api.client.NoteApi;
import zk_os.core.ItemPath;
import zk_os.db.net.WebUsr;
import zk_os.sec.SecMan;
import zk_page.ZKR;
import zk_page.ZKS;
import zk_page.node.NodeDir;
import zk_page.node.fsman.NodeFileTransferMan;

import java.io.IOException;

public class MainTbx extends Tbx {

//	public static Function<String, Pare3<Boolean, String, String>> authFunc = (tk) -> Pare3.of(false, "ni", "{}");

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
//				boolean hasBody = getValue().contains(":");
//				String[] newName = hasBody ? USToken.two(getValue(), ":") : new String[]{getValue(), ""};
				String value = getValue().trim();
				if (value.startsWith("!")) {
					value = value.substring(1).trim();
					String[] two = USToken.twoQk(value, " ");
					String name = ARRi.first(two, 0, null);
					String cnt = ARRi.first(two, 1, null);
					NodeFileTransferMan.addNewFormAndOpenUX(name, cnt);
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
						ZKI.infoBottomRightFast(infoMsg);

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
						ZKI.infoBottomRightFast(infoMsg);
						ZKR.restartPage();
					}
				}

			}
		});

		WebUsr webUsr = WebUsr.get(null);

		if (webUsr != null) {
			placeholder(webUsr.login);
		}

	}

	public static void runMVD(String mvu_cmd) throws IOException {
		Cmd2<Object, Object> c2 = Cmd2.of2(mvu_cmd).throwIsNotWhole();
		ItemPath src = ItemPath.of(c2.keyStr());
		String itemPathStr = c2.valStr();

		String hst = USToken.first(itemPathStr, ":");
		ItemPath targetItmPath = ItemPath.of(USToken.lastGreedy(itemPathStr, ":"));

		IT.state(src.mode == ItemPath.Mode.ALL);
		IT.state(targetItmPath.mode == ItemPath.Mode.ALL);
		NoteApi znApi = AppZosCore.createNoteApiByAlias(hst);
		String vl = znApi.GET_item(targetItmPath);
		NodeDir nodeDir = NodeDir.ofNodeName(src.name(), src.sdn());
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

		String hst = USToken.first(itemPathStr, ":");
		ItemPath dst = ItemPath.of(USToken.lastGreedy(itemPathStr, ":"));

		IT.state(src.mode == ItemPath.Mode.ALL);
		IT.state(dst.mode == ItemPath.Mode.ALL);

		NoteApi noteApi = AppZosCore.createNoteApiByAlias(hst);

		NodeDir nodeDir = NodeDir.ofNodeName(dst.name(), dst.sdn());
		String bodyLines = nodeDir.state().readFcData(null);
		IT.notEmpty(bodyLines);

		noteApi.PUT_item(dst, bodyLines);
		if (L.isInfoEnabled()) {
			L.info("Mvu item with put to : " + mvu_cmd);
		}

		NodeFileTransferMan.deleteItem(nodeDir);

	}

	public static void runDELETE(String rm_cmd) throws IOException {

		String hst = USToken.first(rm_cmd, ":");
		ItemPath targetItemPath = ItemPath.of(USToken.lastGreedy(rm_cmd, ":"));

		IT.state(targetItemPath.mode == ItemPath.Mode.ALL);

		NoteApi noteApi = AppZosCore.createNoteApiByAlias(hst);

		noteApi.DELETE_item(targetItemPath);
		if (L.isInfoEnabled()) {
			L.info("Deleted item via : " + rm_cmd);
		}


	}

}
