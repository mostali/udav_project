package zk_old_core.app_ds.struct;

import lombok.SneakyThrows;
import mpc.env.AppProfile;
import mpc.fs.FileLines;
import zk_old_core.app_ds.AppDS;
import mpu.pare.Pare;
import zk_old_core.sd.Sd3EE;
import zk_old_core.sd.core.RepoPageDir;

import java.nio.file.Path;

public class RepoDS extends AppDS {

	public static final RepoDS SELF = new RepoDS(".");

	public static final String FN_INDEX = ".index";

	public RepoDS(String page) {
		super(page);
	}

//	public RepoDS(String page, boolean isFile) {
//		super(page, isFile);
//	}
	//	public static final RepoDS body = new RepoDS("body");
//	public static final RepoDS INDEX = new RepoDS(FN_INDEX);


	public FileLines getRepoIndex(Path struct, FileLines... defRq) {
		return getAs(struct, AppProfile.prod_local.isActive() ? AppProfile.prod_local + FN_INDEX : FN_INDEX, FileLines.class, defRq);
	}

	@SneakyThrows
	public Pare<RepoPageDir, Path> getPage(Path repo, String pagename, Pare<RepoPageDir, Path>... defRq) {

		Sd3EE.checkExistRepoDir(repo);
		Path pagepath = repo.resolve(pagename);
		Sd3EE.checkExistRepoDir(pagepath);
		return Pare.of(RepoPageDir.of(repo, true), pagepath);
	}

//	public Pare<RepoPageDir, Path> getPage_(Path repo, String pagename) throws Sd3EE {
//		return RepoPageDir.of(repo, true).getPageDir(pagename, true);
//	}
}
