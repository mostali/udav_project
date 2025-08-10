package mpz_deprecated.app_event;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AppEvent {
	final Object state;

	public enum Type {
		UNDEFINED, OK, FAIL, WARN;

		public static Type getType(AppEvent event) {
			if (event.isOk()) {
				return OK;
			} else if (event.isFail()) {
				return FAIL;
			} else if (event.isWarn()) {
				return WARN;
			}
			return UNDEFINED;
		}
	}

	public Type getType() {
		return Type.getType(this);
	}

	public boolean isOk() {
		return this instanceof OK;
	}

	public boolean isWarn() {
		return this instanceof WARN;
	}

	public boolean isFail() {
		return this instanceof FAIL;
	}

	public String first() {
		if (state == null) {
			return "null";
		} else if (state.getClass().isArray()) {
			Object[] ev = (Object[]) state;
			if (ev.length == 0 || ev[0] == null) {
				return "null";
			}
			return ev[0].toString();
		} else {
			return state.toString();
		}
	}

}
