package zk_page.core;

import lombok.SneakyThrows;
import mp.utl_odb.netapp.qxt.AnyQ;
import mpu.X;
import mpu.core.ARG;
import mpc.env.APP;
import mpc.exception.FForbiddenException;
import mpc.exception.IResponseStatusException;
import mpu.str.ToString;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.*;
import mpe.wthttp.CleanDataResponseException;
import utl_rest.StatusException;
import utl_web.URsp;
import zk_os.AppZosConfig;
import zk_os.core.Sdn;
import zk_os.sec.Sec;
import zk_page.ZKR;
//import zk_old_core.sd.Sd3EE;
import zk_page.node_state.FormState;
//import zkbae.security.WebSecurityConfig.CustomAuthenticationProvider;

import java.io.IOException;

//@VariableResolver(DelegatingVariableResolver.class)
public class SpVM extends SpBaseVM {

//	@WireVariable
//	public transient TestService testService;

	public static SpVM get(SpVM... defRq) {
//		return (SpVM) Executions.getCurrent().getDesktop().getFirstPage().getAttribute("vm");
		try {
			SpVM vm = (SpVM) Executions.getCurrent().getDesktop().getFirstPage().getFirstRoot().getAttribute("vm");
			return vm;
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	@Init
	public void init() {//@ExecutionArgParam(SYSTEM_QUERY) String systemQuery
		initRequestAttributes();
	}

	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Window window) throws IOException {
		Throwable endRequest_FINISH = null;
		try {

			afterComposeImpl(window);

		}
//		catch (Sd3EE sd3EE) {
//			switch (sd3EE.type()) {
//				case SD3_PAGE_NOTFOUND:
//				case SD3_REPO_NOTFOUND:
//					endRequest_FINISH = StatusException.C404(sd3EE, sd3EE.getMessage());
//					break;
//				default:
//					endRequest_FINISH = StatusException.C500(sd3EE, sd3EE.type().name());
//					break;
//			}
//		}
		catch (Throwable ex) {
			endRequest_FINISH = ex;
		} finally {
			AnyQ.clearAll();
		}

		/**
		 * SEND STANDART HTTP CODE WITH MESSAGE
		 */

		if (endRequest_FINISH == null) {
			if (L.isInfoEnabled()) {
				L.info(X.f("END:handleRequest OK"));
			}
		} else if (endRequest_FINISH instanceof IResponseStatusException) {
			URsp.sendResponse(ZKR.getResponse(), (IResponseStatusException) endRequest_FINISH);
			if (L.isInfoEnabled()) {
				String msg = X.f("END:handleRequest OK* %s:%s", endRequest_FINISH.getClass().getSimpleName(), endRequest_FINISH.getMessage());
				L.info(msg);
			}
		} else if (endRequest_FINISH instanceof CleanDataResponseException) {
			URsp.sendResponseWithCleanDataAndClose(ZKR.getResponse(), ((CleanDataResponseException) endRequest_FINISH).getCleanData());
			if (L.isInfoEnabled()) {
				String msg = X.f("END:handleRequest OK^ %s:%s", endRequest_FINISH.getClass().getSimpleName(), ToString.toStringSE(endRequest_FINISH.getMessage(), 10));
				L.info(msg);
			}
		} else {
			ZKR.sendError500(endRequest_FINISH);
			if (L.isErrorEnabled()) {
				String msg = X.f("END:handleRequest FAIL");
				L.error(msg, endRequest_FINISH);
			}
		}

		if (endRequest_FINISH != null) {
			boolean hasError = !StatusException.isOk(endRequest_FINISH);
			if (hasError || L.isDebugEnabled()) {
				String msg = X.f("END:handleRequest/Trace response finish-exception %s:%s", endRequest_FINISH.getClass().getSimpleName(), endRequest_FINISH.getMessage());
				if (hasError) {
					L.error(msg, endRequest_FINISH);
				} else {
					L.debug(msg, endRequest_FINISH);
				}
			}
		}

	}

	public Sdn sdn() {
		return Sdn.of(subdomain3(), pagename());
	}

	public FormState pageState() {
		return FormState.ofPageState(SpVM.get().sdn());
	}

	public FormState planeState() {
		return FormState.ofPlaneState(SpVM.get().subdomain3());
	}

	public enum PageType {
		SPACE
	}

	/**
	 * APP REQUEST HANDLER
	 */
	@SneakyThrows
	public void afterComposeImpl(@ContextParam(ContextType.VIEW) Window window) {

		if (AppZosConfig.IS_DEBUG) {
//			Pare3<Sb, Sb, Sb> info = showDebugLog();
//			ZKI_Window.info(info.key());
//			ZKI_Window.info(info.val());
//			ZKI_Window.info(info.ext());
		}


		PageSP pageSP = null;
		FinderPSP.PspNotFoundException pspNotFoundException = null;
		Throwable anyException = null;
		FForbiddenException forbiddenException = null;
		try {

			pageSP = new FinderPSP(this, window).findPage();

		} catch (FinderPSP.PspNotFoundException pspNotFoundException0) {
			pspNotFoundException = pspNotFoundException0;
		} catch (Throwable t) {
			anyException = t;
		}

		if (pageSP != null) {
			try {
				pageSP.buildPage();
				return;
			} catch (FForbiddenException ex) {
				forbiddenException = ex;
			}
		}

		//page not found
		boolean debugInfo = APP.isDebugEnable() && Sec.isNotAnonim();
		if (pspNotFoundException != null) {
			throw debugInfo ? StatusException.C404("page not found") : StatusException.C404();
		} else if (forbiddenException != null) {
			throw debugInfo ? StatusException.C404("anonim forbidden") : StatusException.C404();
		} else if (anyException != null) {
			// StatusException.C500("Internal error:"+anyException.getMessage())
			throw debugInfo ? anyException : StatusException.C500("Internal error");
		}

	}


	//	@Command
//	public void close(@ContextParam(ContextType.VIEW) Component window) {
//		if (window instanceof Window) {
//			((Window) window).onClose();
//		}
//	}

//	@Command
//	@NotifyChange("currentTime")
//	public void updateTime() {
//		//NOOP just for the notify change
//	}

//	@Command
//	@NotifyChange("closeUploader")
//	public void closeUploader() {
//		U.say("close");
//	}

//	public Date getCurrentTime() {
//		return testService.getTime();
//	}


}