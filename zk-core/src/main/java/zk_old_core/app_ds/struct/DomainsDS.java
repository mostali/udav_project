package zk_old_core.app_ds.struct;

import zk_old_core.app_ds.AppDS;

public class DomainsDS extends AppDS {

	public static final DomainsDS SELF = new DomainsDS(".");

	public DomainsDS(String page) {
		super(page);
	}

//
//
//	public FileLines getRepoIndex(Path struct, FileLines... defRq) {
//		return getAs(struct, AppProfile.prod_local.isActive() ? AppProfile.prod_local + FN_INDEX : FN_INDEX, FileLines.class, defRq);
//	}
//
//	@SneakyThrows
//	public Pare<RepoPageDir, Path> getPage(Path repo, String pagename, Pare<RepoPageDir, Path>... defRq) {
//
//		Sd3EE.checkExistRepoDir(repo);
//		Path pagepath = repo.resolve(pagename);
//		Sd3EE.checkExistRepoDir(pagepath);
//		return Pare.of(RepoPageDir.of(repo, true), pagepath);
//	}
//
////	public Pare<RepoPageDir, Path> getPage_(Path repo, String pagename) throws Sd3EE {
////		return RepoPageDir.of(repo, true).getPageDir(pagename, true);
////	}
}
