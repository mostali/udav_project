package mpc.json;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.env.APP;
import mpc.fs.UFS;
import mpc.fs.UFS_BASE;
import mpc.log.L;
import mpe.cmsg.core.INodeDesc;
import mpe.cmsg.core.INodeType;
import mpu.X;
import mpu.core.RW;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RequiredArgsConstructor
public class AppStdTree {

	public static void main(String[] args) {
//		NT.BEA.set();
//		GsonTree gsonTree = stdTypesGTree("");
		GsonTree gsonTree = GsonTree.ofGsonTree(Paths.get("/opt/appVol/bea/stdtypes/stdtypes$tree.json"), true);
		gsonTree.forEach((k, v) -> {

//			StdTypeIC stdTypeIC = StdTypeIC.of((Map) v);

//			X.p(stdTypeIC);
//			X.p(k);
//			X.p(v);
			X.p("--------------------");

		});
//		GsonMap asGsonMap = gsonTree.getAsGsonMap("PUBL");
//		X.exit(stdTypeIC);
	}


	public enum TYPE {
		CURRENT, INIT, OUTER, AFTERINIT;
//		;//TRACE,

		public GsonTree stdTreeLazy() {
//			return GsonTree.ofGsonTree("stdtypes", "stdtypes-" + name(), false);
			return GsonTree.ofGsonTree(getGsonTreePath(), false);
		}

		public GsonTree stdTreeFresh() {
			return stdTreeLazy().readData();
		}

		public boolean exist() {
			return UFS.existFile(getGsonTreePath());
		}

		public Path getGsonTreePath() {
			return GsonTree.getGsonTreePath(APP.RPA_NS_ENV + "/stdtypes", "stdtypes-" + this);
		}

		@SneakyThrows
		public void onMoveToCurrent() {
			Path gsonTreePath = TYPE.CURRENT.getGsonTreePath();
			UFS.RM.removeFileQkImpl(gsonTreePath);
			Path from = TYPE.INIT.getGsonTreePath();
			UFS_BASE.COPY.copy(from, gsonTreePath);
		}

		@SneakyThrows
		public void onMoveDataHere(GsonMap gsonMap) {
			Path gsonTreePath = getGsonTreePath();
//			if(UFS.existFile())
			UFS.RM.removeFileQkImpl(gsonTreePath);
//			RW.writeGsonMap(gsonTreePath, gsonMap);
			RW.write(gsonTreePath, gsonMap.toStringPrettyJson(), true);
		}
	}


	public static void put(TYPE type, INodeDesc nodeType) {

		Map map = nodeType.serializeJson();

		GsonTree gsonTree = type.stdTreeLazy();

		gsonTree.put(nodeType.stdTypeUC(), map);

		gsonTree.writeData();

		L.info("Write stdtypes:" + type + ":" + gsonTree.toStringLog());

	}

	public static void put(TYPE type, INodeType nodeType) {

		Map map = nodeType.serializeJson();

		GsonTree gsonTree = type.stdTreeLazy();

		gsonTree.put(nodeType.stdTypeUC(), map);

		gsonTree.writeData();

		L.info("Write stdtypes:" + type + ":" + gsonTree.toStringLog());

	}


}
