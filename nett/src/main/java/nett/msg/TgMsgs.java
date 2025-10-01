package nett.msg;

import botcore.msg.BotMsgs;
import mpu.core.ARG;
import mpu.IT;
import mpu.X;
import mpc.str.sym.SYMJ;
import nett.Tgh;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TgMsgs<M> extends BotMsgs<M, TgMsg> {

	private boolean disableWebPagePreview = false;
	private String parseMode = ParseMode.HTML;

	public static <M> TgMsgs builder(Function<M, String> line_builder) {
		return new TgMsgs(line_builder);
	}

	public static TgMsgs of() {
		return new TgMsgs();
	}

	public static TgMsg ofObjects(Object... lines) {
		return (TgMsg) new TgMsgs().ol().build(lines);
	}

	public static TgMsg of(Map map) {
		return (TgMsg) TgMsgs.builder(TgMsgs.builderMsg_MapEntry).build(map.entrySet());
	}

	public static TgMsg of(Collection lines) {
		return (TgMsg) TgMsgs.of().ol().build(lines);
	}

	public TgMsgs() {
		super(null);
	}

	public TgMsgs(Function<M, String> line_builder) {
		super(line_builder);
	}

	public TgMsg build(Collection<M> list) {
		if (X.empty(list)) {
			return TgMsg.of(IT.notNull(messageIfEmpty));
		}
		return TgMsg.of(buildContent(list).toString()).setParseMode(parseMode).setDisableWebPagePreview(disableWebPagePreview);
	}

	public StringBuilder buildContent(List<M> list) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (M m : list) {
			if (ol) {
				sb.append(ParseMode.HTML.equals(parseMode) ? Tgh.i(i + 1 + ") ") : i + ") ");
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
		return sb;
	}

	public TgMsgs<M> messageIfEmpty(String messageIfEmpty) {
		return (TgMsgs<M>) super.messageIfEmpty(messageIfEmpty);
	}

	public static final Function<String[], String> DEF_INFO_LINE_BUILDER = l -> Tgh.code(null, l[0], SYMJ.ARROW_RIGHT_SPEC + " " + l[1]);

	public static TgMsg getInfoString(String[][] info, Function<String[], String>... line_builder) {
		return (TgMsg) new TgMsgs<String[]>(ARG.toDefOr(DEF_INFO_LINE_BUILDER, line_builder)).build(info);
	}
}
