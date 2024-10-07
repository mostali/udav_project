package zk_old_core.old.mwin;

import lombok.RequiredArgsConstructor;
import mpc.fs.ext.EXT;
import mpc.str.sym.SYMJ;
import mpu.str.USToken;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Dd;
import zk_com.base_ctr.Span0;
import zk_old_core.mdl.PageDirModel;

import java.nio.file.Path;

@RequiredArgsConstructor
public class ChoicerHead extends Span0 {

	final MWin mWin;

	@Override
	protected void init() {
		appendLb(SYMJ.BRAIN);
		PageDirModel pageDirModel = PageDirModel.get();
		Dd dd3Heads = new Dd(pageDirModel.getHeadTypeAndNames());
		String pageRootPropsFile = pageDirModel.path().relativize(pageDirModel.getFileRootProps()).toString();
		String pageRootPropsWithType = createPageHeadDdFile(EXT.of(pageRootPropsFile).name(), pageRootPropsFile);
		dd3Heads.addDdItem(pageRootPropsWithType, true);
		dd3Heads.setWidth(MWin.DEF_WIDTH_DD);
		dd3Heads.onSELECTION((SerializableEventListener) event -> {
			String choicedHeadEntity = dd3Heads.getValue();
			String formParent = extractPageHeadFile(choicedHeadEntity);
			Path headEntity = pageDirModel.path().resolve(formParent);
			mWin.showContent(headEntity);
		});
		appendChild(dd3Heads);

	}

	//	public String getPageFormSelectedDdItem() {
//		return dd3Heads.getValue();
//	}
	public static String createPageHeadDdFile(String type, String headSelectedDdItem) {
		return type + ":" + headSelectedDdItem;
	}

	public static String extractPageHeadFile(String headSelectedDdItem) {
		return USToken.lastGreedy(headSelectedDdItem, ":");
	}
}
