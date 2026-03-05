package zk_radio.core;

import lombok.SneakyThrows;
import mpc.arr.S_;
import mpc.exception.FIllegalStateException;
import mpc.fs.UF;
import mpc.fs.ext.GEXT;
import mpc.str.sym.SYMJ;
import mpu.IT;
import mpu.core.ARRi;
import mpu.pare.Pare;
import mpu.pare.PareEntry;
import mpu.str.JOIN;
import mpu.str.SPLIT;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Window;
import zk_com.base_ctr.Menupopup0;
import zk_form.notify.ZKI;
import zk_notes.node.NodeDir;
import zk_os.coms.SpaceType;
import zk_os.core.Sdn;
import zk_page.ZKME;
import zk_page.index.ItemDdChoicer;
import zk_page.index.RSPath;
import zk_radio.ZkAudio;
import zk_radio.model.AUM;
import zk_radio.walker.NodePlaylist;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class PlayMenu {

	public static final String PFX_MI_PL = "** ";
	public static final String MM_KEY = "mm";
	public static final String MM_MNGMENU = "mngmenu";
	public static final String MM_PLMENU = "plmenu";
	public static final String MM_CURLIST = "curpl";

	public static ZRadioView getZRadio(Menupopup0 menu) {
		String attribute = (String) menu.getAttribute(MM_KEY);
		switch (attribute) {
			case MM_KEY:
				return (ZRadioView) menu.getParent();

			case MM_CURLIST:
			case MM_MNGMENU:
			case MM_PLMENU:
				Menu playMenu = (Menu) menu.getParent();
				Menupopup0 menupop = (Menupopup0) playMenu.getParent();
				checkMenu(menupop, MM_KEY);
				return (ZRadioView) menupop.getParent();

		}
		throw new FIllegalStateException("Not found zradio");
	}

	public static PlayLb getPlayBt(Menupopup0 menu) {
		return (PlayLb) getZRadio(menu).getChildren().stream().filter(c -> c instanceof PlayLb).findFirst().get();
	}


	private static void checkMenu(Menupopup0 menupop, String mmKey) {
		IT.state(mmKey.equals(menupop.getAttribute(MM_KEY)), "except menu [%s]", mmKey);
	}

	public void buildMenu(Menupopup0 playMenu) {

		playMenu.setAttribute(MM_KEY, MM_KEY);

		Sdn sdn = Sdn.get();

		applyContolMenu(playMenu);

		playMenu.add_______();

		applyShowPlaylist(playMenu);

//		playMenu.add_______();
//		Menupopup0 menuSetPlaylist = playMenu.addInnerMenu("Set playlist..");
//		applySetPlayMenu(sdn, menuSetPlaylist);

		playMenu.add_______();

		Predicate<Path> nodeFilterWithPlays = p -> NodePlaylist.of(sdn, p, null) != null;
//		Predicate<Path> nodeFilter = p -> GEXT.AUDIO.has(p);//Show all
//		Predicate<Path> nodeFilter = null;//Show all
		playMenu.addMI(SYMJ.PLUS + " Add any .. ", e -> {

			AtomicReference<Window> itemsRef = new AtomicReference();
			ItemDdChoicer itemDdChoicer = new ItemDdChoicer() {
				@SneakyThrows
				@Override
				public void onChoiceItem(Event onItemSubmitEvent, String plane, String page, Map<NodeDir, Boolean> items) {

					NodePlaylist nodePlaylist = AUM.current_NodePlaylist();

					ZkAudio.showInfoActivePlaylist(nodePlaylist);

					Map rslt = items.entrySet().stream().filter(n -> n.getValue()) //
							.map(n -> {
								Boolean isAdded = nodePlaylist.addPlaylistAsNode(n.getKey());
								return PareEntry.of(n.getKey(), isAdded);
							}) //
							.collect(Collectors.toMap(k -> k.key(), v -> v.getValue()));

					if (L.isInfoEnabled()) {
						L.info("Play '{}' list updated, {}", nodePlaylist.nodeName(), rslt);
					}


					ZKME.anyWithBtSave(nodePlaylist.getPlaylistFile(), false, true);

					Path first = ARRi.first(nodePlaylist.getPlaylistFiles(true).processPlaylist());

					AUM.get().set_PLAY(first.toString());

					itemsRef.get().onClose();
				}
			}.withFilterItems(nodeFilterWithPlays);

			Window window = itemDdChoicer.openDefaultModalWindow("Choice item");
			itemsRef.set(window);

		});

		playMenu.add_______();

		playMenu.addMI_SESSSION_BOOLATTR(ZRadioView.SK_OPEN_PLAYER, false, false);


	}


	public static void applySetPlayMenu(Sdn sdn, Menupopup0 plMenu) {
		plMenu.setAttribute(MM_KEY, MM_PLMENU);
		Set<Path> ls = SpaceType.NODES.lsView(sdn);
		ls.forEach(playlistNodeDir -> {
			String miLbl = AUM.wrapLbPl(NodeDir.ofDir(sdn, playlistNodeDir).nodeId());
			plMenu.addMI(miLbl, e -> {
				String nodeId = NodeDir.ofDir(sdn, playlistNodeDir).nodeId();
				AUM.get().set_PLAYLIST(nodeId);
				ZKI.infoAfterPointerInfo("Current playlist was updated with playlist from [%s]", nodeId);
			});
		});
	}

	private static void applyMenuCurrent(Menupopup0 curMenu) {
		curMenu.setAttribute(MM_KEY, MM_CURLIST);

		String[] play = AUM.get().getPlayState(null);
		String playlist = play[1];
		if (playlist != null) {
			curMenu.addMI(playlist, e -> getZRadio(curMenu).onNextAudioAuto(true));
		}

//		curMenu.add_______();

		Set<String> playlistInjected = AUM.currentPlaylistSetFiles_Compiled();
		playlistInjected.forEach(i -> {
			Pare<Menuitem, Menupopup0> menuitemMenupopup0Pare = curMenu.addMI(SYMJ.FILE_MUSIC + " " + UF.fn(i), e -> {
				Menuitem target = (Menuitem) e.getTarget();
				String path = (String) target.getAttribute("p");
				ZRadioView zRadio = getZRadio(curMenu);
				zRadio.stopAll();
				zRadio.onNextAudio(path, true);
			});
			menuitemMenupopup0Pare.key().setAttribute("p", i);
		});
	}


	private void applyContolMenu(Menupopup0 playMenu) {
		playMenu.addMI(SYMJ.TRACK_NEXT + " Next", e -> {
			ZRadioView zRadio = getZRadio(playMenu);
			Zmp3 zmp3First = zRadio.getZmp3First();
			ZRadioView.onSearchNextPlayItem(zmp3First, true);
		});
		playMenu.addMI(SYMJ.TRACK_PREV + " Prev", e -> {
			ZRadioView zRadio = getZRadio(playMenu);
			Zmp3 zmp3First = zRadio.getZmp3First();
			ZRadioView.onSearchNextPlayItem(zmp3First, false);
		});
	}

	public void applyManagePlaylist(Menupopup0 mngMenu) {

		mngMenu.setAttribute(MM_KEY, MM_MNGMENU);

		AUM aum = AUM.get();
		String[] play = aum.getPlayState(null);
		if (play != null) {
			mngMenu.addMI(AUM.wrapLbTrackWith(play), e -> {

			});
		}

//		mngMenu.add_______();
//		mngMenu.addMI_Cfm1(SYMJ.PLUS + " Add", "Playlist name", i -> {
//			AUM auc = AUP.addPlaylist(i);
//			ZKI.infoAfterPointerInfo("Playlist [%s] added", i);
//			mngMenu.addMI(PFX_MI_PL + auc.getCurrentPlaylistName(), e -> {
//
//				Menupopup0 playMenuPrev = getPlayMenu(mngMenu);
//				PlayLb playBt = getPlayBt(playMenuPrev);
//				playMenuPrev.detach();
//				Menupopup0 playMenuNew = playBt.getOrCreateMenupopup((HtmlBasedComponent) playBt.getParent());
//				new PlayMenu().buildMenu(playMenuNew);
//
//			});
//			return null;
//		});
//		mngMenu.addMI_Cfm1(SYMJ.MINUS + " Remove", "Playlist name", i -> {
//			if (AUP.rmPlaylist(i)) {
//				ZKI.infoAfterPointerInfo("Playlist [%s] removed", i);
//				mngMenu.getChildren().stream().filter(c -> {
//					if (!(c instanceof Menuitem)) {
//						return false;
//					}
//					Menuitem it = (Menuitem) c;
//					return it.getLabel().startsWith(PFX_MI_PL + i);
//				}).forEach(c -> c.detach());
//			} else {
//				ZKI.infoAfterPointer("Not found playlist - " + i, ZKI.Level.WARN);
//			}
//			return null;
//		});

//		mngMenu.add_______();

//		List<AUC> allPlaylistModels = AUP.getAllPlaylistModels();
//
//		allPlaylistModels.forEach(pl -> {
//			mngMenu.addMI(PFX_MI_PL + pl.getCurrentPlaylistName(), e -> {
//
//			});
//		});
	}

	private static void applyShowPlaylist(Menupopup0 playMenu) {

		playMenu.addMI(SYMJ.MENU_LINES3 + " Show current playlist..", e -> {

			ZRadioView zRadio = getZRadio(playMenu);

			String bi1 = "<b>Track</b></br>" + zRadio.getTooltiptext();
			String bi2 = "</br></br><b>Node Playlist</b>";
			NodePlaylist nodeDir = AUM.current_NodePlaylist();
			String lines = nodeDir.getNode().readNodeDataStr(null);
			lines = lines != null ? lines : JOIN.allByNL(AUM.currentPlaylist_NodeDir_LsNames(GEXT.AUDIO));
			List<String> playlistDataLines = SPLIT.allByNL(lines);
			String bi3 = "<pre>" + JOIN.allByNL(playlistDataLines) + "</pre> </br>";

			String bi4 = "<b>Playlist</b>";
//			Set<String> playlistInjected = AUM.currentPlaylistSetFilesCompiled();
			Set<String> playlistInjected = S_.mapToLinkedSet(AUM.currentPlaylistFiles_Compiled(), UF::fn);
			String bi5 = "<pre>" + JOIN.allByNL(playlistInjected) + "</pre> ";

			ZKI.infoEditorHtmlView((Object) "Playlist", bi1 + bi2 + bi3 + bi4 + bi5, new String[]{"80%", "300%"});
		});

		playMenu.add_______();

		playMenu.addMI(SYMJ.LOGOUT + " Go to playlist page..", e -> {
			NodeDir node = AUM.currentPlaylist_NodeDir();
			RSPath.toPageItem_Redirect(node.nodeID());
		});

	}


	private Menupopup0 getPlayMenu(Menupopup0 mngMenu) {
		Menupopup0 playMenu = (Menupopup0) mngMenu.getParent();
		IT.state(MM_PLMENU.equals(playMenu.getAttribute(PlayMenu.MM_KEY)), "except [%s] menu", MM_PLMENU);
		return playMenu;
	}
}
