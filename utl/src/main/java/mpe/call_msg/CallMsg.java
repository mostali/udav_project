package mpe.call_msg;

import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.SneakyThrows;
import mpc.exception.RequiredRuntimeException;
import mpc.json.GsonMap;
import mpc.map.MAP;
import mpc.rfl.RFL;
import mpc.rfl.UReflScanner;
import mpc.types.ruprops.URuProps;
import mpe.call_msg.core.FileNode;
import mpe.call_msg.core.INode;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.str.*;

import java.util.*;
import java.util.stream.Collectors;

public class CallMsg extends CallErrMsg {

	public static final String ASYNC_WAIT_MS = "async.wait.ms";


	public static void main(String[] args) {

//		BashCallMsg callMsg = (BashCallMsg) ofNode(FileNode.of("/opt/appVol/bea/.planes/.index/test/.forms/note-Lxu/AppNotes.props"));
//		PyCallMsg callMsg = (PyCallMsg) ofNode(FileNode.of("/opt/appVol/bea/.planes/.index/test/.forms/PYTHON-RXO/AppNotes.props"));
		JarCallMsg callMsg = JarCallMsg.of(FileNode.of("/opt/appVol/bea/.planes/.index/test/.forms/get sheet row/AppNotes.props"));

		X.exit(callMsg.invokeJarMethod());
//		X.exit(callMsg.call(true));

	}

	public Object call(boolean throwIfHasError, Object... args) {
		throw new UnsupportedOperationException();
	}

	@SneakyThrows
	public static CallMsg ofNode(INode iNode) {
		String nodeData = iNode.readNodeDataStr();
		List<Class> typesCallMsg = UReflScanner.getAllPackageClassViaClassgraph(new String[]{CallMsg.class.getPackageName()});
		Map<Class, Object> callMsgKeysMap = typesCallMsg.stream().collect(HashMap::new, (m, v) -> m.put(v, RFL.fieldValueSt(v, "KEY", true, null)), HashMap::putAll);
		Optional<Map.Entry<Class, Object>> first = callMsgKeysMap.entrySet().stream().filter(ent -> ent.getValue() == null ? false : nodeData.startsWith(ent.getValue().toString())).findFirst();
		IT.state(first, "not found node entity: %s", iNode);
		Class cmsgType = first.get().getKey();
		return (CallMsg) RFL.inst(cmsgType, new Class[]{INode.class}, new Object[]{iNode});
	}

	public static final String PREFIX_HEADER_COMMENT_INNER = "#";
	public static final String PREFIX_COMMENT_SQL = "--";
	public static final String PREFIX_COMMENT_BASH = "#";
	public static final String PREFIX_COMMENT_TG_SPEC = STR.DASH_SPEC;

	private static final String HEADER_DEL = ":";
	private static final String HEADER_META_DEL = "=";

	public final @Getter String fileData;

	private final List<String> linesMsgHeadersAndBody;

	public List<String> linesMsgHeadersAndBody() {
		return linesMsgHeadersAndBody;
	}

	public final String line0;

	public final State state;

	public final boolean enableFirstLineNoComment;

	//
	//

	@Deprecated
	public CallMsg setFromSrc(Object src) {
//		this.fromSrc = src;
		return this;
	}

	//
	//

	private INode iNode;

	public INode getNode(INode... defRq) {
		return iNode != null ? iNode : ARG.toDefThrowMsg(() -> X.f("INode is null"), defRq);
	}

	public CallMsg fromNode(INode iNode) {
		this.iNode = iNode;
		return this;
	}

	public Object type(Object... defRq) {
		return null;
	}

	public String key() {
		return TKN.first(line0, " ", line0);
	}


	public enum State {
		EMPTY, LINE, BODY
	}

	public CallMsg(INode iNode) {
		this(iNode.readNodeDataStr(), false);
	}

	public CallMsg(String fileData, boolean enableFirstLineNoComment) {
		this.fileData = fileData;
		this.enableFirstLineNoComment = enableFirstLineNoComment;

		linesMsgHeadersAndBody = ARR.asAL(SPLIT.allByNL(fileData));

		if (fileData == null) {
			fileData = "";
		}

		switch (linesMsgHeadersAndBody.size()) {
			case 0:
				state = State.EMPTY;
				line0 = null;
				addError("Empty msg");
				break;
			case 1:
				state = State.LINE;
				line0 = fileData;
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

	public GsonMap getBody_GSONMAP(GsonMap... defRq) {
		return GsonMap.of(getBody_STRING(), defRq);
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

	public String getHeaderKey_AsBashComment(String key, String... defRq) {
		String key0 = key + "=";
		String val = headers_i_body_bash()[0].stream().filter(l -> l.startsWith(key0)).findAny().orElse(null);
		if (val != null) {
			return val.substring(key0.length());
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Set [%s] in header, e.g. #%s=value", key, key), defRq);
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

	public static CallMsg ofAnyNode(String data) {
		return new CallMsg(data, true);
	}

	public static boolean isValid(String data) {
		return CallMsg.ofAnyNode(data).isValid();
	}


	public boolean isSync() {
		return getAsyncWaitMs(-1) < 0;
	}

	public Integer getAsyncWaitMs(Integer... defRq) {
		return MAP.getAsInt(getHeaders_MAP(), ASYNC_WAIT_MS, defRq);
	}
	//
	//

	@Override
	public String toString() {
		return "CallMsg{" + "state='" + state + '\'' + "msg='" + fileData + '\'' + ", line='" + line0 + '\'' + ", errs=" + X.sizeOf0(getErrors()) + '}';
	}

}
