package mp.utl_odb.netapp.qxt;

import mp.utl_odb.netapp.mdl.NetGrpId;
import mp.utl_odb.netapp.mdl.NetMemId;
import mp.utl_odb.netapp.mdl.NetUsrId;

import java.util.Map;

public interface NetAppQ extends AnyQ {

	public final static ThreadLocal<Map<String, String>> Q_REQ_KEYS = AnyQ.reg(NetAppQ.class);
	public final static ThreadLocal<NetUsrId> Q_USRID = AnyQ.reg(NetAppQ.class);
	public final static ThreadLocal<NetGrpId> Q_GRPID = AnyQ.reg(NetAppQ.class);
	public final static ThreadLocal<NetMemId> Q_MEMID = AnyQ.reg(NetAppQ.class);

	public final static ThreadLocal<String> Q_TOKEN = new ThreadLocal<>();

	public static void clearNetAppQ() {
		AnyQ.clear(NetAppQ.class);
	}

}
