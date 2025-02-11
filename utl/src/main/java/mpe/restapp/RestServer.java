package mpe.restapp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WrongLogicRuntimeException;
import mpc.fs.UF;
import mpc.json.UGson;
import mpc.log.L;
import mpc.map.MAP;
import mpe.core.ERR;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;
import mpu.pare.Pare;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Path;
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
		byte[] rDATA_BYTES = null;
		try {
			if (rsp.val() instanceof CharSequence) {
				L.info("Rsp has STRING body*{}", X.sizeOf((CharSequence) rsp.val()));
				rDATA = rsp.val().toString();
			} else if (rsp.val() instanceof Throwable) {
				Throwable val = (Throwable) rsp.val();
				L.info("Rsp has EXCEPTION body", val);
				rDATA = ERR.getStackTrace(val);
			} else if (rsp.val() instanceof Path) {
				Path fileData = (Path) rsp.val();
				rDATA_BYTES = RW.readBytes(IT.isFileExist(fileData));
				//https://stackoverflow.com/questions/1741353/how-to-set-response-filename-without-forcing-save-as-dialog
				exchange.getResponseHeaders().add("Content-Disposition", "inline");
				exchange.getResponseHeaders().add("filename", UF.fn(fileData));
				L.info("Rsp has PATH body bytes from file {} size*{}", UF.fn(fileData), rDATA_BYTES.length);
			} else {
				L.info("Rsp has OBJECT body (as JSON > {})", rsp.val().getClass().getSimpleName());
				rDATA = UGson.toStringPrettyFromObject(rsp.val());
			}
		} finally {
			byte[] bytes;
			if (rDATA_BYTES != null) {
				bytes = rDATA_BYTES;
			} else if (rDATA == null) {
				rDATA = "err500";
				bytes = rDATA.getBytes();
				L.error("rDATA is null*" + rSTATUS);
				rSTATUS = 500;
			} else {
				bytes = rDATA.getBytes();
			}
			try {


				exchange.sendResponseHeaders(rSTATUS, bytes.length);
				//Content-Disposition: inline; filename="myfile.txt"
				OutputStream output = exchange.getResponseBody();
				output.write(bytes);
				output.flush();
			} finally {
				exchange.close();
			}

		}

	}

	public static HttpServer getHttpServer(Integer port, HttpServer... defRq) {
		Object o = MAP.get(ALL, port, defRq);
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
