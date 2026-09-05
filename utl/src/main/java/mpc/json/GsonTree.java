package mpc.json;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mpc.env.APP;
import mpc.env.Env;
import mpc.fs.UF;
import mpc.fs.UFS_BASE;
import mpc.fs.ext.EXT;
import mpe.app.AppCore0;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;

import java.nio.file.Path;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class GsonTree extends GsonMap {

	//	private final @Getter Ns ns;
	private final @Getter Path fileJson;

//	private Path getTreePath() {
//		return ns == null ? fileJson : ns.path();
//	}

	private boolean createDbIfNotExist = true;

	public GsonTree checkLazyCreateDb() {
		if (createDbIfNotExist) {
			UFS_BASE.MKFILE.createEmptyFileIfNotExist(fileJson, true);
			createDbIfNotExist = false;
		}
		return this;
	}

	public static GsonTree ofGsonTree(Path path, boolean freshData) {
//		Ns.ofUnsafe()
		GsonTree gsonTree = new GsonTree(path);
		return freshData ? gsonTree.readData() : gsonTree;
	}

//	public static GsonTree ofGsonTreeFresh(String rpa_ns, String treename) {
//		return ofGsonTreeLazy(rpa_ns, treename).freshData();
//	}

//	public static GsonTree ofGsonTreeLazy(String rpa_ns, String treename) {
//		Path path = getGsonTreePath(rpa_ns, treename);

	/// /		Ns ns = Ns.ofRpa(rpa_ns);
//		GsonTree gsonTree = new GsonTree(path);
//		return gsonTree;
//	}
	public static GsonTree ofEnv(String treename, boolean freshData) {
		Path path = getGsonTreePath(APP.RPA_NS_ENV, treename);
		return GsonTree.ofGsonTree(path, freshData);
	}

	public static Path getGsonTreePath(String rpa_ns, String treenameOrJsonFn) {
		String fn = EXT.JSON.has(treenameOrJsonFn) ? treenameOrJsonFn : treenameOrJsonFn + "$tree.json";
		return AppCore0.of().path(rpa_ns, fn);
	}

	public static GsonTree ofGsonTree(String rpa_ns, String treename, boolean freshData) {
		Path path = getGsonTreePath(rpa_ns, treename);
//		Ns ns = Ns.ofRpa(rpa_ns);
		GsonTree gsonTree = new GsonTree(path);
		if (freshData) {
			gsonTree.readData();
		}
		return gsonTree;
	}

	//
	//
//	public static Path getGsonTreePath_V0(String rpa_ns, String treenameOrJsonFn) {
//		String fn = EXT.JSON.has(treenameOrJsonFn) ? treenameOrJsonFn : treenameOrJsonFn + "$tree.json";
//		Path resolve = Env.RPA_ROOT_APP_DEF().resolve(rpa_ns).resolve(fn);

	/// /		Path resolve = AppCore0.of().namespace(rpa_ns).resolve(fn).toPath();
//		return resolve;
//	}
	public GsonTree readData() {
		checkLazyCreateDb();
		GsonMap gsonMap = RW.readGsonMap(fileJson);
		this.clear();
		putAll(gsonMap);
		return this;
	}

//	private boolean mkdirs_mkdir_orNot = true;

	public GsonTree writeData() {
		checkLazyCreateDb();
//		if (mkdirs_mkdir_orNot) {
//			RW.write(fileJson, toStringPrettyJson(), mkdirs_mkdir_orNot);
//		} else {
		IT.isFileExist(fileJson);
		RW.write(fileJson, toStringPrettyJson());
//		}
		return this;
	}


	public String toStringLog(Logger... logger) {
		String s = X.f_("GsonTree: %s\n%s", UF.ln(getFileJson()), toStringPrettyJson());
		ARG.applyVoidIfDef((l) -> l.info(s), logger);
		return s;
	}

//	@RequiredArgsConstructor
//	public abstract class TreeWalker {
//
//		final Path fileJson;
//
//		public abstract boolean nextMap(GsonMap gsonMap);
//
//		public TreeWalker next() {
//			GsonTree gsonTree = GsonTree.ofGsonTree(fileJson, true);
//			Set<String> set = gsonTree.keySet();
//			for (String stdType : set) {
//				GsonMap asGsonMap = gsonTree.getAsGsonMap(stdType, null);
//				if (asGsonMap != null) {
//					if (!nextMap(asGsonMap)) {
//						continue;
//					}
//				}
//			}
//			return this;
//		}
//
//	}

}
