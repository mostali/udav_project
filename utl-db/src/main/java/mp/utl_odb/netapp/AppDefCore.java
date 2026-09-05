package mp.utl_odb.netapp;

import mp.utl_odb.mdl.AModel;
import mp.utl_odb.typedb.TypeDb;
import mpc.env.Env;

public class AppDefCore {
	public static final AppCore APP_CORE = new AppCore(Env.getAppName());

	public static void regDb(Class<? extends AModel> modelClass, String dbName, boolean... createDbIfNotExist) {
		APP_CORE.regTypeDbEE(modelClass, dbName, createDbIfNotExist);
	}

	public static void regDb(TypeDb typeDb) {
		APP_CORE.regTypeDbEE(typeDb);
	}

}
