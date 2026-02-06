package utl_web;

import lombok.SneakyThrows;
import mpc.exception.FIllegalStateException;
import mpc.exception.IResponseStatusException;
import mpc.exception.RestStatusException;
import mpc.fs.UF;
import mpc.exception.CleanDataResponseException;
import mpc.net.ContentType;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;
import mpu.str.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import utl_rest.StatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class URsp {

	public static final Logger L = LoggerFactory.getLogger(URsp.class);

	public static Void sendError404(HttpServletResponse response) throws IOException {
		return sendErrorWithCode(response, HttpServletResponse.SC_NOT_FOUND, null);
	}

	public static Void sendError404(HttpServletResponse response, String msg, Object... args) throws IOException {
		return sendErrorWithCode(response, HttpServletResponse.SC_NOT_FOUND, msg, args);
	}

	public static Void sendError400(HttpServletResponse response, String msg, Object... args) throws IOException {
		return sendErrorWithCode(response, HttpServletResponse.SC_BAD_REQUEST, msg, args);
	}

	public static Void sendError500(HttpServletResponse response, String msg, Object... args) throws IOException {
		return sendErrorWithCode(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg, args);
	}

	public static Void sendError403(HttpServletResponse response, String msg, Object... args) throws IOException {
		return sendErrorWithCode(response, HttpServletResponse.SC_FORBIDDEN, msg, args);
	}

	public static Void sendMsg(HttpServletResponse response, int code, String msg, Object... args) throws IOException {
		msg = msg == null ? null : X.f(msg, args);
		response.getWriter().println(msg);
		response.setStatus(code);
		return null;
	}


	public static Void sendErrorWithCode(HttpServletResponse response, int code, String msg, Object... args) throws IOException {
		if (msg != null) {
			response.sendError(code, X.f(msg, args));
		} else {
			response.sendError(code);
		}
		return null;
	}

	@SneakyThrows
	public static void sendResponseWithCleanDataAndClose(HttpServletResponse response, String data) {
		response.setStatus(200);
		response.getWriter().write(data);
//		if (true) {
//			new FakeAsyncRest(response).go();
//		}
		response.getWriter().close();
	}

	//	@RequiredArgsConstructor
//	static class FakeAsyncRest {
//		final HttpServletResponse response;
//
//		void go() {
//			while (true) {
//				try {
//					response.getWriter().write("asdasd:" + Thread.currentThread() + "\n");
//					SLEEP.sleep(1000, "wait");
//					response.flushBuffer();
//				} catch (Exception ex) {
//					X.p(ex);
//				}
//			}
//		}
//	}
	@SneakyThrows
	public static void sendResponse500AndClose(HttpServletResponse response, String data) {
		response.setStatus(500);
		response.getWriter().write(data);
		response.getWriter().close();
	}

	@SneakyThrows
	public static void sendResponseAndClose(HttpServletResponse response, RestStatusException rsp) {
		sendResponseAndClose(response, rsp.code(), rsp.getCleanData());
	}

	@SneakyThrows
	public static void sendResponseAndClose(HttpServletResponse response, StatusException rsp) {
		sendResponseAndClose(response, rsp.code(), rsp.getMessage());
	}

	@SneakyThrows
	public static void sendResponseAndClose(HttpServletResponse response, int code, String rspData) {
		response.setStatus(code);
		response.getWriter().write(rspData);
		response.getWriter().close();
	}

	@SneakyThrows
	public static void sendResponseAndClose(HttpServletResponse response, Pare<Integer, String> pareRsp) {
		response.setStatus(pareRsp.key());
		response.getWriter().write(pareRsp.val());
		response.getWriter().close();
	}

	@SneakyThrows
	public static void sendResponseAsException(HttpServletResponse response, Throwable exRslt) {

		if (exRslt instanceof CleanDataResponseException) {
			sendResponse(response, (CleanDataResponseException) exRslt);
		} else if (exRslt instanceof RestStatusException) {
			RestStatusException rse = (RestStatusException) exRslt;
			sendResponse(response, rse);
		} else if (exRslt instanceof StatusException) {
			StatusException rse = (StatusException) exRslt;
			sendResponse(response, rse);
		} else {

//			if (APP.isDebugEnable()) {
//				sendError500(response, )
//			}else {
//				sendError500(response, )
//			}
			//L.error(X.f("build PageSP ERROR '%s'", exRslt);
			X.throwException(exRslt);
		}

	}

	@SneakyThrows
	public static void sendResponse_FromFileByExt(HttpServletResponse response, ContentType contentType, File file) {
		URspContentType.sendResponseContentType_FromFile(response, contentType, file);
	}

	@SneakyThrows
	public static void sendResponseImage_FromFile(HttpServletResponse response, File file) {
		URspContentType.sendResponseContentType_FromFile(response, ContentType.IMG_PNG, file);
	}

	public static void sendResponse(HttpServletResponse response, IResponseStatusException sex, boolean... close) {
		sendResponse(false, response, sex, close);
	}

	@SneakyThrows
	public static void sendResponse(boolean useErrorMethod, HttpServletResponse response, IResponseStatusException sex, boolean... close) {
		try {
			if (sex.isOk()) {
				sendMsg(response, sex.code(), sex.getMessage());
			} else {
				if (useErrorMethod) {
					//not work, body not visible
					sendErrorWithCode(response, sex.code(), sex.getMessage());
				} else {
					sendMsg(response, sex.code(), sex.getMessage());
				}
			}
		} finally {
			if (ARG.isDefEqTrue(close)) {
				response.getWriter().close();
			}
		}
	}


	@SneakyThrows
	public static void sendResponse(HttpServletResponse rsp, CleanDataResponseException cleanDataErr) {

		if (cleanDataErr.isNothing()) {
			if (L.isInfoEnabled()) {
				//L.info(X.f("build PageSP(CleanDataResponse) SUCCESS/NOTHING '%s'\n>>%s", spVM().ppi(), ToString.toStringSE(cleanData, 10)));
				L.info(X.f("Send CleanDataResponseException SUCCESS/NOTHING\n>>%s", cleanDataErr.getMessage()));
			}
//			rsp.getWriter().close();
			return;
		}

		if (cleanDataErr.hasContentFile()) {
			if (L.isInfoEnabled()) {
				L.info(X.f("Send CleanDataResponseException SUCCESS/FILE(%s)\n>>%s", cleanDataErr.getContentFile(), cleanDataErr.getMessage()));
			}
			URspContentType.sendResponseContentType_FromFile(rsp, cleanDataErr.getContentFile().key(), cleanDataErr.getContentFile().val().toFile());
			return;
		}

		String cleanData = cleanDataErr.getCleanData();

		if (X.notEmpty(cleanData)) {
			if (L.isInfoEnabled()) {
//				L.info(X.f("build PageSP(CleanDataResponse) SUCCESS \n>>%s", ToString.toStringSE(cleanData, 10)));
				L.info(X.f("Send CleanDataResponseException SUCCESS/CLEAN_DATA(%s)\n>>%s", rsp.getStatus(), ToString.toStringSE(cleanData, 10)));
			}
			//TODO - if false - that response 400 ( when call get-not-existed-event-tree value) - without msg
			//otherwise response has zkoss-js-page appendix
			sendResponse(rsp, cleanDataErr, true);
//			sendResponse(rsp, (IResponseStatusException) cleanDataErr);

			return;
		}

		//L.error(X.f("build PageSP(CleanDataResponse) WARNING, but CleanDataResponse is null  "));
		L.info(X.f("Send CleanDataResponseException (%s) SUCCESS/NULL(%s)\n>>%s", rsp.getStatus(), cleanDataErr.getContentFile(), ToString.toStringSE(cleanData, 10)));
//		UWeb.sendResponse500AndClose(ZKR.getResponse(), "NULL");
		throw new FIllegalStateException(cleanDataErr, "What is send? MayBe use option 'nothing'?");
	}

	public static HttpServletRequest getRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}

	public static HttpServletResponse getResponse() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
	}

	@SneakyThrows
	public static void sendResponseImage_FromRsrc(HttpServletResponse response, String fromRsrcDir, String fromPath) {
		String rsrc = UF.normFile(fromRsrcDir, fromPath);
		InputStream is = UWeb.class.getResourceAsStream(rsrc);
		URspContentType.sendResponseContentType(response, ContentType.IMG_PNG, is);
	}
}
