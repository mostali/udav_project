package mpe.logs;

import java.util.UUID;

public class MarkWithCounter {

	//	public static void main(String[] args) {
	//		MarkWithCounter mark = MarkWithCounter.create("mark");
	//		String message = mark.next() + "полезное сообщение";
	//		P.p(message);
	//		mark.next();
	//		P.p(mark.next("2"));
	//	}

	public static final String DEF_KEY = "__MARK_WITH_COUNTER__";
	public static final String DEF_DELIMTER = "/";
	public final String id;
	public final String prefixId;
	public final String delimter;
	public int counter;
	private boolean useCounter;
	private boolean useUniqCode;

	public MarkWithCounter() {
		this(3);
	}

	public MarkWithCounter(int length) {
		this(null, null, null, null, true, true);
	}

	public MarkWithCounter(String markId, Integer lengthRandomId, String prefix_nameId, String delimter, boolean useCounter, boolean useUniqCode) {
		if (markId == null) {
			lengthRandomId = lengthRandomId == null || lengthRandomId <= 0 ? 3 : lengthRandomId;
		}
		this.id = markId == null ? UUID.randomUUID().toString().substring(0, lengthRandomId) : markId;
		this.counter = 0;
		this.prefixId = prefix_nameId == null ? "" : prefix_nameId;
		this.delimter = delimter == null ? " " : delimter;
		this.useCounter = useCounter;
		this.useUniqCode = useUniqCode;
	}

	public String next(String message) {
		return next() + delimter + message;
	}

	public String next() {
		return (prefixId.isEmpty() ? "" : (prefixId)) + (useUniqCode ? delimter + (id) : "") + (useCounter ? delimter + (++counter) : "");
	}

	public static MarkWithCounter create(String prefixId) {
		return new MarkWithCounter(null, null, prefixId, DEF_DELIMTER, true, true);
	}

	/**
	 * MarkWithCounter mark=MarkWithCounter.createSimpleWithCounter(MARK_OPER, "/");
	 * String message = mark.next() + "полезное сообщение";
	 */
	public static MarkWithCounter createSimpleWithCounter(String prefixId, String delimter) {
		return new MarkWithCounter(null, null, prefixId, delimter, true, true);
	}

	public static MarkWithCounter createSimple(String prefixId) {
		return new MarkWithCounter(null, null, prefixId, DEF_DELIMTER, false, false);
	}

	public static MarkWithCounter createCustom(String markId, Integer lengthRandomId, String prefixId, String delimter, boolean useCounter) {
		return new MarkWithCounter(markId, lengthRandomId, prefixId, delimter, useCounter, true);
	}


}