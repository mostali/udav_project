package zk_page.core;

import lombok.Getter;
import lombok.SneakyThrows;
import mpc.env.APP;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.core.ARG;
import mpc.exception.FForbiddenException;
import mpc.exception.IResponseStatusException;
import mpu.str.ToString;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.*;
import mpc.exception.CleanDataResponseException;
import utl_rest.StatusException;
import utl_web.URsp;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.impl.PageState;
import zk_notes.node_state.impl.PlaneState;
import zk_os.AppZos;
import zk_os.db.net.WebUsr;
import zk_page.ZKR;
//import zkbae.security.WebSecurityConfig.CustomAuthenticationProvider;

import java.io.IOException;
import java.util.Map;

//@VariableResolver(DelegatingVariableResolver.class)
public class SpVM extends SpBaseVM {

	//	@WireVariable
//	public transient TestService testService;
//	@Getter String pageTitle = "pppppppppp";
//	@Getter String pageDescription = "pageDescription";

	public static Map getTrackContext(Map... defRq) {
		SpVM spVM = SpVM.get(null);
		return spVM != null ? spVM.getQuery().getMapWithKeyPfx("$$", true) : ARG.toDefThrowMsg(() -> X.f("Except track context"), defRq);

	}

	public static SpVM get(SpVM... defRq) {
		try {
			SpVM vm = (SpVM) Executions.getCurrent().getDesktop().getFirstPage().getFirstRoot().getAttribute("vm");
			return vm;
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	public static PagePathInfoWithQuery ppiq(PagePathInfoWithQuery... defRq) {
		SpVM spVM = SpVM.get(null);
		if (spVM != null) {
			return spVM.ppiq();
		}
		return ARG.toDefThrowMsg(() -> "Except request context", defRq);
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

		} catch (Throwable ex) {
			endRequest_FINISH = ex;
		} finally {
//			AnyQ.clearAll();
		}

		/**
		 * SEND STANDART HTTP CODE WITH MESSAGE
		 */

		if (endRequest_FINISH == null) {
			if (L.isInfoEnabled()) {
				L.info("END:handleRequest OK");
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

	public PageState pageState() {
		return AppStateFactory.forPage(SpVM.get().sdn());
	}

	public PlaneState planeState() {
		return AppStateFactory.forPlane(SpVM.get().subdomain3Rq());
	}

	/**
	 * APP REQUEST HANDLER
	 */
	@SneakyThrows
	public void afterComposeImpl(@ContextParam(ContextType.VIEW) Window window) {

		if (L.isInfoEnabled()) {
			L.info(ZKR.getClientIpAddress() + SYMJ.ARROW_RIGHT_SPEC + WebUsr.get().getLogin() + SYMJ.ARROW_RIGHT_SPEC + sdn().toStringPath() + SYMJ.ARROW_RIGHT_SPEC);
		}
//		ZkApp.add(window.getDesktop());

//		if (AppZosConfig.IS_DEBUG) {
		if (APP.IS_DEBUG_ENABLE) {
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
			} catch (Throwable ex) {
				anyException = ex;
			}
		}

		//page not found
		boolean debugInfo = AppZos.isDebugEnable();
		if (pspNotFoundException != null) {
			L.error("PspNotFoundException", pspNotFoundException);
			throw debugInfo ? StatusException.C404("page not found") : StatusException.C404();
		} else if (forbiddenException != null) {
			L.error("ForbiddenException", anyException);
			throw debugInfo ? StatusException.C404("anonim forbidden") : StatusException.C404();
		} else if (anyException != null) {
			// StatusException.C500("Internal error:"+anyException.getMessage())
			L.error("AnyException", anyException);
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
