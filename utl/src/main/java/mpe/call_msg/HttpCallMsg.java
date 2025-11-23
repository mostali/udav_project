package mpe.call_msg;

import com.jayway.jsonpath.JsonPath;
import lombok.Getter;
import mpc.exception.FIllegalArgumentException;
import mpc.exception.NI;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.url.UUrl;
import mpc.net.CON;
import mpc.net.IllegalHttpStatusException;
import mpc.net.JHttp;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;
import mpu.str.STR;
import mpu.str.UST;
import mpu.str.TKN;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class HttpCallMsg extends CallMsg {
	public static final String TID = "tid";
	private final String url;
	private @Getter
	final CON.Method http_method;

	@Override
	public CON.Method type(Object... defRq) {
		return http_method;
	}

	public static boolean isValid(String data) {
		return HttpCallMsg.ofQk(data).isValid();
	}

	public static boolean isValidKeyFirstLine(String data) {
		return TKN.first(data, " ", CON.Method.class, null) != null;
	}


	public HttpCallMsg(String fullMsg) {
		super(fullMsg, true);

		if (fullMsg == null) {
			fullMsg = "";
		}

		if (X.empty(linesMsgHeadersAndBody())) {
			url = null;
			addError("Empty msg");
			http_method = CON.Method.UNDEFINED;
			return;
		}


		String[] two = TKN.two(line0, " ", null);
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
			String rsp = sendHttpCall_200_201_204(trimRight);
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

	private String rsp;

	public String sendHttpCall_200_201_204(boolean... trimRight) throws IOException {
		if (rsp != null) {
			return null;
		}
		rsp = HttpCallMsg.sendHttpCall0(this, String.class, 200, 201, 204);
		return ARG.isDefEqTrue(trimRight) ? STR.trimRightSpecial(rsp) : rsp;
	}

	public <T> T sendHttpCallAs(Class<T> rspType, boolean... trimRight) throws IOException {
		return UST.strTo(sendHttpCall_200_201_204(trimRight), rspType);
	}

	public String sendHttpCallAsJson(boolean... trimRight) throws IOException {
		String rsp = sendHttpCall_200_201_204();
		String jsonPath = getJsonPath(null);
		if (jsonPath != null) {
			return JsonPath.read(rsp, jsonPath);
		}
		return rsp;
	}


	public String getJsonPath(String... defRq) {
		Optional<String> first = headers_i_body()[0].stream().filter(s -> s.startsWith("#$.")).findFirst();
		if (first.isPresent()) {
			first = Optional.of(first.get().substring(1));
		}
		return ARG.toDefThrowOpt(() -> new RequiredRuntimeException("Except json path in header"), first, defRq);
	}

	public String getXPath(String... defRq) {
		Optional<String> first = headers_i_body()[0].stream().filter(s -> s.startsWith("#%.")).findFirst();
		if (first.isPresent()) {
			first = Optional.of(first.get().substring(1));
		}
		return ARG.toDefThrowOpt(() -> new RequiredRuntimeException("Except json path in header"), first, defRq);
	}

	public static <T> T sendHttpCall0(HttpCallMsg httpCallMsg, Class<T> rspAs, Integer... successHttpCode) throws IOException {

		T rsp;
		switch (httpCallMsg.http_method) {
			case GET: {
				rsp = JHttp.GET_BODY(httpCallMsg.url(), httpCallMsg.getHeaders_HTTP(), httpCallMsg.getBody_STRING(), rspAs, successHttpCode);
				break;
			}
			case POST: {
				rsp = JHttp.POST_BODY(httpCallMsg.url(), httpCallMsg.getHeaders_HTTP(), httpCallMsg.getBody_STRING(), rspAs, successHttpCode);
				break;
			}
			case DELETE: {
				rsp = JHttp.DELETE_BODY(httpCallMsg.url(), httpCallMsg.getHeaders_HTTP(), httpCallMsg.getBody_STRING(), rspAs, successHttpCode);
				break;
			}
			case PUT: {
				rsp = JHttp.PUT_BODY(httpCallMsg.url(), httpCallMsg.getHeaders_HTTP(), httpCallMsg.getBody_STRING(), rspAs, successHttpCode);
				break;
//				throw NI.stop("ni put");
////							String rsp = JHttp.PUT_BODY(httpCallMsg.url, JOIN.allByNL(httpCallMsg.body), String.class, 200);
////							ZKI.infoEditorBw(rsp);
////							break;
			}
			default:
				throw new WhatIsTypeException(httpCallMsg.http_method);
		}
		return rsp;
	}

	public String url(String... trackId) {
		return ARG.isDefNNF(trackId) ? UUrl.addQueryParam(this.url, TID, ARG.toDef(trackId)) : this.url;
	}


	@Override
	public String toString() {
		return "HttpCallMsg{" +
				"msg='" + fileData + '\'' +
				", line='" + line0 + '\'' +
				", state=" + state +
				", http_method=" + http_method +
				", errs=" + X.sizeOf0(getErrors()) +
				'}';
	}


//	public static HttpCallMsg of(IPath file) {
//		return of(file.toPath());
//	}

	public static HttpCallMsg of(Path file) {
		return of(RW.readString(file));
	}

	public static HttpCallMsg of(String msg) {
		return (HttpCallMsg) ofQk(msg).throwIsErr();
	}

	public static HttpCallMsg ofQk(String msg) {
		return new HttpCallMsg(msg);
	}

}
