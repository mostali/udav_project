package zklogapp.header;

import mpc.exception.WhatIsTypeException;
import mpc.fs.fd.EFT;
import mpc.fs.path.UPathToken;
import mpu.str.STR;
import mpu.pare.Pare3;
import mpc.ui.ColorTheme;
import zk_com.base.Ln;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_com.base_ctr.Span0;
import zk_com.core.IReRender;
import zk_form.control.QuickProfileListPanel;
import zk_form.events.DefAction;
import zk_notes.ANI;
import zk_page.ZKC;
import zk_page.ZKS;
import zk_notes.node.NodeDir;
import zk_notes.factory.NFOpen;
import zklogapp.*;
import zklogapp.logview.LogFileView;
import zklogapp.merge.LogDirView;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class LogMergerPageHeader extends Div0 implements IReRender {

	public static LogMergerPageHeader rerender(LogMergerPageHeader... defRq) {
		LogMergerPageHeader first = removeMeFirst(defRq);
		if (first != null) {
			first.rerender(true);
		}
		return first;
	}

	public static LogMergerPageHeader removeMeFirst(LogMergerPageHeader... defRq) {
		return ZKC.removeMeFirst(LogMergerPageHeader.class, true, defRq);
	}

	public static void addLogView(String file) {
		Path path = Paths.get(file);
		addLogView(file, UPathToken.pathItems(path, -2, path).toString());
	}

	public static void addLogView(String file, String name) {
		AppLogCore.TREE_QUICK_DIR_HISTORY.put(file, name);
		rerender(null);
	}

	@Override
	protected void init() {
		super.init();

		appendCenter_Panel();

		appendLeft_LastFilesPanel();

		appendChild(new QuickProfileListPanel(AppLogCore.TREE_EXCLUDE_PHRASES) {
			@Override
			public void rerenderParent() {
				rerender(null);
			}
		});

		ZKS.BGCOLOR(this, ColorTheme.BLACK[0]);
		ZKS.POSITION(this, "fixed");
		ZKS.WIDTH(this, 100.0);
		ZKS.HEIGHT(this, 30);
		ZKS.OPACITY(this, 0.5);
		ZKS.MARGIN(this, "-30px 0 0 0");

	}


	private void appendCenter_Panel() {

		Div0 center = appendDiv();
		ZKS.WIDTH(center, 100.0);
		ZKS.ABSOLUTE(center);
		ZKS.TEXT_ALIGN(center, 0);
		ZKS.ZINDEX(center, -1);
		ZKS.PADDING(center, "5px 0 0 0");

//		center.appendLn((DefAction) e -> LogDirView.openSingly(AppLogProps.APR_TASKS_DIR.getValueOrDefault()), "TaskDir").decoration_none().padding("0 5px");
		center.appendLn((DefAction) e -> LogDirView.openSingly("logs"), "TaskDir").decoration_none().padding("0 5px");

//		center.appendLn((DefAction) e -> ZKS.toggleDnoneFirst(StandsControlPanel.class, true, null), "Stands").decoration_none().padding("0 5px");
		center.appendLn((DefAction) e -> ZKS.toggleDnoneFirst(AppLogSettingsPanel.class, true, null), "Config").decoration_none().padding("0 5px");
//		center.appendLn((DefAction) e -> NodeFactory.openFormIdentitySinglyAsWin0(NodeDir.ofNodeName("MyNote", ppi().sdn()), false, false), "MyNote").decoration_none().padding("0 5px");
		center.appendLn((DefAction) e -> NFOpen.openFormRequired(NodeDir.ofNodeName(ppi().sdnAny(), "MyNote")), "MyNote").decoration_none().padding("0 5px");
//		center.appendLn((DefAction) e -> ZKS.toggleDnoneFirst(NotesWin.class, false, null), "Notes").td_none().padding("0 5px");

	}

	private void appendLeft_LastFilesPanel() {

		if (!AppLogCore.TREE_QUICK_DIR_HISTORY.isEmptyDb()) {
			appendLn((DefAction) e -> {
				AppLogCore.TREE_QUICK_DIR_HISTORY.truncateTable();
				rerender(null);
			}, ALI.BASKET).decoration_none().float0(true).padding(5).title("clear quick files");
		}

		//
		//

		Span0 center = appendSpan();

		List<Pare3<String, String, String>> pares = AppLogCore.TREE_QUICK_DIR_HISTORY.getModelsAsPare3();
		for (Pare3<String, String, String> memLink : pares) {

			String keyFile = memLink.key();
			String valName = memLink.val();
			String valNameShort = STR.toStringSE(valName, 3, valName);
			EFT eft = EFT.of(keyFile, null);
			if (eft == null) {
				AppLogCore.TREE_QUICK_DIR_HISTORY.removeByKeyIfExist(keyFile);
				continue;
			}
			Ln lnToDirOrFile;
			switch (eft) {
				case FILE: {
					lnToDirOrFile = center.appendLn((DefAction) e -> LogFileView.openSingly(keyFile), ALI.LOGVIEW + valNameShort);
					Menupopup0 menu = Menupopup0.createMenupopup(this, lnToDirOrFile, null);
					ALM.applyLogFileWithUtils(menu, Paths.get(keyFile));
					addMI_RemoveFromPanel(menu, keyFile);
				}
				break;
				case DIR: {
					lnToDirOrFile = center.appendLn((DefAction) e -> LogDirView.openSingly(keyFile), ANI.DIRVIEW + valNameShort);
					Menupopup0 menu = lnToDirOrFile.getOrCreateMenupopup(this);
					addMI_RemoveFromPanel(menu, keyFile);
					break;
				}
				default:
					throw new WhatIsTypeException(eft);
			}

			lnToDirOrFile.title(keyFile);

			ZKS.COLOR(lnToDirOrFile, ColorTheme.RED[0]);

		}
	}

	private static void addMI_RemoveFromPanel(Menupopup0 menu, String keyFile) {
		menu.addMI("Remove from panel", e -> AppLogCore.TREE_QUICK_DIR_HISTORY.removeByKeyIfExist(keyFile));
	}

}
