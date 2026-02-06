package zk_page;

import lombok.RequiredArgsConstructor;
import mpc.map.IGetterAs;
import mpc.map.MAP;
import mpc.str.ObjTo;

import java.util.Map;

public class ZKSession {

	public static SessionAttrs getSessionAttrs() {
		return new SessionAttrs(getSessionAttrsMap());
	}

	public static Map<String, Object> getSessionAttrsMap() {
		return ZKR.getSession().getAttributes();
	}

	@RequiredArgsConstructor
	public static class SessionAttrs implements IGetterAs {

		private final Map<String, Object> sessionMap;

		public static SessionAttrs get() {
			return getSessionAttrs();
		}

		@Override
		public <T> T getAs(String key, Class<T> asType, T... defRq) {
			return MAP.getAs(sessionMap, key, asType, defRq);
		}

		public <T> void putAs(String key, Object value) {
			sessionMap.put(key, value);
		}

		public <T> void putAs(String key, Object value, Class<T> asType) {
			sessionMap.put(key, ObjTo.objTo(value, asType));
		}
	}
}
