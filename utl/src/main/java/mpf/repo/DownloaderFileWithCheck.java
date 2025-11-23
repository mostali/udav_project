package mpf.repo;

import mpc.fs.UFS;
import org.apache.commons.io.FileUtils;
import mpz_deprecated.EER;
import mpe.UMD5;
import mpc.fs.UF;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class DownloaderFileWithCheck extends DownloaderFile {

	public final DownloaderFile downloaderFileMD5;

	public DownloaderFileWithCheck(String fileUrl$, String fileDest$, boolean isTempDest, String fileUrlMD5$, String fileDestMD5$, boolean isTempDestMD5) throws IOException {
		super(fileUrl$, fileDest$, isTempDest);
		downloaderFileMD5 = DownloaderFile.of(fileUrlMD5$, fileDestMD5$);
		downloaderFileMD5.isTempDest = isTempDestMD5;
	}

	public static DownloaderFileWithCheck of(String fileUrl, String fileDest, String fileUrlMD5, String fileDestMD5) throws IOException {
		boolean isTempDest = fileDest == null;
		boolean isTempDestMD5 = fileDestMD5 == null;
		String sfx = UF.fnFromUrl(fileUrl);
		String sfxMD5 = UF.fnFromUrl(fileUrlMD5);

		fileDest = isTempDest ? File.createTempFile("temp-df-", sfx).getAbsolutePath() : fileDest;
		fileDestMD5 = isTempDestMD5 ? File.createTempFile("temp-df-md5-", sfxMD5).getAbsolutePath() : fileDestMD5;
		return new DownloaderFileWithCheck(fileUrl, fileDest, isTempDest, fileUrlMD5, fileDestMD5, isTempDestMD5);
	}


	public DownloaderFile moveFileMD5ToTargetFile(String targetFile) throws IOException {
		if (downloaderFileMD5.applyDestExistBehaviourAndIsSkip(targetFile)) {
			return this;
		}
		FileUtils.moveFile(new File(downloaderFileMD5.getFileDest()), new File(targetFile));
		downloaderFileMD5.fileDest = targetFile;
		return this;
	}

	@Override
	public String[] getFilesDest() {
		return new String[]{super.getFileDest(), this.downloaderFileMD5.getFileDest()};
	}


	@Override
	public DownloaderFileWithCheck download() {
		String fileLib = super.getFileDest();
		String fileMD5 = this.downloaderFileMD5.getFileDest();
		try {
			DownloaderFile main = super.download();
			DownloaderFile md5 = this.downloaderFileMD5.download();
			if (!UMD5.checkFileWithFileMD5(fileLib, fileMD5)) {
				throw new UMD5.CheckMD5Exception(String.format("Error check MD5 for file '%s'", fileLib));
			}
		} catch (UMD5.CheckMD5Exception | IOException | NoSuchAlgorithmException ex) {
			UFS.RM.fdQk(fileLib, fileMD5);
			throw EER.IS(ex);
		}
		return this;
	}
}
