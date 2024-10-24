package zk_old_core.sd;

import lombok.SneakyThrows;
import mpu.core.ARG;
import mpu.IT;
import mpc.exception.*;
import mpc.fs.UFS;
import mpc.fs.UFS_BASE;
import mpc.log.L;
import mpu.pare.Pare;
import mpu.X;
import zk_old_core.AppZosCore_Old;
import zk_old_core.sd.core.RepoPageDir;
import zk_old_core.sd.core.Sd3ID;
import zk_old_core.sd.core.SdMan;
import zk_page.core.PageRoute;
import zk_old_core.mdl.FormDirModel;
import zk_old_core.mdl.PageDirModel;
import zk_old_core.std_core.FrmEE;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author dav 05.04.2022   23:48
 */
public class Sd3EE extends EException {

	public static void main(String[] args) throws Sd3EE, IOException {

//		P.exit(Subdomain3.buildReport(0));
//		P.exit(SdFinder.findPage("w1", "test"));
//		Subdomain3.removeRepoIndex();
//		Subdomain3.addRepoToSubdomain3(sd3, "/home/dav/.data/sd3_q", true);

//		Subdomain3.init();
//		P.exit(Subdomain3.getAll());
//		Set<RepoPageDir> set = Subdomain3.findReposOfSubdomain3("q");
		String pagename = "test";
		createCom("w1", pagename, FrmEE.createBlankForm(), true, true);

//		P.exit();
	}

	@SneakyThrows
	public static Path getPageDir(Path repoPages, String pagename, boolean... checkExist) {
		Path pageDir = repoPages.resolve(pagename);
		if (ARG.isDefEqTrue(checkExist)) {
			try {
				checkExistPageDir(pageDir);
			} catch (Sd3EE ee) {
				switch (ee.type()) {
					case SD3_PAGE_NOTFOUND:
						if (SdMan.PAGE_INDEX.equals(pagename)) {
							throw EE.SD3_PAGE_INDEX_NOTFOUND.I(pagename);
						}
					default:
						throw ee;
				}
			}
		}
		return pageDir;
	}

	@SneakyThrows
	public static Path getSd3RepoPath(String sd3, boolean... checkExist) {
		Path sd3Dir = AppZosCore_Old.getDomainsRepo().resolve(sd3);
		if (ARG.isDefEqTrue(checkExist)) {
			checkExistRepoDir(sd3Dir);
		}
		return sd3Dir;
	}

	public static boolean checkPagename(String pagename, boolean... RETURN) {
		try {
			IT.isFilename(pagename);
			return true;
		} catch (Exception ex) {
			if (ARG.isDefEqTrue(RETURN)) {
				return false;
			}
			throw ex;
		}
	}

	public enum RepoState {
		EDIT, PUB, DEL, ARCH;

		public void change(Pare<RepoPageDir, Path> repo_page) throws IOException {
			change(repo_page.getKey(), repo_page.getVal());
		}

		public void change(RepoPageDir repo, Path page) throws IOException {
			switch (this) {
				case PUB:
				case EDIT:
					repo.setPropertyState(name());
					break;
				case ARCH:
					RepoPageDir.movePageOrDelete(repo, page, false);
					break;
				case DEL:
					RepoPageDir.movePageOrDelete(repo, page, true);
					break;
				default:
					throw new WhatIsTypeException(this);

			}
		}

	}

	public enum PageState {
		EDIT, PUB, DEL, ARCH;

		public void change(Pare<RepoPageDir, Path> repo_page) throws IOException {
			change(repo_page.getKey(), repo_page.getVal());
		}

		public void change(RepoPageDir repo, Path page) throws IOException {
			switch (this) {
				case PUB:
				case EDIT:
					PageDirModel.of(page).setProperty_State(name());
					break;
				case ARCH:
					RepoPageDir.movePageOrDelete(repo, page, false);
					break;
				case DEL:
					RepoPageDir.movePageOrDelete(repo, page, true);
					break;
				default:
					throw new WhatIsTypeException(this);

			}
		}

	}

	/**
	 * *************************************************************
	 * ---------------------------- COMPONENT -----------------------
	 * *************************************************************
	 */

	public static Path createCom(String sd3, String pagename, String form_html, boolean createSd3IfNotExist, boolean createPageIfNotExist) throws Sd3EE {
		Pare<RepoPageDir, Path> repoPage = SdMan.findPage_(sd3, pagename);
		if (repoPage != null) {
			return repoPage.getVal();
		} else if (!createPageIfNotExist) {
//			throw EE.SD3_PAGE_NOTFOUND.I(sd3 + "@" + pagename);
			throw new FIllegalStateException("Page '%s' not found", sd3 + "@" + pagename);
		}
		Path page = getPageOrCreate(sd3, pagename, createSd3IfNotExist);
		//RepoPageDir defRepo = getRepoOrCreate(sd3);
		//Path pageDir = createPageDir(defRepo.path(), pagename);
		FormDirModel.ADD.addHtmlComponent(IT.NN(page), form_html);
		return page;
	}


	/**
	 * *************************************************************
	 * ---------------------------- PAGE -----------------------
	 * *************************************************************
	 */

	public static boolean checkExistPageDir(Path repoPage, String pagename, boolean... RETURN) throws Sd3EE {
		return checkExistPageDir(RepoPageDir.getPageDir(repoPage, pagename), RETURN);
	}

	public static boolean checkExistPageDir(Path pagepath, boolean... RETURN) throws Sd3EE {
		boolean exist = UFS.existDir(pagepath);
		if (exist || ARG.isDefEqTrue(RETURN)) {
			return exist;
		}
		throw Sd3EE.EE.SD3_PAGE_NOTFOUND.I(pagepath.getFileName().toString());
	}

	public static boolean checkNotExistPageDir(Path repoPage, String pagename, boolean... RETURN) throws Sd3EE {
		boolean not_exists = !checkExistPageDir(repoPage, pagename, true);
		if (not_exists || ARG.isDefEqTrue(RETURN)) {
			return not_exists;
		}
		throw Sd3EE.EE.SD3_PAGE_EXIST.I(pagename);
	}

//	public static Path getPageDirOrCreate(Path repoPage, String pagename) throws Sd3EE {
//		boolean exist = checkExistPageDir(repoPage, pagename, true);
//		if (exist) {
//			return RepoPageDir.getPageDir(repoPage, pagename);
//		}
//		return createPageDir(repoPage, pagename);
//	}

//	public static Path createPageDir(Path repoPage, String pagename) throws Sd3EE {
//		checkNotExistPageDir(repoPage, pagename);
//		Path newPage = RepoPageDir.getPageDir(repoPage, pagename);
//		try {
//			UFS_BASE.MKDIR.createDir_(newPage);
//		} catch (IOException e) {
//			throw EE.SD3_PAGE_CREATE.I(e, "error create page-dir '%s'", newPage);
//		}
//		return newPage;
//	}

	//	public static RepoPageDir getFirstRepoSd3(String sd3, boolean createSd3IfNotExist) throws Sd3EE {
//		try {
//			return SdFinder.findFirstRepo(sd3);
//		} catch (Sd3EE e) {
//			if (!e.is(EE.SD3_REPO_NOTFOUND) || !createSd3IfNotExist) {
//				throw e;
//			}
//		}
//		//REPO NOT FOUND
//		return createRepoDirInDefaultLocation(sd3);
//	}
	public static Path getPageOrCreate(PageRoute pageRoute) throws Sd3EE {
		String pagename = X.empty(pageRoute.pagename()) ? SdMan.PAGE_INDEX : pageRoute.pagename();
		String sd3 = X.empty(pageRoute.sd3()) ? SdMan.ROOT_SD3_DIR : pageRoute.sd3();
		return Sd3EE.getPageOrCreate(sd3, pagename, true);
	}

	public static Path getPageOrCreate(Sd3ID sd3ID, boolean createSd3IfNotExist) throws Sd3EE {
		if (sd3ID.isSingleSd3()) {
			throw EE.SD3_PAGE_CREATE.I("Set pagename");
		}
		return getPageOrCreate(sd3ID.sd3(), sd3ID.page(), createSd3IfNotExist);
	}

	public static Path getPageOrCreate(String sd3, String pagename, boolean createSd3IfNotExist) throws Sd3EE {
		Sd3EE.checkNotExistPage(sd3, pagename);
		RepoPageDir repoSd3 = RepoPageDir.ofSd3(sd3);
		if (repoSd3.exist()) {
			//OK
		} else if (!createSd3IfNotExist) {
			throw EE.SD3_NOTFOUND.I("Repo sd3 '" + sd3 + "' not exist");
		} else {
			repoSd3.mkRepoDir();
		}
		Path newPageDir = repoSd3.mkPageDir(pagename);
		if (L.isInfoEnabled()) {
			L.info("Create new page {}@{}", sd3, pagename);
		}
		Path path = FormDirModel.ADD.addHtmlComponent(newPageDir, FrmEE.createBlankForm());
		return path;
	}

	public static boolean checkExistPage(Sd3ID sd3ID, boolean... RETURN) throws Sd3EE {
		return checkExistPage(sd3ID.sd3(), sd3ID.page(), RETURN);
	}

	public static boolean checkExistPage(String sd3, String pagename, boolean... RETURN) throws Sd3EE {
		try {
			RepoPageDir.getPageDir(RepoPageDir.getSd3RepoPath(sd3), pagename, true);
			return true;
		} catch (Exception ee) {
			if (ARG.isDefEqTrue(RETURN)) {
				return false;
			}
			throw ee;
		}
	}

	public static boolean checkNotExistPage(Sd3ID sd3ID, boolean... RETURN) throws Sd3EE {
		return checkNotExistPage(sd3ID.sd3(), sd3ID.page(), RETURN);
	}

	public static boolean checkNotExistPage(String sd3, String pagename, boolean... RETURN) throws Sd3EE {
		boolean not_exist = !checkExistPage(sd3, pagename, true);
		if (not_exist || ARG.isDefEqTrue(RETURN)) {
			return not_exist;
		}
		throw EE.SD3_PAGE_EXIST.I(sd3 + "@" + pagename);
	}

	/**
	 * *************************************************************
	 * ---------------------------- SD3 -----------------------
	 * *************************************************************
	 */
	public static boolean checkExistSd3(Sd3ID sd3, boolean... RETURN) throws Sd3EE {
		return checkExistSd3(sd3.sd3(), RETURN);
	}

	public static boolean checkExistSd3(String sd3, boolean... RETURN) throws Sd3EE {
//		RepoPageDir.getSd3RepoPath(sd3, true);
//		boolean rslt = X.notEmpty(SdIndex.getAllOrInitCache().get(sd3));
		boolean rslt = checkExistRepoDir(RepoPageDir.getSd3RepoPath(sd3), true);
		if (rslt || ARG.isDefEqTrue(RETURN)) {
			return rslt;
		}
		throw Sd3EE.EE.SD3_NOTFOUND.I(sd3);
	}

	public static boolean checkNotExistSd3(Sd3ID sd3, boolean... RETURN) throws Sd3EE {
		return checkNotExistSd3(sd3.sd3(), RETURN);
	}

	public static boolean checkNotExistSd3(String sd3, boolean... RETURN) throws Sd3EE {
		boolean not_exist = !checkExistSd3(sd3, true);
		if (not_exist || ARG.isDefEqTrue(RETURN)) {
			return not_exist;
		}
		throw EE.SD3_EXIST.I(sd3);
	}

	/**
	 * *************************************************************
	 * ---------------------------- REPO -----------------------
	 * *************************************************************
	 */

	public static RepoPageDir createRepoDirInDefaultLocation(String sd3) throws Sd3EE {
		return createRepoDir(sd3, getDefaultLocationSd3(sd3));
	}

	public static RepoPageDir createRepoDir(String sd3, Path repoDir) throws Sd3EE {
		checkSd3Name(sd3);
		checkNotExistRepoDir(repoDir);
		try {
			UFS_BASE.MKDIR.createDirs_(repoDir, true);
		} catch (IOException e) {
			throw EE.SD3_REPO_CREATE.I(e, "error create dir '%s' for sd3 '%s'", repoDir, sd3);
		}
		//RepoPageDir repoPage = RepoPageDir.of(repoDir).setPropertySubdomain3(sd3);
		//SdIndex.addToIndex(repoPage, false, true);
		return RepoPageDir.of(repoDir);
	}


	public static boolean checkExistRepoDir(Path location, boolean... RETURN) throws Sd3EE {
		boolean rslt = UFS.existDir(location);
		if (rslt || ARG.isDefEqTrue(RETURN)) {
			return rslt;
		}
		throw Sd3EE.EE.SD3_REPO_NOTFOUND.I("Repo '%s' not found", location);
	}

	public static boolean checkNotExistRepoDir(Path repoDir, boolean... RETURN) throws Sd3EE {
		boolean no_exist = !checkExistRepoDir(repoDir, true);
		if (no_exist || ARG.isDefEqTrue(RETURN)) {
			return no_exist;
		}
		throw Sd3EE.EE.SD3_REPO_EXIST.I("Repo '%s' already exist", repoDir);
	}

	public static Path getDefaultLocationSd3(String sd3) {
		Path defLocation = getDefaultLocationSd3_PARENT().resolve(sd3);
		return defLocation;
	}

	public static Path getDefaultLocationSd3_PARENT() {
		return RepoPageDir.getPrimaryRepo().path().getParent();
	}

	public static void checkSd3Name(String sd3) {
		for (int i = 0; i < sd3.length(); i++) {
			switch (sd3.charAt(i)) {
				case '/':
				case '\\':
				case '?':
					throw new FIllegalArgumentException("Sd3 '%s' contains illegal character '%s' at index '%s' ", sd3, sd3.charAt(i), i);
			}
		}
//		UC.isWord(sd3);
	}

	/**
	 * *************************************************************
	 * ---------------------------- INIT --------------------------
	 * *************************************************************
	 */
	@Override
	public Sd3EE.EE type() {
		return super.type(Sd3EE.EE.class);
	}

	public enum EE {
		NOSTATUS,
		SD3_NOTFOUND, SD3_EXIST, //
		SD3_REPO_CREATE, SD3_REPO_NOTFOUND, SD3_REPO_EXIST,//
		SD3_PAGE_CREATE, SD3_PAGE_EXIST, SD3_PAGE_NOTFOUND,//
		SD3_PAGE_INDEX_NOTFOUND,//

		;


		public Sd3EE I() {
			return new Sd3EE(this);
		}

		public Sd3EE I(Throwable ex) {
			return new Sd3EE(this, ex);
		}

		public Sd3EE I(Throwable ex, String msg, Object... args) {
			return new Sd3EE(this, new SimpleMessageRuntimeException(ex, msg, args));
		}

		public Sd3EE I(String message) {
			return new Sd3EE(this, new SimpleMessageRuntimeException(message));
		}

		public Sd3EE I(String message, Object... args) {
			return new Sd3EE(this, new SimpleMessageRuntimeException(X.f(message, args)));
		}

		public Sd3EE M(String message, Object... args) {
			return new Sd3EE(this, new CleanMessageRuntimeException(X.f(message, args)));
		}
	}

	public Sd3EE() {
		super(Sd3EE.EE.NOSTATUS);
	}

	public Sd3EE(Sd3EE.EE error) {
		super(error);
	}

	public Sd3EE(Sd3EE.EE error, Throwable cause) {
		super(error, cause);
	}


}
