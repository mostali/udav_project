package zk_com.core;

import mp.utl_odb.tree.ctxdb.CKey;
import mp.utl_odb.tree.ctxdb.Ctx3Db;
import mp.utl_odb.tree.ctxdb.OCols;
import mpc.json.GsonMap;
import mpe.str.CN;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.impl.PageState;
import zk_notes.node_state.impl.PlaneState;
import zk_os.AppCoreZos;
import zk_os.core.Sdn;
import zk_os.db.net.WebUsr;
import zk_page.core.PagePathInfo;
import zk_page.core.SpVM;
import zk_notes.node_state.ObjState;

public interface IZState {

	default PagePathInfo ppi() {
		return SpVM.get().ppi();
	}

	@Deprecated
	default Sdn sdnAny() {
		return ppi().sdnAny();
	}

	default Sdn sdn() {
		return ppi().sdn();
	}

	default String getComName() {
		return getFormName();
	}

	default String getFormName() {
		return getClass().getSimpleName();
	}

	//
	//

	default PageState getPageState(boolean... create) {
		return AppStateFactory.forPage(ppi().sdnAny(), create);
	}

	default GsonMap getUserState(boolean... create) {
		String userKey = WebUsr.login(CN.ANONIM);
		Ctx3Db.CtxModelCtr userStateModel = AppCoreZos.TREE_USER_STATE().getModelByKeyOrCreate(userKey);
//		GsonMap as = AppCoreZos.TREE_USER_STATE().getAs(WebUsr.login(CN.ANONIM), GsonMap.class,null);
//		AppCoreNotes.TREE_NOTIFY_GLOB();
		GsonMap userState = userStateModel.getAsGsonMap(CN.VALUE, null);
		if (userState != null) {
			return userState;
		}

		userStateModel.setValue(GsonMap.EMPTYFILE);

		AppCoreZos.TREE_USER_STATE().saveModelAsUpdate(userStateModel);

		return userStateModel.getAsGsonMap(CN.VALUE);
	}

	default PlaneState getPlaneState(boolean... create) {
		return AppStateFactory.forPlane(ppi().sdnAny().key(), create);
	}

	//
	//Pagecom


	default ObjState getPagecomState(String comname, boolean... create) {
		return AppStateFactory.forPagecom(ppi().sdnAny(), comname, create);
	}


	//
	// COMs
	default ObjState getComState(boolean... create) {
		return getComState(getComName(), create);
	}

	default ObjState getComState(String comname, boolean... create) {
		return AppStateFactory.forCom(ppi().sdnAny(), comname, create);
	}

	//
	// FORMs
	default ObjState getFormState(boolean... create) {
		return getFormState(getFormName(), create);
	}

	default ObjState getFormState(String formname, boolean... create) {
		return AppStateFactory.forForm(ppi().sdnAny(), formname, create);
	}
}
