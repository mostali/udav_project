package zk_notes.search;

import mpc.str.sym.SYMJ;
import mpu.func.FunctionV1;
import org.zkoss.zk.ui.event.Events;
import zk_com.base.Lb;
import zk_com.base_ctr.Div0;
import zk_page.ZKCFinderExt;
import zk_page.ZKS;

import java.util.concurrent.atomic.AtomicReference;

public class MainSearchPanel extends Div0 {

	public static MainSearchPanel findFirst(MainSearchPanel... defRq) {
		return ZKCFinderExt.findFirst_inPage0(MainSearchPanel.class, true, defRq);
	}

	@Override
	protected void init() {
		super.init();

		inlineBlock();
		//
		//1

		Lb lbIconSearch = new Lb(SYMJ.SEARCH_LUPA_RIGHT);
		ZKS.OPACITY(lbIconSearch, 0.6);
		appendChild(lbIconSearch);

		AtomicReference<NoteBandbox> bandboxRef = new AtomicReference<>();

		//
		//2
//		Ddl<NoteSearchEngine.SearchNoteMode> ddl = new Ddl<NoteSearchEngine.SearchNoteMode>(NoteSearchEngine.SearchNoteMode.NOTE) {
//			@Override
//			public boolean onHappensClickItem(MouseEvent e, NoteSearchEngine.SearchNoteMode value) {
//				bandboxRef.get().setSearchMode(value);
//				set_value(value);
////				super.onHappensClickItem(e, value)
//				return true;
//			}
//		};
//		appendChild(ddl);

		//
		//3

		NoteBandbox bandbox = new NoteBandbox();

		bandboxRef.set(bandbox);
		appendChild(bandbox);

		FunctionV1<Boolean> applyVisible = (yn) -> {
//			ddl.setVisible(yn);
			bandbox.setVisible(yn);
			lbIconSearch.setVisible(!yn);

			if (yn) {
				width(200);
				margin(0, 80, 0, 0);
			} else {
				width(null);
				margin(null);
			}
		};

		bandbox.addEventListener(Events.ON_CANCEL, e -> applyVisible.apply(false));

		applyVisible.apply(false);

		lbIconSearch.addEventListener(Events.ON_MOUSE_OVER, e -> applyVisible.apply(true));


	}

}
