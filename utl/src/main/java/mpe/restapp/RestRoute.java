package mpe.restapp;

import lombok.RequiredArgsConstructor;
import mpc.log.L;
import mpu.core.RW;
import mpu.pare.Pare;

import java.nio.file.Paths;
import java.util.function.Function;

@RequiredArgsConstructor
public class RestRoute {

	final int serverPort;
	final String path;
	private Function<HttpExchange0, Rsp> handler;

	private Function<String, Boolean> funcAuth = null;

	public RestRoute auth(Function<String, Boolean> funcAuth) {
		this.funcAuth = funcAuth;
		return this;
	}

	public static RestRoute of(int serverPort, String path) {
		return new RestRoute(serverPort, path);
	}

	public RestRoute on(Function<HttpExchange0, Rsp> handler) {
		this.handler = handler;
		L.info("Reg [{}] endpoint >>> http://127.0.0.1:{}{}?t={}", (funcAuth != null ? "sec" : "unsafe"), serverPort, path, RestApp.readToken("set token file 't'"));
		RestServer.listenContext(serverPort, path, ex -> {
			try {
				HttpExchange0 ex0 = HttpExchange0.of(ex);
				Pare<Integer, Object> rsp;
				if (funcAuth != null && !funcAuth.apply(ex0.query().getFirstAsStr("t", null))) {
					L.error("Auth is false");
					rsp = Pare.of(404, "Not found");
				} else {
					rsp = this.handler.apply(ex0);
				}
				ex0.onSendResponse(rsp);
			} catch (Exception err) {
				L.error("Handle request '" + ex.getRequestURI() + "' with errors", err);
				throw err;
			}
		});
		return this;
	}


}
