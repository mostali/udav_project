package zklogapp.otr.srvloader;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpu.core.ARG;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpu.str.UST;
import mpu.str.USToken;
import mpu.core.QDate;
import mpu.pare.Pare3;
import mpe.logs.filter.DownloaderLogFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import udav_net_client.AHttp;
import udav_net_client.AConOld;
import zklogapp.AppLogProps;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static mpc.fs.UUrl.toUrl;

public class ServerLogDownloaderSrv {
	public static final Logger L = LoggerFactory.getLogger(ServerLogDownloaderSrv.class);

	public static final String PATTERN_FORMAT = "\\.(\\d{4}-\\d{2}-\\d{2}-\\d{1,5})\\.gz";
//	public static final String HOST_AUBU_TEST = "http://fk-eb-au-test-ufos-jetty.otr.ru:8000";

	public static void main(String[] args) throws IOException {
//		U.p("a");
		String dstDir = "/home/dav/pjbf_stands/SAT/0LOGS/";
		downloadLogToDir(Stand.AU_TEST, Logs.SIGN, dstDir, true);
//		downloadLogToDir(Stand.TEST, Logs.DLC, dstDir, true);
//		downloadLogToDir(Stand.TEST, Logs.SERVER, dstDir, true);
//		downloadLogToDir(Stand.TEST, Logs.KAFKA, dstDir, true);

//		Net.url2file("http://fk-eb-au-test-ufos-jetty.otr.ru:8000/server.log.2023-10-27-0.gz", "/tmp", true);
//		url2file("http://fk-eb-au-test-ufos-jetty.otr.ru:8000/print.log", "/tmp", false);
	}

	public static Path downloadLogToDir(Stand stand, Logs logs, String dstDir, boolean rewriteDstFile) {
		Path path = new DownloaderLogFile(stand.urlToRoot8000_LOGS()) {
			@Override
			public String getFileName() {
				return logs.logFileName;
			}
		}.downloadTo(dstDir, rewriteDstFile);
		return path;
	}

	public static List<Path> downloadLogArchiveToDir(Stand stand, Logs logs, String dstDir, boolean rewriteDstFile, QDate date) {
		int part = 0;
		List<Path> paths = new ArrayList<>();
		try {
			do {
				paths.add(downloadLogArchiveToDir(stand, logs, dstDir, rewriteDstFile, date, part++));
			} while (true);
		} catch (Exception ex) {
			//TODO - silent ERROR
			L.warn("Except err:" + ex);
		}
		return paths;
	}

	public static Path downloadLogArchiveToDir(Stand stand, Logs logs, String dstDir, boolean rewriteDstFile, QDate date, int part) {
		Path path = new DownloaderLogFile(stand.urlToRoot8000_LOGS()) {
			@Override
			public String getFileName() {
				return logs.logFileName;
			}
		}.downloadArchiveTo(dstDir, date, part, rewriteDstFile);
		return path;
	}


//	static class ServerLog extends DownloaderFile {
//		public static final String FILENAME = "server.log";
//		public static final String FILENAME_ARCHIVE_ZIP = FILENAME + PATTERN_FORMAT;
//
//		public ServerLog(String hostUrlPathDir) {
//			super(hostUrlPathDir);
//		}
//
//		public static void main(String[] args) {
//			String host = LogDownloader.HOST_AUBU_TEST;
//
//			boolean needArchive = false;
////			needArchive = true;
//			if (!needArchive) {
//				Path path = new ServerLog(host).downloadTo("/home/dav/pjbf_stands/SAT/0LOGS/", true);
//			} else {
//				new ServerLog(host).downloadArchiveTo("/home/dav/pjbf_stands/SAT/0LOGS/", QDate.of(2023, 10, 30), 0);
//				new ServerLog(host).downloadArchiveTo("/home/dav/pjbf_stands/SAT/0LOGS/", QDate.of(2023, 10, 30), 1);
//				new ServerLog(host).downloadArchiveTo("/home/dav/pjbf_stands/SAT/0LOGS/", QDate.of(2023, 10, 30), 2);
//				new ServerLog(host).downloadArchiveTo("/home/dav/pjbf_stands/SAT/0LOGS/", QDate.of(2023, 10, 30), 3);
//			}
//
//		}
//
//		@Override
//		public String getFileName() {
//			return FILENAME;
//		}
//
//	}
//
//	static class DlcLog extends DownloaderFile {
//		public static final String FILENAME = "dlc.log";
//
//		public DlcLog(String hostUrlPathDir) {
//			super(hostUrlPathDir);
//		}
//
//		public static void main(String[] args) {
//			String host = LogDownloader.HOST_AUBU_TEST;
//			boolean needArchive = false;
////			needArchive = true;
//			if (!needArchive) {
//				Path path = new DlcLog(host).downloadTo("/home/dav/pjbf_stands/SAT/0LOGS/", true);
//				Sys.openCode(path);
//
//			} else {
//				new DlcLog(host).downloadArchiveTo("/home/dav/pjbf_stands/SAT/0LOGS/", QDate.of(2023, 10, 30), 0);
//				new DlcLog(host).downloadArchiveTo("/home/dav/pjbf_stands/SAT/0LOGS/", QDate.of(2023, 10, 30), 1);
//				new DlcLog(host).downloadArchiveTo("/home/dav/pjbf_stands/SAT/0LOGS/", QDate.of(2023, 10, 30), 2);
//				new DlcLog(host).downloadArchiveTo("/home/dav/pjbf_stands/SAT/0LOGS/", QDate.of(2023, 10, 30), 3);
//			}
//		}
//
//		@Override
//		public String getFileName() {
//			return FILENAME;
//		}
//
//	}
//
//	static class KafkaLog extends DownloaderFile {
//		public static final String FILENAME = "kafka.log";
//
//		public KafkaLog(String hostUrlPathDir) {
//			super(hostUrlPathDir);
//		}
//
//		public static void main(String[] args) {
//			String host = LogDownloader.HOST_AUBU_TEST;
//			boolean needArchive = false;
////			needArchive = true;
//			if (!needArchive) {
//				new KafkaLog(host).downloadTo("/home/dav/pjbf_stands/SAT/0LOGS/");
//			} else {
//				new KafkaLog(host).downloadArchiveTo("/home/dav/pjbf_stands/SAT/0LOGS/", QDate.of(2023, 10, 30), 0);
//				new KafkaLog(host).downloadArchiveTo("/home/dav/pjbf_stands/SAT/0LOGS/", QDate.of(2023, 10, 30), 1);
//				new KafkaLog(host).downloadArchiveTo("/home/dav/pjbf_stands/SAT/0LOGS/", QDate.of(2023, 10, 30), 2);
//				new KafkaLog(host).downloadArchiveTo("/home/dav/pjbf_stands/SAT/0LOGS/", QDate.of(2023, 10, 30), 3);
//			}
//		}
//
//		@Override
//		public String getFileName() {
//			return FILENAME;
//		}
//	}

	@RequiredArgsConstructor
	public enum Logs {
		SERVER("server.log"), DLC("dlc.log"), HIBERNATE("hibernate.log"), SIGN("advanceSign.log"), KAFKA("kafka.log");
		public final String logFileName;

	}

	@RequiredArgsConstructor
	public enum Stand {
		LOCAL("localhost", 18080, 5006),//
		AU_DEV("fk-eb-au-dev-ufos-jetty.otr.ru", 18060, 5006),//
		AU_TEST("fk-eb-au-test-ufos-jetty.otr.ru", 18080, 5055),//
		AU_TEST2("fk-eb-au-test-ufos-jetty2.otr.ru", 18080, 5055),//
		AU_DEMO("fk-eb-au-demo-ufos-jetty.otr.ru", 18060, 5006),//
		AU_DEMO2("fk-eb-au-demo-ufos-jetty2.otr.ru", 18060, 5006),//

		AU_RLS("fk-eb-au-rls-ufos-jetty.otr.ru", 18080, 5055),//
		AU_RLS2("fk-eb-au-rls-ufos-jetty2.otr.ru", 18080, 5055),//

		//
		//
		PU_DEV70("fk-eb-tse-dev-ufos-jetty.otr.ru", 18070, 5007),//
		PU_DEV80("fk-eb-tse-dev-ufos-jetty.otr.ru", 18080, 5008),//
		PU_TEST("fk-eb-tse-test-ufos.otr.ru", 18080, 5008),//
		//		PU_TEST2("fk-eb-tse-test-ufos.otr.ru", 18080, 5008),//
		PU_DEMO("fk-eb-tse-demo-ufos.otr.ru", 18080, 5008),//
		PU_DEMO2("fk-eb-tse-demo-ufos.otr.ru", 18080, 5008),//
		PU_RLS1("fk-eb-tse-rls-ufos.otr.ru", 18080, 5008),//
		PU_RLS2("fk-eb-tse-rls-ufos2.otr.ru", 18080, 5009),//
		;
		private final String domainName;
		private final int ufosPort, debugPort;

		public String url(int... port) {
			return "http://" + domainName + (port.length == 0 ? "" : ":" + port[0]);
		}

		public String urlToUfos(String... pagepath) {
			return ARG.isDef(pagepath) ? url(port_ufos()) + "/" + UF.normFile(pagepath[0]) : url(port_ufos());
		}

		public String urlToRoot8000_LOGS(String... pagepath) {
			if (isAUBU()) {
				return urlToRoot8000(pagepath);
			} else if (isPUR()) {
				return urlToRoot8000("ufos/" + port_ufos() + (ARG.isDef(pagepath) ? "/" + UF.normFile(pagepath[0]) : ""));
			}
			throw new WhatIsTypeException(this);
		}

		public String urlToRoot8000(String... pagepath) {
			return ARG.isDef(pagepath) ? url(port_8000()) + "/" + UF.normFile(pagepath[0]) : url(port_8000());
		}

		public String urlToConsole(String... pagepath) {
			return ARG.isDef(pagepath) ? url(portLP_console().key()) + "/" + UF.normFile(pagepath[0]) : url(portLP_console().key());
		}

		public String dstDevDir() {
			String last = USToken.last(name(), USToken.ISDIGIT, null);
			return dstDevDir(UST.INT(last, 0));
		}

		public String dstDevDir(int node) {
			return UF.normDir(AppLogProps.APR_TASKS_DIR.getValueOrDefault(), shortDevName(), node + "LOGS");
		}

		public int port_8000() {
			return 8000;
		}

		public int port_debug() {
			return debugPort;
		}

		public int port_ufos() {
			return ufosPort;
		}

		public String projectPage() {
			if (isAUBU()) {
				return "https://confluence.otr.ru/pages/viewpage.action?pageId=191411501";
			} else if (isPUR()) {
				return "https://confluence.otr.ru/pages/viewpage.action?pageId=77212346";
			}
			throw new WhatIsTypeException(this);
		}

		public boolean isAUBU() {
			return name().startsWith("AU_");
		}

		public boolean isPUR() {
			return name().startsWith("PU_");
		}

		@SneakyThrows
		public String call2console(Call2Console call2Console) {
			return call2Console.call2(this);
		}

		public Pare3<Integer, String, String> portLP_console() {
			switch (this) {
				case LOCAL:
					return Pare3.of(18860, "admin", "123");
				case AU_DEV:
					return Pare3.of(18860, "admin", "123");
				case AU_TEST:
				case AU_TEST2:
					return Pare3.of(8888, "admin", "Oracle33");
				case AU_DEMO:
				case AU_DEMO2:
					return Pare3.of(18860, "admin", "adminpwd");
				case AU_RLS:
				case AU_RLS2:
					return Pare3.of(8888, "admin", "Oracle33");

				//
				case PU_DEV70:
					return Pare3.of(8870, "admin", "123");
				case PU_DEV80:
					return Pare3.of(8880, "admin", "123");
				case PU_TEST:
//				case PU_TEST2:
					return Pare3.of(8880, "admin", "Oracle33");
				case PU_DEMO:
				case PU_DEMO2:
					return Pare3.of(8558, "admin", "adminpwd");
				case PU_RLS1:
				case PU_RLS2:
					return Pare3.of(8558, "admin", "adminpwd");

				default:
					throw new WhatIsTypeException(this);
			}
		}


		public String shortDevName() {
			switch (this) {
				case LOCAL:
					return "LOCAL";
				case AU_DEV:
					return "SAJ";
				case AU_TEST:
				case AU_TEST2:
					return "SAT";
				case AU_DEMO:
				case AU_DEMO2:
					return "SAD";
				case AU_RLS:
				case AU_RLS2:
					return "SAR";

				//
				case PU_DEV70:
					return "SKJ7";
				case PU_DEV80:
					return "SKJ8";
				case PU_TEST:
//				case PU_TEST2:
					return "SKT";
				case PU_DEMO:
				case PU_DEMO2:
					return "SKD";
				case PU_RLS1:
				case PU_RLS2:
					return "SKR";

				default:
					throw new WhatIsTypeException(this);
			}
		}
	}

	public enum Call2Console {
		START, STOP, RESTART, TAIL1000;

		@SneakyThrows
		public String call2(Stand stand) {
			Pare3<Integer, String, String> port_lp = stand.portLP_console();
			String resp = AConOld.CALL(AHttp.Method.GET, toUrl(stand.urlToConsole(), urlPath()), new String[]{port_lp.val(), port_lp.ext()});
			return resp;
		}

		public String urlPath() {
			switch (this) {
				case STOP:
					return "stop_ufos?";
				case START:
					return "start_ufos?";
				case RESTART:
					return "restart_ufos?";
				case TAIL1000:
					return "tailog?";
				default:
					throw new WhatIsTypeException(this);
			}
		}
	}

}
