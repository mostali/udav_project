//package zk_core.com.stuff;
//
//import mp.core.UC;
//import mp.core.fs.RW;
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zul.Html;
//import zk_core.srv.ZComHtmlBuilder;
//import zk_core.srv.ZKC;
//
//import java.io.IOException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//@Deprecated
//public class TCom {
//	final String text;
////	Path fileWithText;
//
//	final String _file;
//
//	private transient Path _fileWithText;
//
//	public Path file() {
//		return _fileWithText == null ? _fileWithText = Paths.get(UC.NN(_file)) : _fileWithText;
//	}
//
//	public TCom(String text) {
//		this.text = text;
//		this._file = null;
//	}
//
////	public TCom(Path file, boolean isFile) {
//////		if (isFile) {
////		this.text = null;
////		this._file = file.toString();
//////		} else {
//////			this.text = file;
//////			this.fileWithText = null;
//////		}
////	}
//
//	public TCom(Path fileWithText) {
//		this.text = null;
//		this._file = fileWithText.toString();
//		_fileWithText = fileWithText;
//	}
//
//	public Component renderComAsTextField(Component parent) {
//		return ZKC.contentQ(parent, ZComHtmlBuilder.TEXTBOX.VALUE(prepareContent()));
//	}
//
//	public Html toComHtml() {
//		Html html = new Html(prepareContent().toString());
//		return html;
//	}
//
//	public Component toComHtmlAndAppendTo(Component parent) {
//		Html html = new Html(prepareContent().toString());
//		parent.appendChild(html);
//		return html;
////			return ZKC.contentQ(parent, ZCom.WINDOW.VALUE(prepareContet()));
//	}
//
//	private CharSequence prepareContent() {
//		if (text != null) {
//			return text;
//		} else if (_file != null) {
//			try {
//				return RW.readContent_(file());
//			} catch (IOException e) {
//				throw new RuntimeException(e);
//			}
//		}
//		throw new IllegalStateException("need text content");
//	}
//
////		Label c = (Label) ZKC.contentQ(window, ZCom.LABEL.VALUE("hel"));
//
////		Component c = ZKC.contentQ(window, "<window title=\"drop here!\" droppable=\"true\" droppable=\"true\"  > " + ZCom.LABEL.VALUE("hel") + " </window>");
//
////		c.setDraggable("true");
////		c.setDroppable("true");
////		c.
////		U.p(c);
////		Textbox c2 =
////				(Textbox) ZKC.contentQ(window, "<zk>\n" +
////											   "\t<textbox value=\"test12\" readonly=\"false\"/>\n" +
////											   "</zk>\n");
////		c2.setDraggable("true");
////		c2.setDroppable("true");
////		c2.dr
////		U.p(c2);
//
//
////		if (!isInit) {
////			ZKC.contentQ("<window title='sd' border='normal' viewModel=\"@id('vm') @init('mp.zkapp.viewmodel.appvm.AppVM')\" mode=\"modal\" >  asd <textbox value=\"@load(vm.systemQuery)\" hflex=\"1\" rows=\"4\" readonly=\"true\"/> </window>", window, ImmutableMap.of(AppVM.SYSTEM_QUERY, "hell"));
////			isInit = true;
////		}
//
////		Component cn = C.contentQ("<window title='sd' border='normal'><div style='height:500px'><spreadsheet id='mss' hflex='1' vflex='1'/> </div></window>", window);
//
//
////		Executions.getCurrent().getDesktop().getFirstPage().getFirstRoot().appendChild(win1);
//}
