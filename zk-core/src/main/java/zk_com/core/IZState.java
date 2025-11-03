package zk_com.core;

import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.libs.PageState;
import zk_notes.node_state.libs.PlaneState;
import zk_os.core.Sdn;
import zk_page.core.PagePathInfo;
import zk_page.core.SpVM;
import zk_notes.node_state.FormState;

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
		return AppStateFactory.ofPageName_orCreate(ppi().sdnAny(), create);
	}

	default PlaneState getPlaneState(boolean... create) {
		return AppStateFactory.ofPlaneName_orCreate(ppi().sdnAny().key(), create);
	}

	//
	//Pagecom


	default FormState getPagecomState(String comname, boolean... create) {
		return AppStateFactory.ofPagecomName_orCreate(ppi().sdnAny(), comname, create);
	}


	//
	// COMs
	default FormState getComState(boolean... create) {
		return getComState(getComName(), create);
	}

	default FormState getComState(String comname, boolean... create) {
		return AppStateFactory.ofComName_orCreate(ppi().sdnAny(), comname, create);
	}

	//
	// FORMs
	default FormState getFormState(boolean... create) {
		return getFormState(getFormName(), create);
	}

	default FormState getFormState(String formname, boolean... create) {
		return AppStateFactory.ofFormName_orCreate(ppi().sdnAny(), formname, create);
	}
}
