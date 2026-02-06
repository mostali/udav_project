package mpe.ftp;

import lombok.SneakyThrows;
import mpc.fs.UFS;
import mpc.fs.fd.IFd;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

//https://commons.apache.org/proper/commons-net/examples/ftp/FTPClientExample.java
//https://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/ftp/FTPClientConfig.html
public class FtpClientApi implements Closeable {

	private final String server;
	private final int port;
	private final String user;
	private final String password;
	private FTPClient ftp;

	private boolean useTimeZoneUtc = false;

	public FtpClientApi useTimeZoneUtc(boolean... useTimeZoneUtc) {
		this.useTimeZoneUtc = ARG.isDefNotEqFalse(useTimeZoneUtc);
		return this;
	}

	public FtpClientApi(String user, String password, String server, int port) {
		this.server = server;
		this.port = port;
		this.user = user;
		this.password = password;
	}

	public void open() throws IOException {

		IT.isNull(ftp, "already opened");

		ftp = new FTPClient();

		FTPClientConfig conf = new FTPClientConfig();
		if (useTimeZoneUtc) {
			conf.setServerTimeZoneId("UTC");
		}
		ftp.configure(conf);

		ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

		ftp.connect(server, port);
		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
			throw new IOException("Exception in connecting to FTP Server");
		}

		ftp.login(user, password);
	}

	@Override
	public void close() throws IOException {
		ftp.disconnect();
		X.p("Closed FTP Client API");
	}

	public List<IFd> ls(String path) throws IOException {
		FTPFile[] files = ftp.listFiles(path);
		return Arrays.stream(files).map(ff -> ff.isDirectory() ? new FtpDir(path, ff) : new FtpFile(path, ff)).collect(Collectors.toList());
	}

	public Collection<String> lsNames(String path) throws IOException {
		FTPFile[] files = ftp.listFiles(path);
		return Arrays.stream(files).map(FTPFile::getName).collect(Collectors.toList());
	}

	public List<FTPFile> ls0(String path) throws IOException {
		return Arrays.stream(ftp.listFiles(path)).collect(Collectors.toList());
	}

	public void putFileToPath(File file, String path) throws IOException {
		ftp.storeFile(path, new FileInputStream(file));
	}

	@SneakyThrows
	public void deleteFile(String path) {
//		try {
		ftp.deleteFile(path);
//		L.info(X.fl("FtpFile deleted delete file '{}'", path), ex);
//		} catch (IOException ex) {
//			L.error(X.fl("Error delete file '{}'", path), ex);
//
//		}
	}

	@SneakyThrows
	public Path downloadFileToDir(FtpFile srcFile, Path dstParentDir, boolean... mkdirsOrSingleDirOrCheck) {
		UFS.MKDIR.createDirsOrSingleDirOrCheckExist(dstParentDir, mkdirsOrSingleDirOrCheck);
		Path dstFile = dstParentDir.resolve(srcFile.fName());
		try (FileOutputStream out = new FileOutputStream(dstFile.toFile())) {
			ftp.retrieveFile(srcFile.toPath().toString(), out);
		}
		return dstFile;

	}

	@SneakyThrows
	public Path downloadFileToFile(FtpFile srcFile, Path dstFile, boolean... mkdirsOrSingleDirOrCheck) {
		UFS.MKDIR.createDirsOrSingleDirOrCheckExist(dstFile.getParent(), mkdirsOrSingleDirOrCheck);
		try (FileOutputStream out = new FileOutputStream(dstFile.toFile())) {
			ftp.retrieveFile(srcFile.toPath().toString(), out);
		}
		return dstFile;
	}
}
