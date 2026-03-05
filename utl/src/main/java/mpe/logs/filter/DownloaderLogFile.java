package mpe.logs.filter;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpu.IT;
import mpu.core.ARG;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.net.DLD;
import mpu.core.QDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
public abstract class DownloaderLogFile {

	public static final Logger L = LoggerFactory.getLogger(DownloaderLogFile.class);

	final String hostUrlPathDir;

	@SneakyThrows
	public static Path downloadFile(String host, String file, String dstDir, boolean rewriteDstFile) {
		String url = UF.normFile(host, file);
		String name = UF.getPathFileNameWithQuery(url);
		Path source = Paths.get(dstDir);

		Path targetFile = source.resolve(name);
		if (!UFS.isFileWithContent(targetFile)) {
			UFS.MKDIR.mkdirIfNotExist(targetFile.getParent(), 2);
			return DLD.url2file_WithRewriteDst(url, dstDir, rewriteDstFile);
		}
		return DLD.url2file_WithRewriteDst(url, dstDir, rewriteDstFile);
	}

	public Path downloadTo(String dstDir, boolean... rewriteDstFile) {
		return downloadFile(hostUrlPathDir, getFileName(), dstDir, ARG.isDefEqTrue(rewriteDstFile));
	}

	public Path downloadArchiveTo(String dstDir, QDate date, int part, boolean... rewriteDstFile) {
		String archiveFileNameWithDate = getArchiveFileName_WithDate(date, part);
		String filename = UF.normFile(dstDir, archiveFileNameWithDate);
		if (!UFS.isFileWithContent(filename)) {
			boolean isRewriteDstFile = ARG.isDefEqTrue(rewriteDstFile);
			if (isRewriteDstFile) {
				UFS.MKDIR.mkdirsIfNotExist(Paths.get(filename).getParent());
			}
			return downloadFile(hostUrlPathDir, archiveFileNameWithDate, dstDir, isRewriteDstFile);
		}
		L.info("File {} already exist", archiveFileNameWithDate);
		return Paths.get(archiveFileNameWithDate);
	}

	public abstract String getFileName();

	public String getArchiveFileName_WithDate(QDate date, int part) {
		IT.isPosOrZero(part);
		return getFileName() + "." + date.f(QDate.F.YYYY_MM_DD) + "-" + part + "." + "gz";
	}

	public String getArchiveFileName_WithPart(int part) {
		IT.isPosNotZero(part);
		return getFileName() + "." + part + "." + "gz";
	}
}
