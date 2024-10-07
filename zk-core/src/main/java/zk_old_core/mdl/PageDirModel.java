package zk_old_core.mdl;

import lombok.SneakyThrows;
import mpc.exception.WrongLogicRuntimeException;
import mpc.exception.NI;
import mpc.fs.LS_SORT;
import mpc.fs.fd.DIR;
import mpc.fs.fd.EFT;
import mpu.pare.Pare;
import mpu.str.Sb;
import mpu.str.STR;
import mpu.core.ARG;
import mpu.X;
import mpu.str.USToken;
import org.zkoss.zk.ui.Component;
import zk_old_core.mdl.pageset.PageSet;
import zk_old_core.old.fswin.FsWin;
import zk_old_core.std_core.CType;
import zk_old_core.AppCoreStateOld;
import zk_old_core.sd.core.RepoPageDir;
import zk_old_core.sd.core.SdMan;
import zk_old_core.app_ds.struct.PageDirDS;
import zk_page.ZKC;
import zk_old_core.mdl.pageset.IFormModel;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class PageDirModel extends DirModel {


	public static void main(String[] args) {
//		String page = "/home/dav/.repo.zkapp/test";
//		String page = "/home/dav/.repo.zkapp/index";
		String page = "/home/dav/.data/bea/domains/master_repo/index/";
		PageDirModel pageDir = PageDirModel.of(page);
//		List<Path> freshedPaths = PageDirModel.reindex(pageDir.path());
//		U.exit(freshedPaths);
//		U.exit(pageDir.getAllBodyFiles(null, "10-part1"));
//		U.exit(pageDir.getPageSet()tgetBodyChilds(null));
	}

//	public static void info(PageDirModel pageDir) {
//		pageDir.name()
//	}


	public static final String ATTR_KEY = "__PDM__";

	public static final String RPD_BODY = "body";
	public static final String RPD_HEAD = "head";
	public static final String RPD_META = "meta";

	public static final String PK_STATE = "state";

	private String repoPathStr;
	private transient Path repoPath;

	public static String getPageName(Path page) {
		return page.getFileName().toString();
	}

	@SneakyThrows
	public static Pare<RepoPageDir, Path> getCurrentRepoWithPage() {
		PageDirModel pageDirModel = PageDirModel.get();
		return Pare.of(pageDirModel.getRepo(), pageDirModel.path());
	}

	public PageDirModel setRepoPath(Path repoPath) {
		this.repoPathStr = repoPath.toString();
		this.repoPath = repoPath;
		return this;
	}

	public Path getRepoPath() {
		return repoPath == null ? repoPath = Paths.get(repoPathStr) : repoPath;
	}


	public static PageDirModel get(PageDirModel... defRq) {
		return SdMan.getPageModelFromComponent(ZKC.getFirstWindow(), defRq);
	}

	public static List<Path> reindex(Path pageDir) {
		PageDirModel pageDirModel = PageDirModel.of(pageDir);
		List<IFormModel> forms = pageDirModel.getPageSet().getIForms();
		List<Path> removedProps = new LinkedList<>();
		for (IFormModel fm : forms) {
			if (fm instanceof FormDirModel) {
				FormDirModel fdm = (FormDirModel) fm;
				removedProps.add(fdm.getFileRootProps());
				fdm.setPropertyCtype(null);
			} else {
				NI.stop("impl ffm:" + fm);
			}
		}
		return removedProps;
	}

	public void setAttributeTo(Component com) {
		setAttributeTo(com, ATTR_KEY);
	}

	@Override
	public String attrKey_Root1() {
		String key1 = repoPathStr;
		String key2 = super.attrKey_Root1();
		return key1 + ":!:::!:" + key2;
	}

	public static Path[] getAttributeFrom_RepoWithPage(Component com, Path[]... defRq) {
		String attributeFromComAsPath = ZKC.getAttributeFromCom(com, ATTR_KEY, null);
		if (attributeFromComAsPath != null) {
			String[] two = USToken.two(attributeFromComAsPath, ":!:::!:", null);
			if (two == null) {
				throw new WrongLogicRuntimeException("Need delimetr ':::'");
			}
			Path pathRepo = Paths.get(two[0]);
			Path pathPage = Paths.get(two[1]);
			return new Path[]{pathRepo, pathPage};
		}
		return ARG.toDefRq(defRq);
	}

	public PageDirModel(Path rootPagePath) {
		super(rootPagePath);
	}

	public static PageDirModel of(String pathUsrDir) {
		return new PageDirModel(Paths.get(pathUsrDir));
	}

	public static PageDirModel of(File file) {
		return of(file.toPath());
	}

	public static PageDirModel of(Path file) {
		return new PageDirModel(file);
	}

	/**
	 * *************************************************************
	 * ------------------------ Get Child's ------------------------
	 * *************************************************************
	 */

	public List<Path> getBodyChilds(EFT fileType, List<Path>... defRq) {
		return getBodyChilds(dir(), fileType, defRq);
	}

	public List<Path> getRootChilds(EFT fileType, List<Path>... defRq) {
		return getRootChilds(dir(), fileType, defRq);
	}


	public static List<Path> getBodyChilds(DIR pageDir, EFT fileType, List<Path>... defRq) {
		return pageDir.getChilds(fileType, RPD_BODY, LS_SORT.NATURAL, defRq);
	}

	public static List<Path> getRootChilds(DIR pageDir, EFT fileType, List<Path>... defRq) {
		return pageDir.getChilds(fileType, LS_SORT.NATURAL, defRq);
	}

	public List<Path> getAllHeadFiles(EFT fileType, List<Path>... defRq) {
		return getAllHeadFiles(fileType, null, defRq);
	}

	public List<Path> getAllHeadFiles(EFT fileType, String childFd, List<Path>... defRq) {
		return dir().getChilds(fileType, RPD_HEAD, LS_SORT.NOT, childFd, defRq);
	}

	/**
	 * *************************************************************
	 * --------------------------- Page Set ------------------------
	 * *************************************************************
	 */

	transient PageSet pageSet = null;

	public PageSet getPageSet(boolean... fresh) {
		if (this.pageSet != null && ARG.isDefNotEqTrue(fresh)) {
			return this.pageSet;
		}
		PageSet pageSet = PageSet.explodePageDirModel(this.getMapExt());
		return this.pageSet = pageSet;
	}


	public List<String> getFormTypesAndRealtivePaths() {
		Path page = path();
		return getPageSet().getIForms().stream().map(f -> f.ctype(CType.UNDEFINED) + ":" + page.relativize(f.fd().path())).collect(Collectors.toList());
	}

	public List<String> getHeadTypeAndNames() {
		Path page = path();
		return getPageSet().getIStaticHeads().stream().map(h -> h.getHeadType() + ":" + page.relativize(h.path())).collect(Collectors.toList());
	}

	public FdModel findFormModel(String name, FormDirModel... defRq) {
		for (IFormModel formDirModel : getPageSet().getIForms()) {
			if (name.equals(formDirModel.fd().name())) {
				return formDirModel.fd();
			}
		}
		return ARG.toDefRq(defRq);
	}


	public void addHtmlComponent(String new_form_html) {
		FormDirModel.ADD.addHtmlComponent(this, new_form_html);
	}

	public void setProperty_State(String name) {
		getRootProps().put(PageDirModel.PK_STATE, name);
		map_write(true);
	}

	public RepoPageDir getRepo() {
		return RepoPageDir.of(getRepoPath());
	}

	@Override
	public Path getFileRootProps() {
		return PageDirDS.SELF.getJsonOrPropsPath(path());
	}

//	public Path getFileHeadHtml() {
//
//		getPageSet().getIStaticHeads().f
//		return PageDirDS.SELF.getJsonOrPropsPath(path());
//	}


	public Path getFileState(Class com, boolean json) {
		return AppCoreStateOld.getPathPageStateOf(path(), com, json);
	}

	public Path getFileStateFs() {
		return getFileState(FsWin.class, false);
//		return PageDirDS.meta.getPathFile_FsWin(path());
	}

//	public Path getFileStateMwin() {
//		return PageDirDS.meta.getPathFile_MWin(path());
//	}

	//use PageSet
//	public Path getFilePageCss() {
//		return PageDirDS.head.getPathFile_PageCss(path());
//	}
//
//	public Path getFilePageJs() {
//		return PageDirDS.head.getPathFile_PageJs(path());
//	}

	public static Sb buildReport(PageDirModel pageDirModel, int tabLevel) {
		String TAB = STR.TAB(tabLevel);
		String TAB2 = STR.TAB(tabLevel + 1);
		String TAB3 = STR.TAB(tabLevel + 2);

		Sb sb = new Sb();

		Path path = pageDirModel.path();
		List<Path> all = pageDirModel.getBodyChilds(null);
		sb.append(TAB).append(path.getFileName()).append(":").append(path).append("(").append(X.sizeOf(all)).append(")").NL();
		if (X.notEmpty(all)) {
			for (Path form : all) {
				sb.append(TAB2).append(form.getFileName()).NL();
			}
		}
		return sb;
	}

	@Override
	public String toString() {
		return X.f("PageDirModel:%s", STR.pfile(path()));
	}

}
