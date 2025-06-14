package zk_notes.control;

import mpu.IT;
import mpu.X;
import mpc.fs.path.UPathToken;
import mpu.str.RANDOM;
import mpu.pare.Pare3;
import mpc.ui.UColorTheme;
import org.jetbrains.annotations.NotNull;
import zk_com.base.Bt;
import zk_com.base.Ln;
import zk_com.base.Tbx;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_com.base_ctr.Span0;
import zk_com.core.IReRender;
import zk_com.core.IZCom;
import zk_form.events.DefAction;
import zk_form.notify.ZKI;
import zk_page.ZKC;
import zk_page.ZKME;
import zk_page.ZKS;
import zk_notes.ANI;
import zk_notes.AppNotesCore;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;

public class NotesPageHeader extends Div0 implements IReRender {

	public static void openSimple() {
		ZKC.getFirstWindow().appendChild(new NotesPageHeader());
	}

	public static NotesPageHeader rerender(NotesPageHeader... defRq) {
		NotesPageHeader first = removeMeFirst(defRq);
		if (first != null) {
			first.rerender(true);
		}
		return first;
	}

	public static NotesPageHeader removeMeFirst(NotesPageHeader... defRq) {
		return IZCom.removeMeFirst(NotesPageHeader.class, true, defRq);
	}

	public static void addPhrases(String keyPhrases, String phrases) {
		addPhrases(null, IT.NN(keyPhrases), phrases);
	}

	public static void addPhrases(String prevKeyPhrases, String keyPhrases, String phrases) {
		if (!keyPhrases.equals(prevKeyPhrases)) {
			AppNotesCore.TREE_QUICK_HISTORY__PAGED().removeByKeyIfExist(prevKeyPhrases);
		}
		AppNotesCore.TREE_QUICK_HISTORY__PAGED().put(keyPhrases, phrases);
		rerender(null);
	}

	public static void addLogView(String file) {
		Path path = Paths.get(file);
		addLogView(file, UPathToken.pathItems(path, -2, path).toString());
	}

	public static void addLogView(String file, String name) {
		AppNotesCore.TREE_QUICK_HISTORY__PAGED().put(file, name);
		rerender(null);
	}

	@Override
	protected void init() {
		super.init();

		appendCenter_Panel();

		appendLeft_LastFilesPanel();

		appendRight_StopPhrasesPanel();

//		ZKS.BGCOLOR(this, "#ecebbd");
//		ZKS.BGCOLOR(this, "#87ceeb");
//		ZKS.BGCOLOR(this, "#b0e0e6");
		ZKS.BGCOLOR(this, "#ffc87c");
//		ZKS.BGCOLOR(this, "#ffa07a");
//		ZKS.BGCOLOR(this, "#f08080");

		ZKS.POSITION(this, "fixed");
		ZKS.WIDTH(this, 100.0);
		ZKS.HEIGHT(this, 30);
		ZKS.OPACITY(this, 0.5);
		ZKS.MARGIN(this, "-30px 0 0 0");

	}

	private void appendRight_StopPhrasesPanel() {
		Div0 leftWordList = (Div0) appendDiv().float0(false);

		List<Pare3<String, String, String>> pares = AppNotesCore.TREE_QUICK_HISTORY__PAGED().getModelsAsPare3();
		for (Pare3<String, String, String> phrases : pares) {

			String keyShortName = phrases.key();
			String stopPhrasesString = phrases.val();

			Tbx tbxKey = new Tbx(keyShortName);
			DefAction act = (e) -> {
				AppNotesCore.TREE_QUICK_HISTORY__PAGED().removeByKeyIfExist(keyShortName);
				ZKC.removeParentWindowForChild(e);
				NotesPageHeader.rerender(null);
			};

			Bt btRmList = (Bt) new Bt("Remove this list").onDefaultAction(act).margin("0 0 0 100px").bgcolor(UColorTheme.RED[0]);
			DefAction defAction = e -> ZKME.openEditorWithBtSave(() -> stopPhrasesString, newSaveCallback(tbxKey), Span0.of(tbxKey, btRmList), false);
			Ln ln = leftWordList.appendLn(defAction, ANI.LIST + keyShortName).decoration_none();

			ln.title(stopPhrasesString);

			ZKS.COLOR(ln, UColorTheme.RED[1]);

		}

		//
		//

		leftWordList.appendChild(newBtAddNewPhrases());

	}

	private Ln newBtAddNewPhrases() {
		Ln ln = (Ln) new Ln(ANI.PLUS).decoration_none().padding(10).title("new stop phrases list");
		Tbx tbxKey = (Tbx) new Tbx("phrases-" + RANDOM.ALPHA(3)).placeholder("set key phrases");
		DefAction defAction = e -> ZKME.openEditorWithBtSave(() -> "new-stop-phrase", newSaveCallback(tbxKey), tbxKey, false);
		ln.onDefaultAction(defAction);
		return ln;
	}

	@NotNull
	private static Function<String, Boolean> newSaveCallback(Tbx tbxKey) {
		String prev = tbxKey.getValue();
		Function<String, Boolean> saveCallback = newString -> {
			String keyPhrases = tbxKey.getValue();
			if (X.empty(keyPhrases)) {
				ZKI.alert("set key for phrases list");
				return false;
			}
			addPhrases(prev, keyPhrases, newString);
			return true;
		};
		return saveCallback;
	}

	private void appendCenter_Panel() {

		Div0 center = appendDiv();
		ZKS.WIDTH(center, 100.0);
		ZKS.ABSOLUTE(center);
		ZKS.TEXT_ALIGN(center, 0);
		ZKS.ZINDEX(center, -1);
//		ZKS.MARGIN(center, "-15px 0 0 0");
		ZKS.PADDING(center, "5px 0 0 0");

		{
//			Function<String, Boolean> saveCallback = s -> {
//				Sys.say("save");
//				return true;
//			};
//			DefAction defAction = e -> EventShowModalEditor.openEditorWithContent(() -> "content", saveCallback, false);
//			center.appendLn(defAction, ALI.QUICKVIEW).td_none();
		}

//		center.appendLn((DefAction) e -> ZKS.toggleDnoneFirst(StandsControlPanel.class, true, null), "Stands").decoration_none().padding("0 5px");
//		center.appendLn((DefAction) e -> ZKS.toggleDnoneFirst(NotesPageHeaderProps.class, true, null), "Config").decoration_none().padding("0 5px");

//		center.appendLn((DefAction) e -> NotesTbxWin.openFormSingly("Notes", false), "Notes").decoration_none().padding("0 5px");

	}

	private void appendLeft_LastFilesPanel() {

//		appendLn((DefAction) e -> {
//			AppNotesCore.TREE_QUICK_HISTORY().clear();
//			NotesPageHeader.rerender(null);
//		}, ANI.BASKET).decoration_none().float0(true).padding(5).title("clear quick files");

		//
		//

		Span0 center = appendSpan();

		List<Pare3<String, String, String>> pares = AppNotesCore.TREE_QUICK_HISTORY__PAGED().getModelsAsPare3();
		for (Pare3<String, String, String> memLink : pares) {

			String keyFile = memLink.key();
			String valName = memLink.val();

//			Ln lnToDirOrFile;
//			switch (eft) {
//				case FILE: {
//					lnToDirOrFile = center.appendLn((DefAction) e -> LogFileView.openSingly(keyFile), ALI.LOGVIEW + valNameShort);
//					Menupopup0 menu = Menupopup0.createMenupopup(this, lnToDirOrFile, null);
//					ALM.applyLogFile(menu, Paths.get(keyFile));
//					addMI_RemoveFromPanel(menu, keyFile);
//				}
//				break;
//				case DIR: {
//					lnToDirOrFile = center.appendLn((DefAction) e -> LogDirView.openSingly(keyFile), ALI.DIRVIEW + valNameShort);
//					Menupopup0 menu = lnToDirOrFile.getOrCreateMenupopup(this);
//					addMI_RemoveFromPanel(menu, keyFile);
//					break;
//				}
//				default:
//					throw new WhatIsTypeException(eft);
//			}

//			lnToDirOrFile.title(keyFile);

//			ZKS.COLOR(lnToDirOrFile, UColorTheme.RED[0]);

		}
	}

	private static void addMI_RemoveFromPanel(Menupopup0 menu, String keyFile) {
		menu.addMI("Remove from panel", e -> AppNotesCore.TREE_QUICK_HISTORY__PAGED().removeByKeyIfExist(keyFile));
	}

}
