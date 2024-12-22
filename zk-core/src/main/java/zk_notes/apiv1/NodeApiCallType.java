package zk_notes.apiv1;

import com.jayway.jsonpath.JsonPath;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.fs.ext.GEXT;
import mpc.fs.fd.EFT;
import mpc.json.UGson;
import mpc.log.L;
import mpc.net.CON;
import mpe.str.CN;
import mpu.IT;
import mpu.Sys;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpu.core.RW;
import mpu.pare.Pare;
import mpu.str.JOIN;
import mpu.str.STR;
import mpe.wthttp.CleanDataResponseException;
import org.codehaus.groovy.control.CompilationFailedException;
import utl_rest.StatusException;
import mpe.wthttp.ContentType;
import zk_notes.apiv1.client.NoteApi;
import zk_os.AFC;
import zk_os.AFCC;
import zk_os.core.ItemPath;
import zk_os.sec.Sec;
import zk_page.ZKR;
import zk_page.core.PagePathInfoWithQuery;
import zk_page.node.NodeDirCallService;
import zk_page.node_state.FormState;
import zk_page.node.NodeDir;
import zk_page.node_state.ISecState;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public enum NodeApiCallType {
	UP(NodeApiChars.UP), UP_COM(NodeApiChars.UP_COM), DOWN(NodeApiChars.DOWN), DOWN_COM(NodeApiChars.DOWN_COM);

	NodeApiCallType(String ctrlSymPart) {
		this.ctrlSym = ctrlSymPart;
		this.ctrlSymPart_ = ctrlSymPart + "/";
		this._ctrlSymPart = "/" + ctrlSymPart;
	}

	public final String ctrlSym, _ctrlSymPart, ctrlSymPart_;

	public boolean isDown() {
		return ctrlSym.charAt(0) == NodeApiChars.DOWN_CHAR;
	}

	public boolean isCom() {
		return ctrlSym.length() > 1 && ARRi.last(ctrlSym) == NodeApiChars.COM_CHAR;
	}

	public static NodeApiCallType valueOf(Path key, NodeApiCallType... defRq) {
		return key != null ? valueOf(key.getFileName().toString(), defRq) : ARG.toDefThrow(() -> new RequiredRuntimeException("Path is null"), defRq);
	}

	public static NodeApiCallType valueOf(String key, NodeApiCallType... defRq) {
		Optional<NodeApiCallType> findFirst = Arrays.stream(values()).filter(en -> en.ctrlSym.equals(key)).findFirst();
		return ARG.toDefThrowOpt(() -> new RequiredRuntimeException("NodeApi not found by key %s", key), findFirst, defRq);
	}

	public static void checkRestCall(PagePathInfoWithQuery curPPI) {//page/*/note

		if (!NoteApi.UP_API_PARTURL.equals(curPPI.pagename())) {
			return;
		}

		CON.Method method = ZKR.getRequestMethod(CON.Method.UNDEFINED);
		switch (method) {
			case GET:
			case PUT:
			case POST:
			case DELETE:
				break;
			case UNDEFINED:
			default:

		}

		Pare<String, String> sdn = Pare.of(curPPI.subdomain3(), curPPI.pathStr(1, null)); //shift _api

		String sd3 = sdn.key();

		String path0_pagename = curPPI.pathStr(1, null);
		String path1_ctrlSym = curPPI.pathStr(2, null);
		String path2_nodeName = curPPI.pathStr(3, null);

		NodeApiCallType nodeApi = NodeApiCallType.valueOf(path0_pagename, null);

		if (NodeApiCallType.UP.isPatternEq(path0_pagename) || NodeApiCallType.DOWN.isPatternEq(path0_pagename)) {
			path0_pagename = AFCC.PAGE_INDEX_ALIAS;
			sdn = Pare.of(sd3, path0_pagename);
			path1_ctrlSym = curPPI.pathStr(1, null);
			path2_nodeName = curPPI.pathStr(2, null);
		}


		if (nodeApi != null) {
			switch (nodeApi) {
				case UP:
				case DOWN:
					Sec.checkIsOwnerOr404();
					if (path1_ctrlSym == null) {//need all
						Map map = FullDataBuilder.buildMap_ROOT(null);
						throw new CleanDataResponseException(UGson.toStringPrettyFromObject(map));
					}
					//ok
					break;
				case UP_COM:
				case DOWN_COM:
				default:
					Sec.checkIsOwnerOr404();
					throw StatusException.C404(nodeApi + " for root impossible");
					//ok - need node ???
			}
		}

		nodeApi = NodeApiCallType.valueOf(path1_ctrlSym, null);
		if (nodeApi == null) {
			//it no rest call
			return;
		}
		if (path2_nodeName == null) {
			if (nodeApi == NodeApiCallType.DOWN) {
				IT.state(nodeApi == NodeApiCallType.UP, "Unsupported %s (only %s)", nodeApi, NodeApiCallType.UP);
//				throw new CleanDataResponseException("Set item name", contentType, videoFile).nothing();

			}
			Path pageComs = AFC.DIR_FORMS(sd3, path0_pagename);
			if (X.emptyDir_NotExist(pageComs)) {
				throw StatusException.C404();
			}
			List<Path> ls = EFT.DIR.ls(pageComs);
			String lsPageComs = ls.stream().map(UF::fn).collect(Collectors.joining(STR.NL));
			throw new CleanDataResponseException(lsPageComs);
		}

		Path fileFormStatePath = AFC.getRpaFormStatePath(sd3, path0_pagename, path2_nodeName);
		FormState formState = FormState.ofPathFormFile_orCreate(fileFormStatePath, sdn);

		Path formStatePath_FormOrProps = nodeApi.isCom() ? formState.pathProps() : fileFormStatePath;

		if (nodeApi.isDown()) {// PUT REQUEST
			ISecState.checkIsAllowedEditOr403(formState);
			NoteApi.postCallWithBody(formStatePath_FormOrProps);
			return;
		}

		// GET REQUEST
		ISecState.checkIsAllowedViewOr403(formState);

		if (nodeApi.isCom()) {
			throw new CleanDataResponseException(formStatePath_FormOrProps);
		}

		NodeDir noteDir = NodeDir.ofFile(fileFormStatePath, sdn);

		Path videoFile = noteDir.firstFile(GEXT.VIDEO, null);
		if (videoFile != null) {
			ContentType contentType = ContentType.VIDEO_MP4;
//			UWeb.sendResponseContentType_FromFile(ZKR.getResponse(), contentType, videoFile.toFile());
//			throw new CleanDataResponseException("File %s/%s is already write to response", contentType, videoFile).nothing();
			throw new CleanDataResponseException("File %s/%s is already write to response", contentType, videoFile).setContentFile(contentType, videoFile);

		}

		checkExeParam(sdn, formStatePath_FormOrProps, path2_nodeName, curPPI.queryUrl().getFirstAsStr("exe", null), curPPI.queryUrl().getFirstAsStr("jp", null));

		boolean existNode = UFS.existFile(formStatePath_FormOrProps);

		if (existNode) {
			throw new CleanDataResponseException(formStatePath_FormOrProps);
		}

//		if (APP.IS_PROM_ENABLE) {
//			throw new CleanDataResponseException(NoteApi.MSG_404_ITEM_NOTE_FOUND, formState.formName());
//		}
//		throw StatusException.C400("item not found '" + formState.formName() + "' from " + formStatePath_FormOrProps);
		throw StatusException.C404(NoteApi.MSG_404_ITEM_NOTE_FOUND, formState.formName());
	}

	private boolean isPatternEq(String path) {
		return ctrlSym.equals(path);
	}

	public static void checkExeParam(Pare sdn, Path pathFc, String path2_nodeName, String exe, String jp) {
//		String exe = ZKR.getRequestQueryParamAsStr("exe", null);
//		String jp = ZKR.getRequestQueryParamAsStr("jp", null);

		if (X.empty(exe)) {
			return;
		}

		switch (exe) {
			case "rest": {
				boolean withOuterJp = jp != null;
				NodeDir nodeDir = NodeDir.ofFile(pathFc, sdn);
				String rsp = NodeDirCallService.doHttpCall(nodeDir, withOuterJp, true);
				if (withOuterJp) {
					String read = JsonPath.read(rsp, jp) + "";
					throw new CleanDataResponseException(read);
				}
				throw new CleanDataResponseException(rsp);
			}
			case "kafka": {
				String rsp = NodeDirCallService.doKafkaCall(NodeDir.ofFile(pathFc, sdn), true);
				throw new CleanDataResponseException(rsp == null ? "sended" : rsp);
			}
		}

		if (!Sec.isOwner()) {
			return;
		}
		switch (exe) {
			case "groovy":
				try {
					String rslt = new GroovyShell().evaluate(pathFc.toFile()) + "";
					throw new CleanDataResponseException(rslt);
				} catch (CompilationFailedException e) {
					L.error("Error compile groovy script: file://" + pathFc.toAbsolutePath(), e);
					throw new CleanDataResponseException(400, e.getMessage());
				} catch (GroovyRuntimeException e) {
					L.error("Error runtime groovy script: file://" + pathFc.toAbsolutePath(), e);
					throw new CleanDataResponseException(400, e.getMessage());
				} catch (IOException e) {
					L.error("Error evaluate groovy script: file://" + pathFc.toAbsolutePath(), e);
					throw new CleanDataResponseException(500, e.getMessage());
				}
			case "bash":
			case "python3":
				break;
			case "bash*":
			case "python3*":
//				String vl = SpVM.get().getUrlTo(RSPath.PAGE, UP.ctrlSym + "/" + path2_nodeName);
				String vl = new NoteApi().zApiUrl.GET_toItem_WithExe(ItemPath.of(sdn, path2_nodeName), exe);
				String callCurl = X.f("curl -s '%s' | ", vl) + STR.substr(exe, -1);
				throw new CleanDataResponseException(callCurl);
			default:
				throw new WhatIsTypeException(exe);
		}

		Pare<Integer, List<String>> rslt = Sys.exec_filetmp(exe, RW.readContent(pathFc), null, false);
		sendCleanResponse(rslt);
	}

	private static void sendCleanResponse(Pare<Integer, List<String>> rslt) {
		boolean isOk = X.equals(rslt.key(), 0);
		String ok = isOk ? CN.OK : CN.FAIL;
		int status = isOk ? 200 : 400;
		if (X.empty(rslt.val())) {
			throw new CleanDataResponseException(status, ok + ":" + "empty");
		} else {
			String data = JOIN.allByNL(rslt.val());
			if (X.blank(data)) {
				throw new CleanDataResponseException(status, ok + ":" + "blank");
			}
			throw new CleanDataResponseException(status, data);
		}
	}
}
