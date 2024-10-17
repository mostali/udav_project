package zk_old_core.app_ds.struct;

import zk_old_core.app_ds.AppDS;
import mpc.json.GsonMap;

import java.nio.file.Files;
import java.nio.file.Path;

public class FormDirDS extends AppDS {

	public static final FormDirDS SELF = new FormDirDS(".");

	public FormDirDS(String form_fdname) {
		super(form_fdname);
	}

	public GsonMap getFormGsonMap(Path pageDir) {
		return super.getGsonMap(pageDir, FN_FORM_JSON);
	}

	public Path getJsonOrPropsPath(Path entityDir) {
		Path path = getPathWith(entityDir, FN_FORM_JSON);
		if (Files.isRegularFile(path)) {
			return path;
		}
		Path propsPath = getPathWith(entityDir, FN_FORM_PROPS);
		if (Files.isRegularFile(propsPath)) {
			return propsPath;
		}
		return path;
	}
}
