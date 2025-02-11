package mpe.wthttp;

import com.google.common.collect.Multimap;
import mpc.exception.RequiredRuntimeException;
import mpc.types.ruprops.URuProps;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.RW;
import mpu.str.*;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class CallMsg extends CallErrMsg {
	public static final String PREFIX_HEADER_COMMENT_INNER = "#";
	public static final String PREFIX_COMMENT = "--";
	public static final String PREFIX_COMMENT_TG_SPEC = STR.DASH_SPEC;

	private static final String HEADER_DEL = ":";
	private static final String HEADER_META_DEL = "=";

	public final String fullMsg;
	public final List<String> linesMsg;
	public final String line0;

	public final State state;

	public final boolean enableFirstLineNoComment;

	private Object fromSrc;

	public Object getFromSrc(Object... defRq) {
		return fromSrc != null ? fromSrc : ARG.toDefThrowMsg(() -> X.f("From Source is null"), defRq);
	}

	public CallMsg setFromSrc(Object src) {
		this.fromSrc = src;
		return this;
	}

	public Object type() {
		return null;
	}

	public enum State {
		EMPTY, LINE, BODY
	}

	public CallMsg(String fullMsg, boolean enableFirstLineNoComment) {
		this.fullMsg = fullMsg;
		this.enableFirstLineNoComment = enableFirstLineNoComment;

		linesMsg = ARR.asAL(SPLIT.allByNL(fullMsg));

		if (fullMsg == null) {
			fullMsg = "";
		}

		switch (linesMsg.size()) {
			case 0:
				state = State.EMPTY;
				line0 = null;
				addError("Empty msg");
				break;
			case 1:
				state = State.LINE;
				line0 = fullMsg;
				break;
			default:
				state = State.BODY;
				line0 = linesMsg.get(0);
				linesMsg.remove(0);
				break;
		}

	}

	public Map getHeadersAsMap_All() {
		return URuProps.getRuPropertiesHeaders(headers_i_body()[0]);
	}

	public Multimap getBodyAsPropertiesMultimap() {
		return URuProps.getRuPropertiesMultiMap(headers_i_body()[1]);
	}

	public Map getBodyAsPropertiesMap() {
		return URuProps.getRuPropertiesClassic(headers_i_body()[1]);
	}

	public static boolean isHeaderComment(String line) {
		return line.startsWith(PREFIX_COMMENT) || line.startsWith(PREFIX_COMMENT_TG_SPEC);
	}

	public List<String> getLinesMsgWoFirstLine() {
		List<String> list = new ArrayList<>();
		if (linesMsg.size() == 1) {
			return list;
		}
		for (int i = 1; i < linesMsg.size(); i++) {
			list.add(linesMsg.get(i));
		}
		return list;
	}

	public Map<String, String> getHeadersAsMap_Meta() {
		return headers_i_body()[0].stream().filter(s -> s.startsWith(PREFIX_HEADER_COMMENT_INNER)).map(h -> USToken.two(h.substring(PREFIX_HEADER_COMMENT_INNER.length()), HEADER_META_DEL, null)).filter(X::NN).collect(Collectors.toMap(k -> k[0], v -> v[1]));
	}

	public String[][] getHeadersAsHeadersArgs() {
		List<String> headers = headers_i_body()[0];
		return headers.stream().filter(l -> !l.startsWith(PREFIX_HEADER_COMMENT_INNER)).map(h -> USToken.two(h, HEADER_DEL, null)).filter(X::NN).toArray(String[][]::new);
	}

	public String getBodyAsString() {
		return JOIN.allByNL(headers_i_body()[1]);
	}

	//
	//
	private List<String>[] headers_i_body = null;

	public List<String>[] headers_i_body() {
		return headers_i_body != null ? headers_i_body : (headers_i_body = getHeadersAndBodyLines(linesMsg, enableFirstLineNoComment));
	}

	public static List<String>[] getHeadersAndBodyLines(String data, boolean... enableFirstLineNoComment) {
		return getHeadersAndBodyLines(SPLIT.allByNL(data), ARG.isDefEqTrue(enableFirstLineNoComment));
	}

	public static List<String>[] getHeadersAndBodyLines(List<String> lines, boolean enableFirstLineNoComment) {
		List<String>[] two = new ArrayList[2];
		List<String> headers = new ArrayList<>();
		List<String> body = new ArrayList<>();
		for (String line : lines) {
			if (body.isEmpty() && isHeaderComment(line)) {
				headers.add(line.substring(PREFIX_COMMENT.length()));
				continue;
			}
			if (!enableFirstLineNoComment || !headers.isEmpty()) {
				body.add(line);
			}
		}
		two[0] = headers;
		two[1] = body;
		return two;
	}

	public String getValueByKeyFromHeader(String key, String... defRq) {
		String key0 = key + ":";
		String val = headers_i_body()[0].stream().filter(l -> l.startsWith(key0)).findAny().orElse(null);
		if (val != null) {
			return val.substring(key0.length());
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Set [%s] in header, e.g. --#%s:%s-value", key, key, key), defRq);
	}

	//
	//


	public static String trimCommentPfx(String line) {
		if (line.startsWith(PREFIX_COMMENT)) {
			return line.substring(PREFIX_COMMENT.length());
		} else if (line.startsWith(PREFIX_COMMENT_TG_SPEC)) {
			return line.substring(PREFIX_COMMENT_TG_SPEC.length());
		}
		return line;
	}

	public static CallMsg of(Path file) {
		return of(RW.readContent(file));
	}

	public static CallMsg of(String msg) {
		return (CallMsg) ofQk(msg).throwIsErr();
	}

	public static CallMsg ofQk(String msg) {
		return new CallMsg(msg, false);
	}

	public static boolean isValid(String data) {
		return CallMsg.ofQk(data).isValid();
	}
	//
	//

	@Override
	public String toString() {
		return "CallMsg{" +
				"state='" + state + '\'' +
				"msg='" + fullMsg + '\'' +
				", line='" + line0 + '\'' +
				", errs=" + X.sizeOf0(getErrors()) +
				'}';
	}
}
