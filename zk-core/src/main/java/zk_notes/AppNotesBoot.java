package zk_notes;

import mpc.env.AppProfile;
import mpc.env.boot.AppBoot;
import mpc.env.boot.BootRunUtils;
import mpu.Sys;
import mpu.str.Sb;
import zk_os.AppZos;
import zk_page.ZKC;
import zk_page.core.FinderPSP;
import zk_page.core.PageSP;
import zk_page.core.SpVM;
import zklogapp.merge.LogDirView;

import java.util.UUID;

public class AppNotesBoot extends AppBoot {


	public static void initAppFinderPSP() {
		FinderPSP.finderPSP_outer = pagename -> { //TODO zkos outer
			switch (pagename) {
				case PageSP.PAGENAME_LOGS:
					return new PageSP(ZKC.getFirstWindow(), SpVM.get()) {
						@Override
						public void buildPageImpl() {
//							NotesHeaderProps.openSimple();
							LogDirView.openSingly("./logs/");
						}
					};
				default:
					return null;
			}
		};
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
		Sys.p("Ok:" + BootRunUtils.getVersionFromAny(appClass, null));
		Sys.say("on");
	}
}
