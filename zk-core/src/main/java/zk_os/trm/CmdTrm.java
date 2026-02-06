package zk_os.trm;

import lombok.RequiredArgsConstructor;
import mpc.exception.EException;
import mpc.types.tks.cmt.Cmd7;
import mp.utl_odb.netapp.mdl.NetUsrId;
import mpt.TrmRsp;

@RequiredArgsConstructor
public abstract class CmdTrm {

	protected final NetUsrId usr;
	protected final Cmd7 full_cmd7;


	public TrmRsp run() {
		try {
			return runImpl();
		} catch (EException err) {
			return TrmRsp.ERR(err);
		} catch (Throwable err) {
			return TrmRsp.FAIL(err);
		}
	}

	public abstract TrmRsp runImpl() throws Exception;
}
