package udav_net_client;

import java.io.*;
import java.net.*;

// https://www.baeldung.com/java-download-file
// https://github.com/eugenp/tutorials/blob/master/core-java-io/src/main/java/com/baeldung/download/ResumableDownload.java
//DownloadExt
public class DLDExt {

	public static long downloadFileWithResume(String downloadUrl, String saveAsFileName) throws IOException, URISyntaxException {
		File outputFile = new File(saveAsFileName);
		URLConnection downloadFileConnection = addFileResumeFunctionality(downloadUrl, outputFile);
		return transferDataAndGetBytesDownloaded(downloadFileConnection, outputFile);
	}

	private static URLConnection addFileResumeFunctionality(String downloadUrl, File outputFile) throws IOException, URISyntaxException, ProtocolException, ProtocolException {
		long existingFileSize = 0L;
		URLConnection downloadFileConnection = new URI(downloadUrl).toURL().openConnection();
		if (outputFile.exists() && downloadFileConnection instanceof HttpURLConnection) {
			HttpURLConnection httpFileConnection = (HttpURLConnection) downloadFileConnection;

			HttpURLConnection tmpFileConn = (HttpURLConnection) new URI(downloadUrl).toURL().openConnection();
			tmpFileConn.setRequestMethod("HEAD");
			long fileLength = tmpFileConn.getContentLengthLong();
			existingFileSize = outputFile.length();

			if (existingFileSize < fileLength) {
				httpFileConnection.setRequestProperty("Range", "bytes=" + existingFileSize + "-" + fileLength);
			} else {
				throw new IOException("File Download already completed.");
			}
		}
		return downloadFileConnection;
	}

	private static long transferDataAndGetBytesDownloaded(URLConnection downloadFileConnection, File outputFile) throws IOException {
		long bytesDownloaded = 0;
		try (InputStream is = downloadFileConnection.getInputStream(); OutputStream os = new FileOutputStream(outputFile, true)) {
			byte[] buffer = new byte[1024];
			int bytesCount;
			while ((bytesCount = is.read(buffer)) > 0) {
				os.write(buffer, 0, bytesCount);
				bytesDownloaded += bytesCount;
			}
		}
		return bytesDownloaded;
	}

}
