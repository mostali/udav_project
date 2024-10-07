//package zk_core.com.form_factory.vf;
//
//import mp.utils.fs.FileExtInfo;
//import mp.utils.fs.UFILE;
//import mp.utils.fs.file.EFT;
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.event.Events;
//import org.zkoss.zul.Menuitem;
//import zk_core.com.SpanCtx;
//import zk_core.com.editable.EditableValueFilePrettyPrint;
//import zk_core.com.editable.TextboxFile;
//import zk_core.com.form_factory.CType;
//import zk_core.com_page.FormDirModel;
//import zk_core.srv.events.control.EventHighlightForm;
//import zk_core.srv.events.control.EventRmmForm;
//
//import java.nio.file.Path;
//import java.util.List;
//
//public class HtmlDonorVF extends AbsVF {
//
//	public HtmlDonorVF(FormDirModel fdm) {
//		super(fdm);
//	}
//
//	@Override
//	protected void initImpl() {
//
//		CType ctype = ctype();
//
//		List<Path> paths = formDirModel.getChilds(EFT.FILE, UFILE.EXT.buildPredicate(true, ctype));
//
//		if (paths.isEmpty()) {
////			setVisibleState(false);
////			ZKCF.checkNotEmpty(paths, ctype, formPath);
//		}
//
////		Map<Path, UFILE.EXT> fxMap = formDirModel.getMapExt();
////		Path htmlFile = UMap.getKey(fxMap, UFILE.EXT.HTML);
//		Component dirCom = null;
//		FileExtInfo fext = FileExtInfo.of(htmlFile);
//		if ("donor".equals(fext.first2)) {
////					EditableComponentFilePrettyPrint ed = EditableComponentFilePrettyPrint.build(file);
////					com = new ModalComponentRender(window, new Label(file.getFileName().toString()), ed, file.getFileName().toString());
////					Textbox txt = new Textbox(UFS.readFileContent(file));
//			TextboxFile ed = new TextboxFile(htmlFile).setDefaultDims().setEnableWrite(false);
//			ed.setClassAndStyle("", "color:green;font: small-caption italic 80% serif;");
//			dirCom = ed;
//		} else if ("edit".equals(fext.ext1)) {
//			EditableValueFilePrettyPrint ed = EditableValueFilePrettyPrint.build(htmlFile);
////					com = new ModalComponentRender(window, new Label(file.getFileName().toString()), ed);
//			dirCom = ed;
////					com = new EditableComponentFile(file) {
////						@Override
////						protected void onUpdatePrimaryText(String text) {
////							super.onUpdatePrimaryText(text);
////							Clients.evalJavaScript("PR.prettyPrint()");
////
////						}
////					}.setDefaultDims();
//		} else {
//			EditableValueFilePrettyPrint ed = EditableValueFilePrettyPrint.build(htmlFile);
////					com = new ModalComponentRender(window, new Label(file.getFileName().toString()), ed);
//			SpanCtx parent = SpanCtx.wrap(ed);
//			Menuitem rmm = new Menuitem("Remove");
//
//			Menuitem higlight = new Menuitem("Highlight");
//			higlight.addEventListener(Events.ON_CLICK, new EventHighlightForm(parent));
//			parent.addMenuItem(higlight);
//
//			rmm.addEventListener(Events.ON_CLICK, new EventRmmForm(formPath));
//			parent.addMenuItem(rmm);
//
////					goooo_evpp.addEventListener(Events.ON_CLICK, new SerializableEventListener<Event>() {
////						boolean start = false;
////
////						@Override
////						public void onEvent(Event event) throws Exception {
////							if (true) {
//////								ZK.evalJavaScriptRsrc("/web/js/clp1.js");
////								ZKC.contentQ(parent, RES.of(RES.class, "/web/zul/test.zul").cat());
////								return;
////							}
////						}
////					});
//
//			dirCom = parent;
//
////					com = new TCom(file).toComHtml();
//		}
//
//		if (dirCom != null) {
//			dirComs.add(dirCom);
//		}
//	}
//}
