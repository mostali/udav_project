package mpe.wthttp;

import com.jayway.jsonpath.JsonPath;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mpc.exception.*;
import mpc.fs.UUrl;
import mpc.net.CON;
import mpc.net.IllegalHttpStatusException;
import mpc.net.JHttp;
import mpc.str.sym.SYMJ;
import mpe.core.ERR;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.RW;
import mpu.str.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class HttpCallMsg {
	public static final String PREFIX_COMMENT_INNER = "#";
	public static final String PREFIX_COMMENT = "--";
	public static final String PREFIX_COMMENT_TG_SPEC = STR.DASH_SPEC;
	public final String fullMsg;
	final List<String> linesMsg;
	public final String line;
	public final String url;
	public final State state;
	public final CON.Method http_method;
	@Getter
	public final List<Exception> errs;

	public HttpCallMsg(String fullMsg) {
		this.fullMsg = fullMsg;
		linesMsg = ARR.ar(SPLIT.allByNL(fullMsg));

		errs = new ArrayList();

		if (fullMsg == null) {
			fullMsg = "";
		}
		switch (linesMsg.size()) {
			case 0:
				state = State.EMPTY;
				line = null;
				url = null;
				errs.add(new FIllegalStateException("Empty"));
				http_method = CON.Method.UNDEFINED;
				return;
			case 1:
				state = State.LINE;
				line = fullMsg;
				break;
			default:
				state = State.BODY;
				line = linesMsg.get(0);
				linesMsg.remove(0);
//				body = lines;
				break;
		}


		String[] two = USToken.two(line, " ", null);
		if (two == null) {
			errs.add(new FIllegalStateException("Except two arg method + url, but came %s", line));
		}

		{//HTTP_METHOD
			if (X.notEmpty(errs)) {
				http_method = CON.Method.valueOf(line, null);
			} else { // METHOD
				http_method = CON.Method.valueOf(two[0], CON.Method.UNDEFINED);
				if (http_method == CON.Method.UNDEFINED) {
					errs.add(new FIllegalStateException("Except first http method from string %s", two[0]));
				}

			}
		}

		{//URL
			out:
			if (X.notEmpty(errs)) {
				url = UST.URL(line, null) == null ? null : line;
			} else { //URL

				this.url = two[1];

				if (UST.URL(url, null) == null) {
					FIllegalArgumentException e = new FIllegalArgumentException("Illegal url '%s'", url);
					errs.add(e);
					break out;
				}

			}
		}
	}

	public String sendHttpCall100_or_custom400_404(boolean... trimRight) throws IOException {
		try {
			String rsp = sendHttpCall(trimRight);
			return rsp;
		} catch (IllegalHttpStatusException ex) {
			switch (ex.code()) {
				case 404:
					return SYMJ.INFO_SIMPLE + " " + ex.getMsg();
				case 400:
					return SYMJ.WARN + " " + ex.getMsg();
				default:
					throw ex;
			}
		}
	}

	public String sendHttpCallWithJsonPath(String jsonPath) throws IOException {
		String rsp = sendHttpCall();
		return JsonPath.read(rsp, jsonPath);
	}

	private String rsp;

	public String sendHttpCall(boolean... trimRight) throws IOException {
		if (rsp != null) {
			return null;
		}
		rsp = HttpCallMsg.sendHttpCall0(this);
		return ARG.isDefEqTrue(trimRight) ? STR.trimRight(rsp) : rsp;
	}


	public static String sendHttpCall0(HttpCallMsg httpCallMsg) throws IOException {
		String rsp;
		switch (httpCallMsg.http_method) {
			case GET: {
				rsp = JHttp.GET_BODY(httpCallMsg.url, httpCallMsg.getAsHeadersArrs(), httpCallMsg.getBody(), String.class, 200);
				break;
			}
			case POST: {
				rsp = JHttp.POST_BODY(httpCallMsg.url, httpCallMsg.getAsHeadersArrs(), httpCallMsg.getBody(), String.class, 200);
				break;
			}
			case DELETE: {
				rsp = JHttp.DELETE_BODY(httpCallMsg.url, httpCallMsg.getAsHeadersArrs(), httpCallMsg.getBody(), String.class, 200);
				break;
			}
			case PUT: {
				throw NI.stop("ni put");
//							String rsp = JHttp.PUT_BODY(httpCallMsg.url, JOIN.allByNL(httpCallMsg.body), String.class, 200);
//							ZKI.infoEditorBw(rsp);
//							break;
			}
			default:
				throw new WhatIsTypeException(httpCallMsg.http_method);
		}
		return rsp;
	}


	@Override
	public String toString() {
		return "HttpCallMsg{" +
				"msg='" + fullMsg + '\'' +
				", line='" + line + '\'' +
				", state=" + state +
				", http_method=" + http_method +
				", errs=" + X.sizeOf0(errs) +
				'}';
	}

	public boolean hasErrors() {
		return X.notEmpty(getErrs());
	}

	public String[][] getAsHeadersArrs() {
		List<String> headers = headers_i_body()[0];
		return headers.stream().filter(l -> !l.startsWith(PREFIX_COMMENT_INNER)).map(h -> USToken.two(h, ":", null)).filter(X::NN).toArray(String[][]::new);
	}

	public String getBody() {
		return JOIN.allByNL(headers_i_body()[1]);
	}

	public boolean isKafkaCall() {
		return X.notEmpty(this.headers_i_body()[0]) && this.headers_i_body()[0].get(0).equals("#kafka/");
	}

	public String[] getTwoHostAndPageRq() {
		String[] pathFirstAndSencondItemFromUrl = UUrl.getHostAndSencondItemPath(url);
		String url = pathFirstAndSencondItemFromUrl[0];
		String topic = IT.notEmpty(pathFirstAndSencondItemFromUrl[1], "set first path in url '%s'", url);
		return new String[]{url, topic};
	}

	public String getJsonPath(String... defRq) {
		Optional<String> first = headers_i_body()[0].stream().filter(s -> s.startsWith("#$.")).findFirst();
		if (first.isPresent()) {
			first = Optional.of(first.get().substring(1));
		}
		return ARG.toDefThrowOpt(() -> new RequiredRuntimeException("Except json path in header"), first, defRq);
	}

	public String getErrsAsMsg() {
		return getErrs().stream().map(ERR::getStackTrace).collect(Collectors.joining(STR.NL));
	}

	public enum State {
		EMPTY, LINE, BODY
	}

	public HttpCallMsg throwIsNotState(State state) {
		IT.state(this.state == state, "except state %s!=%s", state, this);
		return this;
	}

	public Exception getRootErr() {
		if (X.notEmpty(getErrs())) {
			return ERR.multiCause((List) errs);
		}
		return null;
	}

	public HttpCallMsg throwIsErr(boolean... silent) {
		Exception rootErr = getRootErr();
		if (rootErr == null) {
			return this;
		} else if (ARG.isDefEqTrue(silent)) {
			return this;
		}
		return X.throwException(rootErr);
	}

	List<String>[] two = null;

	public List<String>[] headers_i_body() {
		if (two != null) {
			return two;
		}
		two = new ArrayList[2];
		List<String> headers = new ArrayList<>();
		List<String> body = new ArrayList<>();
		for (String line : linesMsg) {
			if (body.isEmpty() && isHeaderComment(line)) {
				headers.add(line.substring(PREFIX_COMMENT.length()));
			} else if (!headers.isEmpty()) {
				body.add(line);
			}
		}
		two[0] = headers;
		two[1] = body;
		return two;
	}

	public static boolean isHeaderComment(String line) {
		return line.startsWith(PREFIX_COMMENT) || line.startsWith(PREFIX_COMMENT_TG_SPEC);
	}

	public static HttpCallMsg of(Path file, boolean... silent) {
		return of(RW.readContent(file), silent);
	}

	public static HttpCallMsg of(String msg, boolean... silent) {
		HttpCallMsg httpCallMsg = new HttpCallMsg(msg);
		return httpCallMsg.throwIsErr(ARG.isDefEqTrue(silent));
	}
}
