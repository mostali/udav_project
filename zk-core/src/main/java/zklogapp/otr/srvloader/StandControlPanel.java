package zklogapp.otr.srvloader;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpu.IT;
import mpu.Sys;
import mpu.X;
import mpe.core.P;
import mpe.core.UErr;
import mpc.exception.FIllegalArgumentException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS;
import mpc.fs.fd.FILE;
import mpc.net.INetRsp;
import mpc.net.DLD;
import mpc.net.JHttp;
import mpu.str.STR;
import mpu.core.QDate;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.util.Clients;
import zk_com.base.Cb;
import zk_com.base.Dtx;
import zk_com.base.Lb;
import zk_com.base.Ln;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Log;
import zk_form.notify.ZKI_Modal;
import zk_page.ZKR;
import zklogapp.ALI;
import zklogapp.AppLogProps;
import zklogapp.logview.LogFileView;
import zklogapp.merge.LogDirView;

import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RequiredArgsConstructor
public class StandControlPanel extends Div0 {

	final ServerLogDownloaderSrv.Stand STAND;

//	Stopwatch timer;

	private void onStartDwnload(ServerLogDownloaderSrv.Logs logType) {


		ZKR.activePushCom(StandControlPanel.this.getDesktop());

		try {
			boolean checked = cbActiveDownloadArchive.isChecked();
			Object logFilePath;
			String dstDir = dstLocalLogDir();
			if (checked) {
				Date choicedDate = choicedDate();
				logFilePath = ServerLogDownloaderSrv.downloadLogArchiveToDir(STAND, logType, dstDir, true, QDate.of(choicedDate).add(Calendar.DAY_OF_MONTH, 1));
			} else {

				Path existedLog = Paths.get(dstDir, logType.logFileName);

				boolean rewrite = false;

				long nextUpdateSec = getNextUpdate(existedLog);

				if (!UFS.existFile(existedLog)) {
					rewrite = true;
				} else {
					if (nextUpdateSec <= 0) {
						rewrite = true;
					} else if (!UFS.existFile(existedLog)) {
						rewrite = true;
					} else {
						rewrite = false;
					}
				}

				if (rewrite) {
					ZKI_Log.log("FRESH MODE " + existedLog);
					try {
						logFilePath = ServerLogDownloaderSrv.downloadLogToDir(STAND, logType, dstDir, true);
					} catch (Exception ex) {
						if (ex instanceof FileNotFoundException | ex instanceof UnknownHostException) {
							ZKI_Log.alert(ex, "downloadLogToDir:" + UErr.getMessageWithType(ex));
							return;
						}
						X.throwException(ex);
						return;
					}
				} else {
					ZKI_Log.log("SKIP, next update at " + nextUpdateSec + "sec. Log:" + existedLog);
					logFilePath = existedLog;
				}

				Path logFilePath0 = (Path) logFilePath;
				boolean openCode = cbActiveOpenCode.isChecked();
				if (openCode) {
					Sys.open_Code(logFilePath0);
				}
				if (logFilePath instanceof Path) {
					LogFileView.openSingly(logFilePath.toString());
				}
			}
			String msg = "Downloaded : " + logFilePath;
			L.info(msg);

			Clients.log(msg);

		} catch (Throwable ex) {
			L.error("Unexcept error, when downloading", ex);
		} finally {
			ZKR.deactivePushCom(StandControlPanel.this.getDesktop());
		}


	}

	private Cb cbActiveDownloadArchive;
	private Cb cbActiveOpenCode;
	private Dtx dateArchive;

	private Date choicedDate() {
		return dateArchive.getValue();
	}

	public enum LEH {
		LINK_REDIRECT, LINK_VALUE, SHOW_VALUE, SHOW_EDITOR, LINK_OPEN_BROWSER, POST_FORMDATA;

		@SneakyThrows
		void apply_post_formdata(String url, String formdata) {
			IT.state(this == POST_FORMDATA, "except LEH#POST_FORMDATA");
			INetRsp rsp = JHttp.POST_FORM(url, JHttp.HEADERS(JHttp.HCT_FORM_URLENCODED, JHttp.HACCEPT_ANY_XML), formdata, null, 200);
			if (rsp.isErrorStatus()) {
				ZKI_Log.alert(rsp.msgWithError());
			} else {
				ZKI.infoSingleLine("Ok");
			}
		}

		@SneakyThrows
		void apply(String value) {
			switch (this) {
				case SHOW_EDITOR:
					ZKI.infoEditorBw(value);
					break;
				case SHOW_VALUE:
					ZKI.infoMultiLine(value);
					break;
				case LINK_VALUE:
					ZKI.infoMultiLine(DLD.url2val(value));
					break;
				case LINK_REDIRECT:
					ZKR.redirectToPage(value, true);
					break;
				case LINK_OPEN_BROWSER:
					Sys.open_Browser(value);
					break;
				case POST_FORMDATA:
					throw new FIllegalArgumentException("Use method 'apply_post_formdata()'");
				default:
					throw new WhatIsTypeException(this);
			}
		}
	}

	@Override
	protected void init() {
		super.init();

		Lb standLogoItem = new Lb(standName());
		appendChild(standLogoItem);

		Menupopup0 mainMenu = Menupopup0.createMenupopup(this, standLogoItem, Events.ON_MOUSE_OVER);

		String dstLogDir = dstLocalLogDir();

		mainMenu.addMenuitem(ALI.DIRVIEW + "View Dir", (SerializableEventListener) event -> getFirstWindow().appendChild(new LogDirView(dstLogDir)));
		mainMenu.addSeparator();
		mainMenu.addMenuitem(ALI.OS_OPEN + "View Dir", (SerializableEventListener) event -> Sys.open_Nautilus(dstLogDir));
		mainMenu.addSeparator();

		Menupopup0 linksMenu = mainMenu.addInnerMenu(ALI.LINK + "Links");
		{
			linksMenu.addMenuitem(ALI.LINK + STAND.url().substring(7), event -> LEH.LINK_REDIRECT.apply(STAND.urlToUfos()));
			linksMenu.addSeparator();
			linksMenu.addMenuitem(ALI.LINK + "logback.xml", (SerializableEventListener) event -> LEH.LINK_REDIRECT.apply(STAND.urlToRoot8000_LOGS("etc/logback.xml")));
			linksMenu.addMenuitem(ALI.LINK + ":8000/logs", (SerializableEventListener) event -> LEH.LINK_REDIRECT.apply(STAND.urlToRoot8000()));
			linksMenu.addMenuitem(ALI.LINK + ":8000/etc", (SerializableEventListener) event -> LEH.LINK_REDIRECT.apply(STAND.urlToRoot8000("etc")));
			linksMenu.addMenuitem(ALI.LINK + "/sufdversion", (SerializableEventListener) event -> LEH.LINK_VALUE.apply(STAND.urlToUfos("sufdversion")));
			linksMenu.addMenuitem(ALI.LINK + "/static/resources/func.version", (SerializableEventListener) event -> LEH.LINK_VALUE.apply(STAND.urlToUfos("static/resources/func.version")));

			linksMenu.addSeparator();
		}

		{
			fillInnerMenu_Console(linksMenu.addInnerMenu("Console"));
		}
		{
			fillInnerMenu_Jmx(linksMenu.addInnerMenu("JMX"));
		}

		linksMenu.addSeparator();

		{
			linksMenu.addMenuitem(ALI.LINK + "Confluence Project Page", event -> LEH.LINK_REDIRECT.apply(STAND.projectPage()));
			linksMenu.addMenuitem(ALI.LINK + "DEBUG PORT:" + STAND.port_debug(), event -> X.nothing());
		}

		ZKR.activePush();

		this.cbActiveOpenCode = new Cb();
		this.cbActiveDownloadArchive = new Cb();

		cbActiveOpenCode.title("Open in Code");
		cbActiveOpenCode.defaultActionLog("Open in Code");

		this.dateArchive = new Dtx(new Date());

		for (ServerLogDownloaderSrv.Logs logType : ServerLogDownloaderSrv.Logs.values()) {

			Ln link = new Ln(logType.name());

			link.onCLICK((SerializableEventListener<Event>) event -> {

				String url2log = STAND.urlToRoot8000();
				ZKI_Log.log("Start downloading '%s' from '%s' to '%s'", logType.logFileName, STR.substrStartEnd(url2log, 7, 12, url2log), STAND.dstDevDir());

				new Thread(() -> {

					onStartDwnload(logType);

				}).start();

			});
			link.setSTYLE("padding:5px;font-size:12px;font-weight:bold");
			appendChild(link);

		}


		appendChild(cbActiveOpenCode);

		dateArchive.setVisible(false);

		cbActiveDownloadArchive.title("download archive by date");
		cbActiveDownloadArchive.addEventListener(Events.ON_CLICK, (SerializableEventListener<Event>) event -> dateArchive.setVisible(cbActiveDownloadArchive.isChecked()));
		appendChild(cbActiveDownloadArchive);
		appendChild(dateArchive);


	}

	private void fillInnerMenu_Jmx(Menupopup0 jmxMenu) {
		jmxMenu.addMenuitem(ALI.LINK + "Open Page JMX", (SerializableEventListener) event -> LEH.LINK_REDIRECT.apply(STAND.urlToUfos("jmx")));
		jmxMenu.addSeparator();
//		http://fk-eb-au-dev-ufos-jetty.otr.ru:18060/jmx/servers/0/domains/org.hibernate/mbeans/name%3Dstatistics/operations/clear%28%29?skin=embedded&ok=1
		jmxMenu.addMenuitem(ALI.LINK + "Clear - org.hibernate - Clear", (SerializableEventListener) event -> LEH.POST_FORMDATA.apply_post_formdata(STAND.urlToUfos("/jmx/servers/0/domains/org.hibernate/mbeans/name=statistics/operations/clear()?ok=1"), "executed=true"));
		jmxMenu.addMenuitem(ALI.LINK + "Clear - Cache - Reset All", (SerializableEventListener) event -> LEH.POST_FORMDATA.apply_post_formdata(STAND.urlToUfos("/jmx/servers/0/domains/sufd/mbeans/type=core,name=cache,dir=manager,group=OTHER,nm=OtherCachesCleaner/operations/resetAll()?ok=1"), "executed=true"));
	}

	@SneakyThrows
	public static void main(String[] args) {
		//		String url = "http://fk-eb-au-dev-ufos-jetty.otr.ru:18060/jmx/servers/0/domains/sufd/mbeans/type%3Dcore%2Cname%3Dcache%2Cdir%3Dmanager%2Cgroup%3DOTHER%2Cnm%3DOtherCachesCleaner/operations/resetAll()?skin=embedded";
		String url = "http://fk-eb-au-dev-ufos-jetty.otr.ru:18060/jmx/servers/0/domains/sufd/mbeans/type%3Dcore%2Cname%3Dcache%2Cdir%3Dmanager%2Cgroup%3DOTHER%2Cnm%3DOtherCachesCleaner/operations1/resetAll()?skin=embedded";
//		String url = "http://fk-eb-au-dev-ufos-jetty.otr.ru:18060/jmx/servers/0/domains/sufd/mbeans/type%3Dcore%2Cname%3Dcache%2Cdir%3Dmanager%2Cgroup%3DOTHER%2Cnm%3DOtherCachesCleaner/operations/resetAll1()?skin=embedded";
//		Object rsp = POST_FORM(url, HEADERS(HEADER_FORM_URLENCODED, HEADER_ACCEPT_ANY_XML), "executed=true", String.class, 200);
		INetRsp rsp = JHttp.POST_FORM(url, JHttp.HEADERS(JHttp.HCT_FORM_URLENCODED, JHttp.HACCEPT_ANY_XML), "executed=true", null, 200);
		P.exit(IT.state(rsp != null));
//
//		String resetAll = "http://fk-eb-tse-demo-ufos:18080/jmx/servers/0/domains/sufd/mbeans/type=core,name=cache,dir=manager,group=OTHER,nm=OtherCachesCleaner/operations/resetAll()?ok=1";
//		String val = Net.url2httpval("http://fk-eb-au-dev-ufos-jetty.otr.ru:18060/jmx/servers/0/domains/org.hibernate/mbeans/name%3Dstatistics/operations/cle1ar%28%29?skin=embedded&ok=1", new ValueOutStream<String>() {
//			@Override
//			public String getValue(boolean... fresh) {
//				String val = super.getValue(fresh);
//				if (val.contains("$operation.name")) {
//
//				}
//				if (val.contains("<span id=\"fade\" style=\"color:green\">Done</span>")) {
//					return "Done";
//				} else {
//					return val;
//				}
//			}
//		}, 2);
//		P.exit(val);
	}

	private void fillInnerMenu_Console(Menupopup0 consoleMenu) {

		consoleMenu.addMenuitem(ALI.LINK + "Open console", (SerializableEventListener) event -> LEH.LINK_REDIRECT.apply(STAND.urlToConsole()));

		Arrays.stream(ServerLogDownloaderSrv.Call2Console.values()).forEach(i -> consoleMenu.addMenuitem(ALI.LINK + i.name(), (SerializableEventListener) event -> {
			if (i == ServerLogDownloaderSrv.Call2Console.TAIL1000) {
				LEH.SHOW_EDITOR.apply(STAND.call2console(i));
			} else {
				ZKI_Modal.showMessageBoxBlueYN("Console action '" + i + "'", "Confirm action '" + i + "'", (Boolean rslt) -> {
					if (rslt) {
						LEH.SHOW_EDITOR.apply(STAND.call2console(i));
					}
					return null;
				});
			}
		}));
	}

	private String standName() {
		return STAND.name();
	}

	private String dstLocalLogDir() {
		return STAND.dstDevDir();
	}

	private long getNextUpdate(Path existedLog) {
		Integer val = AppLogProps.APR_LOG_CACHE_SEC.getValueOrDefault();
		long secondsAgoModify = FILE.getSecondsAgoModified(existedLog);
		long nextUpdateSec = val - secondsAgoModify;
		return nextUpdateSec;
	}

}
