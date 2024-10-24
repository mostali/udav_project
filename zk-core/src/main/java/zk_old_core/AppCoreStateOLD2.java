package zk_old_core;

import zk_os.AppZosCore;

import java.nio.file.Path;

@Deprecated
public class AppCoreStateOLD2 {

	public static final String DIR_STATE = ".state";
	public static final String DIR_COMS = ".coms";
	public static final String DIR_FORMS = ".forms";

	public static String fn(Class com, boolean json) {
		return com.getSimpleName() + (json ? ".json" : ".props");
	}

	public static Path getRpaSTATE() {
		return AppZosCore.getRpa().resolve(DIR_STATE);
	}

//	public static Path getRpaFORMS() {
//		return AppZosCore.getRpa().resolve(DIR_FORMS);
//	}
//
//	public static Path getRpaComs() {
//		return AppZosCore.getRpa().resolve(DIR_COMS);
//	}

	//
	//
	//

	public static Path getPathState_Site(Class statename, boolean json) {
		return getRpaSTATE().resolve(fn(statename, json));
	}

	public static Path getPathState_Page(Class statename, String pagename, boolean json) {
		return getPageDirState(pagename).resolve(fn(statename, json));
	}

	public static Path getPathState_Form(Class statename, String pagename, String formname, boolean json) {
		return getPageDirState(pagename).resolve(formname).resolve(fn(statename, json));
	}

	public static Path getPageDirState(String pagename) {
		return getRpaSTATE().resolve(pagename);
	}


//	public static Path getPathStateSd3(Path repoPageDir, Class com, boolean json) {
//		return repoPageDir.resolve(DIR_NAME).resolve(fn(com, json));
//	}
//
//	public static Path getPathPageStateOf(Path pageDir, Class com, boolean json) {
//		return pageDir.resolve(DIR_NAME).resolve(fn(com, json));
//	}


	//	public static Path getRpaState(String child) {
	//		return getRpaState().resolve(child);
	//	}

}
