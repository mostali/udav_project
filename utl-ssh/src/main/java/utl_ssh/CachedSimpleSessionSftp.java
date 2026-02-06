package utl_ssh;

import mpc.fs.FileLines;

import java.io.IOException;
import java.nio.file.Paths;

public class CachedSimpleSessionSftp extends SimpleSessionSftp {

	private FileLines fileCache;

	public CachedSimpleSessionSftp(String fileCache, String host, int port, String user, String pass) {
		super(host, port, user, pass);
		this.fileCache = new FileLines(Paths.get(fileCache));
	}

	public FileLines getFileCache() {
		if (!fileCache.isExist()) {
			try {
				fileCache.createFile();
				if (SimpleSessionSftp.L.isInfoEnabled()) {
					SimpleSessionSftp.L.info("FileLineCache:CREATE:" + this.fileCache);
				}
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
		return fileCache;
	}

	@Override
	protected void afterUpload(String srcFile, String destParentDir) {
		try {
			getFileCache().appendLine_(srcFile);
			if (SimpleSessionSftp.L.isInfoEnabled()) {
				SimpleSessionSftp.L.info("FileLineCache:APPEND:" + getFileCache() + ":" + srcFile + ":" + destParentDir);
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	protected boolean beforeUpload(String srcFile, String destParentDir) {
		try {
			boolean res = getFileCache().containLine_(srcFile);
			if (SimpleSessionSftp.L.isInfoEnabled()) {
				SimpleSessionSftp.L.info("FileLineCache:EXIST:" + res + ":" + getFileCache() + ":" + srcFile + ":" + destParentDir);
			}
			return !res;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
