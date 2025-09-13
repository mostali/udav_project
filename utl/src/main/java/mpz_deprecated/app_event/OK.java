package mpz_deprecated.app_event;

public class OK extends AppEvent {
	public OK(Object... state) {
		super(state);
	}

	public static OK of(Object... state) {
		return new OK(state);
	}
}
