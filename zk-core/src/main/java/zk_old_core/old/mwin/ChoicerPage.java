package zk_old_core.old.mwin;

import lombok.RequiredArgsConstructor;
import mpc.str.sym.SYMJ;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Dd;
import zk_com.base_ctr.Span0;
import zk_old_core.mdl.PageDirModel;
import zk_old_core.sd.core.RepoPageDir;
import zk_old_core.sd.core.SdMan;
import zk_old_core.sd.GoToSd;

import java.util.Collection;

@RequiredArgsConstructor
public class ChoicerPage extends Span0 {

	final MWin mWin;

	@Override
	protected void init() {
		appendLb(SYMJ.BOOKS);

		Collection<String> sd3s = SdMan.getAllSubdomainNames();
		PageDirModel pageDirModel = PageDirModel.get();
		RepoPageDir repo = pageDirModel.getRepo();
		String sd3 = repo.getSubdomain3();
		Dd ddSd3 = new Dd(sd3, sd3s);
		Dd ddSd3Pages = new Dd(pageDirModel.name(), repo.getAllPagesNames());
		ddSd3.setWidth(MWin.DEF_WIDTH_DD);
		ddSd3Pages.setWidth(MWin.DEF_WIDTH_DD);
		ddSd3Pages.onOK((SerializableEventListener) event -> {
			GoToSd.goTo(ddSd3.getValue(), ddSd3Pages.getValue());
		});
//		Bt go = new Bt("Go").onClick((SerializableEventListener<Event>) event -> {
//			GoToSd.goTo(ddSd3.getValue(), ddSd3Pages.getValue());
//		});
		 ddSd3.onSELECTION((SerializableEventListener) event -> {
			String chocedSd3 = ddSd3.getValue();
			Collection<String> pages = SdMan.getAllPagesOfSd3(chocedSd3);
			ddSd3Pages.fillItems(pages, true);
			ddSd3.invalidate();
			ddSd3Pages.invalidate();
		});

		appendChild(ddSd3);
		appendChild(ddSd3Pages);
//		appendChild(go);

	}
}
