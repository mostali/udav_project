package utl_web;


import lombok.SneakyThrows;
import mpc.exception.EmptyException;
import mpc.fs.UF;
import mpe.core.ERR;
import mpc.net.ContentType;
import mpu.X;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class URspContentType {

	@SneakyThrows
	public static void sendResponseContentType_FromFile(HttpServletResponse response, ContentType contentType, File file) {
		if (!file.isFile()) {
			URsp.sendError404(response);
			return;
		}
		sendResponseContentType(response, contentType, new FileInputStream(file));
	}

	@SneakyThrows
	public static void sendResponseContentType(HttpServletResponse response, ContentType contentType, InputStream data) {
		try {
			sendResponseContentType_(response, contentType, data);
		} catch (IOException e) {
			if (URsp.L.isErrorEnabled()) {
				URsp.L.error("responseContentType", e);
			}
			throw e;
		}
	}

	public static void sendResponseContentType_(HttpServletResponse response, ContentType contentType, InputStream data) throws IOException {
		response.setHeader("Content-Type", contentType.mimeType);
		try {
			IOUtils.copy(data, response.getOutputStream());
		} finally {
			IOUtils.closeQuietly(data);
			response.flushBuffer();
		}
	}

	@SneakyThrows
	public static void sendResponseContentType_FromRsrc(HttpServletResponse response, ContentType contentType, Class fromRsrc, String fromRsrcDir, String fromPath) {
		try {
			sendResponseContentTypeFromRsrc_(response, contentType, fromRsrc, fromRsrcDir, fromPath);
		} catch (EmptyException | IOException e) {
			if (URsp.L.isErrorEnabled()) {
//				L.error(X.f("responseContentTypeFromRsrc '%s', '%s', '%s'", contentType, fromRsrcDir, fromPath), e);
				URsp.L.error(X.f("responseContentTypeFromRsrc '%s', '%s', '%s'\n'%s", contentType, fromRsrcDir, fromPath, ERR.getStackTraceShort(e, 5)));
				URsp.sendError404(response);
			}
		}
	}

	public static void sendResponseContentTypeFromRsrc_(HttpServletResponse response, ContentType contentType, Class fromRsrc, String fromRsrcDir, String fromPath) throws IOException, EmptyException {
		//File path = new File(servletContext.getRealPath("/WEB-INF/includes/css/"));
		String rsrc = ".".equals(fromRsrcDir) ? UF.normUnixRootFile(fromPath) : UF.normUnixRootFile(fromRsrcDir, fromPath);
		InputStream is = fromRsrc.getResourceAsStream(rsrc);
		if (is == null) {
			throw new EmptyException(rsrc);
		}
		response.setHeader("Content-Type", contentType.mimeType);
		IOUtils.copy(is, response.getOutputStream());
		IOUtils.closeQuietly(is);
		response.flushBuffer();
	}
}
