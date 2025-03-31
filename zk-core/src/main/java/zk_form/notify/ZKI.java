package zk_form.notify;

import mpc.exception.NotifyMessageRtException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.ext.EXT;
import mpc.json.UGson;
import mpc.str.sym.SYMJ;
import mpe.core.ERR;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;
import mpu.str.JOIN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.XulElement;
import zk_com.base.Tbxm;
import zk_com.base.Xml;
import zk_com.win.HideBy;
import zk_com.win.WinPos;
import zk_page.ZKM;
import zk_page.ZKM_Editor;

import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public class ZKI {

	public static final Logger L = LoggerFactory.getLogger(ZKI.class);
	public static final Logger ZLOG = new ZkLogger();

	public static void log(CharSequence msg, Object... args) {
		//Clients.evalJavaScript("zk.log('" + message.replaceAll("\'", "\\'") + "');");
		if (Executions.getCurrent() != null) {
			Clients.log(X.fa(msg, args));
		}
	}

	public static Window info_ext_modal_MSG(String title, String msg, Level level) {
		return NotifyLevelDiv.ofMsg(msg, level)._modal()._closable(true)._pos(WinPos.center)._title(title != null ? title : level.icon() + " " + level.namehu())._showInWindow();
	}

	public static Window info_ext_modal_HTML(String title, String data, Level level) {
		return NotifyLevelDiv.ofHtml(data, level)._modal()._closable()._pos(WinPos.center)._title(title != null ? title : level.icon() + " " + level.namehu())._showInWindow();
	}

	public static void infoAfterPointer(String message, ZKI.Level... level) {
		NotifyRef.showAfterPointer(message, ARG.toDefOrNull(level));
	}

	public static void infoBottomCenter(String message, ZKI.Level... level) {
		NotifyRef.showBootomCenter(message, ARG.toDefOrNull(level));
	}

//	public static void orangeAfterPointer(String message, NotifyMessageRtException.LEVEL... level) {
//		NotifyRef.showAfterPointer(message, ARG.toDefOrNull(level));
//	}

	public enum Level {
		ERR, WARN, INFO;

		public String namehu() {
			switch (this) {
				case INFO:
					return "Info";
				case WARN:
					return "Warning";
				case ERR:
					return "Error";
				default:
					throw new WhatIsTypeException(this);
			}
		}

		public String icon() {
			switch (this) {
				case INFO:
					return SYMJ.OK_GREEN;
				case WARN:
					return SYMJ.WARN;
				case ERR:
					return SYMJ.FAIL_STOP;
				default:
					throw new WhatIsTypeException(this);
			}
		}

		public NotifyMessageRtException.LEVEL toLevelColor() {
			switch (this) {
				case INFO:
					return NotifyMessageRtException.LEVEL.GREEN;
				case WARN:
					return NotifyMessageRtException.LEVEL.BLUE;
				case ERR:
					return NotifyMessageRtException.LEVEL.RED;
				default:
					throw new WhatIsTypeException(this);
			}
		}
	}

	public enum Type {
		VOID, LOGBACK, //no GUI
		LOG, //
		INFO_BOTTOM_RIGHT, WARN_BOTTOM_RIGHT, ERR_BOTTOM_RIGHT, //
		MODAL_BW, MODAL_JSON_BW, MODAL_JSON, MODAL_HTML, MODAL_HTML_BW, //
		WARN_MB, INFO_MB, ERR_MB, QUEST_MB, //
		INFO_EXT,
		WARN_EXT,
		ERR_EXT, //

		//
		;

		public void showView(Throwable ex, CharSequence head_or_title) {
			switch (this) {
				case INFO_EXT:
					NotifyLevelDiv.ofMsg(ERR.getMessagesAsStringWithHead(ex, null, true).toString(), Level.INFO).//
							_modal()._title(Level.INFO.icon() + " " + head_or_title)._closable()._sizable()._showInWindow();
					break;
				case WARN_EXT:
					NotifyLevelDiv.ofMsg(ERR.getMessagesAsStringWithHead(ex, null, true).toString(), Level.WARN).//
							_modal()._title(Level.WARN.icon() + " " + head_or_title)._closable()._sizable()._showInWindow();
					break;
				case ERR_EXT:
					NotifyLevelDiv.ofMsg(ERR.getMessagesAsStringWithHead(ex, null, true).toString(), Level.ERR).//
							_modal()._title(Level.ERR.icon() + " " + head_or_title)._closable()._sizable()._showInWindow();
					break;

				default:
					showView(ERR.getMessagesAsStringWithHead(ex, head_or_title.toString(), true));
			}
		}

		public void showView(CharSequence msg) {
			showView((String) null, msg);
		}

		public void showView(String title, CharSequence msg) {
			switch (this) {
				case VOID:
					//nothing
					break;
				case LOGBACK:
					L.info(msg.toString());
					break;
				case LOG:
					log(msg);
					break;

//				case ALERT_MB:
//					ZKI.alert(msg);
//					ZKI_Modal.showMessageBoxRed(msg.toString());
//					break;

				case INFO_MB:
					ZKI_Messagebox.showMessageBox(title(title, Level.INFO), msg.toString(), Messagebox.INFORMATION);
					break;
				case WARN_MB:
					ZKI_Messagebox.showMessageBox(title(title, Level.WARN), msg.toString(), Messagebox.EXCLAMATION);
					break;
				case ERR_MB:
					ZKI_Messagebox.showMessageBox(title(title, Level.ERR), msg.toString(), Messagebox.ERROR);
					break;
				case QUEST_MB:
					ZKI_Messagebox.showMessageBox(title(title, Level.INFO), msg.toString(), Messagebox.QUESTION);
					break;

				case INFO_BOTTOM_RIGHT:
					ZKI.showMsgBottomRightFast_INFO(msg);
					break;
				case WARN_BOTTOM_RIGHT:
					ZKI.showMsgBottomRightSlow(Level.WARN, msg);
					break;
				case ERR_BOTTOM_RIGHT:
					ZKI.showMsgBottomRightSlow(Level.ERR, msg);
					break;

				case MODAL_BW:
					ZKI.infoEditorBw(msg);
					break;
				case MODAL_JSON_BW:
					ZKM_Editor.openEditorJson(title(title, Level.INFO), msg.toString(), true);
//					ZKI.infoEditorJson(msg, true);
					break;
				case MODAL_JSON:
					ZKM_Editor.openEditorJson(title(title, Level.INFO), msg.toString(), false);
					break;
				case MODAL_HTML:
					ZKI.infoEditorHtmlView(title(title, Level.INFO), msg.toString(), true);
					break;
				case MODAL_HTML_BW:
					ZKI.infoEditorHtmlView(title(title, Level.INFO), msg.toString(), false);
					break;

				case INFO_EXT:
					info_ext_modal_MSG(null, msg.toString(), Level.INFO);
					break;
				case WARN_EXT:
					info_ext_modal_MSG(null, msg.toString(), Level.WARN);
					break;
				case ERR_EXT:
					info_ext_modal_MSG(null, msg.toString(), Level.ERR);
					break;

				default:
					throw new WhatIsTypeException(this);
			}
		}

		private String title(String title, Level level) {
			return title != null ? title : level.icon() + " " + level.namehu();
		}

		public boolean isWebType() {
			switch (this) {
				case VOID:
				case LOGBACK:
					return false;
				default:
					return true;
			}
		}
	}


	/**
	 * *************************************************************
	 * ----------------------------  ALERT ----------------------------
	 * *************************************************************
	 */

	public static void alert(CharSequence message, Object... args) {
		Clients.alert(X.f(message, args));
	}

	public static void alert(Throwable err) {
		alert(err, ERR.UNHANDLED_ERROR, true);
//		Clients.alert(ERR.getMessageWithType(err));
	}

	public static void alert(Throwable err, String head, boolean... ol) {
		L.error(head, err);
		Clients.alert(ERR.getMessagesAsStringWithHead(err, head, ol));
	}


	/**
	 * *************************************************************
	 * ----------------------------  INFO ----------------------------
	 * *************************************************************
	 */

	public static void infoSingleLine(CharSequence message, Object... args) {
		Clients.showNotification(X.f(message, args));
	}

	public static void errorSingleLine(CharSequence message, Object... args) {
		Clients.showNotification(X.f(message, args), "error", null, "center", 5000);
	}

	public static Pare<Window, Tbxm> infoEditorBw(Path file) {
		return infoEditorBw(file, UGson.isGsonContent(file) ? EXT.JSON : EXT.TXT);
	}

	public static Pare<Window, Tbxm> infoEditorBw(Path file, EXT type) {
		return ZKM_Editor.openEditorText(file, EXT.JSON == type, true);
	}

	public static Pare<Window, Tbxm> infoEditorBw(Collection<String> lines) {
		return infoEditorBw(JOIN.allByNL(lines));
	}

	public static XulElement infoEditorJson(Collection jsons) {
		return infoEditorJson(JOIN.allByNL(jsons), true);
	}

	public static XulElement infoEditorJson(Object singleJson, boolean linent_many_parts) {
		Supplier<String> stringSupplier = () -> linent_many_parts ? UGson.toStringPrettyLinent(singleJson.toString()) : UGson.toStringPretty(singleJson.toString());
		Function<String, Boolean> saveCallback = null;
		return ZKM_Editor.openEditorWithBtSave(stringSupplier, saveCallback, null, false);
	}

	public static Pare<Window, Tbxm> errorEditorBw(CharSequence msg, Object... args) {
		return ZKM_Editor.openEditorText(SYMJ.WARN + " Error", X.f(msg, args), true);
	}

	public static Pare<Window, Tbxm> infoEditorBwTitle(String title, CharSequence msg, Object... args) {
		return ZKM_Editor.openEditorText(SYMJ.INFO_SIMPLE + " " + title, X.f(msg, args), true);
	}

	public static Pare<Window, Tbxm> infoEditorBw(CharSequence msg, Object... args) {
		return ZKM_Editor.openEditorText(SYMJ.INFO_SIMPLE + " Info", X.f(msg, args), true);
	}

	public static Pare<Window, Tbxm> infoEditorBw(Object title_cap_com, CharSequence msg, Object... args) {
		return ZKM_Editor.openEditorText(title_cap_com, X.f(msg, args), true);
	}

	@Deprecated
	public static void showMsgBottomRightFast_INFO(CharSequence msg, Object... args) {
		NotifyCustom.ViewPosition.BOTTOM_RIGHT.show(X.f(msg, args), HideBy.TIMEOUT_FAST, Level.INFO);
	}

	@Deprecated
	public static void showMsgBottomRightSlow(ZKI.Level level, CharSequence msg, Object... args) {
		NotifyCustom.ViewPosition.BOTTOM_RIGHT.show(X.f(msg, args), HideBy.TIMEOUT_SLOW, level);
	}

	public static void infoEditorHtmlView(Object title_cap_com, String xmlData, boolean... darkTheme) {
		ZKM.showModal(title_cap_com, Xml.ofXml(xmlData), darkTheme);
	}

}
