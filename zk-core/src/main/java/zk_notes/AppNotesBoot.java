package zk_notes;

import mp.utl_odb.netapp.mdl.NetUsrId;
import mpc.env.APP;
import mpc.env.AppProfile;
import mpc.env.boot.AppBoot;
import mpc.rfl.RFL;
import mpt.TRM;
import mpu.Sys;
import mpu.core.ARR;
import mpu.str.Sb;
import zk_notes.node_srv.types.quartzMsg.QzTrm;
import zk_os.AppZos;
import zk_page.core.FinderPSP;

import java.util.UUID;

public class AppNotesBoot extends AppBoot {

	public static void initTrm(String... customPackets) {
		String[] trm_routes = {"zk_core", "zk_os", RFL.pn(QzTrm.class)};
		try {
			TRM.run_scan(NetUsrId.def(), true, true, ARR.addElements(trm_routes, customPackets));
		} catch (Exception ex) {
			L.error("Error on scan packet for terminal entity", ex);
		}
	}

	public static void initAppFinderPSP(String... customPackets) {
//		FinderPSP.finderPSP_outer = pagename -> { //TODO zkos outer
//			switch (pagename) {
//				case PageSP.PAGENAME_LOGS:
//					return new PageSP(ZKC.getFirstWindow(), SpVM.get()) {
//						@Override
//						public void buildPageImpl() {
////							NotesHeaderProps.openSimple();
//							LogDirView.openSingly("./logs/");
//						}
//					};
//				default:
//					return null;
//			}
//		};

		String[] basePackets = {"zklogapp", "zk_os", "zk_pages", "zk_page.index.tabs"};
		FinderPSP.regPageEntity(ARR.addElements(basePackets, customPackets));

	}


	public static void boot_step3(Class appClass) {
		try {
			Sb rp = AppZos.buildReport(true, true);
			Sys.p(rp);
		} catch (Exception ex) {
			L.error("doSomethingAfterStartup", ex);
		}

		Sys.p("ActiveAppProfile:" + AppProfile.getFirstUseful());
		Sys.p("RandomUUID:" + UUID.randomUUID());
		Sys.p("Ok:" + APP.getVersion(appClass, null));

//		Sys.say("on");
	}
}
