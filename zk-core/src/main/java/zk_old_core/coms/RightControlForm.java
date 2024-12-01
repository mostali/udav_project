package zk_old_core.coms;

import mpc.html.STYLE;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Ln;
import zk_com.win.Win0;
import zk_notes.AppNotes;
import zk_notes.AppNotesProps;
import zk_notes.control.NotesSpace;
import zk_os.AppZosProps;
import zk_page.*;
import zk_page.behaviours.UploaderFileOrPhotoEvent;
import zk_page.core.ISpCom;
import zk_page.core.PageSP;
import zk_page.core.SpVM;
import zk_page.node.fsman.NodeFileTransferMan;

import java.nio.file.Path;
import java.util.List;

public class RightControlForm extends Win0 implements ISpCom {

	public static RightControlForm findFirst(RightControlForm... defRq) {
		return ZKCFinder.findFirstIn_Page(RightControlForm.class, true, defRq);
	}

	@Override
	protected void init() {
		super.init();

		ZKS.PADDING_WIN(this, 5, 0);
		fixed();
		width(40);

		addSTYLE("background-color: rgba(255, 255, 255, 0.5)");

		String newStyle = STYLE.addStyle(getContentStyle(), "background-color: rgba(255, 255, 255, 0.3)");
		setContentStyle(newStyle);

		bottom_rigth(5.0, 0.5);
		ZKS.BORDER_RADIUS(this, "10px");

		Pare<String, String> sdn = SpVM.get().sdn();

		{
			SerializableEventListener event = (Event e) -> {
				NodeFileTransferMan.addNewRandomForm(sdn);
			};
			IconLn iconLn = new IconLn(SYMJ.FILE3, event, "Add note");
			appendChild(iconLn);

		}
		{
			SerializableEventListener event = (Event e) -> {
				NodeFileTransferMan.AddNewForm.OptsAdd opts = new NodeFileTransferMan.AddNewForm.OptsAdd();
				opts.setDoubleView(true);
				NodeFileTransferMan.addNewRandomForm(sdn, opts);
			};
			IconLn iconLn = new IconLn(SYMJ.ABCD, event, "Add double note");
			appendChild(iconLn);

		}


		{
			SerializableEventListener event = (Event e) -> {
				NodeFileTransferMan.AddNewForm.OptsAdd opts = new NodeFileTransferMan.AddNewForm.OptsAdd();
				opts.setWysiwygView(true);
				NodeFileTransferMan.addNewRandomForm(sdn, opts);
			};
			IconLn iconLn = new IconLn(SYMJ.FILE_HTML, event, "Add WYSIWYG note");
			appendChild(iconLn);

		}


		{
			Path mediaBlankDir = AppNotes.getRpaForms_BlankDir(sdn, "media-", 3);
			SerializableEventListener hrefAction = new UploaderFileOrPhotoEvent(mediaBlankDir.toString(), true, p -> {
				if (AppZosProps.APP_WEB_SYNC.getValueOrDefault()) {
					ZKR.restartPage();
				}
				return null;
			});
			IconLn iconLn = new IconLn(SYMJ.FILE_IMG3, hrefAction, "Add media note from clipboard");
			appendChild(iconLn);

		}

		{
			if (AppNotesProps.APR_DEV_ENABLE.getValueOrDefault(false)) {

				{
					SerializableEventListener event = (Event e) -> {
						NodeFileTransferMan.AddNewForm.OptsAdd opts = new NodeFileTransferMan.AddNewForm.OptsAdd();
						opts.setHttpCallForm(true);
						NodeFileTransferMan.addNewRandomForm(sdn, opts);
					};
					IconLn iconLn = new IconLn(SYMJ.JET, event, "Add http call", 30);
					appendChild(iconLn);
				}

				{
					SerializableEventListener event = (Event e) -> {
						NodeFileTransferMan.AddNewForm.OptsAdd opts = new NodeFileTransferMan.AddNewForm.OptsAdd();
						opts.setKafkaCallForm(true);
						NodeFileTransferMan.addNewRandomForm(sdn, opts);
					};
					IconLn iconLn = new IconLn(SYMJ.ROCKET, event, "Add kafka call");
					appendChild(iconLn);
				}

				{
					SerializableEventListener event = (Event e) -> {
						List<String> allEmojs = SYMJ.getAllEmojs();
						String strings = X.f("var strings = %s;", ZKJS.getAsJsArray(allEmojs));
//				String screen = "var screen = [100, 100, 1400, 1200];";
						ZKJS.eval(strings + "distributeStringsOnScreen(strings, null, 50);");
					};
					IconLn iconLn = new IconLn(SYMJ.BOT2, event, "Choice Emoj", 50);
					appendChild(iconLn);
				}
				{
					SerializableEventListener event = (Event e) -> {
						String strings = X.f("var strings = %s;", ZKJS.getAsJsArray(ZKColor.getAllColors()));
						String screen = "var screen = null;";
						ZKJS.eval(strings + screen + "distributeStringsOnScreen(strings, null, 150);");
					};
					IconLn iconLn = new IconLn(SYMJ.FILE_IMG2, event, "Choice Color");
					appendChild(iconLn);
				}
				{
					SerializableEventListener event = (Event e) -> {
						PageSP.BoolCom.doAlignPageGrid();
						NotesSpace.rerenderFirst();
					};
					IconLn iconLn = new IconLn(SYMJ.GEAR, event, "Enable auto align grid");
					appendChild(iconLn);
				}

			}
		}

	}

	public static class IconLn extends Ln {


		public IconLn(String label, SerializableEventListener listener, String title, Integer... margin_top) {
			super(label);
			addEventListener(listener);

			title(title);

			String bgColor = ZKColor.ORANGE.variants[6];
			String bgColorControl = "#f3efe9";
			bgcolor(bgColorControl);

			Ln addTextLn = this;

			Object marginPat = 0;
			if (ARG.isDef(margin_top)) {
				marginPat = ARG.toDef(margin_top) + "px 0px 0px 0px";
			}
			addTextLn.decoration_none().relative().block().padding(5).margin(marginPat).bgcolor(bgColor).width(31);
			ZKS.BORDER_RADIUS(addTextLn, 5);
			ZKS.BORDER_BOTTOM(addTextLn, "2px solid " + bgColorControl);

		}


	}

}
