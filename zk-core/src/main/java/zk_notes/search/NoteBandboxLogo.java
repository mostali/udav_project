package zk_notes.search;

import mpc.str.sym.SYMJ;
import mpu.func.FunctionV1;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import zk_com.base.Lb;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Span0;
import zk_com.base_ext.Bandbox0;
import zk_com.ext.Ddl;
import zk_notes.search.engine.NoteSearchEngine;
import zk_page.ZKCFinder;
import zk_page.ZKJS;
import zk_page.ZKS;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class NoteBandboxLogo extends Div0 {

	public static NoteBandboxLogo findFirst(NoteBandboxLogo... defRq) {
		return ZKCFinder.findFirstIn_Page(NoteBandboxLogo.class, true, defRq);
	}

//	public NoteBandboxIcon() {
//		super();
//	}

	@Override
	protected void init() {
		super.init();

		width(200);

		//
		//1

		Lb lb = new Lb(SYMJ.SEARCH_LUPA_RIGHT);
		ZKS.OPACITY(lb, 0.6);
		appendChild(lb);

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

		applyInitHideBehaviour(bandbox);

		FunctionV1<Boolean> applyVisible = (yn) -> {
//			ddl.setVisible(yn);
			bandbox.setVisible(yn);
		};

		applyVisible.apply(false);

		lb.addEventListener(Events.ON_MOUSE_OVER, e -> applyVisible.apply(true));

		ZKS.FIXED(this);
		ZKS.LEFT(this, 10);
		ZKS.TOP(this, 5);
//		ZKS.OPACITY(this, 0.8);
//		ZKS.TEXT_ALIGN(this, 1);
//		ZKS.FONT_SIZE(this, "14pt");
//		ZKS.HEIGHT(this, 24);
//		ZKS.WIDTH(this, 96);

	}

	private void applyInitHideBehaviour(NoteBandbox bandbox) {

//		ZKC.get
//		ZKJS.setAction_ShowEffect(bandbox,5000);


//		bandbox.addEventListener(Events.ON_MOUSE_OUT, e -> bandbox.setVisible(false));
//		bandbox.addEventListener(Events.ON_MOUSE_OUT, e -> );
	}

}
