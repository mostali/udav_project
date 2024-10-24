package zk_com.ext.uploader;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.apache.commons.fileupload.FileItem;
import org.zkoss.bind.BindUtils;
import org.zkoss.mesg.Messages;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Window;
import org.zkoss.zul.mesg.MZul;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.zkoss.lang.Generics.cast;

/**
 * Диалог загрузки импортируемых файлов.
 * В отличии от FileuploadDlg работает с AdvFileUpload.
 *
 * @author Zoya Novikova
 * @since 11.12.2013
 */
public class FileuploadDlg2 extends Window implements AfterCompose {
	public static final String MESSAGE = "message";
	public static final String TITLE = "title";
	public static final String MAX = "max";
	public static final String USE_NATIVE = "useNative";
	public static final String MULTIPLE = "multiple";
	private static final String ZUL_TEMPLATE = "fileuploaddlg2.zul";
	private static final String ATTR_FILEUPLOAD_TARGET = "org.zkoss.zul.Fileupload.target";
	private static final String MAX_FILE_SIZE = "maxFileSize";
	private static final String ALLOWED_FILE_EXTENSIONS = "allowedFileExtesions";
	private AdvFileUpload upload;

	public static void get(String message, String title, int max, int maxsize, boolean alwaysNative,
						   String[] allowedFileExtensions, boolean multiple) {


		final Map<String, Object> params = new HashMap<>(8);
		params.put(MESSAGE, message == null ? Messages.get(MZul.UPLOAD_MESSAGE) : message);
		params.put(TITLE, title == null ? Messages.get(MZul.UPLOAD_TITLE) : title);
		params.put(MAX, max == 0 ? 1 : max > 1000 ? 1000 : max < -1000 ? -1000 : max);
		params.put(USE_NATIVE, alwaysNative);
		params.put(MAX_FILE_SIZE, maxsize);
		params.put(ALLOWED_FILE_EXTENSIONS, Sets.newHashSet(allowedFileExtensions));
		params.put(MULTIPLE, multiple);
		final FileuploadDlg2 dlg = (FileuploadDlg2) Executions.getCurrent().createComponents(ZUL_TEMPLATE, null, params);
		dlg.doHighlighted();
	}

	@Override
	public void afterCompose() {
		addEventListener(Events.ON_CLOSE, new SerializableEventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				onClose(false);
			}
		});
		getFellow("okButton").addEventListener(Events.ON_CLICK, new SerializableEventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				onClose(true);
			}
		});
		getFellow("cancelButton").addEventListener(Events.ON_CLICK, new SerializableEventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				onClose(false);
			}
		});
		upload = (AdvFileUpload) getFellow("fileupload");
		final Map<?, ?> arg = getDesktop().getExecution().getArg();
		if (arg.containsKey(MULTIPLE)) {
			upload.setMultiple(cast(arg.get(MULTIPLE)));
		}
		if (arg.containsKey(ALLOWED_FILE_EXTENSIONS)) {
			upload.setExtensionsSet(cast(arg.get(ALLOWED_FILE_EXTENSIONS)));
		}
		if (arg.containsKey(MAX)) {
			upload.setMaxNumberOfFiles(cast(arg.get(MAX)));
		}

	}

	public void onClose(boolean needStoreData) {
		if (needStoreData) {
			List<FileItem> dialogResult = upload.getResult();
			BindUtils.postGlobalCommand(null, null, "setUploadFiles",
					ImmutableMap.of("files", dialogResult));
		}
		detach();
	}

}
