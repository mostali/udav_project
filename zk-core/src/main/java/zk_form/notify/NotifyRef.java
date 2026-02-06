package zk_form.notify;

import lombok.Builder;
import mpc.exception.NotifyMessageRtException;
import mpu.core.ARG;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;

//ClientNotification's
@Builder
public class NotifyRef {

	public static final int DEF_DURATION = 5000;
	public static final Pos DEF_POS = Pos.bottom_right;
	private final NotifyMessageRtException.LEVEL level;
	private final ZKI.Level levelZki;
	private final String msg;
	private final Pos pos;
	private final int duration;
	private Component ref;

	public NotifyRef ref(Component ref) {
		this.ref = ref;
		return this;
	}

	@Deprecated//always in top
	public static void showBootomCenter(String message, ZKI.Level level) {
		NotifyMessageRtException.LEVEL lev = level == null ? NotifyMessageRtException.LEVEL.LOG : level.toLevelColor();
		NotifyRef.builder().level(lev).duration(DEF_DURATION).pos(ARG.toDefOr(DEF_POS, Pos.bottom_center)).msg(message).build().show();
	}

	public static void showAfterPointer(String message, ZKI.Level level) {
		NotifyMessageRtException.LEVEL lev = level == null ? NotifyMessageRtException.LEVEL.LOG : level.toLevelColor();
		NotifyRef.builder().level(lev).duration(DEF_DURATION).pos(ARG.toDefOr(DEF_POS, Pos.after_pointer)).msg(message).build().show();
	}

	public static void showAfterPointer(String message, NotifyMessageRtException.LEVEL lev) {
		NotifyRef.builder().level(lev).duration(DEF_DURATION).pos(ARG.toDefOr(DEF_POS, Pos.after_pointer)).msg(message).build().show();
	}

	public static NotifyRef ERR(String message, Pos... pos) {
		return NotifyRef.builder().level(NotifyMessageRtException.LEVEL.RED).duration(DEF_DURATION).pos(ARG.toDefOr(DEF_POS, pos)).msg(message).build();
	}

	public static NotifyRef INFO(String message, Pos... pos) {
		return NotifyRef.builder().level(NotifyMessageRtException.LEVEL.GREEN).duration(DEF_DURATION).pos(ARG.toDefOr(DEF_POS, pos)).msg(message).build();
	}

	public static NotifyRef WARN(String message, Pos... pos) {
		return NotifyRef.builder().level(NotifyMessageRtException.LEVEL.BLUE).duration(DEF_DURATION).pos(ARG.toDefOr(DEF_POS, pos)).msg(message).build();
	}

	public void show() {
		Clients.showNotification(msg, level.namesys(), ref, pos.name(), duration);
	}


	//https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Popup
	//https://www.zkoss.org/javadoc/latest/zk/org/zkoss/zk/ui/util/Notification.html
	public enum Pos {
		before_start,//the message appears above the anchor, aligned to the left.

		before_center, //		the message appears above the anchor, aligned to the center.
		before_end, //		the message appears above the anchor, aligned to the right.
		after_start, //		the message appears below the anchor, aligned to the left.
		after_center, //		the message appears below the anchor, aligned to the center.
		after_end, //		the message appears below the anchor, aligned to the right.
		start_before, //		the message appears to the left of the anchor, aligned to the top.
		start_center, //		the message appears to the left of the anchor, aligned to the middle.
		start_after, //		the message appears to the left of the anchor, aligned to the bottom.
		end_before, //		the message appears to the right of the anchor, aligned to the top.
		end_center, //		the message appears to the right of the anchor, aligned to the middle.
		end_after, //		the message appears to the right of the anchor, aligned to the bottom.
		overlap,
		top_left, //		the message overlaps the anchor, with anchor and message aligned at top-left.
		top_center, //		the message overlaps the anchor, with anchor and message aligned at top-center.
		overlap_end,
		top_right, //		the message overlaps the anchor, with anchor and message aligned at top-right.
		middle_left, //		the message overlaps the anchor, with anchor and message aligned at middle-left.
		middle_center, //		the message overlaps the anchor, with anchor and message aligned at middle-center.
		middle_right, //		the message overlaps the anchor, with anchor and message aligned at middle-right.
		overlap_before,
		bottom_left, //		the message overlaps the anchor, with anchor and message aligned at bottom-left.
		bottom_center, //		the message overlaps the anchor, with anchor and message aligned at bottom-center.
		overlap_after,
		bottom_right, //		the message overlaps the anchor, with anchor and message aligned at bottom-right.
		at_pointer, //		the message appears with the upper-left aligned with the mouse cursor.
		after_pointer //    the message appears with the top aligned with the bottom of the mouse cursor, with the left side of the message at the horizontal position of the mouse cursor.

	}
}
