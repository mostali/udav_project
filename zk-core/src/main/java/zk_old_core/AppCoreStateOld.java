package zk_old_core;

import mpu.pare.Pare;
import mpe.state_rw.IMapStateRw;
import mpe.state_rw.MapStateRw;
import zk_old_core.sd.core.RepoPageDir;
import zk_old_core.sd.core.SdMan;

import java.nio.file.Path;

@Deprecated
public class AppCoreStateOld {

	public static final String DIR_NAME = ".state";

	public static String fn(Class com, boolean json) {
		return com.getSimpleName() + (json ? ".json" : ".props");
	}

	//
	//
	//

	public static IMapStateRw getStateGlobal(Class key, boolean json) {
		Path fileState = AppCoreStateOLD2.getPathState_Site(key, json);
		IMapStateRw tmState = MapStateRw.loadStateRw(fileState, json, true);
		return tmState;
	}

	public static IMapStateRw getStateSd3(String sd3, Class comClass, boolean json) {
		RepoPageDir repoPageDir = RepoPageDir.ofSd3(sd3, true);
		Path fileState = getPathStateSd3(repoPageDir.path(), comClass, json);
		IMapStateRw tmState = MapStateRw.loadStateRw(fileState, json, true);
		return tmState;
	}

	public static IMapStateRw getStatePage(String sd3, String pagename, Class comClass, boolean json) {
		Pare<RepoPageDir, Path> page = SdMan.findPage(sd3, pagename);
		Path fileState = getPathPageStateOf(page.getVal(), comClass, json);
		IMapStateRw tmState = MapStateRw.loadStateRw(fileState, json, true);
		return tmState;
	}

	//
	//
	//

//	public static Path getPathState_Page(Class com, String pagename, boolean json) {
//		return getRpaSiteState().resolve(pagename).resolve(fn(com, json));
//	}
//
//	public static Path getPathState_Site(Class com, boolean json) {
//		return getRpaSiteState().resolve(fn(com, json));
//	}

	public static Path getPathStateSd3(Path repoPageDir, Class com, boolean json) {
		return repoPageDir.resolve(DIR_NAME).resolve(fn(com, json));
	}

	public static Path getPathPageStateOf(Path pageDir, Class com, boolean json) {
		return pageDir.resolve(DIR_NAME).resolve(fn(com, json));
	}

//	public static Path getRpaSiteState() {
//		return AppZosCore.getRpa().resolve(DIR_NAME);
//	}

	//	public static Path getRpaState(String child) {
	//		return getRpaState().resolve(child);
	//	}

}
