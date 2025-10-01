package zk_page.events;

public class ZKEventKey {
	final String primaryKey;
	final String[] secondKey;

	public ZKEventKey(String primaryKey, String... secondKey) {
		this.primaryKey = primaryKey;
		this.secondKey = secondKey;
	}

	public static ZKEventKey ALT = new ZKEventKey("@", null);
	public static ZKEventKey CTRL = new ZKEventKey("^", null);
	public static ZKEventKey SHIFT = new ZKEventKey("$", null);
	public static ZKEventKey MAC = new ZKEventKey("%", null);
	public static ZKEventKey NAV = new ZKEventKey("#", null);

	public static ZKEventKey NAV_HOME = new ZKEventKey("#", "home");
	public static ZKEventKey NAV_END = new ZKEventKey("#", "end");
	public static ZKEventKey NAV_INS = new ZKEventKey("#", "ins");
	public static ZKEventKey NAV_DEL = new ZKEventKey("#", "del");

	public static ZKEventKey NAV_PGUP = new ZKEventKey("#", "pgup");
	public static ZKEventKey NAV_PHDN = new ZKEventKey("#", "pgdn");

	public ZKEventKey and(String... letter) {
		return new ZKEventKey(primaryKey, letter);
	}

//	public ZKK and(String letter, String... and) {
//		StringBuilder sb = new StringBuilder(primaryKey + letter);
//		for (String a : and) {
//			sb.append(primaryKey + a);
//		}
//		return new ZKK(primaryKey, sb.toString());
//	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String a : secondKey) {
			sb.append(primaryKey + a);
		}
		return sb.toString();
	}

}
