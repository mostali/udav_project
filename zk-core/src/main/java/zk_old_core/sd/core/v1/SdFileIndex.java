//package zk_os.sd.core.v1;
//
//import lombok.SneakyThrows;
//import mpc.arr.AR;
//import mpc.core.ARG;
//import mpu.X;
//import mpc.env.AppProfile;
//import mpc.env.Env;
//import mpc.fs.FileLines;
//import mpc.string.ruprops.RuProps;
//import mpc.fs.UDIR;
//import mpc.fs.path.UPathToken;
//import mpc.fs.fd.DIR;
//import mpc.fs.fd.EFT;
//import mpc.string.US;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import zk_os.sd.core.RepoPageDir;
//import zk_os.struct.RepoDS;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class SdFileIndex {
//
//	private static final Logger L = LoggerFactory.getLogger(SdIndex.class);
//
//	@SneakyThrows
//	static List<Path> scanSd3AndWriteFileIndex(Path masterPageRepo) {
//		if (L.isInfoEnabled()) {
//			L.info("Start scan SD3 from master-page-repo file://'{}'", masterPageRepo);
//		}
//		List<Path> sd3Paths;
//		AppProfile firstUseful = AppProfile.getFirstUsefulOr(AppProfile.DEFAULT);
//		switch (firstUseful) {
//			case prod_local:
//				sd3Paths = readFileIndex(masterPageRepo, null, Env.RPA.getParent().getParent());
//				break;
//			default:
//				sd3Paths = readFileIndex(masterPageRepo, true, null);
//				break;
//		}
//		if (L.isInfoEnabled()) {
//			L.info("Scan SD3 founded '{}'", X.sizeOf(sd3Paths));
//			sd3Paths.forEach(p -> {
//				L.info(US.TAB + p);
//			});
//		}
//		if (X.empty(sd3Paths)) {
//			return sd3Paths;
//		}
//
//		List<String> indexAppend = new ArrayList<>();
//		for (Path path : sd3Paths) {
//			indexAppend.add(path.toString());
//		}
//
//		SdFileIndex.getFileIndex().addToIndex_(indexAppend);
//
//		return sd3Paths;
//	}
//
//	/**
//	 * *************************************************************
//	 * -------------------- READ FILE INDEX ------------------------
//	 * *************************************************************
//	 */
//
//
//	private static List<Path> readFileIndex(Path masterRepo, Boolean relative, Path reativeOwnerPath) {
//		List<Path> ls = UDIR.ls(masterRepo.getParent(), EFT.DIR, null, potentialRepo -> {
//			RuProps props = RepoPageDir.getProps(potentialRepo, false);
//			boolean test = !masterRepo.equals(potentialRepo) && props.containsKey(RepoPageDir.PK_SUBDOMAIN3);
//			return test;
//		});
//		if (relative != null && relative) {
//			ls = ls.stream().map(p -> p.subpath(UPathToken.lastIndexOf(p, ".."), p.getNameCount())).collect(Collectors.toList());
//		} else if (reativeOwnerPath != null) {
//			ls = ls.stream().map(p -> Paths.get("..").resolve(p.subpath(2, p.getNameCount()))).collect(Collectors.toList());
//		}
//		return ls;
//	}
//
//
//	static Set<String> readAllPaths(Path masterRepo) {
//		FileLines indexWithRepo = getFileIndex(masterRepo);
//		Set<String> all = indexWithRepo.getIndex(null);
//		if (all == null) {
//			all = new LinkedHashSet<>();
//		}
//		return all;
//	}
//
//	static FileLines getFileIndex() {
//		return getFileIndex(RepoPageDir.getPrimaryRepo().dir());
//	}
//
//	static FileLines getFileIndex(Path firstRepoDir) {
//		return getFileIndex(DIR.of(firstRepoDir));
//	}
//
//	private static FileLines getFileIndex(DIR masterRepoDir) {
//		return RepoDS.SELF.getRepoIndex(masterRepoDir.path());
//	}
//
//	/**
//	 * *************************************************************
//	 * ---------------------------- ADD & WRITE --------------------------
//	 * *************************************************************
//	 */
//
//	static void writeFullIndex(Map<String, Set<RepoPageDir>> _ALL_REPO_PAGES) {
//		LinkedHashSet<String> set = _ALL_REPO_PAGES.values().stream().flatMap(s -> s.stream()).map(r -> r.path().toString()).collect(Collectors.toCollection(LinkedHashSet::new));
//		FileLines allRepoINDEX = SdFileIndex.getFileIndex(Paths.get(AR.first(set)));
//		allRepoINDEX.writeIndex(set);
//	}
//
//	static void writeToIndex(String appendThisLine) {
//		FileLines allRepoINDEX = SdFileIndex.getFileIndex(RepoPageDir.getPrimaryRepo().path());
//		allRepoINDEX.appendLine(ARG.toDef(appendThisLine));
//	}
//
//
//	static void clearFileIndex() {
//		SdFileIndex.getFileIndex().clearAndWrite();
//	}
//
//
//}
