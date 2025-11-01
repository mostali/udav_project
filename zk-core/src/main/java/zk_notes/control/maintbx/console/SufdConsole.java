package zk_notes.control.maintbx.console;

import org.zkoss.zk.ui.Executions;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SufdConsole {

	public static final String VARNAME_THIS = MvelTask.VARNAME_THIS;

	public static final Set<String> DEF_IMPORT_DOCDEV = new HashSet<String>();

	static {
		DEF_IMPORT_DOCDEV.add("java.math.BigDecimal");

		DEF_IMPORT_DOCDEV.add("com.otr.sufd.oapi.doc.FieldSet");
		DEF_IMPORT_DOCDEV.add("com.otr.sufd.adapters.ContentAccessor");
		DEF_IMPORT_DOCDEV.add("com.otr.commonlib.constants.DocInfo");
		DEF_IMPORT_DOCDEV.add("com.otr.sufd.core.doc.DocType");
		DEF_IMPORT_DOCDEV.add("com.otr.sufd.forms.actions.SingleDocContext");
		DEF_IMPORT_DOCDEV.add("com.otr.sufd.forms.view.field.FieldView");
		DEF_IMPORT_DOCDEV.add("com.otr.sufd.oapi.edit.AbstractDocument");
		DEF_IMPORT_DOCDEV.add("com.otr.sufd.oapi.edit.Dictionary");
		DEF_IMPORT_DOCDEV.add("com.otr.sufd.oapi.edit.Document");
		DEF_IMPORT_DOCDEV.add("com.otr.sufd.oapi.entity.OrgInfo");
		DEF_IMPORT_DOCDEV.add("com.otr.sufd.oapi.entity.UserInfo");

		DEF_IMPORT_DOCDEV.add("com.otr.sufd_new.fill.edit.StdFill");
		DEF_IMPORT_DOCDEV.add("com.otr.sufd_new.console.SufdConsole");

		DEF_IMPORT_DOCDEV.add("com.otr.sufd_new.console.USite");
		// DEF_IMPORT_DOCDEV.add("");

	}

	public static StringBuilder toStringBuilderImport(List<String> classes) {
		StringBuilder sb = new StringBuilder();
		for (String clas : classes) {
			sb.append("import ").append(clas).append(";");
		}
		return sb;
	}

	private static volatile SufdConsole instance;

	public static SufdConsole get() {
		SufdConsole localInstance = instance;
		if (localInstance == null) {
			synchronized (SufdConsole.class) {
				localInstance = instance;
				if (localInstance == null) {
					instance = localInstance = new SufdConsole();
				}
			}
		}
		return localInstance;
	}

	public static void showLogMessage(String message, Object... arrayContext) {
		Map<String, Object> mapContext = MvelTask.objectsContext2mapContextSE(arrayContext);
		MvelConsolePanel console = getConsolePanel(mapContext);
		Executions.getCurrent().getDesktop().getFirstPage().getFirstRoot().appendChild(console);
	}

	public MvelConsolePanel console;

	public static MvelConsolePanel getConsolePanel(Object[] arrayContext) {
		Map<String, Object> mapContext = MvelTask.objectsContext2mapContextSE(arrayContext);
		return getConsolePanel(mapContext);
	}

	public static MvelConsolePanel getConsolePanel(Map<String, Object> mapContext) {
		SufdConsole app = get();

		MvelConsolePanel console = app.console;
		if (true || console == null) {// true - it hack, because zk throw error,
			// when open console twice
			console = new MvelConsolePanel();
			console.setContextInit(mapContext);
			console.onInitPanel();
			app.console = console;
		}
		return console;
	}

}
