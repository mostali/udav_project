package zk_old_core.std_fb;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import zk_com.base.Tbx;
import zk_com.base.Tbxm;
import zk_old_core.mdl.FormDirModel;
import zk_old_core.mdl.PageDirModel;

import java.nio.file.Path;

public class ImgFb extends AbsFb {

	public ImgFb(PageDirModel pageDirModel, Component... coms) {
		super(pageDirModel, coms);

		appendChild(new InputHtmlTbx("content for image"));
	}

	public class InputHtmlTbx extends Tbxm {

		public InputHtmlTbx(Path path) {
			super(path, Tbx.DIMS.WH100);

		}

		public InputHtmlTbx(String value) {
			super(value, Tbx.DIMS.WH100);
		}

		@Override
		protected void onSubmitTextValue(Event e) {
			String text = getValue();
			FormDirModel.ADD.addHtmlComponent(pageDirModel.path(), text);
		}
	}


}
