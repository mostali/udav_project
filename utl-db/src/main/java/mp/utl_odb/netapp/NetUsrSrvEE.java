package mp.utl_odb.netapp;


import mpu.X;
import mpu.core.ARG;
import mpc.arr.QUEUE;
import mpc.exception.EException;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.SimpleMessageRuntimeException;
import mpe.str.CN;
import mp.utl_odb.typedb.TypeDb;
import mp.utl_odb.typedb.TypeDbEE;
import mp.utl_odb.netapp.mdl.NetUsrId;
import mp.utl_odb.netapp.mdl.NetUserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class NetUsrSrvEE extends EException {

	public static final Logger L = LoggerFactory.getLogger(NetUsrSrvEE.class);

	private static final long serialVersionUID = 1L;
	private final static Map<String, Long> _TOKENS = QUEUE.cache_map_sync_FILO(10);

	public static <M extends NetUserModel> M createNewUserModel(NetUsrId<M> netUsrId) throws NetUsrSrvEE {
		return createNewUserModel(netUsrId.getDbModel(), netUsrId.getNt(), netUsrId.getUserNID(), netUsrId.getUserName());
	}

	public static <M extends NetUserModel> M createNewUserModel(Class<M> mdlClass, String net_uid, String user_nid, String name) throws NetUsrSrvEE {
		return createNewUserModel(null, mdlClass, net_uid, user_nid, name);
	}

	public static <M extends NetUserModel> M createNewUserModel(TypeDb<M> db, Class<M> mdlClass, String net_uid, String user_nid, String name) throws NetUsrSrvEE {
		if (db == null) {
			db = TypeDbEE.findDbByClass(mdlClass);
		}
		M newUser = NetUserModel.loadUserNew_USER_NID(db, net_uid, user_nid);
		db.incrementColValueSync(newUser, CN.SID);
		newUser.setNm(name);
		newUser.saveAsUpdate(db);
		if (L.isInfoEnabled()) {
			L.info("createNewUserModel '{}' /{}*{}/{}", mdlClass, net_uid, user_nid, name);
		}
		return newUser;
	}

	public static <M extends NetUserModel> M loadUserModel(TypeDb<M> db, Class<M> mdlClass, String net_uid, String user_nid) throws NetUsrSrvEE {
		if (db == null) {
			db = TypeDbEE.findDbByClass(mdlClass);
		}
		M newUser = NetUserModel.loadUserRq_USER_NID(db, net_uid, user_nid);
		if (L.isInfoEnabled()) {
			L.info("findUserModel '{}' /{}*{}/{}", mdlClass, net_uid, user_nid, newUser.getUser_name());
		}
		return newUser;
	}

	public static <M extends NetUserModel> M updateUserModel(Class<M> mdlClass, M usrModel) throws NetUsrSrvEE {
		TypeDb<M> db = TypeDbEE.findDbByClass(mdlClass);
		usrModel.saveAsUpdate(db);
		if (L.isInfoEnabled()) {
			L.info("updateUserModel '{}' /{}", mdlClass, usrModel);
		}
		return usrModel;
	}

	public static Long getUserUid(String token, Long... defRq) {
		Long uid = _TOKENS.get(token);
		if (uid != null) {
			return uid;
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("User not found by token");
	}

	public enum UsrRole {
		OWNER("owner"),
		ADMIN("admin"),
		USER("user");

//		MODERATOR("moderator")
		//		EDITOR("editor");

		private final String value;

		private UsrRole(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}

		public String toString() {
			return this.value.toLowerCase();
		}
	}

	/**
	 * *************************************************************
	 * ---------------------------- INIT --------------------------
	 * *************************************************************
	 */
	@Override
	public EE type() {
		return super.type(EE.class);
	}


	public enum EE {
		NOSTATUS, USER_NOT_FOUND, USER_EXIST, USER_BLOCK, NETAPP_NOT_FOUND;

		public NetUsrSrvEE I() {
			return new NetUsrSrvEE(this);
		}

		public NetUsrSrvEE I(Throwable ex) {
			NetUsrSrvEE er = new NetUsrSrvEE(this, ex);
			return er;
		}

		public NetUsrSrvEE I(String message) {
			NetUsrSrvEE er = new NetUsrSrvEE(this, new SimpleMessageRuntimeException(message));
			return er;
		}

		public NetUsrSrvEE I(String message, Object... args) {
			NetUsrSrvEE er = new NetUsrSrvEE(this, new SimpleMessageRuntimeException(X.f(message, args)));
			return er;
		}
	}

	public NetUsrSrvEE() {
		super(EE.NOSTATUS);
	}

	public NetUsrSrvEE(EE error) {
		super(error);
	}

	public NetUsrSrvEE(EE error, Throwable cause) {
		super(error, cause);
	}


}

