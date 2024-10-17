package zk_old_core.old.mwin;

import lombok.RequiredArgsConstructor;
import mpc.fs.UDIR;
import mpc.fs.path.UPath;
import mpc.fs.fd.EFT;
import mpu.pare.Pare;
import mpc.str.sym.SYMJ;
import mpu.str.USToken;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Dd;
import zk_com.base_ctr.Span0;
import zk_old_core.old.per_win.PerWin;
import zk_old_core.std.AbsVF;
import zk_page.core.ISpCom;
import zk_old_core.mdl.PageDirModel;
import zk_old_core.app_ds.struct.FormDirDS;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class ChoicerForm extends Span0 implements ISpCom {

	final MWin mWin;

	DdPageForms ddPageForms = null;

	public static String extractPageFormFile(String formPageFormSelectedDdItem) {
		return USToken.lastGreedy(formPageFormSelectedDdItem, ":");
	}

	private DdPageForms getDdPageForms() {
		if (ddPageForms != null) {
			return ddPageForms;
		}
		PageDirModel pageDirModel = PageDirModel.get();
		ddPageForms = new DdPageForms(pageDirModel);
		ddPageForms.setWidth(MWin.DEF_WIDTH_DD);
		return ddPageForms;
	}

	DdFormComs ddFormComs = null;

	private Dd getDdFormComs() {
		if (ddFormComs != null) {
			return ddFormComs;
		}
//		Path lastFormPath = mWin.getLastEditFormPath(null);
//		Path lastFormComPath = mWin.getLastEditFormComPath(null);
//		String lastFormName = null;
//		if (Files.isRegularFile(lastFormPath)) {
//			lastFormName = UPath.toStringSingleName(mWin.getPageDirModel().path(), lastFormPath);
//		}
		ddFormComs = new DdFormComs(Collections.EMPTY_LIST);
		ddFormComs.setWidth(MWin.DEF_WIDTH_DD);
		return ddFormComs;
	}

	@Override
	protected void init() {
		appendLb(SYMJ.CLIPBOARD);
		appendChild(getDdPageForms());
		appendChild(getDdFormComs());
	}

	class DdPageForms extends Dd {

		final PageDirModel pageDirModel;

		public void setForm(AbsVF target) {
			setPageFormSelectedDdItem(target.buildDdLink());
			showFormComponents(target.path());
		}

		void setPageFormSelectedDdItem(String item) {
			setValue(item);
		}

		public DdPageForms(PageDirModel pageDirModel) {
			super(pageDirModel.getFormTypesAndRealtivePaths());

			this.pageDirModel = pageDirModel;
			onSELECTION((SerializableEventListener) event -> {
				PerWin perWin = ChoicerForm.this.getMWin();
				Pare<Path, Path> formFileWithParent = getFormFile(FormDirDS.FN_FORM_JSON);
				perWin.showContent(formFileWithParent.val());
				perWin.invalidate();

				showFormComponents(formFileWithParent.key());

			});
		}

		private void showFormComponents(Path formDir) {
			List<Path> forms = UDIR.ls(formDir, EFT.FILE, Collections.EMPTY_LIST);
			Collection<String> choices = UPath.path2namesRel(pageDirModel.path(), forms);
			getDdFormComs().fillItems(choices, true);
		}

		//parent + file or RootComponent (without dir child)
		private Pare<Path, Path> getFormFile(String formFile) {
			String choicedFormname = getPageFormSelectedDdItem();
			String formParent = extractPageFormFile(choicedFormname);
			Path page = pageDirModel.path();
			Path formParentPath = page.resolve(formParent);
			Path formPath;
			if (formParent.contains("/")) {
				formPath = formParentPath.resolve(formFile);
			} else {
				//it root component
				formPath = formParentPath;

			}
			return Pare.of(formParentPath, formPath);
		}

		public String getPageFormSelectedDdItem() {
			return getDdPageForms().getValue();
		}
	}

	class DdFormComs extends Dd {

		public DdFormComs(Collection<String> choices) {
			super(choices);
			onSELECTION((SerializableEventListener) event -> {
				String choicedFormComponent = getFormComSelectedDdItem();
				Path choicedFormPath = getChoicedPage().path().resolve(choicedFormComponent);
				mWin.showContent(choicedFormPath);
			});

		}

		void setFormComSelectedDdItem(String item) {
			setValue(item);
		}

		public String getFormComSelectedDdItem() throws WrongValueException {
			return super.getValue();
		}

	}

	PageDirModel getChoicedPage() {
		return getDdPageForms().pageDirModel;

	}
}
