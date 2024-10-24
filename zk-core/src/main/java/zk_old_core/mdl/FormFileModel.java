package zk_old_core.mdl;

import mpu.core.ARG;
import zk_old_core.std_core.CType;
import zk_old_core.mdl.pageset.IFormModel;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FormFileModel extends FdModel implements IFormModel {

	PagePostion pagePostion;

	public PagePostion getLayoutPosition() {
		return pagePostion;
	}

	public FormFileModel(Path file) {
		this(file, PagePostion.BODY);
	}

	public FormFileModel(Path file, PagePostion pagePostion) {
		super(file);
		this.pagePostion = pagePostion;
	}

	public static FormFileModel of(String pathUsrDir) {
		return new FormFileModel(Paths.get(pathUsrDir));
	}

	public static FormFileModel of(File file) {
		return of(file.toPath());
	}

	public static FormFileModel of(Path file) {
		return new FormFileModel(file);
	}

	@Override
	public Path getFileRootProps() {
		return null;
	}

//	private transient Set<CType> ctypeIndex;
//
//	public Set<CType> getCTypeIndex(boolean... fresh) {
//		if (ctypeIndex == null || ARG.isDefEqTrue(fresh)) {
//			return ctypeIndex = AR.asHSet(ctype());
//		}
//		return ctypeIndex;
//	}

	@Override
	public CType ctype(CType... defRq) {
		CType ctype = CType.of(path());
		if (ctype != null) {
			return ctype;
		}
		return ARG.toDefRq(defRq);
	}


//	public void setPropertyCtype(String ctype) {
//		setStringProperty(PK_CTYPE, ctype, true);
//	}

	public void setRootProperty(String key, String value, boolean write) {
//		RuProps props;
//		if (write) {
//			//write property to fresh ( if props has unwritable context);
//			props = readProps(path(), write);
//		} else {
//			props = getProps();
//		}
//		props.setString(key, value);
		throw new UnsupportedOperationException("This is form file");
	}

//	public CType getCTypeWithInit() {
//		CType ctype = ctype(null);
//		if (ctype != null) {
//			return ctype;
//		}
//		Set<CType> cTypes = getCTypeIndex();
//		ctype = AR.first(cTypes, CType.UNDEFINED);
//		setPropertyCtype(ctype.name());
//		return ctype;
//	}

	//	@Deprecated
//	public List<Component> getComponentsOrBuild(List<Component>... defRq) {
//		return getComponentsOrBuild(false, defRq);
//	}
//
//	@Deprecated
//	public List<Component> getComponentsOrBuild(boolean editMode, List<Component>... defRq) {
//		return new SimpleFormBuilder(this).build(editMode, defRq);
//	}
//
//	@Override
//	public String toString() {
//		return U.f("FormFileModel(%s):%s", US.pfile(pathFd()), ctype(CType.UNDEFINED));
//	}


}
