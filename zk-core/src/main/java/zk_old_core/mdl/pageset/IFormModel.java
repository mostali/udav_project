package zk_old_core.mdl.pageset;

import mpu.core.ARRi;
import zk_old_core.mdl.FdModel;
import zk_old_core.mdl.FormDirModel;
import zk_old_core.mdl.PagePostion;
import zk_old_core.std_core.CType;

import java.nio.file.Path;

public interface IFormModel {

	static CType getCTypeOrDefine(IFormModel form) {
		CType ctype = form.ctype(null);
		if (ctype != null) {
			return ctype;
		}
		FdModel fdm = form.fd();
		Path path = fdm.path();
		if (fdm.isFile()) {
			ctype = CType.of(path);
		} else {
			ctype = ARRi.first(CType.defineCTypeIndex(path));
			fdm.setRootProperty(FormDirModel.PK_CTYPE, ctype.name(),true);
		}
		if (ctype == null) {
			throw new NullPointerException("CType is null, after define path: " + path);
		}
		return ctype;
	}

	PagePostion getLayoutPosition();

	CType ctype(CType... defRq);

	default FdModel fd() {
		return ((FdModel) this);
	}

}
