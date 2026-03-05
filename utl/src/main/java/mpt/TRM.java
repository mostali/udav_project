package mpt;

import mpu.core.ARG;
import mpu.core.ARGn;
import mpu.IT;
import mpc.console.ConsoleInput;
import mpc.exception.FIllegalStateException;
import mpc.str.sym.SYMJ;
import mpu.core.QDate;
import mpc.exception.FIllegalArgumentException;
import mpc.exception.RequiredRuntimeException;
import mpu.X;
import mpu.str.Rt;
import mpu.str.STR;
import mpu.str.Sb;
import mpu.str.TKN;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TRM {

	public static final Logger L = LoggerFactory.getLogger(TRM.class);
	public static final AtomicLong CTR = new AtomicLong(0);

	private static IaUser _usr0;

	public static final Map<String, ITrm> _TERMINALS = new LinkedHashMap<>();
	public static final Map<String, Map<String, ITrmCmd>> _CMDS = new ConcurrentHashMap<>();

	public static Map<String, ITrmCmd> cmds(String trm, Map<String, ITrmCmd>... defRq) {
		Map map = TRM._CMDS.get(trm);
		if (map != null) {
			return map;
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Trm '%s' not found cmd's ", trm);
	}

	public static ITrmCmd trmCmd(String trm, String cmd, ITrmCmd... defRq) {
		ITrmCmd iCmd = cmds(trm).get(cmd);
		if (iCmd != null) {
			return iCmd;
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Trm '%s' not found Cmd '%s'", trm, cmd);
	}

	public static ITrm trm(String trm, ITrm... defRq) {
		ITrm iTrm = TRM._TERMINALS.get(trm);
		if (iTrm != null) {
			return iTrm;
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Trm '%s' not found from '%s'", trm);
	}

	@Deprecated
	public static void regTrm(String key, ITrm trm) {
		if (L.isInfoEnabled()) {
			L.info(SYMJ.ARROW_RIGHT_SPEC + "Reg trm '{}' init '{}'", trm.getClass().getSimpleName(), key);
		}
		_TERMINALS.put(key, trm);
	}

	public static void checkStartWith(String cmd, String cmdKey, boolean... ignoreCase) {
		IT.state(STR.startsWith(cmd, cmdKey, ARGn.isDefEqTrue(ignoreCase)));
	}

	/**
	 * *************************************************************
	 * ---------------------------- RUN --------------------------
	 * *************************************************************
	 */

	public static void reg(Class... trmEntities) {
		run(false, false, trmEntities);
	}

	public static void reg(boolean regSysTrm, Class... trmEntities) {
		run(false, regSysTrm, trmEntities);
	}

	public static void reg_scan(boolean regSysTrm, String... scan_packages) {
		run(false, regSysTrm, PackTrmRegistrator.scan(scan_packages));
	}

	public static void run_scan(boolean runNativeTrm, boolean regSysTrm, String... scan_packages) {
		run(runNativeTrm, regSysTrm, PackTrmRegistrator.scan(scan_packages));
	}

	public static void run_scan(IaUser usr0, boolean runNativeTrm, boolean regSysTrm, String... scan_packages) {
		run(usr0, runNativeTrm, regSysTrm, PackTrmRegistrator.scan(scan_packages));
	}

	public static void run(Class... trmEntities) {
		run(true, true, trmEntities);
	}

	public static void run(boolean runNativeTrm, boolean regSysTrm, Class... trmEntities) {
		run(IaUser.def(), runNativeTrm, regSysTrm, trmEntities);
	}

	public static void run(IaUser usr0, boolean runNativeTrm, boolean regSysTrm, Class... trmEntities) {

		if (regSysTrm) {
			PackTrmRegistrator.regTrm(SysTrm.class);
		}

		PackTrmRegistrator.regTrm(trmEntities);

		if (runNativeTrm) {

			if (_usr0 != null) {
				throw new FIllegalStateException("TRM already runned");
			}

			_usr0 = IT.NN(usr0);

			ConsoleInput.waitInfinityAsync(new NativeConsole(usr0));

		}

	}

	/**
	 * *************************************************************
	 * --------------------- EXECUTE CMD VIA USER ------------------
	 * *************************************************************
	 */

	@Deprecated//xz
	public static TrmRsp executeCmdWithViaUser(TrmRq rq) {
		try {
			return executeCmdWithViaUser_(rq).throwIsNoOk();
		} catch (TrmRsp throwable) {
			throw throwable;
		} catch (Throwable throwable) {
			return TrmRsp.FAIL(throwable);
		}
	}

	private static TrmRsp executeCmdWithViaUser_(TrmRq rq) throws Throwable {
		String org = rq.cmd();
		if (!org.startsWith("&")) {
			throw new FIllegalArgumentException("Set link to &user in cmd '%s'", rq.cmd());
		}
		String userLink = TKN.first(org, ' ', org);

		IaUser userId = executeCmdWithViaUser_(TrmRq.fromTrm("app " + userLink)).getResult();

		String nextCmd = TKN.startWith(org, userLink).trim();

		TrmRq clone = rq.clone(nextCmd);
		return TRM.executeCmdNative(userId, clone).throwIsNoOk();
	}

	/**
	 * *************************************************************
	 * ---------------------------- EXECUTE CMD --------------------------
	 * *************************************************************
	 */
	public static TrmRsp executeCmd(IaUser usr, String cmd) {
		return executeCmd(usr, TrmRq.fromTrm(cmd));
	}

	public static TrmRsp executeCmd(TrmRq cmd) {
		return executeCmd(IaUser.def(), cmd);
	}

	public static TrmRsp executeCmd(IaUser usr, TrmRq cmd) {
		try {
			return TRM.executeCmdNative(usr, cmd);
		} catch (TrmEE throwable) {
			return TrmRsp.FAIL(throwable.getCause());
		} catch (TrmRsp throwable) {
			throw throwable;
		} catch (Throwable throwable) {
			return TrmRsp.FAIL(throwable);
		}
	}

	@NotNull
	private static TrmRsp executeCmdNative(IaUser usr, TrmRq trmRq) throws Throwable {
		String pfx = TRM.CTR.incrementAndGet() + "/" + trmRq.key();
		if (L.isDebugEnabled()) {
			L.debug(pfx + SYMJ.ARROW_RIGHT + SYMJ.ARROW_RIGHT + "/ {} / {} / >>> {}", QDate.now().f(QDate.F.MONO20NF), IaUser.toString(usr), trmRq.cmd());
		}
		TrmRsp rsp = null;
		String key = trmRq.key();
		try {
			if (!TRM._TERMINALS.containsKey(key)) {
				throw TrmEE.EE.TRM_NOT_FOUND.I("Unknown TRM '%s'", trmRq.cmd());
			}
			ITrm trm = TRM.trm(key, null);
			if (trm == null) {
				//если отсканировали терминал без интерфейса
				return execute0(null, usr, trmRq);
			} else {
				return rsp = trm.exe_(usr, trmRq);
			}
		} finally {
			if (L.isDebugEnabled()) {
				L.trace("\n+" + rsp);
				L.debug(pfx + SYMJ.ARROW_LEFT + " / {} / {} /<<< {}", QDate.now().f(QDate.F.MONO20NF), TrmRspStr.toStatusWithCode(rsp), trmRq.cmd());
			}
		}
	}

	@NotNull
	static TrmRsp execute0(ITrm trm, IaUser usr, TrmRq trmRq) throws Throwable {
		Map<String, ITrmCmd> cmds = trm == null ? TRM.cmds(trmRq.key()) : trm.cmds(null);
		if (cmds == null) {
			if (trm != null) {
				cmds = PackTrmRegistrator.findTrmCmds(trmRq.key(), trm);
			}
			if (cmds == null) {
				throw TrmEE.EE.TRMCMDS_EMPTY.I("Cmds of TRM '%s' is empty", trmRq.cmd());
			}
		}
		String key = trmRq.key();
		String val = trmRq.val(null);
		if (X.empty(val)) {
			return TrmRsp.OKR(cmds.keySet());
		}
		ITrmCmd cmd = cmds.get(val);
		if (cmd == null) {
			throw TrmEE.EE.TRMCMD_NOT_FOUND.I("Trm '%s' route '%s' unknown. From cmd '%s'", key, val, trmRq.cmd());
		}
		TrmRsp rsp = cmd.exe_(usr, trmRq);
		return rsp;
	}

	public static void checkUserNotNull(IaUser usr) {
		if (usr == null) {
			throw TrmRsp.FAIL("User is null");
		}
	}

	public static Set<String> trms_keys() {
		return _TERMINALS.keySet();
	}

	public static Collection<ITrm> trms() {
		return _TERMINALS.values();
	}

	public static String CMD(String key, String cmd, Object[] args) {
		return key + " " + X.f(cmd, args);
	}

	public static Sb buildReport(int tabLevel, Logger... logger) {
		String TAB = STR.TAB(tabLevel);
		String TAB2 = STR.TAB(tabLevel + 1);
		String TAB3 = STR.TAB(tabLevel + 2);
		Sb rt = new Sb();
		String head = "Terminals (" + X.sizeOf(_TERMINALS) + ") - " + _TERMINALS.keySet();
		rt.NL(head);
		if (X.notEmpty(_CMDS)) {
			for (Map.Entry<String, Map<String, ITrmCmd>> entry : _CMDS.entrySet()) {
				String headMsg = ">> " + entry.getKey() + "(" + X.sizeOf(entry.getValue()) + ")";
				Sb cmdRt = Rt.buildReport(entry.getValue().keySet(), headMsg, tabLevel + 1);
				rt.append(cmdRt).NL();
			}
			rt.deleteLastChar();
		}
		if (ARG.isDef(logger)) {
			ARG.toDef(logger).info(rt.toString());
		}
		return rt;
	}
}