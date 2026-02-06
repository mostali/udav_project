package mp.utl_odb.netapp;

import mpu.core.ARG;
import mpc.rfl.RFL;
import mp.utl_odb.query_core.QP;
import mp.utl_odb.typedb.TypeDb;
import mp.utl_odb.typedb.TypeDbEE;
import mp.utl_odb.netapp.mdl.*;

import java.util.Collections;
import java.util.List;

public abstract class NetApp<U extends NetUsrId, M extends NetUserModel> implements INetApp<U, M> {

	@Override
	public String toString() {
		return "NetApp:" + getNT();
	}

	final String appName;

	@Override
	public String getAppName() {
		return appName;
	}

	protected NetApp(String app) {
		this.appName = app;
		regNetApp(this);
	}

	@Override
	public U getUsrBySID(long sid, U... defRq) {
		U usr = NetUsrId.newId(getEntityClassUserId(), sid);
		boolean RETURN = ARG.isDef(defRq);
		boolean checkExist = usr.checkExist(RETURN);
		return checkExist ? usr : ARG.toDef(defRq);
	}

	@Override
	public List<M> getUsers(long limit, long offset) {
		TypeDb<M> usersDb = getUsersDb();
		if (usersDb.isEmptyDb()) {
			return Collections.EMPTY_LIST;
		}
		return usersDb.getModels(QP.limit(limit), QP.offset(offset));
	}

	public TypeDb<M> getUsersDb() {
		TypeDb usersDb = TypeDbEE.getDbEE(getEntityClassUserModel());
		return usersDb;
	}


	public Class<U> getEntityClassUserId() {
		return RFL.getGenericType(getClass(), 0);
	}

	public Class<M> getEntityClassUserModel() {
		return RFL.getGenericType(getClass(), 1);
	}
}
