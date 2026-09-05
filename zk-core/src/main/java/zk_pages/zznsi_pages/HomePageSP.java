//package zk_pages.zznsi_pages;
//
//import lombok.SneakyThrows;
//import mpc.str.sym.SYMJ;
//import mpe.call_msg.BashCallMsg;
//import mpu.str.SPLIT;
//import org.zkoss.zul.Window;
//import zk_com.base.Bt;
//import zk_com.base.Xml;
//import zk_com.base_ctr.Div0;
//import zk_com.sun_editor.IPerPage;
//import zk_form.notify.ZKI;
//import zk_notes.control.NotesPSP;
//import zk_os.sec.ROLE;
//import zk_page.ZKS;
//import zk_page.core.*;
//import zk_page.with_com.WithMainTbx;
//import zk_page.with_com.WithSearch;
//import zklogapp.AppLogSettingsPanel;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//@PageRoute(pagename = "home", role = ROLE.ANONIM)
//public class HomePageSP extends PageSP implements IPerPage, WithMainTbx, WithSearch {//, WithLogo
//
//	private AppLogSettingsPanel pageHeader = null;
//
//	public HomePageSP(Window window, SpVM spVM) {
//		super(window, spVM);
//	}
//
//	@SneakyThrows
//	public void buildPageImpl() {
//
//		ZKS.PADDING0(window);
//		ZKS.MARGIN(window, "30px 0 0 0");
//
//
//		NotesPSP.initStyleWindowDefault(window);
//
//
//		window.appendChild(new CicdView());
//
//		window.appendChild(Xml.HR());
//		window.appendChild(new DoExportBt());
//
//		window.appendChild(Xml.HR());
//		window.appendChild(new DoExportBt());
//	}
//
//	public static class DoExportBt extends Bt {
//		public DoExportBt() {
//			super("Export " + SYMJ.ROCKET);
//		}
//
//		@Override
//		public void init() {
//			super.init();
//			onCLICK(e -> {
////				Path exportSh = Paths.get("/home/dav/pjnsi/insi/_cicd/nifi/export/export-flow.sh");
//////				PyCallMsg pyCallMsg = PyCallMsg.of(exportSh);
////				BashCallMsg pyCallMsg = BashCallMsg.of(exportSh);
////				pyCallMsg.setWorkDir(exportSh.getParent().getParent());
////				Object call = pyCallMsg.call(true, SPLIT.argsBySpace("-b bucket_DEV -f flnPcg1"));
//				ZKI.infoEditorDark(getClass().getSimpleName() + "");
//			});
//		}
//	}
//
//	//
//	//
//
//	public static class CreateFlowsBt extends Bt {
//		public CreateFlowsBt() {
//			super("Export Flows");
//		}
//
//		@Override
//		public void init() {
//			super.init();
//			onCLICK(e -> {
//				Path exportSh = Paths.get("/home/dav/pjnsi/insi/_cicd/nifi/export/export-flow.sh");
////				PyCallMsg pyCallMsg = PyCallMsg.of(exportSh);
//				BashCallMsg pyCallMsg = BashCallMsg.of(exportSh);
//				pyCallMsg.setWorkDir(exportSh.getParent().getParent());
//				Object call = pyCallMsg.call(true, SPLIT.argsBySpace("-b bucket_DEV -f flnPcg1"));
//				ZKI.infoEditorDark(call + "");
//			});
//		}
//	}
//
//
//	static class CicdOperationDiv extends Div0 {
//
//		@Override
//		protected void init() {
//			super.init();
//		}
//	}
//
//}
