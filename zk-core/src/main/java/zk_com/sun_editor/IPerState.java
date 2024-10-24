package zk_com.sun_editor;

import mpu.core.ARG;
import mpc.exception.NI;
import mpc.exception.WhatIsTypeException;
import zk_old_core.AppCoreStateOLD2;
import zk_old_core.AppCoreStateOld;
import zk_page.core.SpVM;
import zk_old_core.mdl.PageDirModel;

import java.nio.file.Path;

public interface IPerState {

	default TypeState typeState() {
		return TypeState.SITE;
	}

	default Path getPathState(boolean... json) {
		return getPathState_Site(getClass(), json);
	}

	static Path getPathState_Site(Class stateName, boolean... json) {
		return AppCoreStateOLD2.getPathState_Site(stateName, ARG.isDefEqTrue(json));
	}

	static Path getPathState_Page(Class stateName, boolean... json) {
		return AppCoreStateOLD2.getPathState_Page(stateName, SpVM.get().ppi().pagename(), ARG.isDefEqTrue(json));
	}

	@Deprecated
	default Path getPathState(boolean json, Path repo, Path page, Path form) {
		TypeState typeState = typeState();
		switch (typeState) {
			case SITE:
				return AppCoreStateOLD2.getPathState_Site(getClass(), json);
			case DOMAIN: {
				if (repo == null) {
					if (page != null) {
						repo = PageDirModel.of(page).getRepo().path();
					} else if (form != null) {
						//						repo = FormDirModel.of(form).getRepo().path();
						NI.stop("ni domain stat by form");
					} else {
						repo = PageDirModel.get().getRepo().path();
					}
				}
				return AppCoreStateOld.getPathStateSd3(repo, getClass(), json);
			}
			case PAGE: {
				page = page != null ? page : PageDirModel.get().path();
				return AppCoreStateOld.getPathPageStateOf(page, getClass(), json);
			}
			case FORM:
				NI.stop("ni form state");
			default:
				throw new WhatIsTypeException(typeState());
		}
	}

	enum TypeState {
		SITE, DOMAIN, PAGE, FORM;
	}
}
