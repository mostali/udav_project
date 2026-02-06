package zklogapp.logview;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import mpc.str.condition.LogGetterDate;
import mpc.time.QDT;
import mpe.core.ERR;
import mpe.logs.filter.LogProc;
import mpu.IT;
import mpu.Sys;
import mpu.X;
import mpc.log.Log2HtmlConverter;
import mpc.ui.ColorTheme;
import mpu.core.QDate;
import mpv.byteunit.ByteUnit;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Bt;
import zk_form.ext.LogHtmlCom;
import zk_com.base_ctr.Div0;
import zk_com.core.IReRender;
import zk_form.events.DefAction;
import zk_form.notify.ZKI;
import zk_notes.ANI;
import zk_os.AppZos;
import zk_page.ZKC;
import zk_page.ZKR;
import zk_page.ZKS;
import zklogapp.filter.HeadLogFilter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class LogFileView extends Div0 implements IReRender {

	final String path_org;

	@Setter
	private int maxSizeMb = 20;

	public static LogFileView removeMeFirst(LogFileView... defRq) {
		return ZKC.removeMeFirst(LogFileView.class, true, defRq);
	}

	@Override
	public Component newCom() {
		return new LogFileView(IT.NN(newPath, "set new path before build new com"));
	}

	@Getter
	private HeadLogFilter headLogFilter;

	@Setter
	private String newPath;

	public static void openSingly(String pathFile) {
		openSingly(pathFile, null);
	}

	public static void openSingly(String pathFile, String filter) {
		IT.isFileExist(pathFile);
		LogFileView.removeMeFirst(null);
		ZKC.getFirstWindow().appendChild(new LogFileView(pathFile).withFilter(filter));
	}

	Long filterMs = null;

	private LogFileView withFilter(String filter) {
		filterMs = QDT.toLong(filter, TimeUnit.MILLISECONDS, null);
		return this;
	}

	@Override
	protected void init() {
		super.init();

		Bt btHeadOk = (Bt) new Bt("Apply").onDefaultAction(event -> redrawLogView());
		btHeadOk.onCLICK((SerializableEventListener) event -> {
			redrawLogView();
		});
		appendChild(headLogFilter = new HeadLogFilter(btHeadOk, path_org) {
			@Override
			protected void onChangeNewPath(String newPath) {
				LogFileView parent = (LogFileView) getParent();
				parent.setNewPath(newPath);
				parent.rerender();
			}

			@Override
			public void onHeadOk() {
				redrawLogView();
			}
		});

		headLogFilter.getCbOne().title("Sens Filter's ( render content every click on filter )");

		redrawLogView();

	}

	private void redrawLogView() {
		LogHtmlCom logHtml = LogHtmlCom.removeMeFirst(null);
		try {
			Path file = Paths.get(path_org);
			long sizeMb = X.sizeOf(file, ByteUnit.MB);
			if (sizeMb > maxSizeMb) {
				Div0 warn = Div0.of();
				ZKS.CENTER(warn);
				ZKS.BORDER_GRAY(warn);
				ZKS.PADDING(warn, "20px 0px");
				ZKS.BGCOLOR(warn, ColorTheme.WHITE[0]);

				warn.appendLb(ANI.WARN + " File too large (%sMb).", sizeMb).block();
				warn.appendLn((DefAction) (event) -> Sys.open_Code(file), ANI.OS_OPEN + " Open in Code").block();
				warn.appendLn((DefAction) (event) -> ZKR.download(file), ANI.DOWNLOAD + " Download").block();
				appendChild(warn);
				return;
			}
			ArrayList<String> processedlines = applyFilterPreocessed();

			LogHtmlCom logHtmlCom = LogHtmlCom.openWithLinesDefault(processedlines);

			appendChild(logHtmlCom);

		} catch (Log2HtmlConverter.MaxHtmlLogException ex) {
			ZKI.alert(ex, "File '" + path_org + "' too big");
		} catch (Exception ex) {
			ZKI.alert(ex, ERR.UNHANDLED_ERROR);
		}

	}

	@SneakyThrows
	private ArrayList<String> applyFilterPreocessed() {
		boolean explodeMultiline = headLogFilter.getCbCollapseMultiLine().isChecked();
		ArrayList<String> processedlines;
		LogGetterDate logGetterDate = AppZos.getLogGetterDate();
		if (filterMs != null) {
			IT.isNotZero(filterMs);
			QDate[] startEnd = QDate.nowNewPeriod(filterMs);
			LogProc byBetweenDate = LogProc.createByBetweenDate(logGetterDate, startEnd[0], startEnd[1]);
			processedlines = byBetweenDate.processFile(Paths.get(path_org), logGetterDate, explodeMultiline);
		} else {
			processedlines = headLogFilter.processFile(logGetterDate, path_org, explodeMultiline);
		}
		return processedlines;
	}

}
