package utl_rest.core;

import mpu.core.ARG;
import mpu.IT;
import mpc.exception.ICleanMessage;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.str.sym.SYMJ;
import mpu.str.STR;
import mpt.TrmRsp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import utl_rest.ResponseException;
import utl_rest.SrcResponseEntity;
import utl_web.UWeb;

import javax.servlet.http.HttpServletRequest;

public abstract class RestCall<T> {

	private static final Logger L = LoggerFactory.getLogger(RestCall.class);

	final String call_name;
	final Logger logger;

	boolean use_global_error_handler = true;
	HttpServletRequest request;

	public RestCall(String call_name, Logger... logger) {
		this.call_name = call_name;
		this.logger = ARG.toDefOr(L, logger);
	}

	private CharSequence rqKey;

	public CharSequence rqKey() {
		return rqKey;
	}

	public ResponseEntity<T> doCall() throws Throwable {

		rqKey = "" + STR.randAlpha(3) + "/" + call_name;

		if (L.isInfoEnabled()) {
			rqKey = rqKey + "/" + getAuthPrincipal();
		}

		if (request != null && L.isDebugEnabled()) {
			rqKey = rqKey + "/" + UWeb.getClientIpAddress(request);
		}
		if (logger.isInfoEnabled()) {
			logger.info(SYMJ.ARROW_DOWN + SYMJ.ARROW_DOWN + rqKey + " >> " + (request != null ? UWeb.getQueryString(request) : "''"));
		}
		ResponseEntity<T> rspEntity = null;
		Exception clientEx = null;
		Throwable serverEx = null;
		try {

			beforeCall();
			rspEntity = callImpl();

		} catch (ResponseException ex) {
			rspEntity = ex.toResponseEntity();
		} catch (RequiredRuntimeException | IT.CheckException ex) {
			clientEx = ex;
		} catch (TrmRsp trmRsp) {
			switch (trmRsp.status()) {
				case FAIL:
					serverEx = trmRsp;
					break;
				case ERR:
					clientEx = trmRsp;
					break;
				case OK:
					//wth , OK handle early
				default:
					throw new WhatIsTypeException(trmRsp.status());
			}
		} catch (Throwable ex) {
			serverEx = ex;
		}
		boolean hasError = clientEx != null || serverEx != null;

		if (!use_global_error_handler && hasError) {

			if (serverEx != null) {

				if (logger.isErrorEnabled()) {
					logger.error(SYMJ.ARROW_UP + rqKey + "/", serverEx);
				}
				if (serverEx instanceof ICleanMessage) {
					return SrcResponseEntity.C500(((ICleanMessage) serverEx).getCleanMessage());
				}
				return SrcResponseEntity.C500(serverEx);
			} else if (clientEx != null) {

				if (logger.isErrorEnabled()) {
					logger.error(SYMJ.ARROW_UP + rqKey + "/", clientEx);
				}
				String msg;
				if (clientEx instanceof ICleanMessage) {
					msg = ((ICleanMessage) serverEx).getCleanMessage();
				} else if (clientEx instanceof TrmRsp) {
					msg = ((TrmRsp) clientEx).statusWithCodeWithMessage();
				} else {
					msg = clientEx.getMessage() == null ? clientEx.getClass().getSimpleName() + ":NULL" : clientEx.getMessage();
				}
				return SrcResponseEntity.C400(msg);
			}
		}

		if (logger.isErrorEnabled()) {

			if (SrcResponseEntity.isStatusError(rspEntity)) {

				String msg = SYMJ.ARROW_UP + rqKey + "/" + rspEntity;

				Object src = SrcResponseEntity.toSrcObject(rspEntity, null);
				Throwable srcErr = src != null && src instanceof Throwable ? (Throwable) src : null;

				if (srcErr != null) {
					logger.error(msg, srcErr);
				} else if (src != null) {
					logger.error(msg + "\n" + src);
				} else {
					logger.error(msg);
				}

			} else {

				if (hasError || logger.isInfoEnabled()) {

					String msg = SYMJ.ARROW_UP + rqKey + "/" + (hasError ? "ERROR" : rspEntity);

					Object src = SrcResponseEntity.toSrcObject(rspEntity, null);
					Throwable srcErr = src != null && src instanceof Throwable ? (Throwable) src : null;

					srcErr = srcErr == null && !hasError ? null : (clientEx != null ? clientEx : serverEx);

					if (logger.isDebugEnabled()) {

						if (srcErr != null) {
							logger.debug(msg, srcErr);
						} else if (src != null) {
							logger.debug(msg + "\n" + src);
						} else {
							logger.debug(msg);
						}

					} else {

						if (srcErr != null) {
							logger.info(msg, srcErr);
						} else if (src != null) {
							logger.info(msg + "\n" + src);
						} else {
							logger.info(msg);
						}

					}

				}

			}

			if (use_global_error_handler) {
				if (serverEx != null) {
					throw serverEx;
				} else if (clientEx != null) {
					throw clientEx;
				}
			}
		}

		return rspEntity;
	}

	protected void beforeCall() {
	}

	protected Object getAuthPrincipal() {
		return UWeb.getAuthPrincipal(null);
	}

	public abstract ResponseEntity<T> callImpl() throws Throwable;

	public RestCall<T> rq(HttpServletRequest request, boolean... print) {
		this.request = request;
		if (ARG.isDefEqTrue(print)) {
			if (logger != null) {
				UWeb.buildReportRequest(request, true, true, 0, logger);
			}
		}
		return this;
	}
}
