package zk_os;

import mp.utl_odb.mdl.AModel;
import mp.utl_odb.netapp.AppCore;
import mp.utl_odb.tree.UTree;
import mpc.env.APP;
import mpc.env.AutoInitClassProperty;
import mpc.env.Env;
import mpc.exception.WhatIsTypeException;
import mpu.core.QDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import udav_net.apis.zznote.NoteApi;

import java.nio.file.Path;

public class AppZosCore {

	public static final Logger L = LoggerFactory.getLogger(AppZosCore.class);
	public static final AppCore APP_CORE = new AppCore(Env.getAppNameOrDef());

	static {
		UTree tree = UTree.tree(APP.GNC_TREE());
		if (tree.isEmptyDb()) {
			tree.put("fresh", QDate.now() + "");
		}
	}

	static {
		UTree tree = UTree.tree(APP.GND_TREE());
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
		String appDomainName = APP.getAppDomain();
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

	public static void init() {

		Env.initRPA();

		AppZosProps.initAppProps().forEach(p -> {
			if (p.isAutoInit()) {
				p.doAutoInit(p.getValueOrDefault(null));
			}
		});

		AutoInitClassProperty.initClass(AppZosConfig.class);

	}

	public static String getAppDbFile() {
		return Env.RPA.resolve(APP_SQLITE).toString();
	}

}
