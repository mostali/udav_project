package zk_old_core.index;

import lombok.SneakyThrows;
import mpu.core.ARR;
import mpu.X;
import mpc.fs.UF;
import zk_old_core.app_ds.AppDS;
import org.zkoss.zk.ui.Component;
import utl_web.UWeb;
import zk_com.base_ext.SimpleBorderLayout;
import zk_com.base_ctr.Div0;
import zk_os.sec.MatrixAccess;
import zk_page.ADDC;
import zk_page.ADDH;
import zk_page.ZKS;
import zk_old_core.sd.core.RepoPageDir;
import zk_old_core.sd.core.SdMan;

import java.nio.file.Path;
import java.util.List;

@Deprecated
public class FormPageIndex extends Div0 {//extends ViewPageComponent

	private final String sd3;

	public FormPageIndex() {
		this(null);
	}

	public FormPageIndex(String sd3) {
//		super(null, null);
		this.sd3 = sd3;
	}

	@SneakyThrows
	protected void init() {
		appendChild(sd3 != null ? buildView(sd3) : buildView());
	}

	public static Component buildView(String sd3) {
		return buildView(ARR.as(sd3));
	}

	public static Component buildView() {
		return buildView(SdMan.getAllSubdomainNames());
	}

	private static Component buildView(List<String> subdomains) {

		SimpleBorderLayout layout = SimpleBorderLayout.buildCom(true);

		Div0 NORTH = layout.NORTH();
		Div0 WEST = layout.WEST();
//		ZKS.HEIGHT(WEST,"300px");
		ZKS.HEIGHT_MIN(layout, "1024px");
//		ZKS.HEIGHT(layout.getWest(),"300px");

		Div0 CENTER = layout.CENTER();

		String hostWoSd3 = UWeb.getHostWoSd();

//		ZKS.HEIGHT100(WEST);
//		String curSd3 = spVM.ppi().subdomain3();

		for (String sd : subdomains) {
			String hostWithSd3 = "http://" + (".".equals(sd) ? "" : sd + ".") + hostWoSd3;
			hostWithSd3 = UWeb.appendPortToHostWithPath(hostWithSd3);
			hostWithSd3 += "?" + SdMan.QUERY_PAGE_INDEX;
			ADDH.A(WEST, sd, hostWithSd3).block();
//			WEST.appendChild(DivWith.of(Xml.of("<div style='height:50px'><a href='%s'>%s</a></div>",href,sd)));
		}

		//		String currentSd3 = AppZosWeb.getSd3();
		//		boolean isSd3Index = SD3_INDEX.equals(currentSd3);
		//		String name = isSd3Index ? "" : " для домена '" + sd3 + "'";
		String name = "";
		ADDH.H0(NORTH, 1, "Доступные страницы" + name);

		boolean isAdmin = MatrixAccess.ADMIN_FULL.hasAccess();

		boolean isSingleSdView = subdomains.size() == 1;

		for (String showSubdomain : subdomains) {
			RepoPageDir repoPageDir = RepoPageDir.ofSd3(showSubdomain);
			List<Path> pages = repoPageDir.getAllPagesPath();
			if (isAdmin) {
				ADDH.H0(NORTH, 2, "Domain Dir >>> " + repoPageDir.path());
			}
			if (!isSingleSdView) {
				ADDH.B(CENTER, "SubDomain >>> " + showSubdomain + " (" + X.sizeOf(pages) + ")");
			}
			ADDH.BR(CENTER);
			for (Path path : pages) {
				boolean isArch = AppDS.isArchive(path);
				if (isArch) {
					if (isAdmin) {
						//ADDC.H2(CENTER, "ARCHIVE Dir >>> " + path.path());
					}
					continue;
				}
				String pagename = UF.fn(path);
				ADDC.LN(CENTER, pagename, pagename);
				ADDH.BR(CENTER);
			}
		}

		return layout;
	}
}
