package zk_os.sec;

import mp.utl_odb.tree.ctxdb.Ctx3Db;
import mp.utl_odb.tree.UTree;
import mpc.env.APP;
import mpc.exception.FIllegalArgumentException;
import mpc.exception.StackTraceRuntimeException;
import mpc.json.GsonMap;
import mpe.NT;
import mpe.core.P;
import mpe.ftypes.core.FDate;
import mpu.X;
import mpu.core.ARG;
import mpu.core.QDate;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.str.JOIN;
import mpu.str.STR;
import mpu.str.UST;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_notes.AppNotesCore;
import zk_os.db.WebUsrService;
import zk_os.db.net.WebUsr;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AuthTokenSrv {

	public static final Logger L = LoggerFactory.getLogger(AuthTokenSrv.class);
	public static final int TIMEOUT_MIN = 30;

	//+3
	public static Pare<String, QDate> doAuth_ProduceToken(long nid, String additionalyMsgDataFromBot_AfterPlusPattern, NT nt) {
		Integer hoursTimeout = UST.INT(additionalyMsgDataFromBot_AfterPlusPattern, null);
		if (hoursTimeout != null && (hoursTimeout > 9999)) {
			hoursTimeout = null;//it name
		}
//		UTree authTree = AppNotesCore.UTreeAuth.getTreeDb();
		QDate expired = newExpired();
		if (hoursTimeout != null) {
			expired = expired.addHours(hoursTimeout);
		}
		String tk = UUID.randomUUID().toString();

		String net = nt.name();

		if (hoursTimeout == null) {

			String alias = additionalyMsgDataFromBot_AfterPlusPattern;

			WebUsrService webUsrService = WebUsrService.get();
			List<WebUsr> foundAllAliases = webUsrService.loadUserByAlias(alias, true);
			if (X.empty(foundAllAliases)) {
				//alias is free - ok
			} else {
				Optional<WebUsr> first = foundAllAliases.stream().filter(u -> X.equals(u.getNt(), net) && X.equals(u.getNid(), nid)).findFirst();
				if (first.isPresent()) {
					//ok found
				} else {
					throw new FIllegalArgumentException("alias '%s' busy", alias);
				}
			}
		}

		GsonMap outRslt = GsonMap.ofKV("exp", expired.epoch(), "nid", nid, "net", net, "alias", hoursTimeout == null ? additionalyMsgDataFromBot_AfterPlusPattern : null);

		CharSequence json = outRslt.toStringJson();

		AppNotesCore.UTreeAuth.store(nid + "", tk, json);

		if (L.isInfoEnabled()) {
			L.info("Auth '{}' with token '{}'", nid, tk);
		}

		P.warnBig("append clean auth tree with token");

		return Pare.of(tk, expired);
	}

	public static QDate newExpired() {
		return QDate.now().addMinutes(TIMEOUT_MIN);
	}

	//Pare3.of(true, "OK", extJsonString)
	public static Pare3<Boolean, String, String> isAuth_OkStatusJson(String token) {
		Ctx3Db.CtxModelCtr rowByToken = AppNotesCore.UTreeAuth.getRowByToken(token);
		if (rowByToken == null) {
			if (L.isWarnEnabled()) {
				L.warn("Access denied:" + token);
			}
			return Pare3.of(false, "FAIL", null);
		}
		GsonMap extJson = rowByToken.getExtAs(GsonMap.class);

		QDate qDateExp = QDate.ofEpoch((Integer) extJson.getAs("exp", Integer.class));
		boolean isExpired = QDate.now().isAfter(qDateExp);
		long diffabs_sec = qDateExp.diffabs(QDate.now(), TimeUnit.SECONDS).longValue();
		if (isExpired) {
			if (L.isInfoEnabled()) {
				L.info("expired:" + diffabs_sec + "sec");
			}
			return Pare3.of(false, "EXPIRED", rowByToken.getExt());
		}

		String extJsonString = extJson.toStringJson().toString();

		if (diffabs_sec > TIMEOUT_MIN * 60) {
			//wait timeout before store new token
		} else {
			//prolong timeout
			extJson.put("exp", newExpired().epoch());
			extJsonString = extJson.toStringJson().toString();
			AppNotesCore.UTreeAuth.store(rowByToken.getKey(), rowByToken.getValue(), extJsonString);
		}


		if (L.isInfoEnabled()) {
			L.info("Allowed:" + rowByToken.getKey());
		}
		return Pare3.of(true, "OK", extJsonString);
	}

	public static void doAuthByParamToken(String token, String name) {

		Pare3<Boolean, String, String> authContextByToken = AuthTokenSrv.isAuth_OkStatusJson(token);
		if (!authContextByToken.key()) {
			return;
		}
		boolean zAuthByTokenAndApply = SecAuth.ByToken.createZAuth_ByToken_AndApply(authContextByToken, name);

		if (zAuthByTokenAndApply) {
			L.info("Auth by token is success:" + authContextByToken.ext());
		} else {
			L.error("Auth is wrong (after success):\n" + authContextByToken.ext(), new StackTraceRuntimeException());
		}
	}


	@NotNull
	public static String produceTokenAndBuildFreshMsgWithLinkToken(NT nt, String additionalyMsgData, Long fromId, String... name) {
		Pare<String, QDate> auth = AuthTokenSrv.doAuth_ProduceToken(fromId, additionalyMsgData, nt);
		String line1 = auth.key() + STR.NL;
		String line2 = APP.HOST.getUsedHttpProtocol() + nt.shortPfx() + fromId + "." + APP.HOST.getAppHost0(null);
		String line3 = ARG.isDef(name) ? ("?n=" + ARG.toDef(name)) : "";
		String line4 = (line3.isEmpty() ? "?" : "&") + "t=" + auth.key();
		String line5 = STR.NL + "Токен действителен до " + auth.val().f(FDate.YYYY_DB_ISO_STANDART);
		return JOIN.args(line1, line2, line3, line4, line5);
	}
}
