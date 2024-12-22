package zk_notes;

import groovy.lang.GroovyShell;
import mpc.exception.RequiredRuntimeException;
import mpc.str.sym.SYMJ;
import mpc.types.abstype.AbsType;
import mpc.types.tks.LID;
import mpe.NT;
import mpe.core.ERR;
import mpe.wthttp.HttpCallMsg;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.pare.Pare;
import mpu.str.SPLIT;
import mpu.str.STR;
import mpu.str.UST;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Window;
import zk_com.base.Ln;
import zk_com.base.Tbx;
import zk_com.base.Xml;
import zk_com.base_ctr.Cap0;
import zk_com.base_ctr.Span0;
import zk_com.base_ext.Listbox0;
import zk_com.editable.EditableValue;
import zk_com.win.Win0;
import zk_form.notify.ZKI;
import zk_os.sec.Sec;
import zk_page.*;
import zk_page.node.NodeDir;
import zk_com.core.IZWin;
import zk_page.node.NodeDirCallService;
import zk_page.node.fsman.NodeFileTransferMan;
import zk_page.node_state.FormState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

//AppNotesMenuCaption
public class ANMC {

	public static class FormEditableName extends EditableValue {
		final NodeDir node;

		public FormEditableName(NodeDir node) {
			super(node.nodeName());
			this.node = node;
		}

		@Override
		protected void onUpdatePrimaryText(String value) {
			super.onUpdatePrimaryText(value);
			NodeFileTransferMan.rename(node, value);
			ZKR.restartPage();
		}


		public void enableSlaveComs(Supplier<List<Component>> slaveOpacityComs, Integer... ms) {
			addEventListener(Events.ON_MOUSE_OVER, e -> slaveOpacityComs.get().forEach(sc -> ZKJS.eval(X.f_("toggleOpacity('#%s',%s)", sc.getUuid(), ARG.toDefOr(7_000, ms)))));

			slaveOpacityComs.get().forEach(c -> {
				if (c instanceof FormEditableName) {
					//ok
				} else {
					ZKS.OPACITY((HtmlBasedComponent) c, 0.05);
				}
			});
		}
	}

	public static void applyWinTbxCap(Pare<String, String> sd3pn, IZWin izWin, Window winNote, NodeDir node) {

		FormState state = node.state();
		List<String> nodeData = state.readFcDataAsLines(ARR.EMPTY_LIST);

		List<String> urls = nodeData.stream().filter(u -> UST.URL(u, null) != null).collect(Collectors.toList());
		ArrayList<HtmlBasedComponent> links = new ArrayList();
		for (int i = 0; i < urls.size(); i++) {
			if (i == 3) {
				break;
			}
			Ln caption = Ln.ofEmojBlank(urls.get(i), i == 0 ? "" : (i + 1) + "");
			if (i > 0) {
				links.add(Xml.NBSP());
			}
			links.add(caption);
		}

		Cap0 cap = null;
		FormEditableName editableValue = null;
		boolean isEditorAdminOwner = Sec.isEditorAdminOwner();
		if (isEditorAdminOwner) {
			cap = (Cap0) Win0.getCap0OrCreate(winNote);
			cap.getChildren().clear();
			editableValue = new FormEditableName(node);
			links.add(0, editableValue);
			Span0 child = Span0.of((List) links);
			cap.appendChild(child);
			izWin._caption(cap);
		}

		if (isEditorAdminOwner) {
			cap.appendChild(Ln.uploadTo(SYMJ.UPLOAD, node.fPath()));
		}
		//
		//  CALL's

		if (isEditorAdminOwner) {

			//do validation by one first line of msg
			String line0 = state.nodeLine(0, null);

			ok:
			if (X.notEmpty(line0)) {

				//try groovy
				if ("groovy".equals(state.upd().get_EXE(null))) {
					Win0.getCap0OrCreate(winNote).appendChild(new GroovyPlayLn(node));
					break ok;
				}

				//try httpCall
				HttpCallMsg httpCallMsg = HttpCallMsg.of(line0, true);
				if (httpCallMsg != null && !httpCallMsg.hasErrors()) {
					String fullVal = state.nodeData();
					if (!line0.equals(fullVal)) {
						httpCallMsg = HttpCallMsg.of(fullVal);//do validation with full val
					}
					cap = (Cap0) Win0.getCap0OrCreate(winNote);

					Ln playLnFor = getPlayLnFor(httpCallMsg, node, winNote);

					cap.appendChild(playLnFor);

				} else if (SqlPlayLn.isSqlCall(node.state().nodeDataCached())) {
					cap = (Cap0) Win0.getCap0OrCreate(winNote);
					Ln playLnFor = new SqlPlayLn(winNote, node);
					cap.appendChild(playLnFor);
				}
			}


			String fc1 = state.readFcData(1, null);
			String fc2 = state.readFcData(2, null);
			if (X.notEmpty(fc1)) {
				Ln ln = (Ln) new Ln(SYMJ.EYE).onCLICK(e -> ZKI.infoEditorBw(fc1));
				ln.title("Show last OK response");
				cap.appendChild(ln);
			}
			if (X.notEmpty(fc2)) {
				Ln ln = (Ln) new Ln(SYMJ.WARN).onCLICK(e -> ZKI.infoEditorBw(fc2));
				ln.title("Show last ERR response");
				cap.appendChild(ln);
			}

		}


		//
		// SwapTgPostLn

		if (isEditorAdminOwner) {

			String line0 = state.nodeLine(0, "");

			if (line0 != null && line0.startsWith(NT.TG.HOST_URL()) && UST.URL(line0, null) != null || SwapTgPostLn.isScriptPattern(line0)) {
				cap = (Cap0) Win0.getCap0OrCreate(winNote);
				cap.appendChild(new SwapTgPostLn(node));
			}

		}

		//
		// FormSizeTbx

		if (isEditorAdminOwner) {
			if (node.upd().getSize(null) != null) {
				cap = (Cap0) Win0.getCap0OrCreate(winNote);
				cap.appendChild(new FormSizeTbx(node));
			}
		}

		if (editableValue != null) {
			Cap0 finalCap = cap;
			editableValue.enableSlaveComs(() -> finalCap.getComsWithChilds());
		}
	}

	static class FormSizeTbx extends Tbx {
		public FormSizeTbx(NodeDir node) {
			super();
			setValue(node.upd().getSize(1) + "");
			placeholder("1");
			ZKS.OPACITY(this, 0.4);
			ZKS.FLOAT(this, false);
			setWidgetAttribute("type", "number");
			width(30).float0(false);
			onOK(e -> {
				int vl = UST.INT(getValue());
				node.upd().updateSize(vl < 0 ? null : vl);
				ZKR.restartPage();
			});
		}

	}

	private static @NotNull Ln getPlayLnFor(HttpCallMsg httpCallMsg, NodeDir node, Component parent) {
//		if (SqlPlayLn.isSqlCall(node.state().nodeDataCached())) {
//			return new SqlPlayLn(parent, node);
//		}
		if (httpCallMsg.isKafkaCall()) {
			return new KafkaPlayLn(parent, node);
		}
		return new HttpPlayLn(node);
	}


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

					node.state().updateProp_VIEW(NodeDir.NVT.HTML);

				} else if (isScriptPattern(line0)) {

					swapedValue = NT.TG.HOST_URL() + getDataTelegramPost(line0);

					node.state().updateProp_VIEW(NodeDir.NVT.TEXT);

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

		private static boolean isScriptPattern(String line0) {
			return line0 != null && line0.startsWith(SCRIPT_PFX);
		}

		private static boolean isTmeHostPattern(String line0) {
			return line0 != null && line0.startsWith(NT.TG.HOST_URL());
		}
	}

	public static class HttpPlayLn extends Ln {

		public HttpPlayLn(NodeDir node) {
			super(SYMJ.JET);
			title("Send http call");
			addEventListener(e -> NodeDirCallService.doHttpCall(node, false));
		}

	}

	public static class SqlPlayLn extends Ln {
		public static final String PFX_JDBC = "jdbc:";

		public SqlPlayLn(Component parent, NodeDir node) {
			super(SYMJ.TARGET);
			title("Send SQL call");
			addEventListener(e -> {
				try {
					List<List<AbsType>> maps = NodeDirCallService.doSqlCall(node);
					node.state().writeFcData(String.valueOf(maps), 1);
					node.state().deletePathFc(2);
//					ZKI.infoEditorBw((Object) ("Found " + X.sizeOf(maps) + " rows: "), String.valueOf(maps));
					HtmlBasedComponent modalCom = Listbox0.fromListList(maps);
					ZKM.showModal("Found " + X.sizeOf(maps) + " rows", modalCom, ZKC.getFirstWindow(), new String[]{"90%", null});
				} catch (Exception ex) {
					node.state().writeFcData(ERR.getStackTrace(ex), 2);
					node.state().deletePathFc(1);
					ZKI.alert(ex);
				}
			});
		}

		public static boolean isSqlCall(String data) {
			List<String>[] headersAndBodyLines = HttpCallMsg.getHeadersAndBodyLines(data);
			if (X.empty(headersAndBodyLines) || X.empty(headersAndBodyLines[0])) {
				return false;
			}
			return STR.startsWith(headersAndBodyLines[0].get(0), true, PFX_JDBC);
		}
	}

	public static class KafkaPlayLn extends Ln {
		public KafkaPlayLn(Component parent, NodeDir node) {
			super(SYMJ.ROCKET);
			title("Send kafka call");
			addEventListener(e -> NodeDirCallService.doKafkaCall(node));
		}
	}

	public static class GroovyPlayLn extends Ln {

		public GroovyPlayLn(NodeDir node) {
			super(SYMJ.ONOFF_ON_PLAY);
			title("Execute groovy script");
			addEventListener(e -> {
				FormState state = node.state();
				state.deletePathFc(1);
				state.deletePathFc(2);
				try {
					GroovyShell shell = new GroovyShell();
					Object evaluate = shell.evaluate(state.readFcData());
					state.writeFcDataOk(X.toString(evaluate, ""));
					ZKI.infoEditorBw(evaluate + "");
				} catch (Throwable ex) {
					String stackTrace = ERR.getStackTrace(ex);
					L.error("Groovy error:" + node, ex);
					state.writeFcDataError(stackTrace);
					ZKI.alert(stackTrace);
				}

			});
		}
	}


}
