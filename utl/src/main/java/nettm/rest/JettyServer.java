//package nettm.rest;
//
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import lombok.SneakyThrows;
//import mpc.rfl.RFL;
//import mpu.Sys;
//import mpu.X;
//import mpu.core.ARG;
//import org.eclipse.jetty.server.Request;
//import org.eclipse.jetty.server.Server;
//import org.eclipse.jetty.server.handler.AbstractHandler;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.concurrent.atomic.AtomicReference;
//
//public class JettyServer {
//
//	// TODO
//	public static void main(String[] args) throws Exception {
//		runServer(8085);
//	}
//
//
//	public static final Logger L = LoggerFactory.getLogger(JettyServer.class);
//
////	private static final JettyServer I = new JettyServer();
////
////	public static JettyServer get() {
////		return I;
////	}
//
//	@RequiredArgsConstructor
//	public static class TcWorker implements ISingleRequestWorker {
//		public static final Logger L = LoggerFactory.getLogger(TcWorker.class);
//
//		@SneakyThrows
//		@Override
//		public void handle_request() {
//			Sys.say("ra");
//		}
//
//	}
//
//	@SneakyThrows
//	public JettyServer(int port, boolean... daemon) {
//		Server server = new Server(port);
//		server.setHandler(newRootWorker());
//		server.start();
//		L.info("Start {} Server success", getClass().getSimpleName());
//		if (ARG.isDefNotEqTrue(daemon)) {
//			server.join();
//		}
//	}
//
//	protected TcJettyRootWorker newRootWorker() {
//		return new TcJettyRootWorker(TcWorker.class);
//	}
//
//	public static void runServer(int port) throws Exception {
//		new JettyServer(port);
//	}
//
//	public static class TcJettyRootWorker extends AbstractHandler {
//
//		public static final Logger L = LoggerFactory.getLogger(TcJettyRootWorker.class);
//
//		final Class<? extends ISingleRequestWorker> workerClass;
//
//		public TcJettyRootWorker(Class<? extends ISingleRequestWorker> workerClass) {
//			this.workerClass = workerClass;
//		}
//
//		@Override
//		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
//			try {
//				handleImpl(target, baseRequest, request, response);
//			} catch (Throwable t) {
//				L.error("Unhandled WTF Throwable", t);
//			}
//		}
//
//		public void handleImpl(String target, Request _jettyRequest, HttpServletRequest request, HttpServletResponse response) {
//
//			//		if (L.isDebugEnabled()) {
//			//			L.debug(">>> >>> >>> [{}] : [{}] : [{}]", target, _jettyRequest.getInputState(), _jettyRequest.getOriginalURI());
//			//		}
//
//			TcServerSingleWorker singleHandler = newSingleWorker(target, _jettyRequest, request, response);
//
//
//			try {
//				singleHandler.init_request();
//			} catch (Throwable ex) {
//				L.error("Unhandled INIT Throwable", ex);
//				return;
//			}
//
//			onHandle(singleHandler);
//
//			try {
//				singleHandler.destroy_request();
//			} catch (Throwable ex) {
//				L.error("Unhandled DESTROY Throwable", ex);
//			}
//
//		}
//
//		protected TcServerSingleWorker newSingleWorker(String target, Request _jettyRequest, HttpServletRequest request, HttpServletResponse response) {
//			return new TcServerSingleWorker(workerClass, target, _jettyRequest, request, response);
//		}
//
//		protected void onHandle(TcServerSingleWorker singleHandler) {
//			singleHandler.handle_request_as_long_oper_async();
//		}
//
//	}
//
//	public static class TcServerSingleWorker {
//
//		public static final Logger L = LoggerFactory.getLogger(TcServerSingleWorker.class);
//
//		final String _jettyTarget;
//		final Request _jettyRequest;
//		final HttpServletRequest _request;
//		final HttpServletResponse _response;
//		//
//		//
////		private JsonObject requestJson;
////		protected String _projectName;
//
//		final long request_start_ms;
//
//		final Class<? extends ISingleRequestWorker> workerClass;
//
//		public TcServerSingleWorker(Class<? extends ISingleRequestWorker> workerClass, String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
//			this.workerClass = workerClass;
//			this._jettyTarget = target;
//			this._jettyRequest = baseRequest;
//			this._request = request;
//			this._response = response;
//
//			this.request_start_ms = System.currentTimeMillis();
//			//
//
//		}
//
//		@Getter
//		private boolean isSended = false;
//		private final AtomicReference result = new AtomicReference<>();
//
//		public void setResult(Object err) {
//			result.set(err);
//		}
//
//		//	public void sendResponseOk() throws IOException {
//		//		sendResponse("");
//		//	}
//
//		//	public void sendResponse(String body) throws IOException {
//		//		UVkBot.sendResponseOk(_jettyRequest, _response, body);
//		//		isSended = true;
//		//	}
//
//		public TcServerSingleWorker init_request() throws IOException {
//			//		CheckerDDOS.checkDDOS(_jettyTarget, _jettyRequest);
//			//		try {
//			//			initRequestEnv();
//			//		} catch (Throwable t) {
//			//			if (L.isErrorEnabled()) {
//			//				L.error("init request env", t);
//			//			}
//			//			throw t;
//			//		}
//
//			return this;
//		}
//
//		public TcServerSingleWorker destroy_request() {
//			Object rslt = result.get();
//			if (rslt instanceof Throwable) {
//				//			if (L.isErrorEnabled()) {
//				//				CharSequence extInfo = "";
//				//				String msg = X.fl("{} {} / {}{}", SYMJ.ARROW_REPEAT_LEFT_TH, _jettyTarget, EventJson.toStringSimpleLog(eventJson), extInfo, rslt);
//				//				L.error(msg, (Throwable) rslt);
//				//			}
//				L.error("RESULT ERROR", (Throwable) rslt);
//			} else {
//				L.info("RESULT OK:" + rslt);
//
//				//			if (L.isDebugEnabled()) {
//				//				CharSequence extInfo = "";
//				//				String msg = X.fl("{} {} / {}{} >>> {}", SYMJ.ARROW_REPEAT_LEFT_TH, _jettyTarget, EventJson.toStringSimpleLog(eventJson), extInfo, rslt == null ? "@END@" : rslt);
//				//				L.debug(msg);
//				//			}
//			}
//			return this;
//		}
//
//
//		private ISingleRequestWorker worker;
//
//		public ISingleRequestWorker worker() {
//			//		return worker == null ? worker = RFL.inst(workerClass, EventJson.class, eventJson) : worker;
//			return worker == null ? worker = RFL.instEmptyConstructor(workerClass) : worker;
//		}
//
//		@SneakyThrows
//		public void handle_request_as_long_oper_async() {
//			new Thread(() -> {
//				ISingleRequestWorker worker = worker();
//				try {
//					worker.handle_request();
//				} catch (Throwable e) {
//					String msg = X.fl("Worker '{}' handle request with error", worker.getClass());
//					L.error(msg, e);
//				}
//			}).start();
//		}
//
//
//	}
//
//	public interface ISingleRequestWorker {
//		void handle_request();
//	}
//}
