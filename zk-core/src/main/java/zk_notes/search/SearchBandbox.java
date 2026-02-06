package zk_notes.search;

import lombok.Getter;
import lombok.Setter;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpc.log.L;
import mpu.IT;
import mpu.func.FunctionV2;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Bandpopup;
import zk_com.base.Lb;
import zk_com.base_ext.Bandbox0;
import zk_com.base_ext.NextEnumLn;
import zk_form.notify.ZKI;
import zk_notes.search.engine.PathSearchEngine;
import zk_pages.DemoComPageSP;

import java.nio.file.Path;
import java.util.Collection;

public class SearchBandbox<I, T extends Enum> extends Bandbox0 {

	public static final int MAX_DD_SIZE = 100;
	@Setter
	private @Getter T searchMode;

	enum SearchDefaultMode {
		FILE
	}


	FunctionV2<Bandpopup, String> fillerDd = (dropdown, searchPart) -> {

		Collection<I> foundedItems;
		try {
			foundedItems = searchDdItems(searchPart);
		} catch (Exception ex) {
//			ZKI.infoBottomCenter("Search items by [" + searchPart + "]" + ex.getMessage(), ZKI.Level.ERR);
			L.error("searchDdItems", ex);
			ZKI.infoAfterPointer(ex.getMessage(), ZKI.Level.ERR);
			return;
		}

		//
		//0
		dropdown.getChildren().clear();

		//
		//1
		onInitModeSwitcherCom(dropdown);

		//
		//2
		for (I item : foundedItems) {
			onHappensInitAddChildToDd(dropdown, IT.NN(item));
		}

	};

	public SearchBandbox() {
		searchMode = (T) SearchDefaultMode.FILE;
	}

	public SearchBandbox(T defMode) {
		searchMode = defMode;
	}

	@Override
	public void onHappensChange(InputEvent e) {
		super.onHappensChange(e);
		fillerDd.apply(getDropdown(), e.getValue());
	}

	@Override
	public void onHappensChanging(InputEvent e) {
		super.onHappensChanging(e);
		fillerDd.apply(getDropdown(), e.getValue());
	}


	protected void onInitModeSwitcherCom(Bandpopup dropdown) {
		NextEnumLn child = new NextEnumLn(searchMode) {
			@Override
			protected void onNextEnum(Enum type) {
				onChangeSearchMode((T) type);
			}
		};
		dropdown.appendChild(child);
	}

	protected Collection<I> searchDdItems(String searchPart) {
		SearchDefaultMode searchDefaultMode = (SearchDefaultMode) searchMode;
		switch (searchDefaultMode) {
			case FILE:
				Path searchDir = DemoComPageSP.FS_DEMO_COM;
				PathSearchEngine.RelDirPredicate searchPredicate = new PathSearchEngine.RelDirPredicate(searchDir, searchPart);
				return (Collection) new PathSearchEngine(searchDir).search(searchPredicate, MAX_DD_SIZE);
			default:
				throw new WhatIsTypeException(searchDefaultMode);
		}
	}

	protected void onChangeSearchMode(T type) {
		this.searchMode = type;
	}

	protected void onHappensInitAddChildToDd(Bandpopup dropdown, I item) {
		Lb lb;
		if (item instanceof Path) {
			String fn = UF.fn((Path) item, 2, item.toString());
			dropdown.appendChild((Component) (lb = new Lb(fn)).block());
		} else {
			dropdown.appendChild((Component) (lb = new Lb(item + "")).block());
		}

//		applyInitEventClick(lb, item);

	}

	@Override
	public void fillDropDownChild(Bandpopup dropdown) {
		setAutodrop(true);
		fillerDd.apply(dropdown, getSearchText());
	}


}
