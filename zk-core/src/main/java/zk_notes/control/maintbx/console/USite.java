//package zk_notes.control.maintbx.console;
//
//import com.google.common.base.Preconditions;
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.Desktop;
//import org.zkoss.zk.ui.Executions;
//import org.zkoss.zk.ui.util.Clients;
//import org.zkoss.zul.Messagebox;
//
//public class USite {
//
//	public static void showMessageNote(String message) {
//		Clients.showNotification(message, false);
//	}
//
//	public static void showMessageInfo(String message) {
//		Messagebox.show(message, "...", Messagebox.OK, Messagebox.INFORMATION);
//	}
//
//	public static Desktop getDesktop() {
//		return Executions.getCurrent().getDesktop();
//	}
//
//	public static void appendToPage(Component what) {
//		Preconditions.checkNotNull(what, "What component is null");
//		getDesktop().getFirstPage().getFirstRoot().appendChild(what);
//	}
//
//	public static void appendToPage(Component src, Component what) {
//		Preconditions.checkNotNull(src, "Source component is null");
//		Preconditions.checkNotNull(what, "What component is null");
//		src.getPage().getFirstRoot().appendChild(what);
//
//	}
//
//}
