package mpe.wthttp;

import mpc.exception.FIllegalStateException;
import mpc.exception.IErrorsCollector;
import mpc.exception.MultiCauseException;
import mpc.exception.RequiredRuntimeException;
import mpe.core.ERR;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.str.*;

import java.util.*;

public class CallMsg implements IErrorsCollector {
	public static final String PREFIX_COMMENT_INNER = "#";
	public static final String PREFIX_COMMENT = "--";
	public static final String PREFIX_COMMENT_TG_SPEC = STR.DASH_SPEC;

	public final String fullMsg;
	public final List<String> linesMsg;
	public final String line0;

	public final State state;

	public final boolean enableFirstLineNoComment;

	private List<Throwable> _errors;

	public enum State {
		EMPTY, LINE, BODY
	}

	public CallMsg(String fullMsg, boolean enableFirstLineNoComment) {
		this.fullMsg = fullMsg;
		this.enableFirstLineNoComment = enableFirstLineNoComment;

		linesMsg = ARR.asLL(SPLIT.allByNL(fullMsg));

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

	@Override
	public void addError(Throwable... ex) {
		if (_errors == null) {
			_errors = new LinkedList<>();
		}
		for (Throwable e : ex) {
			_errors.add(e);
		}
	}

	public static boolean isHeaderComment(String line) {
		return line.startsWith(PREFIX_COMMENT) || line.startsWith(PREFIX_COMMENT_TG_SPEC);
	}


	public String getJsonPath(String... defRq) {
		Optional<String> first = headers_i_body()[0].stream().filter(s -> s.startsWith("#$.")).findFirst();
		if (first.isPresent()) {
			first = Optional.of(first.get().substring(1));
		}
		return ARG.toDefThrowOpt(() -> new RequiredRuntimeException("Except json path in header"), first, defRq);
	}


	public String[][] getAsHeadersArrs() {
		List<String> headers = headers_i_body()[0];
		return headers.stream().filter(l -> !l.startsWith(PREFIX_COMMENT_INNER)).map(h -> USToken.two(h, ":", null)).filter(X::NN).toArray(String[][]::new);
	}

	public String getBody() {
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
			} else if (!enableFirstLineNoComment || !headers.isEmpty()) {
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
	public boolean isValid() {
		return X.empty(_errors);
	}

	public boolean hasErrors() {
		return X.notEmpty(_errors);
	}

	public String getErrsAsMsg(String head, boolean ol) {
		return isValid() ? "" : ERR.getMessagesAsStringWithHead(getErrors(), head, ol);
	}

	public List<Throwable> getErrors() {
		return _errors;
	}

	public Exception getMultiError() {
		if (hasErrors()) {
			return new MultiCauseException(_errors);
		}
		return null;
	}

	public CallMsg throwIsErr(boolean... silent) {
		if (isValid() || ARG.isDefEqTrue(silent)) {
			return this;
		}
		return X.throwException(getMultiError());
	}

//	public CallMsg throwIsNotState(State state) {
//		IT.state(this.state == state, "except state %s!=%s", state, this);
//		return this;
//	}

	public static String trimCommentPfx(String line) {
		if (line.startsWith(PREFIX_COMMENT)) {
			return line.substring(PREFIX_COMMENT.length());
		} else if (line.startsWith(PREFIX_COMMENT_TG_SPEC)) {
			return line.substring(PREFIX_COMMENT_TG_SPEC.length());
		}
		return line;
	}
	//
	//

	@Override
	public String toString() {
		return "CallMsg{" +
				"msg='" + fullMsg + '\'' +
				", line='" + line0 + '\'' +
				", errs=" + X.sizeOf0(_errors) +
				'}';
	}
}
