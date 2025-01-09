package mpe.wthttp;

import com.jayway.jsonpath.JsonPath;
import mpc.exception.FIllegalArgumentException;
import mpc.exception.FIllegalStateException;
import mpc.exception.NI;
import mpc.exception.WhatIsTypeException;
import mpc.net.CON;
import mpc.net.IllegalHttpStatusException;
import mpc.net.JHttp;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;
import mpu.str.STR;
import mpu.str.UST;
import mpu.str.USToken;

import java.io.IOException;
import java.nio.file.Path;

public class HttpCallMsg extends CallMsg {
	public final String url;
	public final CON.Method http_method;


	public HttpCallMsg(String fullMsg) {
		super(fullMsg, true);

		if (fullMsg == null) {
			fullMsg = "";
		}

		if (X.empty(linesMsg)) {
			url = null;
			addError("Empty msg");
			http_method = CON.Method.UNDEFINED;
			return;
		}


		String[] two = USToken.two(line0, " ", null);
		if (two == null) {
			addError("Except two arg httpMethod + url, but came %s", line0);
		}

		{//HTTP_METHOD
			if (hasErrors()) {
				http_method = CON.Method.valueOf(line0, null);
			} else { // METHOD
				http_method = CON.Method.valueOf(two[0], CON.Method.UNDEFINED);
				if (http_method == CON.Method.UNDEFINED) {
					addError("Except first http method from string %s", two[0]);
				}

			}
		}

		{//URL
			out:
			if (hasErrors()) {
				url = UST.URL(line0, null) == null ? null : line0;
			} else { //URL

				this.url = two[1];

				if (UST.URL(url, null) == null) {
					FIllegalArgumentException e = new FIllegalArgumentException("Illegal url '%s'", url);
					addError(e);
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
				", line='" + line0 + '\'' +
				", state=" + state +
				", http_method=" + http_method +
				", errs=" + X.sizeOf0(getErrors()) +
				'}';
	}


	public static HttpCallMsg of(Path file, boolean... silent) {
		return of(RW.readContent(file), silent);
	}

	public static HttpCallMsg of(String msg, boolean... silent) {
		HttpCallMsg httpCallMsg = new HttpCallMsg(msg);
		return (HttpCallMsg) httpCallMsg.throwIsErr(ARG.isDefEqTrue(silent));
	}

	public static boolean isValid(String data) {
		return HttpCallMsg.of(data, true).isValid();
	}
}
