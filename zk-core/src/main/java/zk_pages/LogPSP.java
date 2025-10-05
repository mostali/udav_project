package zk_pages;

import lombok.SneakyThrows;
import mpc.fs.UFS;
import mpc.log.Log2HtmlConverter;
import mpc.str.condition.LogGetterDate;
import mpe.logs.filter.LogProc;
import org.zkoss.zul.Window;
import mpc.net.query.QueryUrl;
import zk_com.base.Cb;
import zk_form.ext.LogHtmlCom;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Quest;
import zk_page.core.PageSP;
import zk_page.core.SpVM;
import zk_page.ZKR;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

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

		Path fileLog = Paths.get("./logs/server.log");
		try {

			Supplier<LogHtmlCom> creator = () -> {
				return LogHtmlCom.fromFileMaxMb(iGetLogDate, fileLog, 12, true, LogHtmlCom.getBgColorAttr());
			};
			LogHtmlCom logHtml = creator.get();
			window.appendChild(new Cb("Reverse Log", LogHtmlCom.reverse).onCLICK(e -> {
				LogHtmlCom.reverse = ((Cb) e.getTarget()).isChecked();
//				ZKR.restartPage();
				logHtml.detach();
				window.appendChild(creator.get());
			}));

			window.appendChild(logHtml);

		} catch (Log2HtmlConverter.MaxHtmlLogException ex) {
			ZKI.alert(ex.getMessage());
			ZKI_Quest.showMessageBoxBlueYN("Truncate log", "Truncate log?", y -> {
				if (y) {
					UFS.truncate(fileLog);
					ZKR.restartPage();
				}
			});
		}

	}

}
