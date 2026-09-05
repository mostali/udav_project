package zk_pages;

import lombok.SneakyThrows;
import mpc.fs.UFS;
import mpc.fs.UUFS;
import mpc.log.Log2HtmlConverter;
import mpc.str.condition.LogGetterDate;
import mpe.logs.filter.LogProc;
import org.zkoss.zul.Window;
import zk_com.base.Cb;
import zk_form.ext.LogHtmlCom;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Quest;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.ObjState;
import zk_os.coms.AFC;
import zk_os.core.Sdn;
import zk_page.core.PageSP;
import zk_page.core.SpVM;
import zk_page.ZKR;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class LogPSP extends PageSP {

	public static final int LOG_VIEW_MAX_MB = 30;

	public static final Path FILE_LOG = Paths.get("./logs/server.log");

	public LogPSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

	@SneakyThrows
	public void buildPageImpl() {

		LogGetterDate iGetLogDate = LogGetterDate.buildByDefault();
		LogProc lp = new LogProc(iGetLogDate);
		lp.addLineCondition_ByRegex(".*127.*");

		Path fileLog = FILE_LOG;
		try {

			Sdn sdn = Sdn.get();
			ObjState pageState = AppStateFactory.ofState_OrCreate(sdn, AFC.AfcEntity.PAGE, sdn.page());
			Integer maxLogMb = (Integer) pageState.getAs("maxLogMb", Integer.class, LOG_VIEW_MAX_MB);

			Supplier<LogHtmlCom> creator = () -> LogHtmlCom.openWithFileMaxMb(iGetLogDate, fileLog, maxLogMb, true, LogHtmlCom.getBgColorAttr());

			LogHtmlCom logHtml = creator.get();
			window.appendChild(new Cb("Reverse Log", LogHtmlCom.reverse).onCLICK(e -> {
				LogHtmlCom.reverse = ((Cb) e.getTarget()).isChecked();
				logHtml.detach();
				window.appendChild(creator.get());
			}));

			window.appendChild(logHtml);

		} catch (Log2HtmlConverter.MaxHtmlLogException ex) {
			ZKI.alert(ex.getMessage() + " ( use key 'maxLogMb' in page.props for set max size(MB) file log");
			ZKI_Quest.showMessageBoxBlueYN("Truncate log", "Truncate log?", y -> {
				if (y) {
					UUFS.rotateToHistory(fileLog, true);
					UFS.truncate(fileLog);
					ZKR.restartPage();
				}
			});
		}

	}

}
