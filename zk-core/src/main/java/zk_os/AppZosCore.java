package zk_os;

import mp.utl_odb.netapp.AppCore;
import mp.utl_odb.mdl.AModel;
import mp.utl_odb.tree.UTree;
import mpc.env.APP;
import mpc.exception.WhatIsTypeException;
import mpu.X;
import mpu.core.ARG;
import mpc.env.AP;
import mpc.env.Env;
import mpc.env.boot.AppBoot;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.fs.UFS_BASE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_old_core.AppCoreStateOLD2;
import zk_old_core.sd.core.SdMan;
import zk_os.api.client.NoteApi;

import java.nio.file.Path;

public class AppZosCore {

	public static final Logger L = LoggerFactory.getLogger(AppZosCore.class);
	public static final AppCore APP_CORE = new AppCore(Env.getAppName());
	public static final AppCore APP_CORE_PAGES = new AppCore(APP_CORE.path(), "zkos.pages");

	//
	public static final UTree TREE_SD3 = UTree.tree(AppZosCore.class, "sd3");
	public static final UTree TREE_PAGES = UTree.tree(AppZosCore.class, "pages");

	public static final UTree TREE_PROPS = UTree.tree(AppZosCore.class, "app.tree.props");
	public static final UTree TREE_QUICK_DATA = UTree.tree(AppZosCore.class, "app.tree.qp");

	public static void regDb(Class<? extends AModel> modelClass, String dbName, boolean... createDbIfNotExist) {
		APP_CORE.regTypeDbEE(modelClass, dbName, createDbIfNotExist);
	}

	public static Path getRpa() {
		return Env.RPA;
	}

	//
	//
	public static void reinitEnv(boolean reindexSd3) {

		Env.initRPA();

		UFS_BASE.MKDIR.mkdirIfNotExist(AppCoreStateOLD2.getRpaSTATE());

		initMasterPage();
		initMasterRepo();
		initRpaUploads();
		initRpaAssets();
		initRpaUsers();

		if (reindexSd3) {
//			SdIndex.reindex(true, true);
		}
	}

	/**
	 * *************************************************************
	 * ---------------------------- MASTER PAGE --------------------------
	 * *************************************************************
	 */
	private static Path masterPage = null;

	static {
		initMasterPage();
	}

	private static void initMasterPage() {
		Path master_page = AP.getAs("page.master", Path.class, null);
		if (master_page == null) {
			master_page = Env.RPA.resolve("domains/master_repo/" + SdMan.PAGE_INDEX);
			if (!UFS.existDir(master_page)) {
				UFS_BASE.MKDIR.mkdirIfNotExist(master_page, 3);
//				FormDirModel.ADD.addHtmlComponent(master_page, FrmEE.createBlankForm());
			}
		}
		setMasterPage(master_page);
	}

	public static Path getMasterPage(Path... defRq) {
		if (masterPage != null) {
			return masterPage;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Index page is null"), defRq);
	}

	public static void setMasterPage(Path masterPage) {
		AppZosCore.masterPage = masterPage;
		if (AppBoot.L.isInfoEnabled()) {
			AppBoot.L.info("Init MASTER-PAGE file://" + AppZosCore.masterPage);
		}
	}

	/**
	 * *************************************************************
	 * ----------------------------  MASTER REPO --------------------------
	 * *************************************************************
	 */
	private static Path masterRepo = null;

	static {
		initMasterRepo();
	}

	private static void initMasterRepo() {
		Path master_repo = AP.getAs("repo.master", Path.class, null);
		if (master_repo == null) {
			master_repo = getMasterPage().getParent();
		} else {
			//UFS.MKDIR.mkdirsIfNotExist(master_repo);
		}
		setMasterRepo(master_repo);
	}

	public static Path getMasterRepo() {
		return masterRepo;
	}

	public static void setMasterRepo(Path masterRepo) {
		AppZosCore.masterRepo = masterRepo;
		if (AppBoot.L.isInfoEnabled()) {
			AppBoot.L.info("Init MASTER-REPO file://" + AppZosCore.masterRepo);
		}
	}

	/**
	 * *************************************************************
	 * ----------------------------  DOMAIN'S REPO --------------------------
	 * *************************************************************
	 */
	public static Path getDomainsRepo() {
		return getMasterRepo().getParent();
	}

	/**
	 * *************************************************************
	 * ----------------------------  RPA USER'S --------------------------
	 * *************************************************************
	 */
	private static Path rpaUsersPath = null;

	public static String rpaUsersDir = ".users";
	public static String rpaUsersDir_ = rpaUsersDir + "/";

	static {
		initRpaUsers();
	}

	private static void initRpaUsers() {
		Path rpa_users = AP.getAs("rpa.users", Path.class, null);
		if (rpa_users == null) {
			rpa_users = Env.RPA.resolve(rpaUsersDir);
			UFS_BASE.MKDIR.mkdirsIfNotExist(rpa_users);
		}
		setRpaUsersPath(rpa_users);
	}

	public static Path getRpaUsersPath() {
		return rpaUsersPath;
	}

	public static void setRpaUsersPath(Path rpaUsers) {
		AppZosCore.rpaUsersPath = rpaUsers;
		AppZosCore.rpaUsersDir = UF.normFile(rpaUsers.getFileName().toString());
		AppZosCore.rpaUsersDir_ = rpaUsersDir + "/";
		if (AppBoot.L.isInfoEnabled()) {
			AppBoot.L.info("Init RPA-Users file://" + AppZosCore.rpaUsersPath);
		}
	}

	/**
	 * *************************************************************
	 * ----------------------------  RPA ASSETS'S --------------------------
	 * *************************************************************
	 */
	private static Path rpaAssetsPath = null;

	public static String rpaAssetsDir = "@@@assets";
	public static String rpaAssetsDir_ = rpaAssetsDir + "/";

	static {
		initRpaAssets();
	}

	private static void initRpaAssets() {
		Path rpa_assets = AP.getAs("rpa.assets", Path.class, null);
		if (rpa_assets == null) {
			rpa_assets = Env.RPA.resolve(rpaAssetsDir);
			UFS_BASE.MKDIR.mkdirsIfNotExist(rpa_assets);
		}
		setRpaAssetsPath(rpa_assets);
	}

	public static Path getRpaAssetsPath() {
		return rpaAssetsPath;
	}

	public static void setRpaAssetsPath(Path rpaAssets) {
		AppZosCore.rpaAssetsPath = rpaAssets;
		AppZosCore.rpaAssetsDir = UF.normFile(rpaAssets.getFileName().toString());
		AppZosCore.rpaAssetsDir_ = rpaAssetsDir + "/";
		if (AppBoot.L.isInfoEnabled()) {
			AppBoot.L.info("Init RPA-ASSETS file://" + AppZosCore.rpaAssetsPath);
		}
	}

	/**
	 * *************************************************************
	 * ----------------------------  RPA UPLOAD'S --------------------------
	 * *************************************************************
	 */
	private static Path rpaUploadPath = null;

	public static String rpaUploadDir = "@@@uploads";
	public static String rpaUploadsDir_ = rpaUploadDir + "/";

	static {
		initRpaUploads();
	}

	private static void initRpaUploads() {
		Path rpa_upload = AP.getAs("rpa.uploads", Path.class, null);
		if (rpa_upload == null) {
			rpa_upload = Env.RPA.resolve(rpaUploadDir);
			UFS_BASE.MKDIR.mkdirsIfNotExist(rpa_upload);
		}
		setRpaUploadsPath(rpa_upload);
	}

	public static Path getRpaUploadPath() {
		return rpaUploadPath;
	}

	public static Path getRpaUploadChild(String uploads_child) {
		return getRpaUploadPath().resolve(uploads_child);
	}

	public static void setRpaUploadsPath(Path rpaUpload) {
		AppZosCore.rpaUploadPath = rpaUpload;
		AppZosCore.rpaUploadDir = UF.normFile(rpaUpload.getFileName().toString());
		AppZosCore.rpaUploadsDir_ = rpaUploadDir + "/";
		if (AppBoot.L.isInfoEnabled()) {
			AppBoot.L.info("Init RPA-UPLOADS file://" + AppZosCore.rpaUploadPath);
		}
	}


	public static UTree TREE_SUBDOMAINS() {
		return APP_CORE.tree("subdomain-data", "simple");
//		return AppBeaCore.APP_CORE.tree(dbFileName);
	}

	public static Path getPageDirOrCreate(String sd3, String pagename) {
		Path pageDir = getPageDir(sd3, pagename);
		UFS_BASE.MKDIR.mkdirsIfNotExist(pageDir);
		return pageDir;
	}

	public static Path getPageDir(String sd3, String pagename) {
		String sd3DirName = X.empty(sd3) ? SdMan.ROOT_SD3_DIR : sd3;
		String pageDirName = X.empty(pagename) ? SdMan.ROOT_SD3_DIR : pagename;
		return AppZosCore.getDomainsRepo().resolve(sd3DirName).resolve(pageDirName);
	}


	public static NoteApi createNoteApiByAlias(String hostAlias) {
		switch (hostAlias) {
			case "zn": {
				return createNoteApi("zznote.ru");
			}
			default:
				throw new WhatIsTypeException(hostAlias);
		}
	}

	public static NoteApi createNoteApi(String host) {
		return new NoteApi(host, AppZosConfig.SUPER_KEY);
	}

	public static NoteApi createNoteApi() {
		Integer portReturnIf80 = APP.getPort_ReturnIf80(null);
		return createNoteApi("localhost" + (portReturnIf80 == null ? "" : ":" + portReturnIf80));
	}
}
