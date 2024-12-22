package mp.utl_odb.netapp;

import mp.utl_odb.netapp.mdl.NetUserModel;
import mp.utl_odb.netapp.mdl.NetUsrId;
import mp.utl_odb.typedb.TypeDb;
import mpc.fs.Ns;
import mpe.NT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefNetApp extends NetApp<DefNetApp.DefUsrId, DefNetApp.DefUserModel> {

	public static final Logger L = LoggerFactory.getLogger(DefNetApp.class);

	public static void main(String[] args) throws NetUsrSrvEE {
//		AppDefNet netApp = AppDefNet.get();
//		BeaUsrId usr = BeaUsrId.newId(BeaUsrId.class, "go", String.valueOf(11));
//		netApp.updateUsrId__(usr, Upd.add);
//		P.exit(netApp.getUsers(10, 0));
	}

	public static final NT NET = NT.DEF;
	public static final Ns NS = Ns.of(NET.name());

	public TypeDb initUserDb() {

		Class<DefUserModel> userModel = getEntityClassUserModel();

		TypeDb db = getDb(userModel);

		AppDefCore.regDb(db);

		db.checkOrCreateDb(true);

		return db;
	}

	@Override
	public TypeDb getDb(Class model) {
		return TypeDb.of(model, NS);
	}

	public static class DefUsrId extends NetUsrId {
		public DefUsrId(Long user_uid, String nt, String user_nid) {
			super(user_uid, nt, user_nid);
		}
	}

	public static class DefUserModel extends NetUserModel {
	}

	@Override
	public Class<DefUsrId> getEntityClassUserId() {
		return DefUsrId.class;
	}

	@Override
	public Class<DefUserModel> getEntityClassUserModel() {
		return DefUserModel.class;
	}

	public static void init() {

		DefNetApp appDefNet = new DefNetApp();
		appDefNet.initUserDb();

	}

	static {
		init();
	}

	@Override
	public NT getNT() {
		return NET;
	}

	private DefNetApp() {
		super(NET.name());
	}

	public static DefNetApp get() {
		return (DefNetApp) INetApp.getApp(NET.name());
	}

}
