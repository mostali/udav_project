package zk_old_core.old.fswin;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpu.X;
import mpc.exception.NotifyMessageRtException;
import mpc.fs.UFS;
import mpc.json.GsonMap;
import mpu.str.USToken;
import mpc.str.sym.SYMJ;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Dd;
import zk_com.base_ctr.Span0;
import zk_form.notify.ZKI_Log;
import zk_page.ZKR;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class ChoicedFd extends Span0 {

	public static final String HISTORY_CHOICED_FD = "history.fs.items";
	public static final int HISTORY_CHOICED_FD_SIZE = 30;

	public static final String DEL_ITEM = " > ";
	final FsWin fsWin;

//	public static final String EV_PAGE = "`$PAGE`";
//	public static final String EV_APP = "`$APP`";

	Dd innerDdChoicedFd;

	public Dd getInnerDdChoicedFd() {
		return innerDdChoicedFd == null ? innerDdChoicedFd = new Dd("", getAllHistoryItems()) : innerDdChoicedFd;
	}

	@Override
	protected void init() {

		appendLb(SYMJ.ARROW_RIGHT_SPEC);

		Dd innerDdTrm = getInnerDdChoicedFd();
		innerDdTrm.setWidth(FsWin.DEF_WIDTH_DD);
		innerDdTrm.onOK((SerializableEventListener) event -> {
			String cmd = innerDdTrm.getValue();
			try {
				selectCmd:
				switch (cmd) {
					case "r": {
						ZKR.rebuildPage();
						return;
					}

				}
			} finally {
				Path pageUsrJsonPath = fsWin.getPageUsrJsonPath();
				addHistoryItem(pageUsrJsonPath, HISTORY_CHOICED_FD, cmd);
			}

			ZKI_Log.alert("Undefined Command  '%s'", cmd);
			return;
		});

		appendChild(innerDdTrm);

	}

	public static void addHistoryItem(Path pageUsrJsonPath, String historyProperty, String cmd) {
		GsonMap gm = GsonMap.read(pageUsrJsonPath, true);
		List l = (List) gm.get(HISTORY_CHOICED_FD, null);
		if (l == null) {
			gm.put(HISTORY_CHOICED_FD, l = new ArrayList());
		}
		if (!l.contains(cmd)) {
			l.add(cmd);
		}
		while (l.size() > HISTORY_CHOICED_FD_SIZE) {
			l.remove(HISTORY_CHOICED_FD_SIZE);
		}
		GsonMap.write(pageUsrJsonPath, gm);
	}

	private List<String> getAllHistoryItems() {
		return (List<String>) GsonMap.read(fsWin.getPageUsrJsonPath(), true).get(HISTORY_CHOICED_FD, Collections.EMPTY_LIST);
	}

	public void setFd(String path) {
		getInnerDdChoicedFd().setValue(path);
	}

	@SneakyThrows
	public void setPathFd(Path path) {
		getInnerDdChoicedFd().addDdItem(path.getFileName() + DEL_ITEM + path, true);
		Boolean isFileOrDirOrNull = UFS.isFileOrDirOrNull(path);

	}

	public Path getChoicedPath() {
		String value = getInnerDdChoicedFd().getValue();
		if (X.empty(value)) {
			throw NotifyMessageRtException.LEVEL.BLUE.I("Set file");
		}
		return USToken.last(value, DEL_ITEM, Path.class);
	}
}
