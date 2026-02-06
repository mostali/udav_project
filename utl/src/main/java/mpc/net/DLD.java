package mpc.net;

import lombok.SneakyThrows;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.url.UUrl;
import mpc.fs.ext.EXT;
import mpc.fs.ext.GEXT;
import mpc.num.UNum;
import mpc.rfl.RFL;
import mpe.img.ImageHash;
import mpe.img.UImg;
import mpe.rt.ValueOutStream;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.str.RANDOM;
import mpu.str.UST;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static mpc.fs.UF.getExtFromCleanUrl;

//Downloader
public class DLD {


	public static final Logger L = LoggerFactory.getLogger(DLD.class);

	/**
	 * @param urlBlankFiles   -> [url1:file1, url2:file2]
	 * @param createParentDir
	 */
	@SneakyThrows
	public static void url2file(Map<String, String> urlBlankFiles, boolean createParentDir) {
		Map<String, String> writed = new LinkedHashMap();
		for (Map.Entry<String, String> url2file : urlBlankFiles.entrySet()) {
			try {
				url2file_withCreateParent(url2file.getKey(), url2file.getValue(), createParentDir);
			} catch (Throwable t) {
				writed.entrySet().forEach(f -> UFS.RM.fileQk(f.getValue()));
				throw t;
			}
			writed.put(url2file.getKey(), url2file.getValue());
		}
	}

	public static Path url2file_WithRewriteDst(String url, String targetDir, boolean rewriteDst) throws IOException {
		targetDir = UF.normDir(targetDir);
		if (L.isInfoEnabled()) {
			L.info("Start url2filehex [{}] to dir [{}]", url, targetDir);
		}
		String name = UF.getPathFileNameWithQuery(url);
//		String name = UUrl.getExtFromUrlPath().getPathFileName(url);
		String ext = EXT.getExtFromFilename(name);
		String randFileName = RANDOM.alpha(15) + "." + ext;
		String fullRandFile = targetDir + randFileName;

		Path pathFile = Paths.get(targetDir).resolve(name);

		boolean existsDstFile = Files.exists(pathFile);
		if (existsDstFile && !rewriteDst) {
			throw new FileNotFoundException("File already exist's:" + pathFile);
		}

		DLD.url2file0(url, fullRandFile);

		if (existsDstFile) {
			Files.deleteIfExists(pathFile);
		}

		Files.move(Paths.get(fullRandFile), pathFile);

		if (L.isDebugEnabled()) {
			L.debug("File '{}' downloaded from '{}'", pathFile, url);
		}

		return pathFile;
	}

	@SneakyThrows
	public static void url2file_withCreateParent(String url, String file, boolean createParentDir) {
		if (createParentDir) {
			Files.createDirectories(Paths.get(file).getParent());
		}
		url2file0(url, file);
	}

	public static Path url2dir(String url, Path dir) throws IOException {
		IT.isDirExist(dir);
		String fn = IT.NE(UUrl.getPathLastItemWoQuery(url));
		Path targetFile = dir.resolve(fn);
		url2file0(new URL(url), targetFile.toString());
		return targetFile;
	}

	public static void url2file0(String url, String file) throws IOException {
		url2file0(new URL(url), file);
	}

	public static void url2file0(URL url, String file, String[]... headers) throws IOException {
		IT.notNull(url, "Url is null");
		if (L.isDebugEnabled()) {
			L.debug("Start url2file [{}] to file [{}]", url, file);
		}
		ReadableByteChannel rbc = null;
		FileOutputStream fos = null;
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			if (X.notEmpty(headers)) {
				for (String[] header : headers) {
					conn.setRequestProperty(IT.notEmpty(header[0]), IT.notEmpty(header[1]));
				}
			}

//		conn.setInstanceFollowRedirects( true);
			//	conn.setConnectTimeout(2000);
			InputStream is = conn.getInputStream();
			rbc = Channels.newChannel(is);
			fos = new FileOutputStream(file);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		} finally {
			IOUtils.closeQuietly(rbc);
			IOUtils.closeQuietly(fos);
		}
	}

	public static void url2out(String url, OutputStream out) throws IOException {
		url2out(UST.URL(url), out);
	}

	public static String url2val(String url) throws IOException {
		return url2val(url, new ValueOutStream<>());
	}

	public static <T> T url2val(String url, ValueOutStream<T> out) throws IOException {
		url2out(UST.URL(url), out);
		return out.getValue();
	}

	public static void url2out(URL url, OutputStream out) throws IOException {
		IT.notNull(url, "Url is null");
		if (L.isTraceEnabled()) {
			if (out instanceof FileOutputStream) {
				FileOutputStream out1 = (FileOutputStream) out;
				L.trace("Start url2file [{}] -> [{}]", url, RFL.fieldValue(out1, "path", true));
			} else {
				L.trace("Start url2out [{}] -> [{}]", url, RFL.scn(out));
			}
		}
		URLConnection conn = url.openConnection();
		//	conn.setConnectTimeout(2000);
		InputStream is = conn.getInputStream();
		IOUtils.copy(is, out);
		IOUtils.closeQuietly(is, out);
	}

	public static <T> T url2httpval(String url, ValueOutStream<T> out, int... exceptStatus) throws IOException {
		url2httpout(UST.URL(url), out, exceptStatus);
		return out.getValue();
	}

	public static <T> void url2httpout(URL url, ValueOutStream out, int... exceptStatus) throws IOException {
		IT.notNull(url, "Url is null");
		if (L.isTraceEnabled()) {
			L.trace("Start url2httpout [{}] -> [{}]", url, RFL.scn(out));
		}
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.connect();
		int status = conn.getResponseCode();
		if (exceptStatus.length > 0) {
			boolean isSuccess = UNum.isSeries(status, exceptStatus);
			IT.state(isSuccess, "except success, but cam %s", status);
		}
		//	conn.setConnectTimeout(2000);
		InputStream is = conn.getInputStream();
		IOUtils.copy(is, out);
		IOUtils.closeQuietly(is, out);
	}

	public static <T> void url2httpout_POST(URL url, Map<String, String> payload, ValueOutStream out, int... exceptStatus) throws IOException {
		IT.notNull(url, "Url is null");
		if (L.isTraceEnabled()) {
			L.trace("Start url2httpout [{}] -> [{}]", url, RFL.scn(out));
		}
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");

		conn.setDoOutput(true);

		String form = "client_id=mdm-api-service&client_secret=password&realm=/customer&grant_type=urn:roox:params:oauth:grant-type:m2m&service=dispatcher";
		String[][] headers = {{"Accept", "application/json"}, {"Content-Type", "application/x-www-form-urlencoded"}};

		conn.connect();
		int status = conn.getResponseCode();
		if (exceptStatus.length > 0) {
			boolean isSuccess = UNum.isSeries(status, exceptStatus);
			IT.state(isSuccess, "except success, but cam %s", status);
		}
		//	conn.setConnectTimeout(2000);
		InputStream is = conn.getInputStream();
		IOUtils.copy(is, out);
		IOUtils.closeQuietly(is, out);
	}

	/**
	 * @param urls            (url0,url1)
	 * @param gext
	 * @param pfxPathFilename - parent/child
	 * @param defRq
	 * @return [ url0:parent/child.EXT , url1:parent/child_1.EXT, ]
	 */

	public static Map<String, String> prepareMapBlankFilesForUrls(List<String> urls, GEXT gext, String pfxPathFilename, Map<String, String>... defRq) {
		int ctr = 0;
		Map<String, String> map = new LinkedHashMap();
		boolean isCleanNumName = UF.isDir(pfxPathFilename);
		String pfxFn = isCleanNumName ? "" : "_";
		for (String photoUrl : urls) {
			String extFromUrlPath = UUrl.getExtFromUrlPath(photoUrl, null);
			EXT ext = EXT.ofExt(extFromUrlPath);
			if (!gext.has(ext)) {
				if (L.isErrorEnabled()) {
					L.error("PhotoUrl '{}' has unknown type '{}' of Image", photoUrl, ext);
				}
				continue;
			}
			String pathFile = pfxPathFilename;
//			if (ctr++ > 0) {
			pathFile += pfxFn + ctr++;
//			}
			pathFile += "." + extFromUrlPath;
			map.put(photoUrl, pathFile);
		}
		if (!map.isEmpty()) {
			return map;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Data is empty, for gext '%s', urls '%s'", gext, urls), defRq);
	}

	public static String url2fileimagehex(String url, String targetDir) throws IOException {
		if (L.isDebugEnabled()) {
			L.debug("url2fileimagehex, url '{}', targetDir '{}'", url, targetDir);
		}
		try {
			String ext = getExtFromCleanUrl(url);
			if (Objects.equals(ext.toLowerCase(), "gif")) {
				String name = UImg.getNameFromUrl(url);
				url2file0(url, name);
				return null;
			}
			return url2filehex(url, targetDir);
		} catch (Exception ex) {
			if (L.isWarnEnabled()) {
				L.warn("Try alt https-url");
			}
			if (ex.getMessage().startsWith("image == null!") && url.startsWith("http") && !url.startsWith("https")) {
				return url2filehex("https" + url.substring(4), targetDir);
			}
			throw ex;
		}
	}

	private static String url2filehex(String url, String targetDir) throws IOException {
		targetDir = UF.normDir(targetDir);
		if (L.isTraceEnabled()) {
			L.trace("Start url2filehex [{}] to dir [{}]", url, targetDir);
		}
		String ext = getExtFromCleanUrl(url);
		String randFile = RANDOM.alpha(8) + "." + ext;
		String fullRandFile = targetDir + randFile;
		url2file0(url, fullRandFile);
		String hexName = normalizeFileImage2HexName(fullRandFile);
		return hexName;
	}

	public static String normalizeFileImage2HexName(String image) throws IOException {
		String hex = new ImageHash(image).getHashString();
		String fullHexFile = Paths.get(image).getParent() + "/" + hex + "." + getExtFromCleanUrl(image);
		if (Files.exists(Paths.get(fullHexFile)) && !UF.equalsFileName(fullHexFile, image)) {
			Files.deleteIfExists(Paths.get(image));
		} else {
			Files.move(Paths.get(image), Paths.get(fullHexFile));
		}
		return fullHexFile;
	}

	@SneakyThrows
	public static void url2file_MJ18(String url, Path file) {
		// Убедимся, что родительская директория существует
		Files.createDirectories(file.getParent());

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url.trim())) // убираем возможные пробелы в конце
				.timeout(java.time.Duration.ofMinutes(2)).build();

		try {
			HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(file));
			if (response.statusCode() != 200) {
				throw new IOException("HTTP error code: " + response.statusCode());
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Download was interrupted", e);
		}
	}
}
