package mp.utl_odb.netapp.mdl;


import mpu.X;
import mpu.IT;
import mpc.exception.EException;
import mpc.exception.SimpleMessageRuntimeException;
import mpe.str.CN;
import mp.utl_odb.typedb.TypeDb;
import mp.utl_odb.typedb.TypeDbEE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetMemSrvEE extends EException {

	public static final Logger L = LoggerFactory.getLogger(NetMemSrvEE.class);

	private static final long serialVersionUID = 1L;

	public static <M extends NetMemModel> M createNewMem(Class<M> mdlClass, String net_uid, String mem_nid, String name) throws NetMemSrvEE {
		IT.notNull(name, "set mem name");
		TypeDb<M> db = TypeDbEE.findDbByClass(mdlClass);
		M newMem = NetMemModel.loadMemNew_MEM_NID(db, net_uid, mem_nid);
		db.incrementColValueSync(newMem, CN.SID);
		newMem.setNm(name);
		newMem.saveAsUpdate(db);
		return newMem;
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
		NOSTATUS, MEM_NOT_FOUND, MEM_EXIST;

		public NetMemSrvEE I() {
			return new NetMemSrvEE(this);
		}

		public NetMemSrvEE I(Throwable ex) {
			NetMemSrvEE er = new NetMemSrvEE(this, ex);
			return er;
		}

		public NetMemSrvEE I(String message) {
			NetMemSrvEE er = new NetMemSrvEE(this, new SimpleMessageRuntimeException(
					message));
			return er;
		}

		public NetMemSrvEE I(String message, Object... args) {
			NetMemSrvEE er = new NetMemSrvEE(this, new SimpleMessageRuntimeException(
					X.f(message, args)));
			return er;
		}
	}

	public NetMemSrvEE() {
		super(EE.NOSTATUS);
	}

	public NetMemSrvEE(EE error) {
		super(error);
	}

	public NetMemSrvEE(EE error, Throwable cause) {
		super(error, cause);
	}


}

