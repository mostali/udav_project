package mpe.restapp;

import lombok.RequiredArgsConstructor;
import mpu.pare.Pare;

import java.util.function.Function;

@RequiredArgsConstructor
public class RestRoute {
	final int serverPort;
	final String path;
	private Function<HttpExchange0, Rsp> handler;

	public static RestRoute of(int serverPort, String path) {
		return new RestRoute(serverPort, path);
	}

	public RestRoute on(Function<HttpExchange0, Rsp> handler) {
		this.handler = handler;
		RestServer.listenContext(serverPort, path, ex -> {
			HttpExchange0 ex0 = HttpExchange0.of(ex);
			Pare<Integer, Object> rsp = this.handler.apply(ex0);
			ex0.onSendResponse(rsp);
		});
		return this;
	}


}
