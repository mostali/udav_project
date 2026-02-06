package zk_com.base_ctr;

import lombok.extern.slf4j.Slf4j;
import mpc.env.APP;
import mpc.fs.UF;
import mpc.log.L;
import mpc.map.MAP;
import mpc.str.sym.SYMJ;
import mpe.rt.SLEEP;
import mpe.rt.Thread0;
import mpe.rt_exec.ZipExecEE;
import mpu.IT;
import mpu.SysExec;
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
import zk_com.base.Dd;
import zk_com.base.Mi;
import zk_com.editable.RenameFileTextbox;
import zk_com.uploader.DDFileUploader;
import zk_com.uploader.NativeFileUploaderComposer;
import zk_form.events.*;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Quest;
import zk_notes.ANI;
import zk_notes.apiv1.client.NoteApi0;
import zk_form.dirview.DirView0;
import udav_net.apis.zznote.ItemPath;
import zk_notes.control.NotesSpace;
import zk_notes.fsman.NodeFileTransferMan;
import zk_notes.node_state.ObjState;
import zk_notes.node_state.impl.PageState;
import zk_os.AppZosProps;
import zk_os.coms.AFCC;
import zk_os.AppZosBashExecView;
import zk_os.notify.NotifySrv;
import zk_os.sec.SecMan;
import zk_page.*;
import zk_page.behaviours.UploaderFileOrPhotoEvent;
import zk_notes.apiv1.NodeApiChars;
import zk_page.core.SpVM;
import zk_page.index.RSPath;
import zk_page.index.PlaneDdChoicer;
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
		return addMI(label, Events.ON_CLICK, (Event e) -> {
			ZKI.infoEditorDark(pathFile, type);
		});
	}

	public Pare<Menuitem, Menupopup0> addMenuitem_ExecFileTmp(String label, String cmdKey, String tmpFileData) {
		return addMI(label, e -> {
			Pare<Integer, List<String>> rslt = SysExec.exec_filetmp(cmdKey, tmpFileData, null, false);
			List outLines = rslt.val();
			List lines_Err_Out = ARR.mergeToList(ARR.as(cmdKey), SPLIT.allByNL(tmpFileData), ARR.as("-----------------------"), outLines);
			if (rslt.key() == 0) {
				ZKI.infoEditorDark(lines_Err_Out);
			} else {
				ZKI.alert(JOIN.allByNL(lines_Err_Out));
			}
		});

	}

	public Pare<Menuitem, Menupopup0> addMenuitem_ExecAnyScriptOS(String label, String placeholder) {
		return addMI_Cfm1(label, placeholder, AppZosBashExecView.funcExecCmdAndShowResult);
	}

	public Pare<Menuitem, Menupopup0> addMI_Cfm1(String label, String placeholder, Function<String, Object> hiv) {
		return addMI_Cfm1(label, placeholder, "", hiv);
	}

	public Pare<Menuitem, Menupopup0> addMI_Cfm1(String label, String placeholder, String initValue, Function<String, Object> hiv) {
		Mi menuItemComponent = Tbx_CfrmSerializableEventListener.toMenuItemComponent("", label, initValue, placeholder, hiv);
		addMI_Href_in_Self(menuItemComponent);
		return Pare.of(menuItemComponent, this);
	}

	public Pare<Menuitem, Menupopup0> addMI_EDITOR(String label, Supplier<String> contentSupplier) {
		return addMI(label, Events.ON_CLICK, (Event e) -> {
			ZKI.infoEditorDark(contentSupplier.get());
		});
	}

	public Pare<Menuitem, Menupopup0> addMI(String label, EventListener eventListener) {
		return addMI(label, Events.ON_CLICK, eventListener);
	}

	public Pare<Menuitem, Menupopup0> addMI_Href_v1(String label, String href, boolean... newBlank) {
		return addMI(label, Events.ON_CLICK, (Event e) -> ZKR.redirectToPage(href, ARG.isDefEqTrue(newBlank)));
	}

	public Pare<Menuitem, Menupopup0> addMI_Href_OpenWindow(String label, String href) {
		return addMI(label, Events.ON_CLICK, (Event e) -> ZKR.openWindow800_1200(href));
	}

	public Pare<Menuitem, Menupopup0> addMI_Href_v2(String label, String href, boolean... newBlank) {
		return addMI(label, Events.ON_CLICK, new RedirectHrefEvent(href, ARG.isDefEqTrue(newBlank)));
	}

	public Pare<Menuitem, Menupopup0> addMI_Href_in_Self(String label, String href, String src_image) {
		return addMenuItemImpl(label, Events.ON_CLICK, href, src_image, null);
	}

	public Pare<Menuitem, Menupopup0> addMI(String label, String event, EventListener eventListener) {
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
		addMI_Href_in_Self(menuItem);
		return Pare.of(menuItem, this);
	}

	public Menuitem addMI_SimpleLabel(String label) {
		Menuitem menuItem = new Menuitem(label);
		addMI_Href_in_Self(menuItem);
		return menuItem;
	}

	public void addMI_Href_in_Self(Menuitem menuItem) {
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

	public void addMI_UploadTo(String label, Path formPath) {
//		addMI(label, new UploaderFileOrPhotoEvent(formPath.toString(), isImg, null));
		addMI(label, e -> NativeFileUploaderComposer.doNativeUploadEvent(formPath, 1));
	}

	public void addMI_UploadDdTo(String label, Path formPath) {
		addMI(label, e -> DDFileUploader.open(formPath));
	}

	public void addMI_UploadTo(String label, Path formPath, boolean isImg, Function<String, Boolean> saveCallback) {
		addMI(label, new UploaderFileOrPhotoEvent(formPath.toString(), isImg, saveCallback));
	}

	public void addMI_Download(String label, Path file) {
		addMI(label, (Event e) -> ZKR.download(file));
	}

	public void addMI_DownloadAsZip(String label, Path dir) {
		addMI(label, (Event e) -> {
			Path dstZip = UF.newRenameName(dir, UF.fn(dir) + ".zip");
			ZipExecEE.zipFd(dir, dstZip, ZipExecEE.CreateZipOption.REPLACE);
			IT.isFileExist(dstZip, "Zip File not found '%s'", dstZip);
			ZKR.download(dstZip);
			Thread0.run("RemoverTmpFile_" + UF.fn(dstZip).hashCode(), () -> {
				SLEEP.sec(5);
				try {
					UFS.RM.fd(dstZip);
				} catch (Exception ex) {
					L.error("Happens error 'addMI_DownloadAsZip', add notify", ex);
					String msg = X.f("Error remove file '%s'", APP.relativizeAppFile(dstZip));
					NotifySrv.addNotify(msg, ex);
				}
			});
		});
	}

	public void addMI_DeleteFile_WithSec(String label, Path file, boolean... isFormPathComponent_orPage_orNot) {
		if (!UFS.exist(file)) {
			return;
		}
		boolean editorAdminOwner = SecMan.isAllowedEditPlane();
		if (!editorAdminOwner) {
			return;
		}
		addMI(label, new RemoveFileWithConfirmation_SerializableEventListener(file.toString(), (e) -> {
			if (ARG.isDefEqTrue(isFormPathComponent_orPage_orNot)) {
				NodeFileTransferMan.checkEmptyDir_Sd3_Page_andRemove(AFCC.getPageOfNodeFileCom(file));
			} else if (ARG.isDefEqFalse(isFormPathComponent_orPage_orNot)) {
				NodeFileTransferMan.checkEmptyDir_Sd3_Page_andRemove(file);
			}
			ZKR.restartPage();
		}));
	}

	public void addMI_DeleteNode_WithSec(String label, NodeDir nodeDir) {
		boolean editorAdminOwner = SecMan.isAllowedEditPlane();
		if (!editorAdminOwner) {
			return;
		}
		EventListener listen = (SerializableEventListener) event -> {
			FunctionV1<Boolean> func = (rslt) -> {
				if (rslt) {
					nodeDir.fsMan().deleteItem();
					RSPath.toPage_Redirect(nodeDir.sdnPare().keyStr(), nodeDir.sdnPare().valStr());
				}
			};
			String title = X.f(ANI.DELETE_ENTITY + " Delete item '%s'", nodeDir.nodeName());
			ZKI_Quest.showMessageBoxBlueYN(title, title, func);
		};
		addMI(label, listen);
	}

	public void addMI_SESSSION_BOOLATTR(String propKey, Boolean defaultValue, Boolean restart) {
		Map<String, Object> sMap = ZKSession.getSessionAttrsMap();
		Boolean existVl = MAP.getAsBool(sMap, propKey, null);
		if (existVl == null && defaultValue != null) {
			existVl = defaultValue;
			sMap.put(propKey, existVl);
		}
		AtomicReference<Pare<Menuitem, Menupopup0>> ref = new AtomicReference<>();
		ref.set(addMI(propKey + SYMJ.GLOB_RED + existVl, e -> {
			Menuitem key = ref.get().key();
			Map<String, Object> attrs = ZKSession.getSessionAttrsMap();
			Boolean vl = MAP.getAsBool(attrs, propKey, null);
			attrs.put(propKey, vl == null ? vl = true : (vl = !vl));
			key.setLabel(propKey + SYMJ.GLOB_RED + vl);
			if (restart) {
				ZKR.restartPage();
			}
		}));
	}

	public void addMI_PAGESTATE_BOOLATTR(String propKey, Boolean defaultValue, Boolean restart) {
		PageState pageState = PageState.get();
		Boolean existVl = (Boolean) pageState.getAs(propKey, Boolean.class, null);
		if (existVl == null && defaultValue != null) {
			existVl = defaultValue;
			pageState.set(propKey, existVl);
		}
		AtomicReference<Pare<Menuitem, Menupopup0>> ref = new AtomicReference<>();
		ref.set(addMI(propKey + SYMJ.GLOB_RED + existVl, e -> {
			Menuitem key = ref.get().key();
			Boolean vl = (Boolean) pageState.getAs(propKey, Boolean.class, null);
			pageState.set(propKey, vl == null ? vl = true : (vl = !vl));
			key.setLabel(propKey + SYMJ.GLOB_RED + vl);
			if (restart) {
				ZKR.restartPage();
			}
		}));
	}

	public void addMI_DeleteFile(String pathStr, DefAction successCallback_orNull) {
		addMI_Href_in_Self(RemoveFileWithConfirmation_SerializableEventListener.toMenuItemComponent(pathStr, successCallback_orNull));
	}

	public void addMI_RenameFile_Cfrm(String pathStr, FunctionV1 successCallback_orNull, boolean... withRenameChildsDir) {
		if (!UFS.exist(pathStr)) {
			return;
		}
		addMI_Href_in_Self(Tbx_RenameFile_CfrmSerializableEventListener.toMenuItemComponent(pathStr, successCallback_orNull, ARG.isDefEqTrue(withRenameChildsDir)));
	}

	public void addMI_RenameFileDirect(Path path, FunctionV1 successCallback_orNull, boolean... withRenameChildsDir) {
		if (!UFS.exist(path)) {
			return;
		}
		SerializableEventListener eventListener = (e) -> {
			Div0 div = ARG.isDefEqTrue(withRenameChildsDir) ? RenameFileTextbox.buildComsForChildsDir(path, successCallback_orNull) : Div0.of(RenameFileTextbox.buildCom(path, successCallback_orNull));
			div._modal()._showInWindow();
		};
		addMI(SYMJ.EDIT_PENCIL + " Rename " + UF.fn(path), eventListener);
	}

	public void addMI_Position(NodeDir nodeItem, boolean... com) {
		boolean editorAdminOwner = SecMan.isAllowedEditPlane();
		if (editorAdminOwner) {
			return;
		}

		add_______();

		addMI(SYMJ.MOVE + " Set Position - " + (ARG.isDefEqTrue(com) ? "Link" : "Form"), e -> {

			ObjState formState = ARG.isDefEqTrue(com) ? nodeItem.stateCom() : nodeItem.state();
			ObjState.Position position = formState.fields().get_POSITION(ObjState.Position.ABS);
			Dd dd = new Dd(position);
			dd.onOK(onChangeDd -> {
				formState.fields().set_POSITION((ObjState.Position) dd.getValueAsEnum(ObjState.Position.class));
				if (AppZosProps.APP_WEB_SYNC_RESTART_PAGE.getValueOrDefault(false)) {
					NotesSpace.rerenderFirst();
				} else {
					ZKR.restartPage();
				}
			});

			Div0.of(dd)._title("Set Fom/Link position")._closable()._modal()._showInWindow();

		});

	}

	public void addMI_MoveNode2Plane_WithSec(NodeDir nodeItem) {
		boolean editorAdminOwner = SecMan.isAllowedEditPlane();
		if (editorAdminOwner) {
			return;
		}

		add_______();

		AtomicReference<Window> winRef = new AtomicReference<>();

		PlaneDdChoicer child = new PlaneDdChoicer() {
			@Override
			public void onChoiceSd3(String sd3) {
				FunctionV1<Boolean> func = (rslt) -> {
					if (rslt) {
						nodeItem.fsMan().moveItemToSd3(sd3);
						RSPath.toPage_Redirect(sd3, nodeItem.sdnPare().valStr());
//															ZKL.alert("Move %s ot %s", srcCom, dstCom);
					}
				};
				String title = X.f("Move to plane '%s'", nodeItem.nodeName());
				String message = X.f("Move to plane '%s'", sd3);
				ZKI_Quest.showMessageBoxBlueYN(title, message, func);
				winRef.get().onClose();
			}
		};
		addMI("Move to other plane", (Event e) -> {
			Window window = child._modal()._showInWindow();
			winRef.set(window);
		});
		addMI_DeleteNode_WithSec(ANI.DELETE_ENTITY + " Delete Item", nodeItem);
	}

	public void addMeI_CopyCurlBashScriptToClipboard_I_Bash(String formName) {
		if (SecMan.isOwner()) {
			String label = SYMJ.CLIPBOARD + " Download&Execute via Curl Bash Script";
			String url = new NoteApi0().zApiUrl.GET_toItem(ItemPath.of(SpVM.get().sdn(), formName));
			String js = X.f("copyToClb('curl -s ' + '" + url + "'  + ' | bash')", NodeApiChars.UP + "/" + formName);
			addMI(label, (Event) -> ZKJS.eval(js));
		}
	}

	public void addMI_Tbx2_Cfrm(String label, String[] initValues, String[] placeholders, Function2<String, String, Object> successCallback) {
		Mi miNewNotes = Tbx2_CfrmSerializableEventListener.toMI(label, label, initValues, placeholders, successCallback);
		addMI_Href_in_Self(miNewNotes);
	}

	public Pare<Menuitem, Menupopup0> addMI_OpenDirView(String label, Path dir, boolean... mkdirsIfNotExist) {
		return addMI(label, e -> DirView0.openWithSimpleMenu(dir, mkdirsIfNotExist));
	}

}
