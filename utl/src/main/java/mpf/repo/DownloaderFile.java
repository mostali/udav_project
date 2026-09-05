package mpf.repo;

import mpc.fs.UFS;
import mpz_deprecated.simple_task.EBDestFileExist;
import mpz_deprecated.simple_task.IFileMover;
import org.apache.commons.io.FileUtils;
import mpe.core.U;
import mpc.fs.UF;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DownloaderFile implements IFileMover, IFileDownloader {

	private final String fileUrl;
	protected String fileDest;
	protected boolean isTempDest;
	//
	protected String ebDestFileExist = EBDestFileExist.SKIP.name();


	public DownloaderFile(String fileUrl$, String fileDest$, boolean isTempDest) {
		fileUrl = fileUrl$;
		fileDest = fileDest$;
		this.isTempDest = isTempDest;
	}

	public static DownloaderFile of(String fileUrl$, String... fileDest$) throws IOException {
		boolean isTempDest = fileDest$ == null || fileDest$.length == 0;
		String sfx = UF.fnFromUrl(fileUrl$);
		String fileDest = isTempDest ? File.createTempFile("temp-df-", sfx).getAbsolutePath() : fileDest$[0];
		return new DownloaderFile(fileUrl$, fileDest, isTempDest);
	}

	public Path toDownloadedPath() {
		return Paths.get(fileDest);
	}

	public String getFileDest() {
		return fileDest;
	}

	public String[] getFilesDest() {
		return new String[]{this.getFileDest()};
	}

	public void deleteAll() {
		UFS.RM.fdQk(getFilesDest());
	}

	public boolean applyDestExistBehaviourAndIsSkip(String targetFile) throws IOException {
		return EBDestFileExist.valueOf(ebDestFileExist).applyBehaviourIfDestExist_IS_SKIP(targetFile);
	}

	public DownloaderFile download() throws IOException {
		if (!isTempDest && applyDestExistBehaviourAndIsSkip(fileDest)) {
			return this;
		}
		L("Download Core from remote repository :: " + fileUrl);
		L("Downloading to temporary file :: " + fileDest);
		this.downloadFile(fileUrl, fileDest);
		return this;
	}

	public DownloaderFile moveFileToTargetFile(String targetFile) throws IOException {
		if (applyDestExistBehaviourAndIsSkip(targetFile)) {
			return this;
		}
		Lf("Move [%s] >>> [%s]", fileDest, targetFile);
		FileUtils.moveFile(new File(fileDest), new File(targetFile));

		this.fileDest = targetFile;
		return this;
	}

	public static void Lf(String message, Object... args) {
		L(String.format(message, args));
	}

	public static void L(String message, Object... args) {
		U.L.info(message, args);
	}
}
