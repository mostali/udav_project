package zk_notes.search;

import lombok.Setter;
import lombok.SneakyThrows;
import mpc.exception.WhatIsTypeException;
import mpc.log.L;
import mpc.map.BootContext;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.str.STR;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.Bandpopup;
import zk_com.base.Lb;
import zk_com.base_ext.Bandbox0;
import zk_com.ext.Ddl;
import zk_com.win.EventShowFileComInModal;
import zk_form.dirview.FileView;
import zk_notes.factory.NFOpen;
import zk_notes.node.NodeDir;
import zk_notes.search.engine.NoteSearchEngine;
import zk_notes.search.engine.SearchEngine;
import zk_os.core.Sdn;
import zk_os.db.net.WebUsr;
import zk_os.sec.Sec;
import zk_os.sec.SecMan;
import zk_page.ZKCFinderExt;
import zk_page.ZKR;
import zk_page.ZKS;
import zk_page.index.RSPath;

import java.nio.file.Path;
import java.util.Collection;

public class NoteBandbox extends SearchBandbox<Object, NoteSearchEngine.SearchNoteMode> {

//	private static final Logger L = LoggerFactory.getLogger(NoteBandbox.class);

	public static NoteBandbox findFirst(NoteBandbox... defRq) {
		return ZKCFinderExt.findFirst_inPage0(NoteBandbox.class, true, defRq);
	}

	public static NoteBandbox of(Path searchPath, NoteSearchEngine.SearchNoteMode mode) {
		NoteSearchEngine noteSearchEngine = new NoteSearchEngine(mode);
		noteSearchEngine.setSearchPath(searchPath);
		NoteBandbox bandbox = new NoteBandbox(mode);
		bandbox.setSearchEngine(noteSearchEngine);
		return bandbox;
	}


	public NoteBandbox() {
		this(NoteSearchEngine.SearchNoteMode.NOTE);
	}

	public NoteBandbox(NoteSearchEngine.SearchNoteMode mode) {
		super(mode);
	}

	@Override
	protected Bandbox0 init() {
		super.init();

		if (true) {
			ZKS.ABSOLUTE(this);
			ZKS.LEFT(this, 10);
			ZKS.TOP(this, 5);
			ZKS.OPACITY(this, 0.8);
		}

		return this;
	}

	private @Setter SearchEngine searchEngine = NoteSearchEngine.of(NoteSearchEngine.SearchNoteMode.NOTE);

	@Override
	protected void onInitModeSwitcherCom(Bandpopup dropdown) {

		NoteSearchEngine.SearchNoteMode searchMode = getSearchMode();

		Ddl child = new Ddl<NoteSearchEngine.SearchNoteMode>(searchMode) {
			@Override
			public boolean onHappensClickItem(MouseEvent e, NoteSearchEngine.SearchNoteMode value) {
//				onChangeSearchMode(NoteSearchEngine.SearchNoteMode.valueOf(getValue()));
				onChangeSearchMode(value);
				return true;
			}
		};


		dropdown.appendChild(child);
	}

	@Override
	protected void onChangeSearchMode(NoteSearchEngine.SearchNoteMode type) {
		switch (type) {
			case AP:
			case WILDCARD:
			case FILE:
				if (!SecMan.isOwner()) {
					L.info("Access denied to {} for user {}", type, WebUsr.get(null));
					return;
				}
		}

		searchEngine = NoteSearchEngine.of(type);
		setSearchMode(type);
		fillerDd.apply(getDropdown(), getSearchText());
	}

	protected Collection<Object> searchDdItems(String searchPart) {
		return searchEngine.search(searchPart, MAX_DD_SIZE);
	}

	@Override
	protected void onHappensInitAddChildToDd(Bandpopup dropdown, Object item) {

		String label;
		switch (getSearchMode()) {
			case NOTE:
			case NOTE_VAL:
				NodeDir nodeDir = (NodeDir) item;
				label = nodeDir.nodeId();
				break;
			case PAGE:
				Sdn sdn = (Sdn) item;
				label = sdn.toStringPath();
				break;
			case FILE:
			case WILDCARD:
				label = item + "";
				break;
			case AP_VAL:
			case AP:
				Pare3<BootContext.ApType, String, String> val = (Pare3<BootContext.ApType, String, String>) item;
				label = val.key() + Pare.POINT_SYMJ + val.val() + Pare.POINT_SYMJ + STR.toStringSE(val.ext(), 30, val.ext());
				break;
			default:
				throw new WhatIsTypeException(getSearchMode());
		}

		Component itemCom = (Component) new Lb(label).block();


		dropdown.appendChild(itemCom);

		applyInitEventClick(itemCom, item);

	}

	@SneakyThrows
	@Override
	public void onClickDropDownitem(Object com) {

		switch (getSearchMode()) {
			case NOTE:
			case NOTE_VAL:
				NFOpen.openForm((NodeDir) com);
				break;
			case PAGE:
				Sdn sdn = (Sdn) com;
				String planPage = RSPath.PAGE.toPageLink(sdn.key(), sdn.val());

				ZKR.openWindow800_1200(planPage);
//				ZKR.redirectToPage(planPage, true);
				break;
			case FILE:
				EventShowFileComInModal eventShowComInModal = FileView.getEventShowComInModal((Path) com);
				eventShowComInModal.onEvent(null);
//				ZKM.showModal()
//				ZKI.alert("" + com);
				break;
			case AP_VAL:
			case AP: {
				Pare<BootContext.ApType, String> val = (Pare<BootContext.ApType, String>) com;
				Notification.show(String.valueOf(val), this);
				break;
			}
			default:
				throw new WhatIsTypeException(getSearchMode());
		}
	}

}
