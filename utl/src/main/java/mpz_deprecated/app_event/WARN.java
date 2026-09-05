package mpz_deprecated.app_event;

public class WARN extends AppEvent {
	public WARN(Object... state) {
		super(state);
	}

	public static WARN of(Object... state) {
		return new WARN(state);
	}
}
