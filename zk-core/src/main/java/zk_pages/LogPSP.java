package zk_pages;

import lombok.SneakyThrows;
import mpc.str.condition.LogGetterDate;
import mpe.logs.filter.LogProc;
import org.zkoss.zul.Window;
import mpc.fs.query.QueryUrl;
import zk_form.ext.LogHtmlCom;
import zk_page.core.PageSP;
import zk_page.core.SpVM;
import zk_page.ZKR;

import java.nio.file.Paths;

/**
 * @author dav 07.01.2022   19:24
 */
public class LogPSP extends PageSP {

	public LogPSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

	@SneakyThrows
	public void buildPageImpl() {

//				TextboxFile tbf = new TextboxFile(Paths.get("./logs/server.log"));
//				tbf.setEnableWrite(false);
//				tbf.setWidth("100%");

		QueryUrl queryUrl = ZKR.getRequestQuery();
		LogGetterDate iGetLogDate = LogGetterDate.buildByDefault();
		LogProc lp = new LogProc(iGetLogDate);
		lp.addLineCondition_ByRegex(".*127.*");
//		if (X.notEmpty(queryUrl.getMap())) {
//			Map<String, Object> modelView = new HashMap<>();
//			throw new URest.VelocityContentResponseException("rest/log/index-logs-react.vtl.html", modelView);
//		}

		LogHtmlCom logHtml = LogHtmlCom.fromFileMaxMb(iGetLogDate, Paths.get("./logs/server.log"), 12, true, LogHtmlCom.getBgColorAttr());

		window.appendChild(logHtml);

	}

}
