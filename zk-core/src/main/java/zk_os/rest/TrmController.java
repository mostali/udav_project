package zk_os.rest;

import com.google.gson.JsonObject;
import mpu.IT;
import mpc.env.boot.BootRunUtils;
import mpc.exception.NI;
import mpc.fs.UFS;
import mpc.log.Log2HtmlConverter;
import mpc.map.WhatIs;
import mpu.str.Sb;
import mpc.str.condition.LogGetterDate;
import mpt.IaUser;
import mpt.TRM;
import mpt.TrmRq;
import mpt.TrmRsp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import utl_jack.UJack;
import utl_rest.SrcResponseEntity;
import utl_rest.URest;
import utl_rest.core.RestCall;
import utl_web.URsp;
import utl_web.UWeb;
import zk_os.trm.AppProperties;

import java.nio.file.Paths;

@RestController
@Profile(value = {"default", "local", "dev-local", "dev"})
public class TrmController implements TrmApi {
	private static final Logger L = LoggerFactory.getLogger(TrmController.class);

//	private final ObjectMapper objectMapper;

//	private final HttpServletRequest request;

	private final AppProperties appProps;

	//	@Autowired
//	private AppTrm appTrm;
//	@org.springframework.beans.factory.annotation.Autowired
//	public TrmController(ObjectMapper objectMapper, HttpServletRequest request) {
//		this.objectMapper = objectMapper;
//		this.request = request;
//	}
	@Autowired
	public TrmController(AppProperties appProps) {
//		this.objectMapper = objectMapper;
//		this.request = request;
		this.appProps = appProps;
	}

	//http://q.com:8080/trm?q=53311979-80d8-4c0d-8a8c-e14238f6057e

//	@Override
////	public ResponseEntity gen(GenEntity requestBosy) {
//	public ResponseEntity gen(String requestBody) {
//		GenEntity.generateFromCompositeString(requestBody);
//		return ResponseEntity.ok("ok");
//	}

	@Override
	public ResponseEntity ping() throws Throwable {
		return SrcResponseEntity.OK("ping-ok");
	}

	@Override
	public ResponseEntity exe(String q) throws Throwable {
		return new TrmRestCall(q).rq(URsp.getRequest(), false).doCall();
	}

	private class TrmRestCall extends RestCall {
		private final String q;

		public TrmRestCall(String q) {
			super("trm", TrmController.L);
			this.q = q;
		}

		@Override
		public ResponseEntity callImpl() throws Exception {
			String cmd0 = UWeb.getQueryStringTyped(URsp.getRequest()).getFirst("q", WhatIs.NB);
			String cmd = IT.NE(q);
			IT.isEq(cmd0, cmd);

			if (L.isInfoEnabled()) {
				L.info("Handle TRM query '{}'", cmd);
			}
			TrmRq cmdRq = TrmRq.fromWeb(cmd);
			String key = cmdRq.cmd7().key();
			switch (key) {
				case "logs":
					String file = "/logs/server.log";
					if (!UFS.isFileWithContent(file)) {
						file = "." + file;
					}
					if (!UFS.isFileWithContent(file)) {
						return SrcResponseEntity.OK("Logs not found");
					}
					String message = Log2HtmlConverter.fromFile(Paths.get(file), -1000, false,2, LogGetterDate.buildByDefault());
					return SrcResponseEntity.OK(message);
				case "v":
					return SrcResponseEntity.OK(BootRunUtils.getVersionFromBuildInfo());
				case "help":
				case "man":
					throw NI.stop(key);
					//					return URest.getResponse_OK(AppDss.readRsrcHtml("trm-help.html"));
			}
			TrmRsp rsp = TRM.executeCmd(IaUser.def(), cmdRq);
			rsp.throwIsNoOk();
			String val = cmdRq.cmd7().val();
			if (val == null) {
				return SrcResponseEntity.C400("Command '%s' undefined", cmd);
			}
			switch (val) {
				case "td":
				case "fd":
				case "hd":
					return URest.getResponse_DOWNLOAD(rsp);
			}
			if (rsp.hasResult() && rsp.getResult() instanceof JsonObject) {
				return URest.getResponse_OK(UJack.toStringPretty(rsp.getResult()));
			}
			String msg = rsp.statusWithCodeWithMessage();
			if (rsp.isOk() && rsp.hasResult()) {
				String rslt = rsp.getResult().toString();
				if (rslt.indexOf('\n') >= 0) {
					return URest.getResponse_OK(rslt);
				}
			}
			Sb html = new Sb();
			html.append(msg);
			if (rsp.hasResult()) {
				html.append("<hr/>");
				html.append(rsp.getResult());
				html.append("<hr/>");
			}
			return URest.getResponse_OK(html);
		}
	}

}
