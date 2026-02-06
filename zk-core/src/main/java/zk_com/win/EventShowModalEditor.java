//package zk_com.win;
//
//import mpc.str.sym.SYMJ;
//import zk_com.base.Bt;
//import zk_com.base.Tbx;
//import zk_com.base.Tbxm;
//import zk_page.ZKC;
//
//import java.nio.file.Path;
//
//public class EventShowModalEditor extends EventShowComInModal {
//	public EventShowModalEditor(Path editableFile) {
//		this((Tbxm) new Tbxm(editableFile, Tbx.DIMS.WH100).saveble());
//	}
//
//
//	private EventShowModalEditor(Tbxm modalCom) {
//		super(new Bt(SYMJ.SAVE).onCLICK(modalCom.getEventSave()), ZKC.getFirstWindow(), modalCom);
//	}
//
//
//}
