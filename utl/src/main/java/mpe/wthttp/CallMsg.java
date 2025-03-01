package mpe.wthttp;

import com.google.common.collect.Multimap;
import mpc.exception.RequiredRuntimeException;
import mpc.types.ruprops.URuProps;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.str.*;

import java.util.*;
import java.util.stream.Collectors;

public class CallMsg extends CallErrMsg {
	public static final String PREFIX_HEADER_COMMENT_INNER = "#";
	public static final String PREFIX_COMMENT_SQL = "--";
	public static final String PREFIX_COMMENT_BASH = "#";
	public static final String PREFIX_COMMENT_TG_SPEC = STR.DASH_SPEC;

	private static final String HEADER_DEL = ":";
	private static final String HEADER_META_DEL = "=";

	public final String fullMsg;
	public final List<String> linesMsgHeadersAndBody;
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

	public Object type(Object... defRq) {
		return null;
	}

	public enum State {
		EMPTY, LINE, BODY
	}

	public CallMsg(String fullMsg, boolean enableFirstLineNoComment) {
		this.fullMsg = fullMsg;
		this.enableFirstLineNoComment = enableFirstLineNoComment;

		linesMsgHeadersAndBody = ARR.asAL(SPLIT.allByNL(fullMsg));

		if (fullMsg == null) {
			fullMsg = "";
		}

		switch (linesMsgHeadersAndBody.size()) {
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
				line0 = linesMsgHeadersAndBody.get(0);
				linesMsgHeadersAndBody.remove(0);
				break;
		}

	}

	public String[] getHeaders_SEQARGS() {
		String[] linesMultimapAsSeq = URuProps.toLinesMultimapAsSeq(getHeaders_MAP());
		return linesMultimapAsSeq;
	}

	public Map getHeaders_MAP() {
		return URuProps.getRuPropertiesHeaders(headers_i_body()[0]);
	}

	public Multimap getBody_MMAP() {
		return URuProps.getRuPropertiesMultiMap(headers_i_body()[1]);
	}

	public Map getBody_MAP() {
		return URuProps.getRuPropertiesClassic(headers_i_body()[1]);
	}

	public static boolean isHeaderCommentCommon(String line) {
		return line.startsWith(PREFIX_COMMENT_SQL) || line.startsWith(PREFIX_COMMENT_TG_SPEC);
	}

	public static boolean isHeaderCommentBash(String line) {
		return line.startsWith(PREFIX_COMMENT_BASH);
	}

	public Map<String, String> getHeaders_METAMAP() {
		return headers_i_body()[0].stream().filter(s -> s.startsWith(PREFIX_HEADER_COMMENT_INNER)).map(h -> TKN.two(h.substring(PREFIX_HEADER_COMMENT_INNER.length()), HEADER_META_DEL, null)).filter(X::NN).collect(Collectors.toMap(k -> k[0], v -> v[1]));
	}

	public String[][] getHeaders_HTTP() {
		List<String> headers = headers_i_body()[0];
		return headers.stream().filter(l -> !l.startsWith(PREFIX_HEADER_COMMENT_INNER)).map(h -> TKN.two(h, HEADER_DEL, null)).filter(X::NN).toArray(String[][]::new);
	}

	public String getBody_STRING() {
		return JOIN.allByNL(headers_i_body()[1]);
	}

	public String getHeaderBashValueByKey(String key, String... defRq) {
		String key0 = key + "=";
		String val = headers_i_body_bash()[0].stream().filter(l -> l.startsWith(key0)).findAny().orElse(null);
		if (val != null) {
			return val.substring(key0.length());
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Set [%s] in header, e.g. ##%s=%s-value", key, key, key), defRq);
	}

	public String getHeaderValueByKey(String key, String... defRq) {
		String key0 = key + ":";
		String val = headers_i_body()[0].stream().filter(l -> l.startsWith(key0)).findAny().orElse(null);
		if (val != null) {
			return val.substring(key0.length());
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Set [%s] in header, e.g. --#%s:%s-value", key, key, key), defRq);
	}

	public Sb updateHeaderValueByKey(String key, String value, boolean update) {
		String key0 = key + ":";

		Sb sb = new Sb();

		List<String> all = headers_i_body()[0];
		boolean found = false;
		for (int i = 0; i < all.size(); i++) {
			String line = all.get(i);
			if (!line.startsWith(key0)) {
				sb.NL(line);
				continue;
			}
			found = true;
			String newLine = key0 + value;
			sb.NL(newLine);
			all.set(i, newLine);
		}
		if (!found) {
			if (!update) {
				throw new NullPointerException("Not found header for update (activate update?) - '" + key + "'");
			}
			String newLine = key0 + value;
			headers_i_body()[0].add(newLine);
			sb.NL(newLine);
		}
		return sb;

	}


	//
	//
	private List<String>[] headers_i_body = null;

	public List<String>[] headers_i_body() {
		return headers_i_body != null ? headers_i_body : (headers_i_body = getHeadersAndBodyLinesByCommonComment(linesMsgHeadersAndBody, enableFirstLineNoComment));
	}

	public List<String>[] headers_i_body_bash() {
		return getHeadersAndBodyLinesByBashComment(linesMsgHeadersAndBody);
	}

	public static List<String>[] getHeadersAndBodyLinesByCommonComment(List<String> lines, boolean enableFirstLineNoComment) {
		List<String>[] two = new ArrayList[2];
		List<String> headers = new ArrayList<>();
		List<String> body = new ArrayList<>();
		for (String line : lines) {
			if (body.isEmpty() && isHeaderCommentCommon(line)) {
				headers.add(line.substring(PREFIX_COMMENT_SQL.length()));
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

	public static List<String>[] getHeadersAndBodyLinesByBashComment(List<String> lines) {
		List<String>[] two = new ArrayList[2];
		List<String> headers = new ArrayList<>();
		List<String> body = new ArrayList<>();
		for (String line : lines) {
			if (body.isEmpty() && isHeaderCommentBash(line)) {
				headers.add(line.substring(PREFIX_COMMENT_BASH.length()));
				continue;
			}
			if (!headers.isEmpty()) {
				body.add(line);
			}
		}
		two[0] = headers;
		two[1] = body;
		return two;
	}
	//
	//


	public static String trimCommentPfx(String line) {
		if (line.startsWith(PREFIX_COMMENT_SQL)) {
			return line.substring(PREFIX_COMMENT_SQL.length());
		} else if (line.startsWith(PREFIX_COMMENT_TG_SPEC)) {
			return line.substring(PREFIX_COMMENT_TG_SPEC.length());
		}
		return line;
	}

//	public static CallMsg of(Path file) {
//		return of(RW.readContent(file));
//	}
//
//	public static CallMsg of(String msg) {
//		return (CallMsg) ofQk(msg).throwIsErr();
//	}

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
