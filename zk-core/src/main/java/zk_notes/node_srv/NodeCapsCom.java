package zk_notes.node_srv;

import mpc.exception.RequiredRuntimeException;
import mpc.str.sym.SYMJ;
import mpc.types.tks.LID;
import mpe.NT;
import mpu.X;
import mpu.core.ARG;
import mpu.str.UST;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Events;
import zk_com.base.Ln;
import zk_com.base.Tbx;
import zk_com.editable.EditableValue;
import zk_notes.events.ANMF;
import zk_notes.node.NodeDir;
import zk_notes.node.core.NVT;
import zk_notes.fsman.NodeFileTransferMan;
import zk_os.sec.SecMan;
import zk_os.sec.SecManRMM;
import zk_page.ZKC;
import zk_page.ZKJS;
import zk_page.ZKR;
import zk_page.ZKS;

import java.util.List;
import java.util.function.Supplier;

public class NodeCapsCom {

	public static final Logger L = LoggerFactory.getLogger(NodeCapsCom.class);

	//
	//
	//

	public static class FormSizeTbx extends Tbx {
		public FormSizeTbx(NodeDir node) {
			super();
			setValue(node.fields().get_SIZE(1) + "");
			placeholder("1");
			ZKS.OPACITY(this, 0.4);
			ZKS.FLOAT(this, false);
			setWidgetAttribute("type", "number");
			width(30).float0(false);
			onOK(e -> {
				int vl = UST.INT(getValue());
				node.fields().set_SIZE(vl < 0 ? null : vl);
				ZKR.restartPage();
			});
		}

	}

	public static class FormEditableName extends EditableValue {
		final NodeDir node;

		public FormEditableName(NodeDir node) {
			super(node.nodeName());
			this.node = node;
			if (SecMan.isAllowedEditPlane()) {
				ANMF.applyForm(getOrCreateMenupopup(ZKC.getFirstWindow()), node);
			}
		}

		@Override
		protected void onUpdatePrimaryText(String value) {
			super.onUpdatePrimaryText(value);
			NodeFileTransferMan.rename(node, value, false);
			ZKR.restartPage();
		}


		public void enableDisappearComs(Supplier<List<Component>> slaveOpacityComs, Integer... ms) {

			addEventListener(Events.ON_MOUSE_OVER, e -> slaveOpacityComs.get().forEach(sc -> ZKJS.eval(X.f_("toggleOpacity('#%s',%s)", sc.getUuid(), ARG.toDefOr(7_000, ms)))));

			slaveOpacityComs.get().forEach(c -> {
				if (c instanceof FormEditableName || c instanceof PlayContainer.PlayLn) {
					//ok
				} else {
					ZKS.OPACITY((HtmlBasedComponent) c, 0.05);
				}
			});

		}
	}

	//
	//-------------------- SWAP TG CALL -------------------------------
	//

	public static class SwapTgPostLn extends Ln {

		public static final String PT_TG_WIDGET = "<script src=\"https://telegram.org/js/telegram-widget.js?22\" data-telegram-post=\"{0}\" data-width=\"100%\"></script>";
		public static final String SCRIPT_PFX = "<script src=";

		public static String getDataTelegramPost(String html, String... defRq) {
			Document document = Jsoup.parse(html);
			Element scriptElement = document.selectFirst("script[data-telegram-post]");
			if (scriptElement != null) {
				return scriptElement.attr("data-telegram-post");
			}
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Except valid script[data-telegram-post], from %s", html), defRq);
		}

		public SwapTgPostLn(NodeDir node) {
			super(SYMJ.ARROW_REPEAT_LEFT_TH);
			title("Swap tg post link to script");
			addEventListener(e -> {
				String swapedValue;
				String line0 = node.state().nodeLine(0);

				boolean skip = false;
				if (isTmeHostPattern(line0)) {
					line0 = line0.substring(NT.TG.HOST_URL().length());
					LID lid = LID.of(line0);
					String chatName = lid.first();
					Long msgId = lid.secondLong();
//					String[] pageItem = UUrl.getPathFirstAndSencondItemFromUrl(line0);

					swapedValue = X.fm(PT_TG_WIDGET, chatName + "/" + msgId);

					node.state().fields().set_VIEW(NVT.HTML);

				} else if (isScriptPattern(line0)) {

					swapedValue = NT.TG.HOST_URL() + getDataTelegramPost(line0);

					node.state().fields().set_VIEW(NVT.TEXT);

				} else {
					swapedValue = null;
					skip = true;
				}

				if (!skip && swapedValue != null) {
					node.state().writeFcData(swapedValue);
				}

//				NotesSpace.rerenderFirst();
				ZKR.restartPage();

			});
		}

		private boolean isTmeOrScriptPattern(String line0) {
			return isTmeHostPattern(line0) || isScriptPattern(line0);
		}

		public static boolean isScriptPattern(String line0) {
			return line0 != null && line0.startsWith(SCRIPT_PFX);
		}

		private static boolean isTmeHostPattern(String line0) {
			return line0 != null && line0.startsWith(NT.TG.HOST_URL());
		}
	}

}
