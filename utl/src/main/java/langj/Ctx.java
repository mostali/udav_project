package langj;

import java.util.LinkedHashMap;
import java.util.Map;

public class Ctx {
	final Map ctx;

	public Ctx() {
		this(new LinkedHashMap());
	}

	public Ctx(Map ctx) {
		this.ctx = ctx;
	}

	public boolean isLink(String link) {
		return ctx.containsKey(link);
	}

	public Object get(String link) {
		return ctx.get(link);
	}

	@Override
	public String toString() {
		return "Ctx{" +
			   "ctx=" + ctx +
			   '}';
	}
}
