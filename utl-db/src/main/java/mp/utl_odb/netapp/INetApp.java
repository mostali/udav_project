package mp.utl_odb.netapp;

import lombok.SneakyThrows;
import mp.utl_odb.typedb.TypeDb;
import mp.utl_odb.typedb.TypeDbEE;
import mp.utl_odb.netapp.mdl.NetUsrId;
import mp.utl_odb.netapp.mdl.NetUserModel;
import mpu.core.ARG;
import mpu.IT;
import mpe.NT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface INetApp<U extends NetUsrId, M extends NetUserModel> extends IApp {

	public static final Logger L = LoggerFactory.getLogger(INetApp.class);

	public static final Map<String, NetApp> _NET_APPS = new LinkedHashMap<>();

	default NetApp regNetApp(NetApp netApp) {
		String appName = netApp.getAppName().toUpperCase();
		IT.state(!_NET_APPS.containsKey(appName));
		return _NET_APPS.put(netApp.getNT().name(), netApp);
	}

	default NetApp getApp(NetApp... defRq) {
		return getApp(getAppName(), defRq);
	}

	@SneakyThrows
	static NetApp getApp(String netapp, NetApp... defRq) {
		NetApp netApp = _NET_APPS.get(netapp.toUpperCase());
		if (netApp != null) {
			return netApp;
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw NetUsrSrvEE.EE.NETAPP_NOT_FOUND.I(netapp);
	}

	default NetUsrId getUsrBySID(long sid, U... defRq) throws NetUsrSrvEE {
		NetApp netApp = getApp();
		Class<? extends NetUsrId> entityClassUserId = netApp.getEntityClassUserId();
		NetUsrId usr = NetUsrId.newId(entityClassUserId, sid);
		boolean RETURN = ARG.isDef(defRq);
		boolean checkExist = usr.checkExist(RETURN);
		return checkExist ? usr : ARG.toDef(defRq);
	}

	default NetUsrId getUsrByNID(String net, String user_nid, NetUsrId... defRq) throws NetUsrSrvEE {
		return getUsrByNIDImpl(net, user_nid, defRq);
	}

	static NetUsrId getUsrByNIDImpl(String net, String user_nid, NetUsrId... defRq) throws NetUsrSrvEE {
		NetApp netApp = getApp(net);
		Class<? extends NetUsrId> entityClassUserId = netApp.getEntityClassUserId();
		NetUsrId usr = NetUsrId.newId(entityClassUserId, net, user_nid);
		return usr.checkExist(ARG.isDef(defRq)) ? usr : ARG.toDef(defRq);
	}

	default NetUsrId createBlankUser(NT donorNt, String user_nid) {
		Class<? extends NetUsrId> entityClassUserId = getApp().getEntityClassUserId();
		NetUsrId usr = NetUsrId.newId(entityClassUserId, donorNt.uidStr(), user_nid);
		return usr;
	}

	default NetUsrId getOrCreateNewUser(NetUsrId usrId) throws NetUsrSrvEE {
		NetApp netApp = getApp();
		boolean exist = usrId.checkExist(true);
		if (exist) {
			return usrId;
		}
		usrId = netApp.createNewUser(usrId);
		if (L.isInfoEnabled()) {
			L.info("Create NEW userId for net-app '{}' was created '{}'", netApp.getAppName(), usrId);
		}
		return usrId;
	}

	default TypeDb getDb(Class model) {
		Class entityClassUser = getApp().getEntityClassUserModel();
		TypeDb db = TypeDbEE.findDbByClass(entityClassUser, null);
		if (db == null) {
			db = TypeDbEE.getDbEE(entityClassUser);
		}
		return db;
	}

	default NetUsrId createNewUser(NetUsrId usrId) throws NetUsrSrvEE {
		Class entityClassUser = getApp().getEntityClassUserModel();
		TypeDb db = getDb(entityClassUser);
		NetUserModel newUserModel = NetUsrSrvEE.createNewUserModel(db, entityClassUser, usrId.getNtUid(), usrId.getUserNID(), usrId.getName(false));
		return newUserModel.getUsrId();
	}

	List<M> getUsers(long limit, long offset) throws NetUsrSrvEE;

	default NT getNT() {
		return NT.valueOfIC(getAppName());
	}
}
