package zk_old_core.mdl;

import mpu.core.ARG;
import mpu.core.ARRi;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.fd.DIR;
import mpc.map.UMap;
import mpu.X;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_old_core.std_core.FrmEE;
import zk_form.notify.ZKI_Window;
import zk_old_core.app_ds.struct.FormDirDS;
import zk_old_core.std_core.CType;
import zk_old_core.app_ds.struct.PageDirDS;
import zk_old_core.mdl.pageset.IFormModel;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class FormDirModel extends DirModel implements IFormModel {

	public static final Logger L = LoggerFactory.getLogger(FormDirModel.class);
	public static final String PK_CTYPE = "ctype";

	PagePostion pagePostion = PagePostion.BODY;

	@Override
	public PagePostion getLayoutPosition() {
		return pagePostion;
	}

	public FormDirModel(Path dir) {
		this(dir, PagePostion.BODY);
	}

	public FormDirModel(Path dir, PagePostion pagePostion) {
		super(dir);
		this.pagePostion = pagePostion;
	}

	@Override
	public Path getFileRootProps() {
		return FormDirDS.SELF.getJsonOrPropsPath(path());
	}

	public Path getRootFormPath() {
		return super.dir().path();
	}


	public static FormDirModel of(String pathUsrDir) {
		return new FormDirModel(Paths.get(pathUsrDir));
	}

	public static FormDirModel of(File file) {
		return of(file.toPath());
	}

	public static FormDirModel of(Path file) {
		return new FormDirModel(file);
	}

	@Override
	public CType ctype(CType... defRq) {
		Map props = getRootProps();
		CType ctype;
		if (props != null) {
			ctype = UMap.getAs(props, PK_CTYPE, CType.class, null);
			if (ctype != null) {
				return ctype;
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Set CType in " + getFileRootProps());
	}

	public void setPropertyCtype(String ctype) {
		setRootProperty(PK_CTYPE, ctype, true);
	}

	public static class ADD {

		public static Path addHtmlComponent(PageDirModel pdm, String new_form_html) {
			return addHtmlComponent(pdm.dir(), new_form_html);
		}

		public static Path addHtmlComponent(Path pageDir, String form_html) {
			return addHtmlComponent(DIR.of(pageDir), form_html);
		}

		public static Path addHtmlComponent(DIR pageDir, String form_html) {
			List<Path> bodyComs = PageDirModel.getBodyChilds(pageDir, null, null);
			String newName;
			if (X.empty(bodyComs)) {
				newName = "10";
			} else {
				newName = FrmEE.incrementNextFormName(ARRi.last(bodyComs));
			}

			Path form = PageDirDS.BODY.writeToDir(pageDir.path(), newName, newName + ".html", form_html, true);
			if (DirModel.L.isInfoEnabled()) {
				String msg = X.fl("CreateAndAddNewHtmlComponent '{}'\n{}", newName, form_html);
				DirModel.L.info(msg);
				ZKI_Window.info(msg);
			}
			return form;
		}
	}

//	@Override
//	public String toString() {
//		return U.f("FormDirModel(%s):%s:%s", US.pfile(path()), ctype(CType.UNDEFINED), X.sizeOf(getComponentsOrBuild(null)));
//	}


}
