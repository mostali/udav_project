package mpc.core;

import mpc.Sys;
import mpc.X;
import mpc.ERR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Алиас +
 * Класс утилита для всего.
 * используется для дебага, временного размещения каких-то функций приложения, в общем для всего, что не куда положить
 */
public class U extends Sys {

	//	public static void main(String[] args) {
	//		P.exit(f_("1=%s 2=%s %s", 1, 2));
	//		P.exit(U.f("1=%s 2=%s %s", 1, 2));
	//	}

	public static final Logger L = LoggerFactory.getLogger(U.class);

	public static final Void VOID = null;//UNRefl.instRq(Void.class, true);
	public static final String __NULL__ = "__NULL__";

	public static Boolean test = true;
//	public static Map<String, Object> TMP_MAP = new HashMap();

	public static String s(Object obj) {
		return String.valueOf(obj);
	}

	//
	//*******************LOGD**********************
	//
	public static void LOGD(Logger logger, String message, Throwable throwable, Object... args) {
		if (!logger.isInfoEnabled()) {
			return;
		}
		boolean hasArgs = !X.empty(args);
		boolean isDE = logger.isDebugEnabled();

		if (throwable != null) {
			if (!hasArgs) {
				if (isDE) {
					logger.debug(message, throwable);
				} else {
					logger.info(message, throwable);
				}
			} else {
				if (isDE) {
					logger.debug(X.fl(message, args), throwable);
				} else {
					logger.info(X.fl(message, args), throwable);
				}
			}
		} else {//throwable=null
			if (!hasArgs) {
				if (isDE) {
					logger.debug(message);
				} else {
					logger.info(message);
				}
			} else {
				if (isDE) {
					logger.debug(message, args);
				} else {
					logger.info(message, args);
				}
			}
		}

	}


	public static UUID stringToUuidQuick(String uuid) {
		try {
			return UUID.fromString(uuid);
		} catch (Exception ex) {
			return null;
		}
	}

	public static <T> Map<T, T> clone(Map<T, T> src, Map dst) {
		dst.putAll(ERR.notNull(src));
		return dst;
	}

	public static Map<?, ?> merge(Map<?, ?> srcMap, Map<?, ?> mergedMap, boolean checkSrictKeys, Map... newSrcMapInstance) {
		Map targetMap = newSrcMapInstance.length > 0 ? newSrcMapInstance[0] : srcMap;
		if (srcMap != null) {
			if (newSrcMapInstance.length > 0) {
				for (Map.Entry entry : srcMap.entrySet()) {
					targetMap.put(entry.getKey(), entry.getValue());
				}
			}
		}
		ERR.notNull(targetMap);
		if (mergedMap != null) {
			for (Map.Entry entry : mergedMap.entrySet()) {
				if (checkSrictKeys && targetMap.containsKey(entry.getKey())) {
					throw new IllegalStateException("Merge maps. Use 'strict'=true. Key exist=[" + entry.getKey() + "]");
				}
				targetMap.put(entry.getKey(), entry.getValue());
			}
		}
		return targetMap;
	}

	public static String multiMessageMerge(Object... messages) {
		return multiMessageWithDel(";;;", messages);
	}

	public static String multiMessageWithDel(String delimetr, Object... messages) {
		switch (messages.length) {
			case 0:
				return "";
			case 1:
				return String.valueOf(messages[0]);
			default:
				return Arrays.asList(messages).stream().map(String::valueOf).collect(Collectors.joining(delimetr));
		}
	}

	public static TimerTask RUN_TIMER(int beforeDelay, int period, TimerTask runnable) {
		new Timer().schedule(runnable, beforeDelay, period);
		return runnable;
	}

}
