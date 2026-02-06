package mpz_deprecated.app_event;

public class FAIL extends AppEvent {
	public FAIL(Object... state) {
		super(state);
	}

	public static FAIL of(Object... state) {
		return new FAIL(state);
	}
}
