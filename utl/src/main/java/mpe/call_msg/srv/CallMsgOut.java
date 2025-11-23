package mpe.call_msg.srv;

import lombok.Getter;
import mpu.X;
import mpu.func.FunctionV1;
import mpu.func.FunctionV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallMsgOut {

	public static final Logger L = LoggerFactory.getLogger(CallMsgOut.class);

	public enum SyncAsyncCallType {
		WEB_SYNC, WEB_ASYNC, REST_SYNC, REST_ASYNC;
	}

	private final @Getter FunctionV2<String, String> writerState;
	private final @Getter FunctionV1<CharSequence> writerInfo;
	private final @Getter FunctionV2<Throwable, CharSequence> writerAlert;

	public static final FunctionV2<CharSequence, CharSequence> defaultWriterInfo = (i, e) -> {
		if (X.notEmpty(i)) {
			L.info(i + "");
		}
		if (X.notEmpty(e)) {
			L.error(e + "");
		}
	};

	private final @Getter SyncAsyncCallType syncAsyncCall;

	public CallMsgOut(SyncAsyncCallType syncAsyncCall, FunctionV2<String, String> writerState, FunctionV1<CharSequence> writerInfo, FunctionV2<Throwable, CharSequence> writerAlert) {
		this.writerState = writerState;
		this.writerInfo = writerInfo;
		this.writerAlert = writerAlert;

		this.syncAsyncCall = syncAsyncCall;
//		if (this instanceof WebSync_CallOutWriter0) {
////			this.syncAsyncCall = SyncAsyncCallType.WEB_SYNC;
//		} else if (this instanceof WebAsync_CallOutWriter0) {
////			this.syncAsyncCall = SyncAsyncCallType.WEB_ASYNC;
//		} else if (this instanceof RestSync_CallOutWriter0) {
////			this.syncAsyncCall = SyncAsyncCallType.REST_SYNC;
//		} else if (this instanceof RestAsync_CallOutWriter0) {
////			this.syncAsyncCall = SyncAsyncCallType.REST_ASYNC;
//		} else {
//			throw new WhatIsTypeException(getClass());
//		}
	}

	public static class RestSync_CallOutWriter0 extends CallMsgOut {
		public RestSync_CallOutWriter0(FunctionV2<String, String> writerState, FunctionV1<CharSequence> writerInfo, FunctionV2<Throwable, CharSequence> writerAlert) {
			super(SyncAsyncCallType.REST_SYNC, writerState, writerInfo, writerAlert);
		}
	}

	public static class RestAsync_CallOutWriter0 extends CallMsgOut {

		public RestAsync_CallOutWriter0(FunctionV2<String, String> writerState, FunctionV1<CharSequence> writerInfo, FunctionV2<Throwable, CharSequence> writerAlert) {
			super(SyncAsyncCallType.REST_ASYNC, writerState, writerInfo, writerAlert);
		}

	}

	public static class WebSync_CallOutWriter0 extends CallMsgOut {
		public WebSync_CallOutWriter0(FunctionV2<String, String> writerState, FunctionV1<CharSequence> writerInfo, FunctionV2<Throwable, CharSequence> writerAlert) {
			super(SyncAsyncCallType.WEB_SYNC, writerState, writerInfo, writerAlert);
		}
	}

	public static class WebAsync_CallOutWriter0 extends CallMsgOut {
		public WebAsync_CallOutWriter0(FunctionV2<String, String> writerState, FunctionV1<CharSequence> writerInfo, FunctionV2<Throwable, CharSequence> writerAlert) {
			super(SyncAsyncCallType.WEB_ASYNC, writerState, writerInfo, writerAlert);
		}
	}

	public void write_state(String okData, String errData) {
		if (writerState == null) {
			if (L.isDebugEnabled()) {
				L.debug("WriterState is off");
			}
		} else {
			writerState.apply(okData, errData);
		}
	}

	public void write_info(CharSequence okData) {
		if (writerInfo == null) {
			if (L.isDebugEnabled()) {
				L.debug("WriterInfo is off");
			}
		} else {
			writerInfo.apply(okData);
		}
	}

	public void write_alert(Throwable err, CharSequence head) {
		if (writerAlert == null) {
			if (L.isDebugEnabled()) {
				L.debug("WriterAlert is off");
			}
		} else {
			writerAlert.apply(err, head);
		}
	}

}
