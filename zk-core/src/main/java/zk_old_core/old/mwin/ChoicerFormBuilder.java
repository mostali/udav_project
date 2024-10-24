package zk_old_core.old.mwin;

import lombok.RequiredArgsConstructor;
import mpc.json.GsonMap;
import mpc.str.sym.SYMJ;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Dd;
import zk_com.base_ctr.Span0;
import zk_old_core.std_fb.HtmlFb;
import zk_old_core.std_fb.ImgFb;
import zk_form.notify.ZKI_Log;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class ChoicerFormBuilder extends Span0 {

	public static final String HISTORY_FB = "history.fb";
	final MWin mWin;

	@Override
	protected void init() {
		appendLb(SYMJ.PLUS);
		Dd innerDdTrm = new Dd("", getAllHistoryItems());
		innerDdTrm.setWidth(MWin.DEF_WIDTH_DD);
		innerDdTrm.onOK((SerializableEventListener) event -> {
			String cmd = innerDdTrm.getValue();
			try {
				switch (cmd) {
					case "html": {
						mWin.showComponent(new HtmlFb(mWin.getPageDirModel()));
						return;
					}
					case "img": {
						mWin.showComponent(new ImgFb(mWin.getPageDirModel()));
						return;
					}
					default:
						ZKI_Log.alert("Undefined FormBuilder Component '%s'", cmd);
						return;
				}
			} finally {
				Path pageUsrJsonPath = mWin.getPageUsrJsonPath();
				MwInnerTrm.addHistoryItem(pageUsrJsonPath, HISTORY_FB, cmd);
			}
		});

		appendChild(innerDdTrm);

	}

	private List<String> getAllHistoryItems() {
		return (List<String>) GsonMap.read(mWin.getPageUsrJsonPath(), true).get(HISTORY_FB, Collections.EMPTY_LIST);
	}


}
