package botcore.msg;

import botcore.clb.IBotButton;
import mpu.core.ARG;
import mpu.core.ARGn;
import mpu.IT;
import mpc.str.sym.SYMJ;
import mpu.str.STR;
import mpu.X;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class BotMsgs<M, BM extends IBotMsg> {
	public static final Function<Map.Entry<Object, Object>, String> builderMsg_MapEntry = (e) -> e.getKey() + ":" + e.getValue();
	public static final Function<Map.Entry<Object, Object>, String> builderMsg_MapEntry_UL_SPEC = (e) -> SYMJ.ARROW_RIGHT_SPEC + e.getValue();
	public static final Function<String[], String> builderMsg_StringArray = l -> l[0] + SYMJ.ARROW_RIGHT_SPEC + " " + l[1];
	protected boolean isAppendNL = true;

	private final Function<M, String> line_builder;

	public BotMsgs() {
		this(null);
	}

	public BotMsgs(Function<M, String> line_builder) {
		this.line_builder = line_builder;
	}

	public BM buildFromAny(Object... messages) {
		return build((M[]) messages);
	}

	public BM build(M[] messages) {
		return build(Arrays.asList(messages));
	}

	public BM build(Map map) {
		return (BM) build(map.entrySet());
	}

	public abstract BM build(Collection<M> list);

	protected StringBuilder buildContent(Collection<M> list) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		if (title != null) {
			sb.append(title).append(STR.NL);
		}
		if (X.empty(list)) {
			sb.append(messageIfEmpty);
		} else {
			for (M m : list) {
				if (ol) {
					sb.append(++i + ") ");
				}
				String str = buildMsg(m);
				if (str == null) {
					continue;
				}
				sb.append(str);
				if (isAppendNL && i != list.size()) {
					sb.append("\n");
				}
			}
		}
		return sb;
	}

	protected String buildEmptyMessage() {
		String msg = "";
		if (X.notEmpty(title)) {
			msg += title;
		}
		if (X.notEmpty(messageIfEmpty)) {
			msg = msg.isEmpty() ? messageIfEmpty : STR.NL + messageIfEmpty;
		}
		return IT.NE(msg);
	}

	public String buildMsg(M mdl) {
		if (line_builder != null) {
			return line_builder.apply(mdl);
		}
		return String.valueOf(mdl);
	}

	public String messageIfEmpty = "empty";

	public BotMsgs<M, BM> messageIfEmpty(String messageIfEmpty) {
		this.messageIfEmpty = messageIfEmpty;
		return this;
	}

	protected String title;

	protected List<List<IBotButton>> keys;
	protected boolean global;
	protected Integer emsg;


	public BotMsgs<M, BM> title(String msg, Object... args) {
		this.title = X.f(msg, args);
		return this;
	}

	public BotMsgs<M, BM> emsg(Integer emsg) {
		this.emsg = emsg;
		return this;
	}

	public BotMsgs<M, BM> keys(List<List<IBotButton>> keys, boolean... global) {
		this.keys = keys;
		this.global = ARG.isDefEqTrue(global);
		return this;
	}

	protected boolean ol = false;

	public BotMsgs<M, BM> ol(boolean... olTrue) {
		this.ol = ARGn.toDefOr(true, olTrue);
		return this;
	}

}
