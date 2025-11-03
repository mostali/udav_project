package mpe.restapp;

import com.sun.net.httpserver.HttpExchange;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.fs.UF;
import mpc.url.UUrl;
import mpc.fs.path.UPath;
import mpc.net.query.QueryUrl;
import mpe.core.ERR;
import mpu.core.ARG;
import mpu.pare.Pare;

import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
public class HttpExchange0 {
	public final HttpExchange exchange;

	private String uri = null;
	private Path path = null;
	private String firstItem = null;
	private String lastItem = null;
	private QueryUrl query = null;

	//
	//
	//

	public String uri() {
		return uri != null ? uri : (uri = exchange.getRequestURI().toString());
	}

	public Path path() {
		return path != null ? path : (path = Paths.get(UUrl.getUrlWoQuery(uri())));
	}

	public String firstName(Integer... item) {
		Path path0 = path();
		if (ARG.isDef(item)) {
			return UF.item(path0, item[0]).toString();
		}
		return firstItem != null ? firstItem : (firstItem = (path0.getNameCount() <= 0 ? "" : UPath.item(path0, 0).toString()));
	}

	public String lastName() {
		Path path0 = path();
		return lastItem != null ? lastItem : (lastItem = (path0.getNameCount() <= 0 ? "" : UPath.item(path0, path0.getNameCount() - 1).toString()));
	}

	public QueryUrl query() {
		return query != null ? query : (query = QueryUrl.ofUrl(uri()));
	}

	public static HttpExchange0 of(HttpExchange httpExchange) {
		return new HttpExchange0(httpExchange);
	}

	public String toStringSimple() {
		HttpExchange0 ex0 = this;
		return ex0 + ":" + ex0.path() + ":" + ex0.lastName() + ":" + ex0.query();
	}

	@SneakyThrows
	public void onSendResponse(Pare<Integer, Object> rsp) {
		RestServer.onSendResponse(exchange, rsp);
	}

	@SneakyThrows
	public void onSend(Pare<Integer, Object> rsp) {
		RestServer.onSendResponse(exchange, rsp);
	}

	@SneakyThrows
	public void onSend(Throwable ex) {
		onSend(Pare.of(500, ERR.getStackTrace(ex)));
	}
}
