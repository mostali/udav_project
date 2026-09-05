package mpf.repo;

import mpc.net.DLD;

import java.io.IOException;

public interface IFileDownloader {

	default void downloadFile(String fileUrl, String destFile) throws IOException {
		DLD.url2file_withCreateParent(fileUrl, destFile, true);
	}
}
