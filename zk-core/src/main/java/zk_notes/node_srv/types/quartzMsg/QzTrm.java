package zk_notes.node_srv.types.quartzMsg;

import mp.utl_odb.netapp.mdl.NetUsrId;
import mpt.*;
import mpu.Sys;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_os.quartz.QzApiEE;

import java.util.Collection;

@TrmEntity(value = QzTrm.KEY)
public class QzTrm {

	public static final Logger L = LoggerFactory.getLogger(QzTrm.class);

	public static final String KEY = "qz";

	public static String CMD(String cmd, Object... args) {
		return TRM.CMD(KEY, cmd, args);
	}

	public static void main(String[] args) throws Throwable {//
		TrmRsp rsp = QzTrm.TC_ALL.exe_(NetUsrId.def(), TrmRq.fromTrm("rv next -gr 56956214 -ST -1000"));
		Sys.exit(rsp);
	}

	@TrmCmdEntity(value = "ls")
	public static ITrmCmd TC_ALL = (usr, cmd) -> {
		Scheduler scheduler = QzApiEE.getScheduler();
		Collection all = scheduler.getJobKeys(GroupMatcher.anyGroup());
		return TrmRsp.OKR(all);
	};

	@TrmCmdEntity(value = "clean")
	public static ITrmCmd TC_RM_ALL = (usr, cmd) -> {
		Scheduler scheduler = QzApiEE.getScheduler();
		Collection<JobKey> all = scheduler.getJobKeys(GroupMatcher.anyGroup());
		QzApiEE.deleteAll(all, false);
		return TrmRsp.OKR(all);
	};


}
