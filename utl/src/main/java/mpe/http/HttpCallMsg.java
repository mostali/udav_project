package mpe.http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mpc.exception.*;
import mpc.net.CON;
import mpc.net.IllegalHttpStatusException;
import mpc.net.JHttp;
import mpc.str.sym.SYMJ;
import mpe.core.ERR;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.str.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class HttpCallMsg {
	public static final String PREFIX_COMMENT_INNER = "#";
	public static final String PREFIX_COMMENT = "--";
	public static final String PREFIX_COMMENT_TG_SPEC = STR.DASH_SPEC;
	public final String msg;
	final List<String> linesMsg;
	public final String line;
	public final String url;
	public final State state;
	public final CON.Method http_method;
	public final List<Exception> errs;

	public HttpCallMsg(String msg) {
		this.msg = msg;
		linesMsg = ARR.ar(SPLIT.allByNL(msg));

		errs = new ArrayList();

		if (msg == null) {
			msg = "";
		}
		switch (linesMsg.size()) {
			case 0:
				state = State.EMPTY;
				line = null;
				url = null;
//				body = ARR.EMPTY_LIST;
				http_method = CON.Method.UNDEFINED;
				return;
			case 1:
				state = State.LINE;
				line = msg;
//				body = ARR.EMPTY_LIST;
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

				if (UST.URL(url) == null) {
					FIllegalArgumentException e = new FIllegalArgumentException("Illegal url '%s'", url);
					errs.add(e);
					break out;
				}

			}
		}
	}

	public String sendHttpCall100_or_custom400_404(boolean... trimRight) throws IOException {
		try {
			String rsp = sendHttpCall();
			return ARG.isDefEqTrue(trimRight) ? STR.trimRight(rsp) : rsp;
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

	public String sendHttpCall() throws IOException {
		String rsp = HttpCallMsg.sendHttpCall(this);
		return rsp;
	}

	public static String sendHttpCall(HttpCallMsg httpCallMsg) throws IOException {
		String rsp;
		switch (httpCallMsg.http_method) {
			case GET: {
				rsp = JHttp.GET_BODY(httpCallMsg.url, httpCallMsg.asHeadersArrs(), httpCallMsg.asBody(), String.class, 200);
				break;
			}
			case POST: {
				rsp = JHttp.POST_BODY(httpCallMsg.url, httpCallMsg.asHeadersArrs(), httpCallMsg.asBody(), String.class, 200);
				break;
			}
			case DELETE: {
				rsp = JHttp.DELETE_BODY(httpCallMsg.url, httpCallMsg.asHeadersArrs(), httpCallMsg.asBody(), String.class, 200);
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
				"msg='" + msg + '\'' +
				", line='" + line + '\'' +
				", state=" + state +
				", http_method=" + http_method +
				", errs=" + X.sizeOf0(errs) +
				'}';
	}

	public boolean hasErrors() {
		return X.notEmpty(getErrs());
	}

	public String[][] asHeadersArrs() {
		List<String> headers = headers_i_body()[0];
		return headers.stream().filter(l -> !l.startsWith(PREFIX_COMMENT_INNER)).map(h -> USToken.two(h, ":", null)).filter(X::NN).toArray(String[][]::new);
	}

	public String asBody() {
		return JOIN.allByNL(headers_i_body()[1]);
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

	public static HttpCallMsg of(String msg, boolean... silent) {
		HttpCallMsg httpCallMsg = new HttpCallMsg(msg);
		return httpCallMsg.throwIsErr(ARG.isDefEqTrue(silent));
	}
}
