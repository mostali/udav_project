package zk_com.base_ctr;

import lombok.extern.slf4j.Slf4j;
import mpc.fs.UF;
import mpc.map.MAP;
import mpc.str.sym.SYMJ;
import mpe.rt.core.ExecRq;
import mpu.IT;
import mpu.Sys;
import mpu.X;
import mpu.core.ARG;
import mpc.fs.UFS;
import mpc.fs.ext.EXT;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.func.Function2;
import mpu.func.FunctionV1;
import mpu.pare.Pare;
import mpu.str.JOIN;
import mpu.str.SPLIT;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.*;
import org.zkoss.zul.impl.XulElement;
import zk_com.base.Mi;
import zk_com.editable.RenameFileTextbox;
import zk_form.events.*;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Messagebox;
import zk_notes.apiv1.client.LocalNoteApi;
import zk_form.dirview.SimpleDirView;
import udav_net.apis.zznote.ItemPath;
import zk_notes.node_srv.fsman.NodeFileTransferMan;
import zk_os.AFCC;
import zk_os.sec.Sec;
import zk_page.*;
import zk_page.behaviours.UploaderFileOrPhotoEvent;
import zk_notes.apiv1.NodeApiChars;
import zk_page.core.SpVM;
import zk_page.index.RSPath;
import zk_page.index.Sd3DdChoicer;
import zk_notes.node.NodeDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public class Menupopup0 extends Menupopup {
	final HtmlBasedComponent hostHolder;

	private Menupopup0() {
		hostHolder = null;
	}

	public Menupopup0(XulElement hostHolder) {
		this(hostHolder, hostHolder, null);
	}

	public Menupopup0(HtmlBasedComponent hostHolder, XulElement onChild, String eventForOpenMenu_orNullIfRightClick) {
		this.hostHolder = hostHolder;
		hostHolder.appendChild(this);
		onChild.setContext(this);

		if (eventForOpenMenu_orNullIfRightClick != null && !Events.ON_RIGHT_CLICK.equals(eventForOpenMenu_orNullIfRightClick)) {
			onChild.addEventListener(eventForOpenMenu_orNullIfRightClick, (SerializableEventListener<Event>) event -> {
				//ctxMenu.getContextMenuPopup().open(100,100);
				getContextMenupopup().open(hostHolder);
			});
		}
	}

	public static Menupopup0 createMenupopup(HtmlBasedComponent host) {
		return Menupopup0.createMenupopup(host, (XulElement) host, null);
	}

	public static Menupopup0 createMenupopup(HtmlBasedComponent hostHolder, XulElement onChild, String eventForOpenMenu_orNullIfRightClick) {
		if (false) {
//			Menupopup menuPopup = new Menupopup();
//			hostHolder.appendChild(menuPopup);
//			onChild.setContext(menuPopup);
//			return menuPopup;
		}
		return new Menupopup0(hostHolder, onChild, eventForOpenMenu_orNullIfRightClick);
	}

	/**
	 * *************************************************************-
	 * ---------------------------- CONTEXT MENU ITEMS --------------------------
	 * *************************************************************
	 */

	public Pare<Menuitem, Menupopup0> addMI_EDITOR(String label, Path pathFile, boolean ifExist, EXT type) {
		if (ifExist && (pathFile == null || !UFS.existFile(pathFile))) {
			return null;
		}
		return addMenuItem(label, Events.ON_CLICK, (Event e) -> {
			ZKI.infoEditorBw(pathFile, type);
		});
	}

	public Pare<Menuitem, Menupopup0> addMenuitem_ExecFileTmp(String label, String cmdKey, String tmpFileData) {
		return addMenuItem(label, e -> {
			Pare<Integer, List<String>> rslt = Sys.exec_filetmp(cmdKey, tmpFileData, null, false);
			List outLines = rslt.val();
			List lines_Err_Out = ARR.mergeToList(ARR.as(cmdKey), SPLIT.allByNL(tmpFileData), ARR.as("-----------------------"), outLines);
			if (rslt.key() == 0) {
				ZKI.infoEditorBw(lines_Err_Out);
			} else {
				ZKI.alert(JOIN.allByNL(lines_Err_Out));
			}
		});

	}

	public Pare<Menuitem, Menupopup0> addMenuitem_ExecAnyScriptOS(String label, String placeholder) {
		Function<String, Object> hiv = (s) -> {
			ExecRq execRq = Sys.exec_rq(SPLIT.argsBySpace(s));
			List<String> lines_Err_Out = ARR.mergeToList(ARR.as(s, "-----------------------"), execRq.getOut(false), execRq.getOut(true));
			ZKI.infoEditorBw(lines_Err_Out);
			return null;
		};
		return addMI_Cfm1(label, placeholder, hiv);
	}

	public Pare<Menuitem, Menupopup0> addMI_Cfm1(String label, String placeholder, Function<String, Object> hiv) {
		return addMI_Cfm1(label, placeholder, "", hiv);
	}

	public Pare<Menuitem, Menupopup0> addMI_Cfm1(String label, String placeholder, String initValue, Function<String, Object> hiv) {
		Mi menuItemComponent = Tbx_CfrmSerializableEventListener.toMenuItemComponent("", label, initValue, placeholder, hiv);
		addMI_Href(menuItemComponent);
		return Pare.of(menuItemComponent, this);
	}

	public Pare<Menuitem, Menupopup0> addMI_EDITOR(String label, Supplier<String> contentSupplier) {
		return addMenuItem(label, Events.ON_CLICK, (Event e) -> {
			ZKI.infoEditorBw(contentSupplier.get());
		});
	}

	public Pare<Menuitem, Menupopup0> addMenuItem(String label, EventListener eventListener) {
		return addMenuItem(label, Events.ON_CLICK, eventListener);
	}

	public Pare<Menuitem, Menupopup0> addMI_Href_v1(String label, String href, boolean... newBlank) {
		return addMenuItem(label, Events.ON_CLICK, (Event e) -> ZKR.redirectToPage(href, ARG.isDefEqTrue(newBlank)));
	}

	public Pare<Menuitem, Menupopup0> addMI_Href_v2(String label, String href, boolean... newBlank) {
		return addMenuItem(label, Events.ON_CLICK, new RedirectHrefEvent(href, ARG.isDefEqTrue(newBlank)));
	}

	public Pare<Menuitem, Menupopup0> addMI_Href(String label, String href, String src_image) {
		return addMenuItemImpl(label, Events.ON_CLICK, href, src_image, null);
	}

	public Pare<Menuitem, Menupopup0> addMenuItem(String label, String event, EventListener eventListener) {
		return addMenuItemImpl(label, event, null, null, eventListener);
	}

	private Pare<Menuitem, Menupopup0> addMenuItemImpl(String label, String event, String href, String src, EventListener eventListener) {
		IT.notBlank(label, "set label for menu item");
		Menuitem menuItem = src == null ? new Menuitem(label) : new Menuitem(label, src);
		if (eventListener != null) {
			menuItem.addEventListener(event, eventListener);
		}
		if (href != null) {
			menuItem.setHref(href);
		}
		addMI_Href(menuItem);
		return Pare.of(menuItem, this);
	}

	public Menuitem addMI_Href(String label) {
		Menuitem menuItem = new Menuitem(label);
		addMI_Href(menuItem);
		return menuItem;
	}

	public void addMI_Href(Menuitem menuItem) {
		getContextMenupopup().appendChild(menuItem);
	}

	public void addMenu(Menupopup menuItem) {
		getContextMenupopup().appendChild(menuItem);
	}

	private Menupopup getContextMenupopup() {
		return this;
	}

	public void add_______(boolean... ifNotExist) {
		Menupopup contextMenupopup = getContextMenupopup();
		if (ARG.isDefEqTrue(ifNotExist)) {
			Component last = ARRi.last(contextMenupopup.getChildren(), null);
			if (last != null && last instanceof Menuseparator) {
				return;
			}
		}
		Menuseparator separator = new Menuseparator();
		contextMenupopup.appendChild(separator);
	}


	public Menupopup0 addInnerMenu(String label, boolean... skipAppend) {
		return addInnerMenu0(label, skipAppend).val();
	}

	public Pare<Menu, Menupopup0> addInnerMenu0(String label, boolean... skipAppend) {
		Menu iMenu = new Menu(label);
		if (ARG.isDefEqTrue(skipAppend)) {
			//skip
		} else {
			appendChild(iMenu);
		}
		Menupopup0 iMenuPopup = new Menupopup0();
		iMenu.appendChild(iMenuPopup);
		return Pare.of(iMenu, iMenuPopup);
	}

	public void addMI_UploadTo(String label, Path formPath, boolean isImg) {
		addMenuItem(label, new UploaderFileOrPhotoEvent(formPath.toString(), isImg, null));
	}

	public void addMI_UploadTo(String label, Path formPath, boolean isImg, Function<String, Boolean> saveCallback) {
		addMenuItem(label, new UploaderFileOrPhotoEvent(formPath.toString(), isImg, saveCallback));
	}

	public void addMI_Download(String label, Path file) {
		addMenuItem(label, (Event e) -> ZKR.download(file));
	}

	public void addMI_DeleteFile_WithSec(String label, Path file, boolean... isFormPathComponent_orPage_orNot) {
		if (!UFS.exist(file)) {
			return;
		}
		boolean editorAdminOwner = Sec.isEditorAdminOwner();
		if (!editorAdminOwner) {
			return;
		}
		addMenuItem(label, new RemoveFileWithConfirmation_SerializableEventListener(file.toString(), (e) -> {
			if (ARG.isDefEqTrue(isFormPathComponent_orPage_orNot)) {
				NodeFileTransferMan.checkEmptyDir_Sd3_Page_andRemove(AFCC.getPageOfNodeFileCom(file));
			} else if (ARG.isDefEqFalse(isFormPathComponent_orPage_orNot)) {
				NodeFileTransferMan.checkEmptyDir_Sd3_Page_andRemove(file);
			}
			ZKR.restartPage();
		}));
	}

	public void addMI_DeleteNode_WithSec(String label, NodeDir nodeDir) {
		boolean editorAdminOwner = Sec.isEditorAdminOwner();
		if (!editorAdminOwner) {
			return;
		}
		EventListener listen = (SerializableEventListener) event -> {
			FunctionV1<Boolean> func = (rslt) -> {
				if (rslt) {
					nodeDir.fsMan().deleteItem();
					RSPath.toPlanPage_Redirect(nodeDir.sdn().keyStr(), nodeDir.sdn().valStr());
				}
			};
			String title = X.f(SYMJ.FILE_BASKET_MAN + " Delete item '%s'", nodeDir.nodeName());
			ZKI_Messagebox.showMessageBoxBlueYN(title, title, func);
		};
		addMenuItem(label, listen);
	}

	public void addMI_SESSSION_BOOLATTR(String propKey, Boolean defaultValue, Boolean restart) {
		Boolean existVl = MAP.getAsBool(ZKSession.getSessionAttrsMap(), propKey, null);
		if (existVl == null && defaultValue != null) {
			existVl = defaultValue;
			ZKSession.getSessionAttrsMap().put(propKey, existVl);
		}
		AtomicReference<Pare<Menuitem, Menupopup0>> ref = new AtomicReference<>();
		ref.set(addMenuItem(propKey + SYMJ.GLOB + existVl, e -> {
			Menuitem key = ref.get().key();
			Map<String, Object> attrs = ZKSession.getSessionAttrsMap();
			Boolean vl = MAP.getAsBool(attrs, propKey, null);
			attrs.put(propKey, vl == null ? vl = true : (vl = !vl));
			key.setLabel(propKey + SYMJ.GLOB + vl);
			if (restart) {
				ZKR.restartPage();
			}
		}));
	}

	public void addMI_DeleteFile(String pathStr, DefAction successCallback_orNull) {
		addMI_Href(RemoveFileWithConfirmation_SerializableEventListener.toMenuItemComponent(pathStr, successCallback_orNull));
	}

	public void addMI_RenameFile_Cfrm(String pathStr, FunctionV1 successCallback_orNull, boolean... withRenameChildsDir) {
		if (!UFS.exist(pathStr)) {
			return;
		}
		addMI_Href(Tbx_RenameFile_CfrmSerializableEventListener.toMenuItemComponent(pathStr, successCallback_orNull, ARG.isDefEqTrue(withRenameChildsDir)));
	}

	public void addMI_RenameFileDirect(Path path, FunctionV1 successCallback_orNull, boolean... withRenameChildsDir) {
		if (!UFS.exist(path)) {
			return;
		}
		SerializableEventListener eventListener = (e) -> {
			Div0 div = ARG.isDefEqTrue(withRenameChildsDir) ? RenameFileTextbox.buildComsForChildsDir(path, successCallback_orNull) : Div0.of(RenameFileTextbox.buildCom(path, successCallback_orNull));
			div._modal()._showInWindow();
		};
		addMenuItem(SYMJ.EDIT_PENCIL + " Rename " + UF.fn(path), eventListener);
	}


	public void addMI_MovePageNode_WithSec(NodeDir nodeItem) {
		boolean editorAdminOwner = Sec.isEditorAdminOwner();
		if (editorAdminOwner) {
			return;
		}

		add_______();

		AtomicReference<Window> winRef = new AtomicReference<>();

		Sd3DdChoicer child = new Sd3DdChoicer() {
			@Override
			public void onChoicePath(String sd3) {
				FunctionV1<Boolean> func = (rslt) -> {
					if (rslt) {
						nodeItem.fsMan().moveItemToSd3(sd3);
						RSPath.toPlanPage_Redirect(sd3, nodeItem.sdn().valStr());
//															ZKL.alert("Move %s ot %s", srcCom, dstCom);
					}
				};
				String title = X.f("Move item '%s'", nodeItem.nodeName());
				String message = X.f("Move to domain '%s'", sd3);
				ZKI_Messagebox.showMessageBoxBlueYN(title, message, func);
				winRef.get().onClose();
			}
		};
		addMenuItem("Move item", (Event e) -> {
			Window window = child._modal()._showInWindow();
			winRef.set(window);
		});
		addMI_DeleteNode_WithSec(SYMJ.FILE_BASKET_MAN + " Delete Item", nodeItem);
	}

	public void addMeI_CopyCurlBashScriptToClipboard_I_Bash(String formName) {
		if (Sec.isOwner()) {
			String label = SYMJ.CLIPBOARD + " Copy To Clipboard - Curl Bash Script";
			String url = new LocalNoteApi().zApiUrl.GET_toItem_With(ItemPath.of(SpVM.get().sdn0(), formName));
			String js = X.f("copyToClb('curl -s ' + '" + url + "'  + ' | bash')", NodeApiChars.UP + "/" + formName);
//			String js = X.f("copyToClb('curl -s ' + window.location.protocol + '//' +window.location.host + '/' + pathname(0) + '/%s' + ' | bash')", NodeApiChars.UP + "/" + formName);
			addMenuItem(label, (Event) -> ZKJS.eval(js));
//			addMenuitem(label, (Event) -> ZKJS.eval("copyToClb('curl -s ' + window.location.protocol + '//' +window.location.host + '/' + pathname(0) + '/%s' + ' | bash')", NodeApiChars.UP + "/" + formName));
		}
	}

	public void addMI_Tbx2_Cfrm(String label, String[] initValues, String[] placeholders, Function2<String, String, Object> successCallback) {
		Mi miNewNotes = Tbx2_CfrmSerializableEventListener.toMenuItemComponent(label, label, initValues, placeholders, successCallback);
		addMI_Href(miNewNotes);
	}

	public void addMI_OpenDirView(String label, Path dir) {
		addMenuItem(label, e -> SimpleDirView.openWithSimpleMenu(dir));
	}

}
