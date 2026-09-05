package mpt;

import lombok.RequiredArgsConstructor;
import mpu.core.ARG;
import mpu.IT;
import mpu.core.EQ;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.SimpleMessageRuntimeException;
import mpu.str.JOIN;
import mpe.str.StringWalkBuilder;
import mpu.core.QDate;
import mpu.X;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class TrmRsp extends RuntimeException {

	public static final int STATUS_OK = 0;
	public static final int STATUS_FAIL = -1;
	public static final int STATUS_ERR = 1;
	public static final String MSG_OK = "OK";
	public static final String MSG_ERR = "ERR";
	public static final String MSG_FAIL = "FAIL";

	private final int exitCode;
	private final AtomicReference result;
	private final AtomicReference view;

	private final long created = System.currentTimeMillis();

	//----------------------------------------------------
	//----------------------------------------------------
	public int exitcode() {
		return exitCode;
	}

	public String msg() {
		return super.getMessage();
	}

	public Throwable error() {
		return getCause();
	}

	public Status status() {
		return Status.of(exitCode);
	}

	//----------------------------------------------------
	//----------------------------------------------------
	public boolean isOk() {
		return status() == Status.OK;
	}

	public boolean isFail() {
		return status() == Status.FAIL;
	}

	public boolean isError() {
		return status() == Status.ERR;
	}

	//----------------------------------------------------
	//----------------------------------------------------
	public boolean hasResult() {
		return result.get() != null;
	}

	public boolean hasError() {
		return getCause() != null;
	}

	public boolean hasView() {
		return view != null && view.get() != null;
	}

	//----------------------------------------------------
	//----------------------------------------------------
	public TrmRsp() {
		this(STATUS_OK, null, MSG_OK);
	}

	public TrmRsp(CharSequence message) {
		this(STATUS_OK, null, message);
	}

	public TrmRsp(int status, Object result, CharSequence message) {
		this(status, result, null, message);
	}

	public TrmRsp(int status, Object result, Object view, CharSequence message) {
		super(message == null ? null : message.toString());
		this.result = new AtomicReference(result);
		this.view = new AtomicReference(view);
		exitCode = status;
	}

	public TrmRsp(int status, Object result, CharSequence message, Throwable error) {
		super(message == null ? null : message.toString(), error);
		this.result = new AtomicReference(result);
		this.view = null;
		exitCode = status;
	}

	/**
	 * *************************************************************
	 * ---------------------------- GET -------------------------
	 * *************************************************************
	 */
	public Object getView(Object... defRq) {
		Object o = view == null ? null : view.get();
		if (o != null) {
			return o;
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("View is null");
	}

	public Throwable getError(Throwable... defRq) {
		Throwable e = error();
		if (e != null) {
			return e;
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Error is null");
	}

	public <T> T getResult(T... defRq) {
		Object o = result.get();
		if (o != null) {
			return (T) o;
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Result is null");
	}

	/**
	 * *************************************************************
	 * ---------------------------- GET EXT -------------------------
	 * *************************************************************
	 */
	@NotNull
	public Object getResultOrMessage() {
		if (hasResult()) {
			return getResult();
		}
		return msg();
	}

	@NotNull
	public String getMessageDetails(String... defRq) {
		if (isNotDefMsg(msg())) {
			return msg();
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("MessageDetails not found");
	}

	/**
	 * *************************************************************
	 * ---------------------------- ThrowIf -------------------------
	 * *************************************************************
	 */
	public TrmRsp throwIsNoOk() {
		return throwIs(Status.ERR).throwIs(Status.FAIL);
	}

	public TrmRsp throwIs(Status state) {
		if (status() == state) {
			throw this;
		}
		return this;
	}


	/**
	 * *************************************************************
	 * ---------------------------- OK -------------------------
	 * *************************************************************
	 */

	public static TrmRsp OK() {
		return new TrmRsp();
	}

	public static TrmRsp OK(String[][] info) {
		return OK(StringWalkBuilder.getInfoString(info, StringWalkBuilder.ARGS_LINE_BUILD));
	}

	public static TrmRsp OK(CharSequence msg, Object... args) {
		return new TrmRsp(X.f(msg.toString(), args));
	}

	public static TrmRsp OK(Collection msg) {
		return new TrmRsp(JOIN.allByNL(msg));
	}

	public static TrmRsp OKR(Object result, String message, Object... args) {
		return new TrmRsp(STATUS_OK, result, X.f(message, args));
	}

	public static TrmRsp OKm(String message, Object... args) {
		return new TrmRsp(X.fm(message, args));
	}

	public static TrmRsp OKR(Object result) {
		return new TrmRsp(STATUS_OK, result, MSG_OK);
	}

	//--------------------------OBJ------------------------
	public static TrmRsp OBJ(Object result) {
		return new TrmRsp(STATUS_OK, result, null, MSG_OK);
	}

	//--------------------------MSG------------------------
	public static TrmRsp MSG(CharSequence msg) {
		return new TrmRsp("\n" + msg);
	}

	public static TrmRsp MSG(Collection msg) {
		String collect = JOIN.allByNL(msg).toString();
		return new TrmRsp("\n" + collect);
	}

	/**
	 * *************************************************************
	 * ---------------------------- ERR -------------------------
	 * *************************************************************
	 */

	public static TrmRsp ERR(Throwable error, String message, Object... args) {
		return ERR(error, STATUS_ERR, null, message, args);
	}

	public static TrmRsp ERR(String msg, Object... args) {
		return ERR(null, STATUS_ERR, null, msg, args);
	}

	public static TrmRsp ERR(Throwable error) {
		return ERR((Object) null, STATUS_ERR, error, MSG_ERR);
	}

	public static TrmRsp ERR(Object result, int status, Throwable error, String message, Object... msgArgs) {
		String msgFail = X.f(message, msgArgs);
		TrmRsp trmResponse = error == null ? new TrmRsp(IT.isPosNotZero(status), result, msgFail) : new TrmRsp(IT.isPosNotZero(status), result, msgFail, error);
		return trmResponse;
	}

	/**
	 * *************************************************************
	 * ---------------------------- FAIL -------------------------
	 * *************************************************************
	 */

	public static TrmRsp FAIL(String msg, Object... args) {
		return FAIL(new SimpleMessageRuntimeException(msg, args));
	}

	public static TrmRsp FAIL(Throwable error) {
		return FAIL(error, STATUS_FAIL, null, MSG_FAIL);
	}

	public static TrmRsp FAIL(Throwable error, String msg, Object... args) {
		return FAIL(error, STATUS_FAIL, null, msg, args);
	}

	public static TrmRsp FAIL(Throwable error, int status, Object result, String msg, Object... msgArgs) {
		String msgFail = X.f(msg, msgArgs);
		TrmRsp trmResponse = new TrmRsp(IT.isNegNotZero(status), result, msgFail, IT.NN(error));
		return trmResponse;
	}

	/**
	 * *************************************************************
	 * ---------------------------- VIEW -------------------------
	 * *************************************************************
	 */

	public static TrmRsp VIEW(CharSequence view) {
		return new TrmRsp(STATUS_OK, null, view, MSG_OK);
	}

	public String statusWithCodeWithMessage() {
		String msg = msg();
		return isDefMsg(msg) ? statusWithCode() : statusWithCode(true) + ":" + msg;
	}

	public String statusWithCode(boolean... wrapedStatus) {
		if (isDefStatus(exitcode())) {
			return status().name();
		} else if (ARG.isDefEqTrue(wrapedStatus)) {
			return status() + "[" + exitcode() + "]";
		}
		return status() + ":" + exitcode();
	}

	/**
	 * *************************************************************
	 * ---------------------------- OTHER -------------------------
	 * *************************************************************
	 */

	public static boolean isNotDefMsg(String msg) {
		return !isDefMsg(msg);
	}

	public static boolean isDefMsg(String msg) {
		return EQ.equalsStringIgnoreCaseAny(msg, MSG_OK, MSG_ERR, MSG_FAIL);
	}

	public static boolean isDefStatus(int status) {
		return status < 2 && status >= -2;
	}


	@Override
	public String toString() {
		return "TrmRsp{" + "code=" + exitCode + ", msg=" + getMessage() + ", result=" + result +
//			   ", out=" + out +
				", err=" + error() + ", created=" + QDate.of(created).f(QDate.F.MONO20NF) + '}';
	}

	public enum Status {
		OK, ERR, FAIL;

		public static Status of(int exitCode) {
			return exitCode == 0 ? Status.OK : (exitCode > 0 ? Status.ERR : Status.FAIL);
		}
	}
}
