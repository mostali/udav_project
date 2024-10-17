package mpe.restapp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WrongLogicRuntimeException;
import mpc.json.UGson;
import mpc.log.L;
import mpc.map.UMap;
import mpe.core.ERR;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public abstract class RestServer {

	public static Map<Integer, Object> ALL = new HashMap<>();

	public static void onSendResponse(HttpExchange exchange, Pare<Integer, Object> rsp) throws IOException {

		Rsp.STATE state = Rsp.STATE.ofRsp(rsp);
		switch (state) {
			case EMPTY:
				L.warn("Rsp has EMPTY state:" + rsp);
				return;
			case ASYNC:
				L.warn("Rsp has ASYNC state:" + rsp);
				return;
			default:
			case UNDEFINED:
				L.warn("Rsp has UNDEFINED state:" + rsp);
				return;
			case INFO:
			case OK:
			case RDR:
			case CLI:
			case SRV:
		}


		Integer rSTATUS = rsp.key();
		String rDATA = null;
		try {
			if (rsp.val() instanceof CharSequence) {
				L.warn("Rsp has STRING body*{}", X.sizeOf((CharSequence) rsp.val()));
				rDATA = rsp.val().toString();
			} else if (rsp.val() instanceof Throwable) {
				Throwable val = (Throwable) rsp.val();
				L.warn("Rsp has EXCEPTION body", val);
				rDATA = ERR.getStackTrace(val);
			} else {
				L.warn("Rsp has OBJECT body (as JSON > {})", rsp.val().getClass().getSimpleName());
				rDATA = UGson.toStringPrettyFromObject(rsp.val());
			}
		} finally {
			if (rDATA == null) {
				rDATA = "err500";
				L.error("rDATA is null*" + rSTATUS);
				rSTATUS = 500;
			}
			exchange.sendResponseHeaders(rSTATUS, rDATA.getBytes().length);
			OutputStream output = exchange.getResponseBody();
			output.write(rDATA.getBytes());
			output.flush();
			exchange.close();
		}

	}

	public static HttpServer getHttpServer(Integer port, HttpServer... defRq) {
		Object o = UMap.get(ALL, port, defRq);
		if (o instanceof HttpServer) {
			return (HttpServer) o;
		} else if (o instanceof Throwable) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException((Throwable) o, "Init path error on port '%s'", port), defRq);
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new WrongLogicRuntimeException("wth:" + port);
	}

//		public static HttpServer listenContext(Integer port, String path, Function<HttpExchange0, Pare<Integer, String>> response) {
//			return listenContext(port, path, ex -> {
//				new AbsRq(ex) {
//					@Override
//					public Pare<Integer, String> response() {
//						return response.apply(HttpExchange0.of(ex));
//					}
//				}.apply();
//
//			});
//		}

	public static HttpServer listenContext(Integer port, String path, HttpHandler handler) {

		HttpServer httpServer = getHttpServer(port, null);

		if (httpServer == null) {
			try {
				httpServer = HttpServer.create(new InetSocketAddress(port), 0);
				httpServer.setExecutor(null); // creates a default executor
				httpServer.createContext(path, handler);
				httpServer.start();
				ALL.put(port, httpServer);
				return httpServer;
			} catch (Exception ex) {
				L.error("createServerContext", ex);
				ALL.put(port, ex);
				X.throwException(ex);
			}
		} else {
			httpServer.createContext(path, handler);
		}


		return httpServer;
	}
}
