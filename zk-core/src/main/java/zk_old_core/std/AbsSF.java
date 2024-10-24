//package zk_form.std;
//
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import mpc.arr.AR;
//import mpc.core.ARG;
//import mpu.X;
//import mpc.exception.RequiredRuntimeException;
//import mpc.exception.SimpleMessageRuntimeException;
//import mpc.fs.ext.EXT;
//import mpu.core.RW;
//import mpc.fs.fd.EFT;
//import mpc.ns.space.ISrcModel;
//import mpc.ns.space.core.SpaceHomeMap;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.zkoss.zk.ui.event.Events;
//import org.zkoss.zk.ui.event.SerializableEventListener;
//import org.zkoss.zul.Fileupload;
//import zk_com.base_ctr.DivWith;
//import zk_com.base_ctr.SimpleMenupopup;
//import zk_com.base_ctr.SpanWith;
//import zk_com.ck_editor.CkEditorComposer;
//import zk_com.editable.EditableValueFile;
//import zk_com.editable.RenameFileTextbox;
//import zk_com.sun_editor.SeWin;
//import zk_com.uploader.FileUploaderComposer;
//import zk_com.win.EventShowModal;
//import zk_form.fswin.FsWin;
//import zk_form.mwin.MWin;
//import zk_form.std_core.CType;
//import zk_os.struct.FormDirDS;
//import zk_page.behaviours.EventAppendLoader;
//import zk_page.behaviours.EventHighlightForm;
//import zk_rmm.events.EventRmmForm;
//import zk_page.mdl.FdModel;
//import zk_page.mdl.FormDirModel;
//import zk_page.mdl.PageDirModel;
//import zk_page.mdl.pageset.IFormModel;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Predicate;
//
//@RequiredArgsConstructor
//public abstract class AbsSF extends SpanWith {
//
//	public static final Logger L = LoggerFactory.getLogger(AbsSF.class);
//
//	public final SpaceHomeMap props;
//
//	public AbsSF(Map<String, Object> formProps) {
//		this(formProps, null);
//	}
//
//	public AbsSF(Map<String, Object> formProps, FdModel fdModel) {
//		super();
//		this.formProps = formProps;
//		this.fdModel = fdModel;
//	}
//
//	public boolean isVisibleState() {
//		return viewMode != ViewMode.error;
//	}
//
//	@Getter
//	private ViewMode viewMode = ViewMode.view;
//
//	@Getter
//	private Exception initError = null;
//
//	public void setViewMode(ViewMode viewMode) {
//		this.viewMode = viewMode;
//	}
//
//	public void setInitError(Exception initError) {
//		if (L.isErrorEnabled()) {
//			String msg = "Error init form:" + path() + "(" + initError.getMessage() + ")";
//			L.error(msg, initError);
//		}
//		setViewMode(ViewMode.error);
//		this.initError = initError;
//	}
//
//	public String buildDdLink() {
//		return ctype() + ":" + PageDirModel.get().path().relativize(path());
//	}
//
//	public Path getPathRootProps() {
//		return FormDirDS.SELF.getJsonOrPropsPath(path());
//	}
//
//	public String getHtmlData() {
//		throw new UnsupportedOperationException("Override this method");
//	}
//
//	@Deprecated
//	public enum ViewMode {
//		view, edit, error
//	}
//
//	public CType ctype() {
//		return ((IFormModel) fdModel).ctype();
//	}
//
//	public Path path() {
//		return fdModel.path();
//	}
//
//	public String name() {
//		return fdModel.name();
//	}
//
//
//	public Map<String, Object> getFormProps(Map<String, Object>... defRq) {
//		if (X.notEmpty(formProps)) {
//			return formProps;
//		} else if
//		(fdModel != null) {
//			return fdModel.getRootProps();
//		}
//		return ARG.toDefThrow(() -> new RequiredRuntimeException("FormRootProps from '%s' is null", cn()), defRq);
//	}
//
////	public RuProps props() {
////		return path_child(AppDS.FN_PROPS, RuProps.class);
////	}
//
//	protected abstract void initImpl() throws Exception;
//
//	@Override
//	protected void init() {
//		//super.init();
//		try {
//			if (fdModel != null && getRootChilds().isEmpty()) {
//				setInitError(new SimpleMessageRuntimeException("Form '%s' has empty childs", name()));
//				return;
//			}
//
//			initImpl();
//
////			if (viewMode != ViewMode.view) {
////				appendContextMenu();
////			}
//
//			//setTITLE(name());
//
//		} catch (Exception e) {
//
//			try {
//				setInitError(e);
//			} catch (Exception ex) {
//				if (L.isErrorEnabled()) {
//					L.error("Cause1 - buildForm", e);
//					L.error("Cause2 - buildForm - happensError", e);
//				}
//			}
//		}
//	}
//
//	transient List<Path> childs = null;
//
//	public List<Path> getRootChilds() {
//		if (childs != null) {
//			return childs;
//		}
//		Predicate<Path> predicateChildsFilter = getPredicateChildsFilter();
//
//		List<Path> paths;
//		if (fdModel.isFile()) {
//			paths = AR.as(fdModel.path());
//		} else {
//			FormDirModel fdm = (FormDirModel) fdModel;
//			paths = fdm.getChilds(EFT.FILE, predicateChildsFilter);
//		}
////		P.p("AbsVF:Found:" + ctype() + ":" + viewMode + ":" + paths);
//		return this.childs = paths;
//	}
//
//	;
//
//	public Predicate<Path> getPredicateChildsFilter() {
//		return EXT.buildPredicate(true, getAllowedExt());
//	}
//
//	public abstract String[] getAllowedExt();
//
//
//	//
////	public void appendContextMenu() {
////		if (ImgVF.class.isAssignableFrom(getClass())) {
////			appendContextMenu(this, true, true, true);
////		} else {
////			appendContextMenu(this, true, true, false);
////		}
////	}
//
//	public static void appendContextMenu(AbsSF absVf, boolean rmm, boolean highlight, boolean uploadFile, boolean uploadImage) {
//
//		PageDirModel pageDirModel = PageDirModel.get();
//
//		SimpleMenupopup poppMenu = absVf.appendMenupopup(Events.ON_DOUBLE_CLICK);
//		//		SimpleMenupopup poppMenu = new SimpleMenupopup(absVf);
//
//		String name = absVf.name();
//
//		Path formPath = absVf.path();
//		String formPathStr = formPath.toString();
//		/**
//		 * *************************************************************
//		 * ---------------------------- CTYPE --------------------------
//		 * *************************************************************
//		 */
//
//		if (!absVf.fdModel.isFile()) {
//			String title = "Form '" + name + "' / " + absVf.ctype();
//
//			Path file = absVf.fdModel.getFileRootProps();
//			EditableValueFile editValue = (EditableValueFile) new EditableValueFile(file).isLabelVew(false).setDefaultDims();
//
//			EventShowModal eventListener = new EventShowModal(title, absVf, editValue);
//			poppMenu.addMenuitem(title, Events.ON_CLICK, eventListener);
//		}
//
//		poppMenu.addContextMenuSeparator();
//
//		poppMenu.addMenuitem("Open SeEditor", Events.ON_CLICK, (SerializableEventListener) event -> SeWin.open(pageDirModel, absVf.getRootChilds().get(0)));
//		poppMenu.addMenuitem("Open CkEditor", Events.ON_CLICK, (SerializableEventListener) event -> CkEditorComposer.loadComponent(absVf.getRootChilds().get(0)));
//
//		poppMenu.addMenuitem("Open MEditor", Events.ON_CLICK, (SerializableEventListener) event -> MWin.openForm(absVf));
//
//		poppMenu.addMenuitem("Open Dir", Events.ON_CLICK, (SerializableEventListener) event -> FsWin.openNoSecurity(Paths.get(formPathStr)));
//
//		poppMenu.addContextMenuSeparator();
//
//		/**
//		 * *************************************************************
//		 * ---------------------------- FULL EDIT --------------------------
//		 * *************************************************************
//		 */
//		{
////			tbeditor ds;
//			String title = "Edit form '" + name + "'";
////			DivWith divEitView = EditableValueFile.buildComs_EditView(absVf.getRootChilds());
//			DivWith divEitView = EditableValueFile.buildComs_EditViewWithRename(absVf.getRootChilds());
//			EventShowModal eventListener = new EventShowModal(title, absVf, divEitView);
//			poppMenu.addMenuitem("Edit", Events.ON_CLICK, eventListener);
//		}
//
//
//		if (highlight) {
//			poppMenu.addMenuitem("Highlight", Events.ON_CLICK, (SerializableEventListener) event -> EventHighlightForm.applyOnOff_MouseOverOut(absVf));
//		}
//
//		if (uploadFile) {
//			//poppMenu.addMenuitem("Upload File", Events.ON_CLICK, FileUploaderComposer.getEventOpenMenuUpload(absVf.path().toString()));
//			poppMenu.addMenuitem("Upload File", Events.ON_CLICK, event -> Fileupload.get(1, FileUploaderComposer.getEventUpload(formPath.toString())));
//		}
//
//		if (uploadImage) {
//			poppMenu.addMenuitem("Upload Clipboard Image", Events.ON_CLICK, new EventAppendLoader(formPath.toString(), false));
//		}
//
//		poppMenu.addContextMenuSeparator();
//
//		if (!absVf.fdModel.isFile()) {
//			DivWith div = RenameFileTextbox.buildComsForDir(formPath);
//			EventShowModal eventListener = new EventShowModal("Rename", absVf, div);
//			poppMenu.addMenuitem("Rename", Events.ON_CLICK, eventListener);
//		}
//
//		if (rmm) {
//			poppMenu.addMenuitem("Remove", Events.ON_CLICK, new EventRmmForm(formPath));
//		}
//
//	}
//
//	public Path path_child(String child) {
//		return path().resolve(child);
//	}
//
//	public <T> T path_child(String child, Class<T> asType, T... defRq) {
//		return RW.readAs(path_child(child), asType, defRq);
//	}
//
//
//	public static AbsSF build(IFormModel formModel, ViewMode viewMode) throws Exception {
//		CType ctype = IFormModel.getCTypeOrDefine(formModel);
//		return build(ctype.type(), formModel, viewMode);
//	}
//
//	public static AbsSF build(Class<? extends AbsSF> clazz, IFormModel fdForm, ViewMode viewMode) throws Exception {
//		AbsSF form = clazz.getDeclaredConstructor(FdModel.class).newInstance((FdModel) fdForm);
//		form.setViewMode(viewMode);
//		return form;
//	}
//
//}
