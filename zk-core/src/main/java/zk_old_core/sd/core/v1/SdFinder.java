//package zk_os.sd.core.v1;
//
//import mpc.core.ARG;
//import mpe.core.P;
//import mpc.pare.Pare;
//import mpc.UC;
//import zk_os.AppZosCore;
//import zk_os.sd.Sd3EE;
//import zk_os.sd.core.RepoPageDir;
//import zk_os.sd.core.SdMan;
//
//import java.nio.file.Path;
//import java.util.Collection;
//import java.util.Set;
//
///**
// * @author dav 05.04.2022   23:48
// */
//public class SdFinder {
//
//	public static void main(String[] args) throws Sd3EE {
//
//		P.exit(AppZosCore.getMasterRepo());
//
////		Set<RepoPageDir> set = Subdomain3.findReposOfSubdomain3("q");
////		Subdomain3.addSubdomain3("q", "/home/dav/.data/sd3_q", true);
////		P.exit(set);
//		Path pageHome0 = AppZosCore.getMasterRepo();
//		RepoPageDir repoPageDir = RepoPageDir.of(pageHome0, true);
//		P.exit(repoPageDir.getAllPagesPath());
//	}
//
//	/**
//	 * *************************************************************
//	 * ---------------------------- PAGE --------------------------
//	 * *************************************************************
//	 */
//
//
//	public static Pare<RepoPageDir, Path> findPageIndex(String sd3, Pare<RepoPageDir, Path>... defRq) throws Sd3EE {
//		Set<RepoPageDir> repoPages = SdIndex.findAllRepos(sd3);
//		Pare<RepoPageDir, Path> pare = findPageIndex(repoPages, defRq);
//		return pare;
//	}
//
//	public static Pare<RepoPageDir, Path> findPage(String pagename, Pare<RepoPageDir, Path>... defRq) throws Sd3EE {
//		Pare<RepoPageDir, Path> pare = findPage(SdMan.ROOT_SD3, pagename, defRq);
//		return pare;
//	}
//
//	public static Pare<RepoPageDir, Path> findPage(String subdomain3, String pagename, Pare<RepoPageDir, Path>... defRq) throws Sd3EE {
//		Set<RepoPageDir> repoPages = null;
//		try {
//			repoPages = findAllRepos(subdomain3);
//			Pare<RepoPageDir, Path> pare = findPage(repoPages, pagename);
//			return pare;
//		} catch (Exception e) {
//			if (ARG.isDef(defRq)) {
//				return ARG.toDef(defRq);
//			} else if (e instanceof Sd3EE) {
//				throw e;
//			}
//			throw Sd3EE.EE.SD3_PAGE_NOTFOUND.I(e, "#" + subdomain3 + "@" + pagename);
//		}
//	}
//
//	public static Pare<RepoPageDir, Path> findPage(Collection<RepoPageDir> repoPages, String pagename, Pare<RepoPageDir, Path>... defRq) {
//		for (RepoPageDir repoPageDir : repoPages) {
//			Path pagePath = repoPageDir.findPagePath(pagename, null);
//			if (pagePath != null) {
//				return Pare.of(repoPageDir, pagePath);
//			}
//		}
//		return ARG.toDefRq(defRq);
//	}
//
//	public static Pare<RepoPageDir, Path> findPageIndex(Set<RepoPageDir> repoPages, Pare<RepoPageDir, Path>... defRq) {
//		try {
//			for (RepoPageDir repo : repoPages) {
//				Path pageDirIndex = repo.getPropsIndex(null);
//				if (pageDirIndex != null) {
//					return Pare.of(repo, UC.isDirExist(pageDirIndex));
//				}
//			}
//			for (RepoPageDir repo : repoPages) {
//				if (repo.existPage(RepoPageDir.PAGENAME_INDEX)) {
//					return Pare.of(repo, repo.getPageDir(RepoPageDir.PAGENAME_INDEX));
//				}
//			}
//			throw Sd3EE.EE.SD3_PAGE_INDEX_NOTFOUND.I("*@index");
//		} catch (Exception e) {
//			return ARG.toDefThrow(e, defRq);
//		}
//	}
//
//	public static Set<RepoPageDir> findAllRepos(String subdomain3, Set<RepoPageDir>... defRq) throws Sd3EE {
//		return SdIndex.findAllRepos(subdomain3, defRq);
//	}
//
//	public static RepoPageDir findFirstRepo(String sd3, RepoPageDir... defRq) throws Sd3EE {
//		return findRepo(sd3, 0, defRq);
//	}
//
//	public static RepoPageDir findRepo(String sd3, int index, RepoPageDir... defRq) throws Sd3EE {
//		return SdIndex.findRepo(sd3, index, defRq);
//	}
//
//}
