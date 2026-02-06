package mpc.types.unesc_types;

public class UnescHtml extends EscHtml {

	public UnescHtml(String data) {
		super(data);
	}

	public String convert() {
		return unescape(data);
	}

	public static UnescHtml of(String args) {
		return new UnescHtml(args);
	}
}
