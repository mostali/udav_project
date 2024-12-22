package zk_form.notify;

import mpu.core.ARG;
import mpu.core.ENUM;
import mpc.exception.EException;
import mpc.exception.NotifyMessageRtException;
import org.zkoss.zk.ui.Component;
import zk_com.base.Lb;
import zk_com.win.Win0;
import zk_com.win.WinPos;
import zk_com.elements.Pos6TRBL_H;

public class TstEE extends EException {

	private static final long serialVersionUID = 1L;

	public NotifyMessageRtException.LEVEL type() {
		return ENUM.getEnum(index(), NotifyMessageRtException.LEVEL.class);
	}

	public TstEE(NotifyMessageRtException.LEVEL error) {
		super(error);
	}

	public TstEE(NotifyMessageRtException.LEVEL error, Throwable cause) {
		super(error, cause);
	}

//	public enum LEVEL {
//		ERR, WRN, INFO;
//
//		public void ON() throws TstEE {
//			throw I();
//		}
//
//		public TstEE I() {
//			return new TstEE(this);
//		}
//
//		public TstEE I(String message, Object... args) {
//			return new TstEE(this, new SimpleMessageRuntimeException(message, args));
//		}
//
//		public TstEE I(Exception ex) {
//			return new TstEE(this, ex);
//		}
//
//		public String clazz() {
//			switch (this) {
//				case INFO:
//					return "info";
//				case ERR:
//					return "error";
//				case WRN:
//					return "warning";
//				default:
//					throw new WhatIsTypeException(this);
//			}
//		}
//	}

	public static void main(String[] args) {
//		LEVEL.WRN.I("goo").closable().position(NPos.before_end).duration(5000).show();
//		Ntf.LEVEL.
	}

	public void show() {
//		Notification.show(UC.NE(msg, "set msg"), type().clazz(), ref, position == null ? null : position.name(), duration, closable);
//		Toast.show(UC.NE(msg, "set msg"), type().clazz(), ref, position == null ? null : position.name(), duration, closable);
//		Toast.show("This toast is closable", rgType.getSelectedItem().getLabel(), "middle_" + rgPosition.getSelectedItem().getLabel(), 2000, false);

		Win0 of = Win0.of(Lb.of(getMessage()));
		if (closable) {
			of.setClosable(closable);
		}
		of.ovl();
//		of.popup();
//		of.hl();
//		of.embd();

//		of.setCLASS(ZkTheme.DIV_NOTIFY_PARENT_BR);
//		of.setContentSclass(ZkTheme.DIV_NOTIFY_CHILD);
//		ZKS.padding0(of);

		of.position(position).show();
	}

	Component ref = null;

	public TstEE ref(Component ref) {
		this.ref = ref;
		return this;
	}

	boolean closable = false;

	public TstEE closable(boolean... closable) {
		this.closable = ARG.isDefNotEqFalse(closable);
		return this;
	}

	private String position;

	public TstEE position(WinPos position) {
		this.position = position.name();
		return this;
	}

	public TstEE position(Pos6TRBL_H pos6TRBLH) {
		this.position = pos6TRBLH.name();
		return this;
	}

//	private int duration;
//
//	public TstEE duration(int duration) {
//		this.duration = duration;
//		return this;
//	}

	//https://www.zkoss.org/zkdemo/popup/popup_position
//	public enum NPos {
//		before_start, before_end, end_before, end_after, after_end, after_start, start_after, start_before,//
//		overlap, overlap_end, overlap_before, overlap_after, at_pointer, after_pointer
//	}
}
