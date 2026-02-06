package mpe.restapp;

import mpc.exception.FIllegalStateException;
import mpc.exception.RequiredRuntimeException;
import mpc.log.L;
import mpe.core.ERR;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;
import mpu.str.STR;

import java.io.IOException;
import java.nio.file.Path;

public class Rsp extends Pare<Integer, Object> {

	public static final Rsp ASYNC = Rsp.of(0, 0);

	public Rsp(Integer code, Object val) {
		super(code, val);
	}

	public static Rsp of(Integer code, Object rsp) {
		return new Rsp(code, rsp);
	}

	public static STATE checkCodeHttpType(STATE state) {
		switch (state) {
			case INFO:
			case OK:
			case RDR:
			case CLI:
			case SRV:
				return state;
			default:
				throw new FIllegalStateException("Except only httpStatus <- '%s'", state);
		}
	}

	public static Rsp ok(Path file) {
		return new Rsp(200, file);
	}

	public Throwable err() {
		return (Throwable) val();
	}

	public static Rsp err(String msg, Throwable err) {
		return of(500, msg + STR.NL + ERR.getStackTrace(err));
	}

	public static Rsp ok(String rsp) {
		return of(200, rsp);
	}

	//
	//
	//
	public static boolean isInformational(int code) {
		return 100 <= code && code <= 199;
	}

	public static boolean isSuccess(int code) {
		return 200 <= code && code <= 299;
	}

	public static boolean isRedirection(int code) {
		return 300 <= code && code <= 399;
	}

	public static boolean isClientError(int code) {
		return 400 <= code && code <= 499;
	}

	public static boolean isServerError(int code) {
		return 500 <= code && code <= 599;
	}

	public enum STATE {
		EMPTY, ASYNC, UNDEFINED,//
		INFO, OK, RDR, CLI, SRV;

		public static STATE ofRsp(Pare<Integer, Object> rsp) throws IOException {
			if (rsp == null || rsp.empty()) {
				return EMPTY;
			} else if (rsp.key() == 0 && X.equals(0, rsp.val())) {
				return ASYNC;
			} else if (rsp.key() == null) {
				L.warn("Rsp has UNDEFINED state:" + rsp);
				return UNDEFINED;
			}
			return ofCode(rsp.key(), UNDEFINED);
		}

		public static STATE ofCode(int code, STATE... defRq) {
			if (isInformational(code)) {
				return INFO;
			} else if (isSuccess(code)) {
				return OK;
			} else if (isRedirection(code)) {
				return RDR;
			} else if (isClientError(code)) {
				return CLI;
			} else if (isServerError(code)) {
				return SRV;
			}
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Except legal http-code '%s'", code), defRq);
		}

	}
}
