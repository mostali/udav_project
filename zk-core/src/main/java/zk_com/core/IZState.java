package zk_com.core;

import mpc.fs.ext.EXT;
import mpu.core.ARG;
import mpu.pare.Pare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_notes.node_state.libs.PageState;
import zk_page.core.PagePathInfo;
import zk_page.core.SpVM;
import zk_notes.node_state.FormState;

public interface IZState {

	Logger L = LoggerFactory.getLogger(IZState.class);

	default PagePathInfo ppi() {
		return SpVM.get().ppi();
	}

	default Pare<String, String> sdn() {
		return ppi().sdnHybryd();
	}

	default String getComName() {
		return getFormName();
	}

	default String getFormName() {
		return getClass().getSimpleName();
	}

	default String getPageComName() {
		return getFormName();
	}

	default PageState getPageState() {
		return getPageState(false);
	}

	default PageState getPageState(boolean create) {
		return FormState.ofPageState_orCreate(ppi().sdnHybryd(), create);
	}

	default FormState getPageComState_JSON(boolean... create) {
		return getComState(getPageComName(), ARG.isDefEqTrue(create), EXT.JSON);
	}

	default FormState getPageComState_JSON(String comname, boolean create) {
		return getPageComState(comname, create, EXT.JSON);
	}

	default FormState getPageComState(String comname, boolean create, EXT ext) {
		return FormState.ofPathPageComFile_OrCreate(ppi().sdnHybryd(), comname, ext, create);
	}


	//
	// COMs
	default FormState getComState_PROPS(String comname, boolean create) {
		return getComState(comname, create, EXT.PROPS);
	}

	default FormState getComState_JSON(boolean... create) {
		return getComState(getComName(), ARG.isDefEqTrue(create), EXT.JSON);
	}

	default FormState getComState_JSON(String comname, boolean create) {
		return getComState(comname, create, EXT.JSON);
	}

	default FormState getComState(String comname, boolean create, EXT ext) {
		return FormState.ofPathComFile_orCreate(ppi().sdnHybryd(), comname, ext, create);
	}

	//
	// FORMs
	default FormState getFormState_PROPS(boolean... create) {
		return getFormState(getFormName(), ARG.isDefEqTrue(create), EXT.PROPS);
	}

	default FormState getFormState_PROPS(String comname, boolean create) {
		return getFormState(comname, create, EXT.PROPS);
	}

	default FormState getFormState(String formname, boolean create, EXT ext) {
		return FormState.ofPathFormFile_orCreate(ppi().sdnHybryd(), formname, ext, create);
	}

}
