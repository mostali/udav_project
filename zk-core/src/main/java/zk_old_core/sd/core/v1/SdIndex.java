//package zk_os.sd.core.v1;
//
//import lombok.SneakyThrows;
//import mpc.*;
//import mpc.exception.FIllegalStateException;
//import mpc.exception.InvalidLogicRuntimeException;
//import mpc.exception.RequiredRuntimeException;
//import mpc.string.Rt;
//import mpc.string.Sb;
//import mpc.string.US;
//import mpc.rt.SyncProcess;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import zk_os.AppZosCore;
//import zk_os.sd.core.RepoPageDir;
//import zk_os.sd.Sd3EE;
//import zk_os.sd.core.SdMan;
//
//import java.io.IOException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CopyOnWriteArraySet;
//
//public class SdIndex {
//
//	private static final Logger L = LoggerFactory.getLogger(SdIndex.class);
//
//	public static final Map<String, Set<RepoPageDir>> _ALL_REPO = new ConcurrentHashMap<>();
//	public static final String LOCK_PROCESS_REPO_PAGES = "LockSdIndex";
//
//
//	public static void main(String[] args) throws Sd3EE {
//		P.exit(buildReport(0));
//	}
//
//	/**
//	 * *************************************************************
//	 * ---------------------------- FIND --------------------------
//	 * *************************************************************
//	 */
//
//	public static Set<RepoPageDir> findAllRepos(String subdomain3, Set<RepoPageDir>... defRq) throws Sd3EE {
//		Set<RepoPageDir> repoPageDirs = getAllOrInitCache().get(subdomain3);
//		if (X.notEmpty(repoPageDirs)) {
//			return repoPageDirs;
//		}
//		if (ARG.isDef(defRq)) {
//			return ARG.toDef(defRq);
//		}
//		throw Sd3EE.EE.SD3_REPO_NOTFOUND.I(subdomain3);
//	}
//
//	public static RepoPageDir findRepoWithIndex0(String subdomain3, RepoPageDir... defRq) throws Sd3EE {
//		return findRepo(subdomain3, 0, defRq);
//	}
//
//	public static RepoPageDir findRepo(String subdomain3, int index, RepoPageDir... defRq) throws Sd3EE {
//		Set<RepoPageDir> set = findAllRepos(subdomain3, null);
//		if (set != null && AR.isIndex(index, set)) {
//			return AR.item(set, index);
//		}
//		return ARG.toDefThrow(() -> new RequiredRuntimeException("Repo '%s' not found by index '%s'", subdomain3, index), defRq);
//	}
//
//	/**
//	 * *************************************************************
//	 * ---------------------------- GET ALL --------------------------
//	 * *************************************************************
//	 */
//
//	public static Set<String> getAllNames() {
//		return getAllOrInitCache().keySet();
//	}
//
//	public static Map<String, Set<RepoPageDir>> getAllOrInitCache() {
//		checkIndexInited();
//		return _ALL_REPO;
//	}
//
//	private static void checkIndexInited() {
//		boolean empty = _ALL_REPO.isEmpty();
//		if (empty) {
//			init();
//			UC.notEmpty(_ALL_REPO);
//		}
//	}
//
//	/**
//	 * *************************************************************
//	 * ---------------------------- SCAN --------------------------
//	 * *************************************************************
//	 */
//
//	public static Set<String> reindex(boolean clearFileIndex, boolean scanSd3) {
//		if (L.isInfoEnabled()) {
//			L.info("Reindex SdIndex clearFileIndex:{}, scanSd3:{}", clearFileIndex, scanSd3);
//		}
//		_ALL_REPO.clear();
//		if (clearFileIndex) {
//			SdFileIndex.clearFileIndex();
//		}
//		if (scanSd3) {
//			SdFileIndex.scanSd3AndWriteFileIndex(AppZosCore.getMasterRepo());
//		}
//		if (L.isInfoEnabled()) {
//			Sb rt = Rt.buildReport(getAllOrInitCache(), "SdIndex", 0);
//			L.info(rt.toString());
//		}
//		return getAllOrInitCache().keySet();
//	}
//
//	public static void addToIndex(RepoPageDir repo, boolean checkRepoExist, boolean write) throws Sd3EE {
//		checkIndexInited();
//		String sd3 = repo.getSubdomain3();
////		String key = sd3 == null ? "" : sd3;//WTF?
//		UC.NE(sd3, "Repo has empty subdomain", repo);
//		Set<RepoPageDir> sd3Set = _ALL_REPO.get(sd3);
//		if (sd3Set == null) {
//			_ALL_REPO.put(sd3, sd3Set = new CopyOnWriteArraySet<>());
//		} else if (checkRepoExist) {
//			if (sd3Set.contains(repo)) {
//				throw Sd3EE.EE.SD3_REPO_EXIST.I(repo.path().toString());
//			}
//		}
//		sd3Set.add(repo);
//		if (write) {
//			SdFileIndex.writeToIndex(repo.path().toString());
//		}
//	}
//
//
//	private static void init() {
//		try {
//			try {
//				SyncProcess.tryLock(LOCK_PROCESS_REPO_PAGES);
//			} catch (SyncProcess.SyncProcessLocked e) {
//				if (L.isWarnEnabled()) {
//					L.warn("Already work:" + e.getMessage());
//				}
//				return;
//			}
//			initImpl();
//		} finally {
//			SyncProcess.releaseProcess(LOCK_PROCESS_REPO_PAGES);
//		}
//	}
//
//	private static void initImpl() {
//		RepoPageDir masterRepo = RepoPageDir.getPrimaryRepo();
//		Set<RepoPageDir> rootSet = new CopyOnWriteArraySet<>();
//		_ALL_REPO.put(SdMan.ROOT_SD3, rootSet);
//		Set<String> allRepoPaths = SdFileIndex.readAllPaths(masterRepo.path());
//		for (String repoPath : allRepoPaths) {
//			Path repoPageDirPath = Paths.get(repoPath);
//			RepoPageDir repo = RepoPageDir.of(repoPageDirPath, true);
//			String sd3 = repo.getSubdomain3();
//			if (sd3 == null) {
//				UC.notAbsPath(repo.path());
//				rootSet.add(repo);
//				continue;
//			}
//			Set allSd3Repos = _ALL_REPO.get(sd3);
//			if (allSd3Repos == null) {
//				_ALL_REPO.put(sd3, allSd3Repos = new CopyOnWriteArraySet<>());
//			}
//			allSd3Repos.add(repo);
//		}
//		if (rootSet.isEmpty()) {
//			//UC.notAbsPath(first.path());
//			rootSet.add(masterRepo);
//		}
//		checkTwins();
//		SdFileIndex.writeFullIndex(_ALL_REPO);
//	}
//
//	private static void checkTwins() {
//		HashSet<RepoPageDir> set = new HashSet();
//		int count = 0;
//		String sd3 = null;
//		for (Map.Entry<String, Set<RepoPageDir>> e : _ALL_REPO.entrySet()) {
//			set.addAll(e.getValue());
//			count += X.sizeOf(e.getValue());
//			if (count != set.size()) {
//				sd3 = e.getKey();
//				break;
//			}
//		}
//		if (sd3 != null) {
//			set = new HashSet();
//			for (Map.Entry<String, Set<RepoPageDir>> e : _ALL_REPO.entrySet()) {
//				if (!e.getKey().equals(sd3)) {
//					set.addAll(e.getValue());
//					continue;
//				}
//				for (RepoPageDir repoPageDir : e.getValue()) {
//					if (set.contains(repoPageDir)) {
//						throw new FIllegalStateException("Sd3 '%s' found twins:", repoPageDir);
//					}
//				}
//			}
//			throw new InvalidLogicRuntimeException("twins not found in sd3 '%s'", sd3);
//		}
//	}
//
//	/**
//	 * *************************************************************
//	 * ---------------------------- REPORT --------------------------
//	 * *************************************************************
//	 */
//
//	@SneakyThrows
//	public static Sb buildReport(String sd3, int tabLevel, Logger... logger) {
////		String TAB = US.TAB(tabLevel);
////		String TAB2 = US.TAB(tabLevel + 1);
////		String TAB3 = US.TAB(tabLevel + 2);
//		Set<RepoPageDir> repos = SdFinder.findAllRepos(sd3);
//		Sb sb = new Sb();
//		if (X.empty(repos)) {
//			sb.append("Subdomain '%s' has 0 repos").to();
//		} else {
//			for (RepoPageDir repo : repos) {
//				StringBuilder repoRt = RepoPageDir.buildReport(repo, tabLevel);
//				sb.append(repoRt).NL();
//			}
//			sb.deleteLastChar();
//		}
//		if (ARG.isDef(logger)) {
//			ARG.toDef(logger).info(sb.toString());
//		}
//		return sb;
//	}
//
//	public static Sb buildReport(int tabLevel, Logger... logger) {
//		String TAB = US.TAB(tabLevel);
//		String TAB2 = US.TAB(tabLevel + 1);
//		String TAB3 = US.TAB(tabLevel + 2);
//		Sb sb = new Sb();
//		Map<String, Set<RepoPageDir>> all = SdIndex.getAllOrInitCache();
//		sb.append(TAB).append("Site Index has '%s' subdomain's", X.sizeOf(all)).NL();
//		if (X.notEmpty(all)) {
//			for (Map.Entry<String, Set<RepoPageDir>> entry : all.entrySet()) {
//				Sb rt = buildReport(entry.getKey(), tabLevel + 1);
//				sb.append(rt).NL();
//			}
//			sb.deleteLastChar();
//		}
//		sb.deleteLastChar();
//		if (ARG.isDef(logger)) {
//			ARG.toDef(logger).info(sb.toString());
//		}
//		return sb;
//
//	}
//
//	public static void removeRepoIndex() throws IOException {
//		SdFileIndex.getFileIndex().removeMe();
//	}
//
//
//}
