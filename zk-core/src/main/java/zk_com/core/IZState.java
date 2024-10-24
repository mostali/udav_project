package zk_com.core;

import mpc.fs.ext.EXT;
import mpu.core.ARG;
import mpu.pare.Pare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_os.AFC;
import zk_page.core.PagePathInfo;
import zk_page.core.SpVM;
import zk_page.node_state.FormState;

import java.nio.file.Path;

public interface IZState {

	public static final Logger L = LoggerFactory.getLogger(IZState.class);

	default PagePathInfo ppi() {
		return SpVM.get().ppi();
	}

	default Pare<String, String> sdn() {
		return ppi().sdn();
	}

	default String getComStateName() {
		return getClass().getSimpleName();
	}

	default String getFormStateName() {
		return getClass().getSimpleName();
	}

	default FormState getPageState() {
		return getPageState(false);
	}

	default FormState getPageState(boolean create) {
		return FormState.ofPageState(ppi().sdn(), create);
	}


	//
	// COMs
	default FormState getComState_PROPS(String comname, boolean create) {
		return getComState(comname, create, EXT.PROPS);
	}

	default FormState getComState_JSON(boolean... create) {
		return getComState(getComStateName(), ARG.isDefEqTrue(create), EXT.JSON);
	}

	default FormState getComState_JSON(String comname, boolean create) {
		return getComState(comname, create, EXT.JSON);
	}

	default FormState getComState(String comname, boolean create, EXT ext) {
		return FormState.ofPathComFile_OrCreate(ppi().sdn(), comname, ext, create);
	}

	//
	// FORMs
	default FormState getFormState_PROPS(boolean... create) {
		return getFormState(getFormStateName(), ARG.isDefEqTrue(create), EXT.PROPS);
	}

	default FormState getFormState_PROPS(String comname, boolean create) {
		return getFormState(comname, create, EXT.PROPS);
	}

	default FormState getFormState(String formname, boolean create, EXT ext) {
		return FormState.ofPathFormFile_orCreate(ppi().sdn(), formname, ext, create);
	}

}
