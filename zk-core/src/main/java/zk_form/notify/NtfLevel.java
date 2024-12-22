package zk_form.notify;

import mpu.core.ARG;
import mpc.exception.NotifyMessageRtException;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.SimpleMessageRuntimeException;
import mpc.exception.WhatIsTypeException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;
import zk_com.win.WinPos;

//		NtfEE.LEVEL.ERR.I("connect remote fs from swby").closable().position(NtfEE.NPos.overlap_end).ref(getAdgnaOrCreate()).show();
//		NotifyPanel.View.TOP_CENTER.show("connect remote fs from swby", NotifyPanel.HideBy.DEFAULT, NtfLevel.WRN);
//		SWindow.of(new Html("connect remote fs from swby")).position(SWindow.WPos.TC).popup().hl().show();
//		MWindow.show("Warning", NtfLevel.WRN.toDiv("connect remote fs from swby"));
//      NtfDiv
//		NtfLevel.WRN.toDivMsg("connect remote fs from swby")._modal()._closable(false)._pos(WPos.center)._title("Warning")._showInWindow();
public enum NtfLevel {
	ERR, WARN, INFO;

	public static NtfLevel of(NotifyMessageRtException.LEVEL level, NtfLevel... defRq) {
		switch (level) {
			case RED:
				return ERR;
			case BLUE:
				return WARN;
			case GREEN:
				return INFO;
			default:
				return ARG.toDefThrow(() -> new RequiredRuntimeException("Unknown LEVEL type '%s'", level), defRq);
		}
	}

	public NtfDiv toDivMsg(String msg) {
		return NtfDiv.ofMsg(msg, this);
	}

	public NtfDiv toDivHtml(String html) {
		return NtfDiv.ofHtml(html, this);
	}

	public NtfDiv toDivCom(Component com) {
		return NtfDiv.of(com, this);
	}

	public String clazz() {
		switch (this) {
			case INFO:
				return "info";
			case ERR:
				return "error";
			case WARN:
				return "warning";
			default:
				throw new WhatIsTypeException(this);
		}
	}

	public String title() {
		switch (this) {
			case INFO:
				return "Info";
			case ERR:
				return "Error";
			case WARN:
				return "Warning";
			default:
				throw new WhatIsTypeException(this);
		}
	}

	public void ON() throws NtfEE {
		throw I();
	}

	public NtfEE I() {
		return new NtfEE(this);
	}

	public NtfEE I(String message, Object... args) {
		return new NtfEE(this, new SimpleMessageRuntimeException(message, args));
	}

	public NtfEE I(Exception ex) {
		return new NtfEE(this, ex);
	}

	public Window openWindow(String msg, String title) {
		Window window = toDivMsg(msg)._modal()._closable(true)._pos(WinPos.center)._title(title == null ? title() : title)._showInWindow();
		return window;
	}
}
