package utl_rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mpu.core.ARG;
import mpc.exception.NI;
import mpc.fs.UFS;
import mpc.json.UGson;
import mpc.net.CON;
import mpt.TrmRsp;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.velocity.VelocityContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import mpc.exception.EmptyRuntimeException;
import mpc.exception.RequiredRuntimeException;
import org.springframework.web.server.ResponseStatusException;
import utl_jack.MapJacksonModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Map;

public class URest {

	public static final Logger L = LoggerFactory.getLogger(URest.class);

	/**
	 * *************************************************************
	 * ---------------------------- Response JSON & STATUS --------------------------
	 * *************************************************************
	 */
	public static ResponseStatusException getResponseExceptionStatus_DEFJSON(HttpStatus httpStatus, String cause) {
		return new ResponseStatusException(httpStatus, DEFJSON(httpStatus, cause).toJson());
	}

	public static MapJacksonModel DEFJSON(HttpStatus status, String cause) {
		return MapJacksonModel.of("code", status.value(), "cause", cause);
	}

	/**
	 * *************************************************************
	 * ---------------------------- Response ERROR STATUS --------------------------
	 * *************************************************************
	 */
	public static ResponseStatusException getResponseExceptionStatus_ERROR(String msg) {
		return getResponseExceptionStatus_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, msg);
	}


	public static ResponseStatusException getResponseExceptionStatus_ERROR(Throwable err, String msg) {
		return getResponseExceptionStatus_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, err, msg);
	}

	public static ResponseStatusException getResponseExceptionStatus_ERROR(HttpStatus httpStatus, String msg) {
		return new ResponseStatusException(httpStatus, msg);
	}

	public static ResponseStatusException getResponseExceptionStatus_ERROR(HttpStatus httpStatus, Throwable err, String msg) {
		return new ResponseStatusException(httpStatus, msg, err);
	}

	/**
	 * *************************************************************
	 * ---------------------------- Response ERROR --------------------------
	 * *************************************************************
	 */

	public static ResponseEntity<?> getResponse_OK_JSON_CUSTOM(Object json) throws JsonProcessingException {
		return getResponseByJsonCustom(json, HttpStatus.OK);
	}

	public static ResponseEntity<String> getResponseByJsonCustom(Object resultMap, HttpStatus httpStatus) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return new ResponseEntity<>(objectMapper.writeValueAsString(resultMap), getHeaders_JsonCustom(), httpStatus);
	}

	public static Path storeMultipartFileToDir(MultipartFile uploadFile, Path rootLocation) throws IOException {
		if (uploadFile == null || uploadFile.isEmpty()) {
			throw new EmptyRuntimeException("Failed to store empty file.");
		}
		Path destinationFile = rootLocation.resolve(uploadFile.getOriginalFilename()).normalize().toAbsolutePath();
		//		if (!destinationFile.getParent().equals(rootLocation.toAbsolutePath())) {
		//			throw new CleanMessageExtRuntimeException("Cannot store file '%s' outside app directory '%s'", uploadFile.getOriginalFilename(), destinationFile)
		//					.setCleanCause("Cannot store file outside app directory.");
		//		}
		try (InputStream inputStream = uploadFile.getInputStream()) {
			Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
		}
		return destinationFile;
	}

	public static RequestMap getRequestMap() {
		return RequestMap.of(getRequest());
	}

	@NotNull
	public static String decodeRestChars(String str, boolean decodeSpace, boolean decodeRSlash) {
		if (decodeSpace) {
			str = str.replace("___", " ");
		}
		if (decodeRSlash) {
			str = str.replace(":::", "/");
		}
		return str;
	}

	public static ResponseEntity getResponse_DOWNLOAD(TrmRsp rsp) {
		try {
			File file = UFS.convert(rsp.getResult(), File.class, false);
			return getResponse_DOWNLOAD(file);
		} catch (Exception ex) {
			return SrcResponseEntity.C500(ex, "Error get file from trm-response");
		}
	}

	public static ResponseEntity getResponse_DOWNLOAD(File file) {
		if (file == null || !file.exists()) {
			return SrcResponseEntity.C400("File '%s' not exist", file);
		}
		LinkedMultiValueMap<String, String> headersMap = new LinkedMultiValueMap<>();
		headersMap.add(com.google.common.net.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");
		try {
			return new ResponseEntity<byte[]>(IOUtils.toByteArray(new FileInputStream(file)), headersMap, HttpStatus.OK);
		} catch (IOException e) {
			return SrcResponseEntity.C500(e, "Error IO with file '%s'", file);
		}
	}

	public static CON.Method getRequestMethod() {
		return CON.Method.valueOf(getRequest().getMethod());
	}

	public static HttpServletRequest getRequest(HttpServletRequest... defRq) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		if (request != null) {
			return request;
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException();
	}

	public static ResponseEntity<?> getResponse_OK_VELOCITY(String relativePathOfResourceFreemakerTemplate, Map model) {
		String tpl = UVelocity.toPackageFileStringPattern(relativePathOfResourceFreemakerTemplate, new VelocityContext(model));
		return URest.getResponse_OK(tpl);

	}

	//  Used by Freemaker Templates
	//	public static ResponseEntity<?> getResponse_OK_FREEMAKER(String relativePathOfResourceFreemakerTemplate, Map model) {
	//		try {
	//			Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
	//			cfg.setClassForTemplateLoading(UWeb.class, "/");
	//			Template temp = cfg.getTemplate(relativePathOfResourceFreemakerTemplate);
	//			ByteArrayOutputStream os = new ByteArrayOutputStream();
	//			Writer out = new OutputStreamWriter(os);//System.out
	//			temp.process(model, out);
	//			String strDefaultCharset = IOUtils.toString(os.toByteArray(), Charset.defaultCharset().name());
	//			return UWeb.getResponse_OK_RU(strDefaultCharset);
	//
	//		} catch (Exception ex) {
	//			return getResponse_STATUS(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	//		}
	//	}

	public static ResponseEntity<?> getResponse_OK_RU(CharSequence message) {
		return getResponse_OK(message, "IBM866");
	}

	public static ResponseEntity<?> getResponse_BODY(CharSequence body) {
		return getResponse_OK("<!doctype html><html><head></head><body>" + body + "</body></html>");
	}

	public static ResponseEntity<?> getResponse_OK(CharSequence message) {
		return getResponse_OK(message, StandardCharsets.UTF_8.name());
	}

	public static ResponseEntity<?> getResponse_OK(CharSequence message, String encoding) {
		try {
			byte[] response = IOUtils.toByteArray(IOUtils.toInputStream(message, encoding));
			return getResponse_OK(response);
		} catch (IOException e) {
			return SrcResponseEntity.C500(e, e.getMessage());
		}
	}

	public static ResponseEntity<?> getResponse_OK(byte[] response) {
		ByteArrayResource resource = new ByteArrayResource(response);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentLength(response.length);
		return getResponse_OK(resource, httpHeaders);
	}

	public static ResponseEntity<ByteArrayResource> getResponse_OK(ByteArrayResource response, HttpHeaders headers) {
		return new ResponseEntity(response, headers, HttpStatus.OK);
	}

	public static void throwStatus404() {
		throwStatus(HttpStatus.NOT_FOUND, "entity not found");
	}

	public static RuntimeException throwStatus400(String... message) {
		return throwStatus(HttpStatus.BAD_REQUEST, message);
	}

	public static RuntimeException throwStatus404(String msg) {
		return throwStatus(HttpStatus.NOT_FOUND, msg);
	}

	public static RuntimeException throwStatus(HttpStatus httpStatus, String... message) {
		ResponseStatusException responseStatusException;
		if (ARG.isDef(message)) {
			responseStatusException = new ResponseStatusException(httpStatus, ARG.toDef(message));
		} else {
			responseStatusException = new ResponseStatusException(httpStatus);
		}
		if (true) {
			throw responseStatusException;
		}
		return responseStatusException;
	}

	public static ResponseEntity<byte[]> createResponseForDownloadFiles(Collection<String> files) {
		Pair<String, byte[]> pair;
		try {
			pair = getFilesContent(files);
		} catch (Exception e) {
			L.error("Ошибка обработки запроса", e);
			return new ResponseEntity<>(e.getMessage().getBytes(StandardCharsets.UTF_8), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		LinkedMultiValueMap<String, String> headersMap = new LinkedMultiValueMap<>();
		headersMap.add(com.google.common.net.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pair.getLeft() + "\"");

		return new ResponseEntity<>(pair.getRight(), headersMap, HttpStatus.OK);
	}

	//TODO preapreFilesForDownloadIfMany
	public static Pair<String, byte[]> getFilesContent(Collection<String> files) throws Exception {
		NI.stop("ni");
//		byte[] resultContent;
//		String resultFileName;
//		boolean useZip = files.size() > 1;
//		InputStream originalContent;
//		if (!useZip && !files.isEmpty()) {
//			//если файл один, то его и отдаем
//			String fileFirst = Iterables.get(files, 0);
//			File file = new File(fileFirst);
//			resultFileName = file.getName();
//			originalContent = new FileInputStream(file);
//		} else {
//			//если файлов много, то пакуемся в zip
//			resultFileName = "logs.zip";
//			File zipFile = TempFileUtils.createTempFile("log" + UUID.randomUUID(), ".zip");
//			for (String file : files) {
//				ZipFileUtils.packFileToZip(file, zipFile.getAbsolutePath(), true);
//			}
//			originalContent = new FileInputStream(zipFile);
//		}
//		try {
//			resultContent = IOUtils.toByteArray(originalContent);
//		} finally {
//			IOUtils.closeQuietly(originalContent);
//		}
//		return Pair.of(resultFileName, resultContent);
		return null;
	}


	public static HttpHeaders getHeaders_JsonCustom() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Content-type", "application/json; charset=utf-8");
		httpHeaders.add("Access-Control-Allow-Origin", "*");
		httpHeaders.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		return httpHeaders;
	}

	public static HttpHeaders getHeaders_Json() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Content-type", "application/json; charset=utf-8");
		return httpHeaders;
	}

	public static HttpHeaders getHeaders_PlainText() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Content-type", "text/plain; charset=utf-8");
		return httpHeaders;
	}

	@SneakyThrows
	public static void sendOk_HTML(String data) {
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(data);
	}

	@SneakyThrows
	public static void send500(String data) {
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		response.getWriter().println(data);
	}

	@SneakyThrows
	public static void send404(String data) {
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		response.getWriter().println(data);
	}

	@SneakyThrows
	public static void sendOk_JSON(String data) {
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
		response.setContentType("application/json;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(data);
	}

	public static void sendOk_JSON_OR(String cnt) {
		if (UGson.isGson(cnt)) {
			URest.sendOk_JSON(cnt);
		} else {
			URest.sendOk_HTML(cnt);
		}
	}
}
