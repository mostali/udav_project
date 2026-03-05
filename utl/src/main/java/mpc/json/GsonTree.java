package mpc.json;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mpc.env.Env;
import mpc.fs.UF;
import mpc.fs.ext.EXT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;

import java.nio.file.Path;
import java.util.Set;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class GsonTree extends GsonMap {

	private final @Getter Path fileJson;

	public static GsonTree ofGsonTree(Path path, boolean lazy) {
		GsonTree gsonTree = new GsonTree(path);
		return lazy ? gsonTree : gsonTree.fresh();
	}

	public static GsonTree ofGsonTreeFresh(String rpa_ns, String treename) {
		return ofGsonTreeLazy(rpa_ns, treename).fresh();
	}

	public static GsonTree ofGsonTreeLazy(String rpa_ns, String treename) {
		GsonTree gsonTree = new GsonTree(getGsonTreePath(rpa_ns, treename));
		return gsonTree;
	}

	//
	//
	public static Path getGsonTreePath(String rpa_ns, String treename) {
		String fn = EXT.JSON.has(treename) ? treename : treename + "$tree.json";
		Path resolve = Env.RPA.resolve(rpa_ns).resolve(fn);
//		Path resolve = AppCore0.of().namespace(rpa_ns).resolve(fn).toPath();
		return resolve;
	}

	public GsonTree fresh() {
		GsonMap gsonMap = RW.readGsonMap(fileJson);
		this.clear();
		putAll(gsonMap);
		return this;
	}

	private boolean mkdirs_mkdir_orNot = true;

	public GsonTree write() {
		if (mkdirs_mkdir_orNot) {
			RW.write(fileJson, toStringPrettyJson(), mkdirs_mkdir_orNot);
		} else {
			RW.write(fileJson, toStringPrettyJson());
		}
		return this;
	}


	public String toStringLog(Logger... logger) {
		String s = X.f_("GsonTree: %s\n%s", UF.ln(getFileJson()), toStringPrettyJson());
		ARG.applyVoid((l) -> l.info(s), logger);
		return s;
	}

	@RequiredArgsConstructor
	public abstract class TreeWalker {

		final Path fileJson;

		public abstract boolean nextMap(GsonMap gsonMap);

		public TreeWalker next() {
			GsonTree gsonTree = GsonTree.ofGsonTree(fileJson, false);
			Set<String> set = gsonTree.keySet();
			for (String stdType : set) {
				GsonMap asGsonMap = gsonTree.getAsGsonMap(stdType, null);
				if (asGsonMap != null) {
					if (!nextMap(asGsonMap)) {
						continue;
					}
				}
			}
			return this;
		}

	}

}
