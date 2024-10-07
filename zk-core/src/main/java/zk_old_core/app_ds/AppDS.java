package zk_old_core.app_ds;

import lombok.SneakyThrows;
import mpc.fs.UFS_BASE;
import mpc.fs.path.UPath;
import mpc.fs.dir_struct.DirStruct;

import java.nio.file.Path;

public class AppDS extends DirStruct {

	public static final String FN_PAGE_PROPS = "page.props";
	public static final String FN_PAGE_JSON = "page.json";

	//RMM
	@Deprecated
	public static final String FN_FORM_PROPS = "form.props";
	public static final String FN_FORM_JSON = "form.json";
	public static final String FN_USR_JSON = "usr.json";

	public static final String FN_ROOT_PROPS = ".props";
	public static final String DN_ROOT_PROPS = ".rmm";
	public static final String DN_ARCHIVE = ".ARCHIVE";

	public AppDS(String fd_name) {
		super(fd_name);
	}

	@SneakyThrows
	public static Path rmm(Path path) {
		return UFS_BASE.MV.move(path, path.getParent().resolve(DN_ROOT_PROPS).resolve(path.getFileName()), false);
	}

	public static boolean isArchive(Path path) {
		return UPath.eqName(path, DN_ARCHIVE);
	}

	public static final AppDS ARCHIVE = new AppDS(DN_ARCHIVE);
	public static final AppDS SELF = new AppDS(".");

	public void setPropsProperty(Path struct, String key, String value) {
		getRuProps(struct, FN_ROOT_PROPS, true).setString(key, value);
	}

	public String getPropsProperty(Path struct, String key) {
		return getRuProps(struct, FN_ROOT_PROPS, true).getString(key);
	}

	public <T> T getPropsPropertyAs(Path struct, String key, Class<T> asType) {
		return getRuProps(struct, FN_ROOT_PROPS, true).getAsType(key, asType);
	}

	public Path getPropsPath(Path struct) {
		return getPathWith(struct, FN_ROOT_PROPS);
	}

//	public  void setPropertySync(Path entity, String key, String value) {
//		getProps(entity, true).setString(key, value);
//	}

//	public static RuProps getProps(Path entity, boolean sync) {
//		return props.getRuProps(entity, sync);
//	}

//	public static Path getPropsPath(Path entity) {
//		return props.getPath(entity);
//	}

//	public RuProps getRuProps(Path from, boolean sync) {
//		return RuProps.of(getPathWith(from,)).syncWrite(sync);
//	}


//	public static Path moveToArchive(Path repoDir, boolean... mkdirs_mkdir_ornot) {
//		return archive.moveToMe(repoDir, mkdirs_mkdir_ornot);
//	}
}
