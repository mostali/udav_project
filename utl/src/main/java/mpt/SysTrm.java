package mpt;

import ch.qos.logback.classic.Level;
import lifebeat.HeapDumpPrinter;
import lifebeat.LifePrinter;
import lifebeat.ThreadDumpPrinter;
import mpu.X;
import mpc.env.AP;
import mpc.exception.NI;
import mpc.exception.WhatIsTypeException;
import mpu.core.RW;
import mpc.fs.UDIR;
import mpc.fs.fd.EFT;
import mpc.log.L;
import mpu.str.Rt;
import mpu.str.Sb;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@TrmEntity(value = SysTrm.KEY)
public class SysTrm implements ITrm {

	public static final String KEY = "s";

	@Override
	public String key() {
		return KEY;
	}

	@TrmCmdEntity(value = "log")
	public static final ITrmCmd TC_LOG = (usr, cmd) -> {
		String packName = cmd.cmd7().extObj.str(null);
		if (X.empty(packName)) {
			return TrmRsp.ERR("Set packname");
		}
		String levelName = cmd.cmd7().opt1Obj.str(null);
		if (X.empty(levelName)) {
			return TrmRsp.ERR("Set level name");
		}
		Level level = Level.toLevel(levelName, null);
		if (level == null) {
			return TrmRsp.ERR("Incorrect level value '%s'. Set correct value", levelName);
		}
		L.setLogLevel(packName, level);
		return TrmRsp.OK("Logger '%s' is change to '%s'", packName, level);
	};

	@TrmCmdEntity(value = {"apv"})
	public static ITrmCmd TC_AP_SHOW = (usr, cmd) -> {
		NI.stop("ni case app profile");
		Map<String, String> map = AP.getMap(null, null);
		if (map == null) {
			return TrmRsp.ERR("AP not found");
		}
		Sb rt = Rt.buildReport(map);
		return TrmRsp.MSG(rt);
	};
	@TrmCmdEntity(value = {"fd", "fdv"})
	public static final ITrmCmd TC_FD = (usr, cmd) -> {
		String f = cmd.cmd7().extObj.str(null);
		if (f == null) {
			return TrmRsp.ERR("Set file or dir");
		}
		return fd_cmd(f, "fdv".equals(cmd.val(null)));
	};

	@TrmCmdEntity(value = {"td", "tdv"})
	public static final ITrmCmd TC_THREAD_DUMP = (usr, cmd) -> {
		StringBuilder rt = ThreadDumpPrinter.buildReport();
		if ("td".equals(cmd.val())) {
			String parent_dir = AP.get("app.dump.dir", "tmp");
			Path rtPath = Paths.get(ThreadDumpPrinter.FILENAME(parent_dir));
			ThreadDumpPrinter.writeReport(rtPath, rt);
			return TrmRsp.OKR(rtPath);
		}
		return TrmRsp.OKR(rt);
	};
	@TrmCmdEntity(value = {"hd"})
	public static final ITrmCmd TC_HEAP_DUMP = (usr, cmd) -> {
		String parent_dir = AP.get("app.dump.dir", "tmp");
		String file = HeapDumpPrinter.FILENAME(parent_dir);
		HeapDumpPrinter.writeDump(file);
		return TrmRsp.OKR(Paths.get(file));
	};

	@TrmCmdEntity(value = {"ts"})
	public static final ITrmCmd TC_THREAD_STACK = (usr, cmd) -> TrmRsp.OKR(Thread.getAllStackTraces().keySet());

	@TrmCmdEntity(value = {"mp"})
	public static final ITrmCmd TC_MEMORY_PRINT = (usr, cmd) -> TrmRsp.OKR(LifePrinter.buildReport());

	@TrmCmdEntity(value = {"mp-on", "mp-off"})
	public static final ITrmCmd TC_MEMORY_PRINT_LIFE = (usr, cmd) -> {
		switch (cmd.val()) {
			case "mp-on":
				LifePrinter.RUN(60_000, TRM.L);
				return TrmRsp.MSG("LifePrinter Runnned: every 60sec");
			case "mp-off":
				if (LifePrinter.LIFE_PRINTER != null) {
					LifePrinter.LIFE_PRINTER.cancel();
					LifePrinter.LIFE_PRINTER = null;
				}
				return TrmRsp.MSG("LifePrinter Stopped");
			default:
				throw new WhatIsTypeException("What is cmd '%s'?", cmd.key());
		}
	};

	@TrmCmdEntity(value = "ls-db")
	public static final ITrmCmd TC_LS_DB = (usr, cmd) -> {
		throw NI.stop("ls-db");
	};

	@TrmCmdEntity(value = "ls")
	public static final ITrmCmd TC_LS_TRM = (usr, cmd) -> {
		Collection<String> trmsAll = TRM._TERMINALS.keySet();
		Sb rp = new Sb();
		for (String trm : trmsAll) {
			ITrm iTrm = TRM._TERMINALS.get(trm);
			Map<String, ITrmCmd> cmds = TRM.cmds(trm, null);
			Set cmdsNames = cmds == null ? null : cmds.keySet();
			rp.NL(trm + " - " + (iTrm == null ? null : (iTrm.getClass().getName()) + (cmds == null ? "" : " :: " + cmdsNames)));
		}
		rp.deleteLastChar();
		return TrmRsp.MSG(rp);
	};

	public static TrmRsp fd_cmd(String file, boolean isFdv) {
		try {
			Path filePath = Paths.get(file);
			EFT ft = EFT.of(filePath, null);
			if (ft == null) {
				return TrmRsp.ERR("File '%s' not exist", filePath);
			}
			if (!isFdv) {
				return TrmRsp.OKR(filePath);
			}
			switch (ft) {
				case FILE:
					return TrmRsp.OKR(RW.readContent_(filePath));
				case DIR:
					return TrmRsp.OKR(UDIR.ls_paths(filePath));
				default:
					throw new WhatIsTypeException(ft);
			}
		} catch (Exception ex) {
			return TrmRsp.FAIL(ex);
		}
	}

	@Override
	public TrmRsp exe_(IaUser usr, TrmRq cmd) throws Throwable {
		try {
			return TRM.execute0(this, usr, cmd);
		} catch (TrmEE ee) {
			switch (ee.type()) {
				case TRMCMD_NOT_FOUND:
				default:
					throw ee;
			}
		}
	}

}
