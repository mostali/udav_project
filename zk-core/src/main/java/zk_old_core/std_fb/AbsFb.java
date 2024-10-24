package zk_old_core.std_fb;

import org.zkoss.zk.ui.Component;
import zk_com.base_ctr.Div0;
import zk_old_core.mdl.PageDirModel;
import zk_old_core.old.mwin.MWin;
import zk_page.ZKR;

import java.nio.file.Path;

public class AbsFb extends Div0 {
	final PageDirModel pageDirModel;

	public AbsFb(PageDirModel pageDirModel, Component... coms) {
		super(coms);
		this.pageDirModel = pageDirModel;
	}

	public void updateCurrentMwinPath(Path newForm) {
		MWin.findFirstOrOpen(true).showContent(newForm);//stare value in dd - when restart(rebuild) page
		ZKR.rebuildPage(pageDirModel);
	}
}
