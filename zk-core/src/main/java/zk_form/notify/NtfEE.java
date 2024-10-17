package zk_form.notify;

import mpu.core.ARG;
import mpu.core.ENUM;
import mpu.IT;
import mpe.core.ERR;
import mpc.exception.EException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Notification;

public class NtfEE extends EException {

	private static final long serialVersionUID = 1L;

	public NtfLevel type() {
		return ENUM.getEnum(index(), NtfLevel.class);
	}

	public NtfEE(NtfLevel error) {
		super(error);
	}

	public NtfEE(NtfLevel error, Throwable cause) {
		super(error, cause);
	}

	public static void main(String[] args) {
		NtfLevel.WARN.I("goo").closable().position(NPos.before_end).duration(5000).show();
//		Ntf.LEVEL.
	}

	public void show() {
		String msg = ERR.getCauseMessageOr(getCause(), null, null);
		if (msg == null) {
			msg = getMessage();
		}
		Notification.show(IT.NE(msg, "set msg"), type().clazz(), ref, position == null ? null : position.name(), duration, closable);
	}

	Component ref = null;

	public NtfEE ref(Component... ref) {
		if (ARG.isDef(ref)) {
			this.ref = ARG.toDef(ref);
		}
		return this;
	}

	boolean closable = false;

	public NtfEE closable(boolean... closable) {
		this.closable = ARG.isDefNotEqFalse(closable);
		return this;
	}

	private NPos position;

	public NtfEE position(NPos position) {
		this.position = position;
		return this;
	}

	private int duration;

	public NtfEE duration(int duration) {
		this.duration = duration;
		return this;
	}

	//https://www.zkoss.org/zkdemo/popup/popup_position
	public enum NPos {
		before_start, before_end, end_before, end_after, after_end, after_start, start_after, start_before,//
		overlap, overlap_end, overlap_before, overlap_after, at_pointer, after_pointer
	}
}
