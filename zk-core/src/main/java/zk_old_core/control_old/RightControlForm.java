package zk_old_core.control_old;

import mpc.str.sym.SYMJ;
import mpu.core.ARG;
import mpu.pare.Pare;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Ln;
import zk_com.win.Win0;
import zk_notes.AppNotes;
import zk_notes.AppNotesProps;
import zk_page.ZKCFinder;
import zk_page.ZKColor;
import zk_page.ZKS;
import zk_page.behaviours.UploaderFileOrPhotoEvent;
import zk_page.core.ISpCom;
import zk_page.core.SpVM;
import zk_page.node.fsman.NodeFileTransferMan;

import java.nio.file.Path;

public class RightControlForm extends Win0 implements ISpCom {

	public static final String TM_POS = "pos";

	public static RightControlForm findFirst(RightControlForm... defRq) {
		return ZKCFinder.findFirstIn_Page(RightControlForm.class, true, defRq);
	}

	@Override
	protected void init() {
		super.init();

		ZKS.PADDING_WIN(this, 5, 0);
		fixed();
		width(40);

		bottom_rigth(5.0, 0.5);
		ZKS.BORDER_RADIUS(this, "10px");

		Pare<String, String> sdn = SpVM.get().sdn();

		{
			SerializableEventListener event = (Event e) -> {
				NodeFileTransferMan.addNewRandomForm(sdn);
			};
			IconLn iconLn = new IconLn(SYMJ.FILE3, event, "simple note");
			appendChild(iconLn);

		}

		{
			SerializableEventListener event = (Event e) -> {
				NodeFileTransferMan.AddNewForm.OptsAdd opts = new NodeFileTransferMan.AddNewForm.OptsAdd();
				opts.setWysiwygView(true);
				NodeFileTransferMan.addNewRandomForm(sdn, opts);
			};
			IconLn iconLn = new IconLn(SYMJ.FILE_HTML, event, "WYSIWYG");
			appendChild(iconLn);

		}


		{
			Path imgBlankDir = AppNotes.getRpaForms_BlankDir(sdn, "img", 3);
			SerializableEventListener hrefAction = new UploaderFileOrPhotoEvent(imgBlankDir.toString(), true, null);
			IconLn iconLn = new IconLn(SYMJ.FILE_IMG3, hrefAction, "Copy image from clipboard");
			appendChild(iconLn);

		}

		{
			if (AppNotesProps.APR_DEV_ENABLE.getValueOrDefault(false)) {
				SerializableEventListener event = (Event e) -> {
					NodeFileTransferMan.AddNewForm.OptsAdd opts = new NodeFileTransferMan.AddNewForm.OptsAdd();
					opts.setHttpCallForm(true);
					NodeFileTransferMan.addNewRandomForm(sdn, opts);
				};
				IconLn iconLn = new IconLn(SYMJ.FILE4, event, "Add http call", true);
				appendChild(iconLn);
			}
		}

	}

	public static class IconLn extends Ln {


		public IconLn(String label, SerializableEventListener listener, String title, boolean... specify) {
			super(label);
			addEventListener(listener);

			title(title);

			String bgColor = ZKColor.ORANGE.variants[6];
			String bgColorControl = "#f3efe9";
			bgcolor(bgColorControl);

			Ln addTextLn = this;

			Object margin = 0;
			if (ARG.isDefEqTrue(specify)) {
				margin = "10px 0px 0px 0px";
			}
			addTextLn.decoration_none().relative().block().padding(5).margin(margin).bgcolor(bgColor).width(31);
			ZKS.BORDER_RADIUS(addTextLn, 5);
			ZKS.BORDER_BOTTOM(addTextLn, "2px solid " + bgColorControl);

		}


	}

}
