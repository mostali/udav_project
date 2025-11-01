package zk_notes.control.maintbx;

import mpc.url.UUrl;
import mpc.net.DLD;
import mpf.CallLine;
import mpu.pare.Pare;
import mpu.str.UST;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;

public class UMainTbx {

	public static class DldCallLine extends CallLine {

		public static final String KEY = "!!";
		public final boolean rewrite;
		public final String fn, url;

		public DldCallLine(String line) {
			super(line);
			if (!line.startsWith(KEY)) {
				addError("Except key '%s' from line '%s'", KEY, line);
				rewrite = false;
				fn = null;
				url = null;
				return;
			}

			String url = line.substring(2);
			this.rewrite = url.startsWith("!");
			if (rewrite) {
				url = url.substring(1);
			}
			URL url0 = UST.URL(url, null);
			if (url0 == null) {
				addError("Except url for downloading from cmd '%s'", line);
				fn = null;
				this.url = null;
				return;
			}
			this.url = URLDecoder.decode(url);
			this.fn = UUrl.getPathLastItemWoQuery(this.url);

		}

		public static DldCallLine of(String data) {
			return (DldCallLine) ofQk(data).throwIsErr();
		}

		public static DldCallLine ofQk(String data) {
			return new DldCallLine(data);
		}

		public static boolean isValid(String data) {
			return DldCallLine.ofQk(data).isValid();
		}
	}

	public static void doDownloadToDir(String cmd, Pare<String, String> sdn, Path toDir) throws IOException {
		DldCallLine dldCallLine = DldCallLine.of(cmd);
//		ZKI.alert("Except url after !!");
//		NodeFileTransferMan.AddNewForm.addNewFormAndOpen()
//		if (dldCallLine.rewrite) {
//			DLD.url2file_WithRewriteDst(dldCallLine.url);
//		} else {
		DLD.url2dir(dldCallLine.url, toDir);
//		}

	}
}
