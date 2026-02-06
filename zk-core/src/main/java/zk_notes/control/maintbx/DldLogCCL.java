package zk_notes.control.maintbx;

import lombok.SneakyThrows;
import mpc.net.DLD;
import mpc.url.UUrl;
import mpf.CallCmdLine;
import mpu.pare.Pare;
import mpu.str.UST;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;

public class DldLogCCL extends CallCmdLine {

	public static final String KEY = "L!!";
//	public final boolean rewrite;
	public final String fn, url;

	public DldLogCCL(String line) {
		super(line);

		if (!line.startsWith(KEY)) {
			addError("Except key '%s' from line '%s'", KEY, line);
//			rewrite = true;
			fn = null;
			url = null;
			return;
		}

		String url = line.substring(KEY.length());
//		this.rewrite = url.startsWith("!");
//		if (rewrite) {
//			url = url.substring(1);
//		}

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

	public static DldLogCCL of(String data) {
		return (DldLogCCL) ofQk(data).throwIsErr();
	}

	public static DldLogCCL ofQk(String data) {
		return new DldLogCCL(data);
	}

	public static boolean isValid(String data) {
		return DldLogCCL.ofQk(data).isValid();
	}

	@SneakyThrows
	public static Path doDownloadToDir(String cmd, Pare<String, String> sdn, Path toDir) {
		DldLogCCL dldCallLine = of(cmd);
//		ZKI.alert("Except url after !!");
//		NodeFileTransferMan.AddNewForm.addNewFormAndOpen()
//		if (dldCallLine.rewrite) {
//			DLD.url2file_WithRewriteDst(dldCallLine.url);
//		} else {
		return DLD.url2dir(dldCallLine.url, toDir);
//		}

	}
}
