package mp.utl_odb.netapp.mdl;


import mpu.X;
import mpu.IT;
import mpc.exception.EException;
import mpc.exception.SimpleMessageRuntimeException;
import mpe.str.CN;
import mp.utl_odb.typedb.TypeDb;
import mp.utl_odb.typedb.TypeDbEE;
import mp.utl_odb.netapp.AppLang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetGrpSrvEE extends EException {

	public static final Logger L = LoggerFactory.getLogger(NetGrpSrvEE.class);

	private static final long serialVersionUID = 1L;

	public static <M extends NetGrpModel> M createNewGrp(Class<M> mdlClass, String net_uid, String grp_nid, String name) throws NetGrpSrvEE {
		IT.notNull(name, "set grp name");
		TypeDb<M> db = TypeDbEE.findDbByClass(mdlClass);
		M newGrp = NetGrpModel.loadGrpNew_GRP_NID(db, net_uid, grp_nid);
		db.incrementColValueSync(newGrp, CN.SID);
		newGrp.setNm(name);
		newGrp.saveAsUpdate(db);
		return newGrp;
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
		NOSTATUS, GROUP_NOT_FOUND, GROUP_EXIST;

		public NetGrpSrvEE I() {
			return new NetGrpSrvEE(this);
		}

		public NetGrpSrvEE I(Throwable ex) {
			NetGrpSrvEE er = new NetGrpSrvEE(this, ex);
			return er;
		}

		public NetGrpSrvEE I(String message) {
			NetGrpSrvEE er = new NetGrpSrvEE(this, new SimpleMessageRuntimeException(
					message));
			return er;
		}

		public NetGrpSrvEE I(String message, Object... args) {
			NetGrpSrvEE er = new NetGrpSrvEE(this, new SimpleMessageRuntimeException(
					X.f(message, args)));
			return er;
		}

		public String nameLang(AppLang lang) {
			switch (this) {
				case GROUP_NOT_FOUND:
					switch (lang) {
						case RU:
							return "Группа не найдена";
						default:
							return "Group is not found";
					}
				default:
					return name();

			}
		}
	}

	public NetGrpSrvEE() {
		super(EE.NOSTATUS);
	}

	public NetGrpSrvEE(EE error) {
		super(error);
	}

	public NetGrpSrvEE(EE error, Throwable cause) {
		super(error, cause);
	}


}

