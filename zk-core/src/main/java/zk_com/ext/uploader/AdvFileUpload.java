package zk_com.ext.uploader;


import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zk.ui.util.UiLifeCycle;
import org.zkoss.zul.Messagebox;

import java.io.IOException;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.zkoss.lang.Generics.cast;

/**
 * Компонент для загрузки файлов на сервер. Работает в связке с FileUploadServlet.
 * Данные загружаются с клиента сервлетом, кладутся в десктоп, откуда их забирает компонент.
 * Клиeнтская часть использует jqueryFileUpload для загрузки файлов.
 * TODO: доработать загрузку по кнопке для каждого ресурса (?),
 *
 * @author Zoya Novikova
 * @since 12.12.2013
 */
public class AdvFileUpload extends HtmlBasedComponent implements UiLifeCycle, AfterCompose, FileUploadSpecification {
	public static final String ON_FILE_ADD = "onFileAdd";
	public static final String ON_FILE_UPLOAD = "onFileUpload";
	public static final String ON_FILE_DELETE = "onFileDelete";
	public static final String ON_FILES_CLEAR = "onFilesClear";
	private static final String CREATE_RESOURCE_URL = "create.resource.url";

	private static final Logger log = LoggerFactory.getLogger(AdvFileUpload.class);

	static {
		addClientEvent(AdvFileUpload.class, ON_FILE_ADD, CE_IMPORTANT);
		addClientEvent(AdvFileUpload.class, ON_FILE_UPLOAD, CE_IMPORTANT);
		addClientEvent(AdvFileUpload.class, ON_FILE_DELETE, CE_IMPORTANT);
		addClientEvent(AdvFileUpload.class, ON_FILES_CLEAR, CE_IMPORTANT);
	}

	private List<FileItem> files = newArrayList();
	private String url;
	// default true
	private boolean multiple = true;
	// Ограничение на размер файла в байтах
	private Integer maxFileSize;
	// Ограничение на количество загружаемых файлов
	private Integer maxNumberOfFiles;
	// Список допустимых расширений
	private Set<String> extensionsSet;
	// default true
	private boolean showProgress = true;
	// default true
	private boolean showFileList = true;
	// default true
	private boolean showDropzone = true;
	private boolean isScanDocumentDialog = false;
	private boolean isAllowedList = true;

	public AdvFileUpload() {
	}

	public static String encodeResourceUrl(String uri) {
		Execution current = Executions.getCurrent();
		try {
			current.setAttribute(CREATE_RESOURCE_URL, Boolean.TRUE);
			return Executions.encodeURL(uri);
		} finally {
			current.removeAttribute(CREATE_RESOURCE_URL);
		}
	}

//	@Override
//	public String getWidgetClass() {
//		return super.getWidgetClass();
//	}

	@Override
	public void afterCompose() {
		getDesktop().addListener(this);
		url = encodeResourceUrl("/upload");
	}

	@Override
	public void afterShadowAttached(ShadowElement shadow, Component host) {

	}

	@Override
	public void afterShadowDetached(ShadowElement shadow, Component prevhost) {

	}

	public List<FileItem> getResult() {
		return files;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		if (this.url != url) {
			this.url = url;
			smartUpdate("url", url);
		}
	}

	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple(boolean multiple) {
		if (this.multiple != multiple) {
			this.multiple = multiple;
			smartUpdate("multiple", multiple ? "true" : "false");
		}
	}

	public boolean isShowProgress() {
		return showProgress;
	}

	public void setShowProgress(boolean showProgress) {
		if (this.showProgress != showProgress) {
			this.showProgress = showProgress;
			smartUpdate("showProgress", showProgress ? "true" : "false");
		}
	}

	public boolean isShowFileList() {
		return showFileList;
	}

	public void setShowFileList(boolean showFileList) {
		if (this.showFileList != showFileList) {
			this.showFileList = showFileList;
			smartUpdate("showFileList", showFileList ? "true" : "false");
		}
	}

	public boolean isShowDropzone() {
		return showDropzone;
	}

	public void setShowDropzone(boolean showDropzone) {
		if (this.showDropzone != showDropzone) {
			this.showDropzone = showDropzone;
			smartUpdate("showDropzone", showDropzone ? "true" : "false");
		}
	}

	public boolean isAllowedList() {
		return isAllowedList;
	}

	public void setAllowedList(boolean isAllowedList) {
		if (this.isAllowedList != isAllowedList) {
			this.isAllowedList = isAllowedList;
			smartUpdate("isAllowedList", isAllowedList ? "true" : "false");
		}
	}

	public boolean isScanDocumentDialog() {
		return isScanDocumentDialog;
	}

	public void setIsScanDocumentDialog(boolean isScanDocumentDialog) {
		if (this.isScanDocumentDialog != isScanDocumentDialog) {
			this.isScanDocumentDialog = isScanDocumentDialog;
			smartUpdate("isScanDocumentDialog", isScanDocumentDialog ? "true" : "false");
		}
	}

	public Integer getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(Integer maxFileSize) {
		if (this.maxFileSize != maxFileSize) {
			this.maxFileSize = maxFileSize;
			smartUpdate("maxFileSize", maxFileSize);
			zk_com.ext.uploader.upload.FileUploadStoreLocator.getInstance().getStore().setMaxFileSize(getUuid(), getDesktop().getId(),
					Executions.getCurrent().getSession().getWebApp(),
					Executions.getCurrent().getSession().getNativeSession(),
					maxFileSize);
			getDesktop().setAttribute(MAX_FILE_SIZE_PREFIX + getUuid(), maxFileSize);
		}
	}

	public Integer getMaxNumberOfFiles() {
		return maxNumberOfFiles;
	}

	public void setMaxNumberOfFiles(Integer maxNumberOfFiles) {
		if (this.maxNumberOfFiles != maxNumberOfFiles) {
			this.maxNumberOfFiles = maxNumberOfFiles;
			smartUpdate("maxNumberOfFiles", maxNumberOfFiles);
			getDesktop().setAttribute(MAX_NUMBER_OF_FILES + getUuid(), maxNumberOfFiles);
		}
	}

	public Set<String> getExtensionsSet() {
		return extensionsSet;
	}

	public void setExtensionsSet(Set<String> extensionsSet) {
		if (!ObjectUtils.equals(this.extensionsSet, extensionsSet)) {
			this.extensionsSet = extensionsSet;
			smartUpdate("extensionsSet", getExtensionsSetString());
		}
	}

	public String getExtensionsSetString() {
		StringBuilder sb = new StringBuilder();
		if (extensionsSet == null || extensionsSet.isEmpty() || extensionsSet.contains("*")) {
			return "";
		}
		for (String ext : extensionsSet) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(ext);
		}

		return sb.toString();
	}

	private void updateTotalCount() {
		smartUpdate("totalCount", files.size());
	}

	@Override
	protected void renderProperties(ContentRenderer renderer) throws IOException {
		super.renderProperties(renderer);
		render(renderer, "url", url);
		render(renderer, "multiple", multiple ? "true" : "false");
		render(renderer, "maxFileSize", maxFileSize);
		render(renderer, "maxNumberOfFiles", maxNumberOfFiles);
		render(renderer, "showFileList", showFileList ? "true" : "false");
		render(renderer, "showProgress", showProgress ? "true" : "false");
		render(renderer, "totalCount", files.size());
		render(renderer, "extensionsSet", getExtensionsSetString());
		render(renderer, "isAllowedList", isAllowedList ? "true" : "false");
		render(renderer, "showDropzone", showDropzone ? "true" : "false");
		render(renderer, "isScanDocumentDialog", isScanDocumentDialog ? "true" : "false");
	}

	@Override
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();

		// Файл только выбран пользователем и еще не загружен на сервер
		if (cmd.equals(ON_FILE_ADD)) {
			try {
				fireEvent(ON_FILE_ADD, request.getData());
			} catch (Exception e) {

			}
		}
		// Удаляем загруженный файл
		if (cmd.equals(ON_FILE_DELETE)) {
			String fileName = (String) request.getData().get("fileName");
			Integer fileSize = (Integer) request.getData().get("fileSize");
			for (FileItem uploadedFile : files) {
				if (uploadedFile.getName().equals(fileName)) {
					if (uploadedFile.getSize() == fileSize) {
						files.remove(uploadedFile);
						updateTotalCount();
						break;
					}
				}
			}
		}
		// Очищаем все загруженные файлы
		if (cmd.equals(ON_FILES_CLEAR)) {
			files.clear();
			updateTotalCount();
			try {
				fireEvent(ON_FILES_CLEAR, request.getData());
			} catch (Exception e) {
			}
		}

		// Забираем данные из десктопа и переносим в список загруженных файлов
		if (cmd.equals(ON_FILE_UPLOAD)) {
			Desktop desktop = getDesktop();
			if (request.getData().isEmpty()) {
				return;
			}
			Map<String, Object> result = request.getData();
			if (result != null) {
				JSONArray items = cast(result.values().iterator().next());
				Iterator iter = items.iterator();
				String contenId;

				if (!iter.hasNext()) {
					return;
				}
				do {
					JSONObject item = cast(iter.next());
					contenId = cast(item.get(CONTEN_ID_KEY));
				} while (iter.hasNext() && isEmpty(contenId));

				// Если файлов слишком много - удаляем, не загружая
				if (maxNumberOfFiles != null && files.size() >= maxNumberOfFiles) {

					Messagebox.show(2106, maxNumberOfFiles, 2107, Messagebox.OK, Messagebox.ERROR);
					zk_com.ext.uploader.upload.FileUploadStoreLocator.getInstance().getStore().remove(getDesktop().getId(), contenId,
							Executions.getCurrent().getSession().getWebApp(),
							Executions.getCurrent().getSession().getNativeSession());
					return;
				}

				List<FileItem> uploaded = zk_com.ext.uploader.upload.FileUploadStoreLocator.getInstance().getStore().get(getDesktop().getId(), contenId,
						Executions.getCurrent().getSession().getWebApp(),
						Executions.getCurrent().getSession().getNativeSession());
				// Проверка изменения расширения при передачи от клиента
				if (CollectionUtils.isEmpty(uploaded)) {
					log.debug("Не найдены загруженные файлы для пользователя [{}] и id [{}]", SecurityContextHolder.getContext().getAuthentication().getName(), contenId);
					return;
				}
				for (FileItem item : uploaded) {
					if (notAccepted(item.getName(), extensionsSet, isAllowedList)) {
						if (isAllowedList) {
							Messagebox.show(2109, maxNumberOfFiles, 2108, Messagebox.OK, Messagebox.ERROR);
						} else {
							Messagebox.show(2110, maxNumberOfFiles, 2108, Messagebox.OK, Messagebox.ERROR);
						}
						zk_com.ext.uploader.upload.FileUploadStoreLocator.getInstance().getStore().remove(getDesktop().getId(), contenId,
								Executions.getCurrent().getSession().getWebApp(),
								Executions.getCurrent().getSession().getNativeSession());
						return;
					}
				}

				files.addAll(uploaded);
				updateTotalCount();
				zk_com.ext.uploader.upload.FileUploadStoreLocator.getInstance().getStore().remove(getDesktop().getId(), contenId,
						Executions.getCurrent().getSession().getWebApp(),
						Executions.getCurrent().getSession().getNativeSession());

				try {
					fireEvent(ON_FILE_UPLOAD, uploaded);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		} else {
			super.service(request, everError);
		}
	}

	/**
	 * Возвращает true если файл имеет недопустимое расширение
	 *
	 * @param fileName      имя файла
	 * @param extList       список расширений
	 * @param isAllowedList должен ли файл входить в список
	 * @return имеет ли файл недопустимое расширение
	 */
	private boolean notAccepted(String fileName, Collection<String> extList, boolean isAllowedList) {
		if (extList == null || extList.isEmpty() || extList.contains("*")) {
			return false;
		}
		fileName = fileName.toLowerCase();
		for (String ext : extList) {
			if (fileName.endsWith(ext.toLowerCase())) {
				return !isAllowedList;
			}
		}
		return isAllowedList;
	}

	protected void fireEvent(String evtName, Object data) throws Exception {
		for (EventListener listener : getEventListeners(evtName)) {
			listener.onEvent(new Event(evtName, null, data));
		}
	}

	protected void destroy(Desktop desktop) {
		desktop.removeAttribute(MAX_FILE_SIZE_PREFIX + getUuid());
		desktop.removeListener(this);
		//desktop.removeAttribute(MAX_NUMBER_OF_FILES + getUuid());
	}

	@Override
	public void detach() {
		destroy(getDesktop());
		super.detach();
	}

	@Override
	public void afterComponentAttached(Component comp, Page page) {
	}

	@Override
	public void afterComponentDetached(Component comp, Page prevpage) {
		if (comp.equals(getParent())) {
			destroy(prevpage.getDesktop());
		}
	}

	@Override
	public void afterComponentMoved(Component parent, Component child, Component prevparent) {
	}

	@Override
	public void afterPageAttached(Page page, Desktop desktop) {
	}

	@Override
	public void afterPageDetached(Page page, Desktop prevdesktop) {
	}
}
