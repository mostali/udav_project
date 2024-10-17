package zk_com.ext.uploader.upload;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.http.WebManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;


/**
 * Сервлет загрузки файлов для компонента AdvFileUpload
 * Загруженные данные помещает в desktop
 *
 * @author Zoya Novikova
 * @since 11.12.2013
 */
public class FileUploadServlet extends HttpServlet implements FileUploadSpecification {
	private static final Logger log = LoggerFactory.getLogger(FileUploadServlet.class);
	private static final String ATTR_UPLOAD_SERVLET = "com.otr.sufd.server.fileupload";

	private static final String fileSizeError = "Ахтунг";
	private JsonFactory jsonFactory = new JsonFactory();
	private ServletContext servletContext;
	private MultipartRequestHandler requestHandler;
	private FileUploadStore fileUploadStore;
	private WebApp webApp;

	public static FileUploadServlet getFileUploadServlet(WebApp wapp) {
		return (FileUploadServlet) (wapp.getServletContext()).getAttribute(ATTR_UPLOAD_SERVLET);
	}

	@Override
	public void init() throws ServletException {
		super.init();
		servletContext = getServletContext();
		webApp = WebManager.getWebApp(servletContext);
		requestHandler = new MultipartRequestHandler(servletContext);
//		String storeClass = this.getInitParameter("fileStoreClass");
		fileUploadStore = FileUploadStoreLocator.registerStoreInstance(tryInitStore(SessionFileUploadStore.class.getName())).getStore();
		servletContext.setAttribute(ATTR_UPLOAD_SERVLET, this);
	}

	private FileUploadStore tryInitStore(String storeClass) {
		try {
			if (StringUtils.isNotBlank(storeClass)) {
				Class<?> clazz = Class.forName(storeClass);
				return (FileUploadStore) clazz.newInstance();
			}
		} catch (ClassNotFoundException e) {
			log.error("Ошибка загрузки класса для хранения файлов аплоада", e);
		} catch (Exception e) {
			log.error("Ошибка создания класса для хранения файлов аплоада", e);

		}
		return new SessionFileUploadStore();
	}

	/**
	 * ************************************************
	 * URL: /upload
	 * doPost(): upload the files and other parameters
	 * **************************************************
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		process(request, response);
	}

	public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			List<FileItem> fileItems = requestHandler.uploadFiles(request);
			Map<String, String> params = newHashMap();

			HttpSession httpSession = request.getSession();
			for (FileItem item : fileItems) {
				if (item.isFormField()) {
					params.put(item.getFieldName(), item.getString());
				}

			}

			String desktopId = params.get("dtid");
			String compId = params.get("compId");

			response.setContentType("application/json;charset=utf-8");
			response.setCharacterEncoding("UTF-8");

			PrintWriter writer = response.getWriter();

			JsonGenerator g = jsonFactory.createGenerator(writer);

			Integer maxFileSize = fileUploadStore.getMaxFileSize(compId, desktopId, webApp, httpSession);

			List<FileItem> items = newArrayList();

			g.writeStartObject();
			g.writeArrayFieldStart("files");

			final String contentId = DESKTOP_ATTRIB_PREFIX + compId + UUID.randomUUID().toString();

			for (FileItem item : fileItems) {
				if (!item.isFormField()) {
					g.writeStartObject();
					g.writeStringField(FILE_NAME_KEY, item.getName());
					g.writeNumberField(FILE_SIZE_KEY, item.getSize());
					g.writeStringField(CONTEN_ID_KEY, contentId);

					if (maxFileSize != null && item.getSize() > maxFileSize) {
						g.writeStringField("error", fileSizeError);
						item.delete();
					} else {
						items.add(item);
					}
					g.writeEndObject();
				}
			}
			if (!items.isEmpty()) {
				fileUploadStore.put(desktopId, contentId, servletContext, httpSession, items);
//				if (log.isDebugEnabled()) {
//					String userName = "";
//					UserInfo userInfo = (UserInfo) httpSession.getAttribute("currentUser");
//					if (userInfo != null) {
//						userName = userInfo.getSystemName();
//					} else {
//						userName = "user info is null";
//					}
//					Collection<String> fileNames = Collections2.transform(items, new Function<FileItem, String>() {
//						@Override
//						public String apply(FileItem input) {
//							return input.getName();
//						}
//					});
//					log.debug("Загружены файлы {} для пользователя [{}] с id [{}]", Joiner.on(",").join(fileNames), userName, contentId);
//				}
			}

			g.writeEndArray();
			g.writeEndObject();
			g.close();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
