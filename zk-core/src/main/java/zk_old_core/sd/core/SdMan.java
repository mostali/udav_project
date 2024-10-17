package zk_old_core.sd.core;

import lombok.SneakyThrows;
import mpu.core.ARG;
import mpu.IT;
import mpu.X;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.UDIR;
import mpc.fs.UF;
import mpc.fs.fd.EFT;
import mpu.pare.Pare;
import mpu.str.Sb;
import org.zkoss.zk.ui.Component;
import zk_os.AppZosCore;
import zk_old_core.sd.Sd3EE;
import zk_page.core.PagePathInfo;
import zk_old_core.mdl.PageDirModel;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class SdMan {

	public static final String SD3_INDEX = "index";
	public static final String QUERY_PAGE_INDEX = "index";
	public static final String PAGE_INDEX = "index";
	public static final String ROOT_SD3_DIR = ".index";
	public static final String ROOT_AILAS_SD3 = ".";

	public static void main(String[] args) {
//		URx.exit("^(^[1-9][0-9]?$)|(^100$)|(^[1-9][0-9][.,][0-9]{1,2}$)|(^100[.,][0]+$)", "0.01", "0,01", "0", "1", "99,99", "100", "100.000", "100.00", "100.01", "100.00", "101");
//		URx.exit("^[1-9][0-9]?$|^100$", "1234567890", "12345678", "a2345678", "lPo2345678", "lPo234567");
//		URx.exit("^([0-9A-zА-яЁё]{8})|([0-9A-zА-яЁё]{10})$", "1234567890", "12345678", "a2345678", "lPo2345678", "lPo234567");
//		URx.exit("^\\d{1,12}([.,]{1}\\d{0,2})?$", "123", "123.0", "123,0", "123,09", "123,09.", "123,09s.", "123,09,", "123456789012,0", "123456789012", "1234567890120", "12345678901201", "123456789012012");
////		URx.exit("^\\d{1,12}$", "123", "123.0", "123,0", "123,09");
	}

	public static Pare<RepoPageDir, Path> findPageIndex() {
		return Pare.of(RepoPageDir.of(AppZosCore.getMasterRepo()), AppZosCore.getMasterPage());
	}

	public static Pare<RepoPageDir, Path> findPage(String pagename) {
		return RepoPageDir.getPageWithRepo(AppZosCore.getMasterRepo(), pagename, true);
	}

	public static Pare<RepoPageDir, Path> findPage(String sd3, String pagename) {
		return RepoPageDir.getPageWithRepo(RepoPageDir.getSd3RepoPath(sd3), pagename, true);
	}

	public static Pare<RepoPageDir, Path> findPageIndex(String sd3) {
		return RepoPageDir.getPageWithRepo(RepoPageDir.getSd3RepoPath(sd3), SdMan.PAGE_INDEX, true);

	}

	public static Pare<RepoPageDir, Path> findPage(PagePathInfo ppi) throws Sd3EE {
		return findPage_(ppi.isRootDomain(), ppi.isEmptyPagename(), ppi.subdomain3(), ppi.pagename());
	}

	public static Pare<RepoPageDir, Path> findPage_(String sd3, String pagename) throws Sd3EE {
		return findPage_(sd3.isEmpty(), pagename.isEmpty(), sd3, pagename);
	}

	public static Pare<RepoPageDir, Path> findPage_(boolean isRootSubomain, boolean isRootPath, String sd3, String pagename) throws Sd3EE {
		if (isRootSubomain) {
			if (isRootPath) {
				return findPageIndex();
			}
			return findPage(pagename);
		}
		if (isRootPath) {
			return findPageIndex(sd3);
		}
		return findPage(sd3, pagename);
	}

	/**
	 * *************************************************************
	 * ---------------------------- OLD WAY ----------------------
	 * *************************************************************
	 */
//	public static Pare<RepoPageDir, Path> findPage2(boolean isRootSubomain, boolean isRootPath, String sd3, String pagename) throws Sd3EE {
//		if (isRootSubomain) {
//			if (isRootPath) {
//				return findPageIndex();
//			} else {
//				return SdFinder.findPage(pagename);
//			}
//		} else if (true) { //from SD3
//			if (isRootPath) {
//				return SdFinder.findPageIndex(sd3);
//			} else {
//				return SdFinder.findPage(sd3, pagename);
//			}
//		}
//
//		if (false) {
//			//
//			//
//			//not work
//			//			EventBae pem = BaePageDataSrv.findPageModel(spVM.getAddress());
//			//			if (pem == null) {
//			//				new PageNotFoundSP(spVM).create(window);
//			//				return null;
//			//			}
//			//			PageState state = BaePageDataSrv.getPageState(pem);
//			//			if (state != PageState.PUBLISH && !spVM.isUserAdmin()) {
//			//				new PageNotFoundSP(spVM).create(window);
//			//				return null;
//			//			}
//			//			Path mainPageDir = BaePageDataSrv.createMainPageDir(pem);
//			return null;
//		}
//		throw NI.stop("ni");
//	}
	public static List<String> getAllSubdomainNames() {
		//return SdIndex.getAllNames();
		return UF.fn(UDIR.ls(AppZosCore.getDomainsRepo(), EFT.DIR));
	}

	@SneakyThrows
	public static Collection<String> getAllPagesOfSd3(String sd3) {
		//		Collection<RepoPageDir> repos = SdFinder.findAllRepos(sd3, Collections.EMPTY_SET);
		//		return repos.isEmpty() ? Collections.EMPTY_SET : repos.stream().flatMap(repo -> repo.getAllPagesNames().stream()).collect(Collectors.toSet());
		return UF.fn(UDIR.ls(RepoPageDir.getSd3RepoPath(sd3, true), EFT.DIR));

	}

	/**
	 * *************************************************************
	 * ---------------------------- REPO --------------------------
	 * *************************************************************
	 */


	public static Pare<RepoPageDir, Path> findPage(Sd3ID sd3ID) throws Sd3EE {
		String[] sd3_page = sd3ID.two;
		IT.isLength(sd3_page, 2);
		return findPage(sd3_page[0], sd3_page[1]);
	}

	public static RepoPageDir findRepo(String sd3) throws Sd3EE {
		//Set<RepoPageDir> allRepos = SdFinder.findAllRepos(sd3);
		return RepoPageDir.ofSd3(sd3);
	}

	/**
	 * *************************************************************
	 * ---------------------------- FIND PAGE MODEL -----------------------
	 * *************************************************************
	 */
	public static PageDirModel getPageModelFromComponent(Component com, PageDirModel... defRq) {
		Path[] attributeFrom = PageDirModel.getAttributeFrom_RepoWithPage(com, null);
		if (attributeFrom != null) {
			return PageDirModel.of(attributeFrom[1]).setRepoPath(attributeFrom[0]);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Except pdm"), defRq);
	}

	public static PageDirModel findPageModel(PagePathInfo ppi, PageDirModel... defRq) throws Sd3EE {
		try {
			Pare<RepoPageDir, Path> repoWithPage = findPage(ppi);
			return PageDirModel.of(repoWithPage.val()).setRepoPath(repoWithPage.key().path());
		} catch (Sd3EE ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("PageDirModel not found by ppi '%s'", ppi), defRq);
		}
	}

	public static Sb buildReport(int level) {
		List<String> sd3s = SdMan.getAllSubdomainNames();
		Sb sb = new Sb();
		for (String sd3 : sd3s) {
			RepoPageDir repoPageDir = RepoPageDir.ofSd3(sd3);
			List<Path> pages = repoPageDir.getAllPagesPath();
			if (X.empty(pages)) {
				sb.TABNL(level, "SubDomain '%s' is empty", sd3);
			} else {
				sb.TABNL(level, "SubDomain '%s' has '%s' pages", sd3, X.sizeOf(pages));
				for (Path page : pages) {
					sb.TABNL(level + 1, "Page '%s'", page.getFileName().toString());
				}
			}
		}
		return sb;
	}

}
