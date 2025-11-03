package zk_os;

import mp.utl_odb.mdl.AModel;
import mp.utl_odb.netapp.AppCore;
import mp.utl_odb.tree.UTree;
import mpc.env.APP;
import mpc.env.AutoInitClassProperty;
import mpc.env.Env;
import mpc.exception.WhatIsTypeException;
import mpu.X;
import mpu.core.ARG;
import mpu.core.QDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import udav_net.apis.zznote.NoteApi;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.InjectSrv;
import mpe.call_msg.injector.NodeData;

import java.nio.file.Files;
import java.nio.file.Path;

public class AppZosCore {

	public static final Logger L = LoggerFactory.getLogger(AppZosCore.class);
	public static final AppCore APP_CORE = new AppCore(Env.getAppNameOrDef());

	static {
		UTree tree = UTree.tree(APP.TREE_GNC());
		if (tree.isEmptyDb()) {
			tree.put("fresh", QDate.now() + "");
		}
	}

	public static UTree TREE_GNC() {
		return (UTree) UTree.tree(APP.TREE_GNC());
	}

	//

	public static final Integer[] AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND = new Integer[]{0, 1000, 1000, 5000, 1000, 0};

	public static UTree TREE_GUL() {
		return (UTree) UTree.tree(APP.TREE_GUL()).withAutoCleanCfg(AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND);
	}

	//

	static {
		UTree tree = UTree.tree(APP.TREE_GND_TASKS_V1());
		if (tree.isEmptyDb()) {
			tree.put("fresh", QDate.now() + "");
		}
	}


	private static final String APP_SQLITE = "app.sqlite";


	public static void regDb(Class<? extends AModel> modelClass, String dbName, boolean... createDbIfNotExist) {
		APP_CORE.regTypeDbEE(modelClass, dbName, createDbIfNotExist);
	}

	public static Path getRpa() {
		return Env.RPA;
	}

	public static NoteApi createLocalNoteApi() {
		String appDomainName = APP.HOST.getAppHost0();
		return createLocalNoteApi(appDomainName);
	}

	public static NoteApi createNoteApiByAlias(String hostAlias) {
		switch (hostAlias) {
			case "zn": {
				return createLocalNoteApi("zznote.ru");
			}
			default:
				NoteApi noteApi = createNoteApiWithFindHostByAlias(hostAlias);
				if (noteApi != null) {
					return noteApi;
				}
				throw new WhatIsTypeException(hostAlias);
		}
	}

	public static NoteApi createNoteApiWithFindHostByAlias(String hostAlias) {
		if (NoteApi.funcFindHostByAlias == null) {
			return null;
		}
		String host = NoteApi.funcFindHostByAlias.apply(hostAlias);
		return host == null ? null : new NoteApi(host, AppZosConfig.SUPER_KEY);
	}

	public static NoteApi createLocalNoteApi(String host) {
		return new NoteApi(host, AppZosConfig.SUPER_KEY);
	}

	public static void initEnv(Environment environment) {

		Env.initRPA();

		initWith_SpringEnv_GNC(environment);

	}

	private static void initWith_SpringEnv_GNC(Environment environment) {

		AutoInitClassProperty.SPRING_ENV_PROP_LOADER = environment == null ? null : (key) -> environment.getProperty(key);

		if (UTree.tree(APP.TREE_GNC()).isExistDb()) {
			AutoInitClassProperty.GNC_LOADER = (key) -> AppZosCore.TREE_GNC().getValue(key, null);
		}

		AutoInitClassProperty.initClass(APP.class);

		AppZosProps.initAppProps().forEach(p -> {
			if (p.isAutoInit()) {
				p.doAutoInit(p.getValueOrDefault(null));
			}
		});

		AutoInitClassProperty.initClass(AppZosConfig.class);

		NodeData.injectorSrv = (node, track) -> InjectSrv.inject((NodeDir) node, track);

	}

	public static String getAppDbFile() {
		return Env.RPA.resolve(APP_SQLITE).toString();
	}

	public static Path getFileExisted(String appFile, Path... defRq) {
		if (X.empty(appFile)) {
			return ARG.toDefThrowMsg(() -> X.f("Except not empty app name file '%s'", appFile), defRq);
		}
		Path rpaAppFile = getRpa().resolve(appFile);
		return Files.isRegularFile(rpaAppFile) ? rpaAppFile : ARG.toDefThrowMsg(() -> X.f("Except existed app file '%s'", rpaAppFile), defRq);
	}

	public static Path getDirExisted(String appDir, Path... defRq) {
		if (X.empty(appDir)) {
			return ARG.toDefThrowMsg(() -> X.f("Except not empty app name dir '%s'", appDir), defRq);
		}
		Path rpaAppDir = getRpa().resolve(appDir);
		return Files.isDirectory(rpaAppDir) ? rpaAppDir : ARG.toDefThrowMsg(() -> X.f("Except existed app folder '%s'", rpaAppDir), defRq);
	}
}
