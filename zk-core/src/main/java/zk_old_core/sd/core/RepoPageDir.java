package zk_old_core.sd.core;

import lombok.SneakyThrows;
import mpu.core.ARG;
import mpu.IT;
import mpc.fs.*;
import mpc.fs.fd.DIR;
import mpc.fs.fd.EFT;
import mpu.pare.Pare;
import mpc.types.ruprops.RuProps;
import mpu.str.Sb;
import mpu.str.STR;
import mpu.core.QDate;
import mpu.X;
import mpu.core.RW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_old_core.AppZosCore_Old;
import zk_old_core.sd.Sd3EE;
import zk_old_core.app_ds.struct.RepoDS;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static mpu.Sys.e;

public class RepoPageDir {
	public static final Logger L = LoggerFactory.getLogger(RepoPageDir.class);

	private final DIR homeDir;
	public static final String REPO_PROPS = "repo.props";
	public static final String PK_SUBDOMAIN3 = "sd";
	public static final String PK_STATE = "state";
	public static final String PK_PAGE_INDEX = "page.index";
	public static final String PAGENAME_INDEX = "index";

	public static Pare<RepoPageDir, Path> getPageWithRepo(Path repoPages, String pagename, boolean... checkExist) {
		RepoPageDir repoPageDir = of(repoPages, checkExist);
		Path pageDir = getPageDir(repoPages, pagename, checkExist);
		return Pare.of(repoPageDir, pageDir);
	}

	@SneakyThrows
	public static Path getPageDir(Path repoPages, String pagename, boolean... checkExist) {
		return Sd3EE.getPageDir(repoPages, pagename, checkExist);
	}

	public static RepoPageDir getPrimaryRepo() {
		return of(AppZosCore_Old.getMasterRepo());
	}

	@SneakyThrows
	public static RepoPageDir ofSd3(String sd3, boolean... checkExist) {
		return of(getSd3RepoPath(sd3, checkExist));
	}

	@SneakyThrows
	public static Path getSd3RepoPath(String sd3, boolean... checkExist) {
		return Sd3EE.getSd3RepoPath(sd3, checkExist);
	}

	public Path getPageDir(String pagename, boolean... checkExist) {
		return getPageDir(path(), pagename, checkExist);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		RepoPageDir that = (RepoPageDir) o;
		return Objects.equals(homeDir, that.homeDir);
	}

	@Override
	public int hashCode() {
		return Objects.hash(homeDir);
	}

	public RepoPageDir(Path homeDir) {
		this.homeDir = DIR.of(homeDir);
	}

//	public static RepoPageDir of(String sd3, boolean createIfNotExist) {
//		Path repoSd3 = Env.RPA.resolve(sd3);
//		if (UDIR.empty(repoSd3, null, true)) {
//			if (createIfNotExist) {
//				UFS_BASE.MKDIR.mkdirIfNotExist(repoSd3);
//			} else {
//				throw new EmptyRuntimeException("Repo '%s' is not exist", repoSd3);
//			}
//		}
//		return new RepoPageDir(repoSd3);
//	}

	@SneakyThrows
	public static RepoPageDir of(Path repoPage, boolean... checkExist) {
		if (ARG.isDefEqTrue(checkExist)) {
			Sd3EE.checkExistRepoDir(repoPage);
		}
		return new RepoPageDir(repoPage);
	}

	public static StringBuilder buildReport(RepoPageDir repoPageDir, int tabLevel) {
		String TAB = STR.TAB(tabLevel);
		String TAB2 = STR.TAB(tabLevel + 1);
		String TAB3 = STR.TAB(tabLevel + 2);
		Sb sb = new Sb();
		List<Path> paths = repoPageDir.getAllPagesPath();
		int countRepos = X.sizeOf(paths);
		switch (countRepos) {
			case 0:
			case 1:
			default:
				sb.append(TAB).appendf("Repo '%s' - %s pages", repoPageDir.name() + ":" + repoPageDir.path(), countRepos).NL();
				for (Path path : paths) {
					sb.append(TAB2).appendf("Page >> '%s'", path.getFileName()).NL();
				}
				break;
		}
		sb.deleteLastChar();
		return sb.to();

	}

	public String getSubdomain3(String... defRq) {
//		String sd3 = getProps(false).getString(PK_SUBDOMAIN3, null);
//		if (sd3 != null) {
//			return sd3;
//		}
		if (isMasterRepo()) {
			return SdMan.ROOT_SD3_DIR;
		}
		return path().getFileName().toString();
//		AppZosCore.getDomainsRepo().relativize(path())

//		sd3 = findSubdomain3(AppZosCore.getDomainsRepo(), path());
//		if (X.notEmpty(sd3)) {
//			return sd3;
//		}
//		return ARG.toDefThrow(() -> new RequiredRuntimeException("Subdomain name not found for path '%s' and domains '%s'", AppZosCore.getDomainsRepo(), path()), defRq);
//		return ARG.toDefRq(defRq);
	}

//	public static String findSubdomain3(Path domainsDir, Path repoPage) {
//		Path relativize = domainsDir.relativize(repoPage);
//		return relativize.getName(0).toString();
//	}

	public boolean isMasterRepo() {
		return AppZosCore_Old.getMasterRepo().equals(path());
	}

	public RuProps getProps(boolean syncWrite) {
		return getProps(homeDir.path(), syncWrite);
	}

	public static RuProps getProps(Path repoDir, boolean syncWrite) {
		return RW.readAs(repoDir.resolve(REPO_PROPS), RuProps.class).syncWrite(ARG.isDefEqTrue(syncWrite));
	}

	public List<Path> getAllPagesPath() {
		return homeDir.getChilds(EFT.DIR, LS_SORT.NATURAL);
	}

	public List<String> getAllPagesNames() {
		return getAllPagesPath().stream().map(p -> p.getFileName().toString()).collect(Collectors.toList());
	}

	public Path findPagePath(String pagename, Path... defRq) {
		for (Path path : getAllPagesPath()) {
			if (path.getFileName().toString().equals(pagename)) {
				return path;
			}
		}
		return ARG.toDefRq(defRq);
	}

	public Path path() {
		return homeDir.path();
	}

	public DIR dir() {
		return homeDir;
	}

	public RepoPageDir setPropertyState(String state) {
		getProps(true).setString(PK_STATE, state);
		return this;
	}

	public RepoPageDir setPropertySubdomain3(String subdomain3) {
		Sd3EE.checkSd3Name(subdomain3);
		getProps(true).setString(PK_SUBDOMAIN3, subdomain3);
		return this;
	}

	/**
	 * *************************************************************
	 * ---------------------------- MOVE PAGE --------------------------
	 * *************************************************************
	 */

	@Deprecated
	public static void movePageOrDelete(RepoPageDir repo, Path pageDir, boolean moveToArchiveOrDelete) throws IOException {
		if (moveToArchiveOrDelete) {
			movePageToArchive(repo, pageDir);
			if (L.isInfoEnabled()) {
				L.info("Page file://" + pageDir + "' removed, from repo file://" + repo.path() + "");
			}
		} else {
			UFS_BASE.RM.deleteDir_(pageDir);
			if (L.isInfoEnabled()) {
				L.info("Page file://" + pageDir + "' archived, from repo file://" + repo.path() + "");
			}
		}
	}

	public static void movePageToArchive(RepoPageDir repo, Path srcPageDir) {
		IT.isDirExist(srcPageDir);
		String dstFileName = srcPageDir.getFileName().toString() + "--ARCH--" + QDate.now().f(QDate.F.MONO15_SEC);
		Path dst = repo.getDirArchive().resolve(dstFileName);
		UFS_BASE.MV.move(srcPageDir, dst, false);
	}

	/**
	 * *************************************************************
	 * ---------------------------- TO STRING --------------------------
	 * *************************************************************
	 */
	@Override
	public String toString() {
		return path().toString();
	}

	@SneakyThrows
	public boolean existPage(String pagename) {
		return Sd3EE.checkExistPageDir(path(), pagename, true);
	}

	public Path getPropsIndex(Path... defRq) {
		String pageIndexName = getProps(false).getString(PK_PAGE_INDEX, null);
		if (pageIndexName != null) {
			return path().resolve(pageIndexName);
		}
		return ARG.toDefRq(defRq);
	}

	public Path getDirArchive() {
		return RepoDS.ARCHIVE.getPropsPath(path());
	}

	public String name() {
		return path().getFileName().toString();
	}

	public Path renamePage(String srcPageName, String dstPageName) {
		Path dst = RepoDS.SELF.rename(path(), srcPageName, dstPageName);
		if (L.isInfoEnabled()) {
			String msg = X.fl("SubDomain '{}'. Page '{}' renamed to '{}'", path(), srcPageName, dstPageName);
			L.info(msg);
		}
		return dst;
	}

	public Path renameMe(String dstRepoName) {
		Path dst = RepoDS.SELF.renameMe(path(), dstRepoName);
		if (L.isInfoEnabled()) {
			String msg = X.fl("Repo '{}' renamed to '{}'", path(), dst);
			L.info(msg);
		}
		return dst;
	}

	public Path moveToMe(Path path) {
		Path dst = RepoDS.SELF.moveToMe(path(), path);
		if (L.isInfoEnabled()) {
			String msg = X.fl("SubDomain '{}'. Page '{}' move to '{}'", path(), path, dst);
			L.info(msg);
		}
		return dst;
	}

	public boolean exist() {
		return Files.isDirectory(path());
	}

	public void mkRepoDir() {
		UFS_BASE.MKDIR.createDirs(path());
	}

	public Path mkPageDir(String pagename) {
		Sd3EE.checkPagename(pagename, true);
		Path newDir = path().resolve(pagename);
		UFS_BASE.MKDIR.createDirs(newDir);
		return newDir;
	}

//	public static class ADD {
//
//		public static Path addPageWithHtmlComponent(PageDirModel pdm, String new_form_html) {
//			return addPageWithHtmlComponent(pdm.dir(), new_form_html);
//		}
//
//		public static Path addPageWithHtmlComponent(Path pageDir, String form_html) {
//			return addPageWithHtmlComponent(DIR.of(pageDir), form_html);
//		}
//
//		public static Path addPageWithHtmlComponent(DIR pageDir, String form_html) {
//			List<Path> bodyComs = PageDirModel.getBodyChilds(pageDir, null, null);
//			String newName;
//			if (X.empty(bodyComs)) {
//				newName = "10";
//			} else {
//				newName = FrmEE.incrementNextFormName(AR.last(bodyComs));
//			}
//
//			Path form = PageDirDS.BODY.writeToDir(pageDir.path(), newName, newName + ".html", form_html, true);
//			if (DirModel.L.isInfoEnabled()) {
//				String msg = U.fl("CreateAndAddNewHtmlComponent '{}'\n{}", newName, form_html);
//				DirModel.L.info(msg);
//				ZKNotify.info(msg);
//			}
//			return form;
//		}
//	}
}
