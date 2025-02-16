package zk_notes.apiv1;

import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.RestStatusException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.fs.ext.GEXT;
import mpc.fs.fd.EFT;
import mpc.json.UGson;
import mpc.net.CON;
import mpc.net.query.QueryUrl;
import mpe.core.ERR;
import mpe.str.CN;
import mpe.wthttp.*;
import mpu.IT;
import mpu.Sys;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpu.core.RW;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.str.JOIN;
import mpu.str.STR;
import org.apache.commons.io.IOUtils;
import utl_rest.StatusException;
import zk_notes.ACN;
import zk_notes.apiv1.client.LocalNoteApi;
import udav_net.apis.zznote.NoteApi;
import zk_notes.node_srv.*;
import zk_notes.node_srv.jarcall.JarCallService;
import zk_os.AFC;
import udav_net.apis.zznote.ItemPath;
import zk_os.sec.Sec;
import zk_page.ZKR;
import zk_page.core.PagePathInfoWithQuery;
import zk_notes.node_state.FormState;
import zk_notes.node.NodeDir;
import zk_notes.node_state.ISecState;

import javax.servlet.ServletInputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public enum NodeApiCallType {
	UP(NodeApiChars.UP), UP_COM(NodeApiChars.UP_COM), DOWN(NodeApiChars.DOWN), DOWN_COM(NodeApiChars.DOWN_COM);

	NodeApiCallType(String ctrlSymPart) {
		this.ctrlSym = ctrlSymPart;
		this.ctrlSymPart_ = ctrlSymPart + "/";
		this._ctrlSymPart = "/" + ctrlSymPart;
	}

	public final String ctrlSym, _ctrlSymPart, ctrlSymPart_;

	@SneakyThrows
	public static void handlePostCallWithBody(Path formStatePath) {
		ServletInputStream inputStream = ZKR.getRequest().getInputStream();
		String inData;
		if (inputStream.available() == 0) {
			inData = ZKR.getRequestQueryParamAsStr("v", null);
			if (inData == null) {
				throw RestStatusException.C400(NoteApi.MSG_400_SET_BODY);
			}
			inData = inData.replace(STR.NL_HTML, inData);
		} else {
			inData = IOUtils.toString(inputStream);
		}
		RW.write(formStatePath, inData, true);
		throw new CleanDataResponseException(CN.OK + ":" + inData.length());
	}

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

		if (!NoteApi.UP_API_PARTURL.equals(curPPI.pagename0())) {
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

		Pare<String, String> sdn = Pare.of(curPPI.subdomain30(), curPPI.pathStr(1, null)); //shift _api

		String sd3 = sdn.key();

		String path0_pagename = curPPI.pathStr(1, null);
		String path1_ctrlSym = curPPI.pathStr(2, null);
		String path2_nodeName = curPPI.pathStr(3, null);

		NodeApiCallType nodeApi = NodeApiCallType.valueOf(path0_pagename, null);

		if (NodeApiCallType.UP.isPatternEq(path0_pagename) || NodeApiCallType.DOWN.isPatternEq(path0_pagename)) {
			path0_pagename = ItemPath.PAGE_INDEX_ALIAS;
			sdn = Pare.of(sd3, path0_pagename);
			path1_ctrlSym = curPPI.pathStr(1, null);
			path2_nodeName = curPPI.pathStr(2, null);
		}


		if (nodeApi != null) {
			switch (nodeApi) {
				case UP:
				case DOWN:
					Sec.checkIsAdminOrOwnerOr404();
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

			Path pageComs = AFC.FORMS.DIR_FORMS(sd3, path0_pagename);
			if (X.emptyDir_NotExist(pageComs)) {
				throw StatusException.C404();
			}
			List<Path> ls = EFT.DIR.ls(pageComs);
			String lsPageComs = ls.stream().map(UF::fn).collect(Collectors.joining(STR.NL));
			throw new CleanDataResponseException(lsPageComs);
		}

		Path fileFormStatePath = AFC.FORMS.getRpaFormStatePath(sd3, path0_pagename, path2_nodeName);
		FormState formState = FormState.ofPathFormFile_orCreate(sdn, fileFormStatePath);

		Path formStatePath_FormOrProps = nodeApi.isCom() ? formState.pathProps() : fileFormStatePath;

		if (nodeApi.isDown()) {// PUT REQUEST
			ISecState.checkIsAllowedEditOr403(formState);

			checkDstUpdateState(curPPI, sdn, path2_nodeName);

			handlePostCallWithBody(formStatePath_FormOrProps);

			return;
		}

		// GET REQUEST
		ISecState.checkIsAllowedViewOr403(formState);

		if (nodeApi.isCom()) {
			throw new CleanDataResponseException(formStatePath_FormOrProps);
		}

		NodeDir noteDir = NodeDir.ofFile(sdn, fileFormStatePath);

		Path videoFile = noteDir.firstFile(GEXT.VIDEO, null);
		if (videoFile != null) {
			ContentType contentType = ContentType.VIDEO_MP4;
//			UWeb.sendResponseContentType_FromFile(ZKR.getResponse(), contentType, videoFile.toFile());
//			throw new CleanDataResponseException("File %s/%s is already write to response", contentType, videoFile).nothing();
			throw new CleanDataResponseException("File %s/%s is already write to response", contentType, videoFile).setContentFile(contentType, videoFile);

		}

		{//CHECK EXE PARAM
			checkExeParam(sdn, noteDir, curPPI);
		}

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

	private static void checkDstUpdateState(PagePathInfoWithQuery curPPI, Pare<String, String> sdn, String path2_nodeName) {
		QueryUrl queryUrl = curPPI.queryUrl();
		String state = queryUrl.getFirstAsStr("state", null);
		if (state == null) {
			return;
		}
		state = "." + state;
		String key = queryUrl.getFirstAsStr("k");
		String val = queryUrl.getFirstAsStr("v");

		FormState stateDst = FormState.ofState_OrCreate(sdn, state, path2_nodeName, null);
		if (stateDst == null) {
			throw StatusException.C404("state '%s' for update not found", state);
		}

		stateDst.set(key, val);

		throw new CleanDataResponseException("Updated");

	}

	private boolean isPatternEq(String path) {
		return ctrlSym.equals(path);
	}

	public static void checkExeParam(Pare sdn, NodeDir nodeDir, PagePathInfoWithQuery curPPI) {
		String exe = curPPI.queryUrl().getFirstAsStr(CN.EXE, null);

		if (X.empty(exe)) {
			return;
		}
//		String trackId = curPPI.queryUrl().getFirstAsStr(HttpCallMsg.TID, null);

		String trackId = TrackMap.getTrackId(true);
		try {
			checkExeParamImpl(sdn, nodeDir, curPPI, exe, trackId);
		} finally {
			TrackMap.clear(trackId);
		}
	}

	public static void checkExeParamImpl(Pare sdn, NodeDir nodeDir, PagePathInfoWithQuery curPPI, String exe, String trackId) {


		switch (exe) {
			case "eval": {
				String evalNode = InjectNode.inject(nodeDir, null, trackId);
				throw new CleanDataResponseException(evalNode);
			}
			case NoteApi.EXE_REST: {

				boolean withOuterJp = curPPI.queryUrl().getFirstAsStr("jp", null) != null;
				boolean withOuterXp = curPPI.queryUrl().getFirstAsStr("xp", null) != null;

				String rsp = HttpCallService.doHttpCall(trackId, nodeDir, withOuterJp, withOuterXp, true);
				Supplier<String> msgIf400 = () -> "Except data for response of node '" + nodeDir.nodeName() + "'";
				if (withOuterJp) {
					String read = JsonPath.read(rsp, jp) + "";
					throw CleanDataResponseException.ofNotEmptyRsp_or400(read, msgIf400);
				}
				throw CleanDataResponseException.ofNotEmptyRsp_or400(rsp, msgIf400);
			}
			case "kafka": {
				Pare3<KafkaCallMsg, Object, Throwable> rslt = KafkaCallService.doKafkaCall(nodeDir);
				throw toCleanDataResponseException((Pare3) rslt, nodeDir);
			}
			case JarCallMsg.KEY: {
				JarCallMsg jcm = JarCallMsg.ofQk(nodeDir.state().nodeData(), true);
				if (jcm.isSync()) {
					throw toCleanDataResponseException((Pare3) JarCallService.doJarCallSyncRest(nodeDir), nodeDir);
				} else {
					throw JarCallService.doJarCallAsyncRest(nodeDir, ZKR.getResponse());
				}

			}
		}

		if (!Sec.isOwner()) {
			return;
		}
		switch (exe) {
			case ACN.GROOVY:
				Pare3<GroovyCallMsg, Object, Throwable> rslt = GroovyCallService.doGroovyCall(nodeDir);
				throw toCleanDataResponseException((Pare3) rslt, nodeDir);
//				Path pathFc = nodeDir.getPathFormFc();
//				try {
//					String rslt = new GroovyShell().evaluate(pathFc.toFile()) + "";
//					throw new CleanDataResponseException(rslt);
//				} catch (CompilationFailedException e) {
//					L.error("Error compile groovy script: file://" + pathFc.toAbsolutePath(), e);
//					throw new CleanDataResponseException(400, e.getMessage());
//				} catch (GroovyRuntimeException e) {
//					L.error("Error runtime groovy script: file://" + pathFc.toAbsolutePath(), e);
//					throw new CleanDataResponseException(400, e.getMessage());
//				} catch (IOException e) {
//					L.error("Error evaluate groovy script: file://" + pathFc.toAbsolutePath(), e);
//					throw new CleanDataResponseException(500, e.getMessage());
//				}
			case "bash":
			case "python3":
				break;
			case "bash*":
			case "python3*":
//				String vl = SpVM.get().getUrlTo(RSPath.PAGE, UP.ctrlSym + "/" + path2_nodeName);
				String nodeName = nodeDir.nodeName();
				String vl = new LocalNoteApi().zApiUrl.GET_toItem_With(ItemPath.of(sdn, nodeName), Pare.of(CN.EXE, exe));
				String callCurl = X.f("curl -s '%s' | ", vl) + STR.substrCount(exe, -1);
				throw new CleanDataResponseException(callCurl);
			default:
				throw new WhatIsTypeException(exe);
		}

		Pare<Integer, List<String>> rslt = Sys.exec_filetmp(exe, nodeDir.nodeData(), null, false);
		sendCleanResponse(rslt);
	}

	private static CleanDataResponseException toCleanDataResponseException(Pare3<CallMsg, Object, Throwable> rslt, NodeDir node) {
		Map map = new HashMap();
		CallMsg key = rslt.key();
		map.put("type", key.type());
		map.put("result", rslt.val());
		int code = 200;
		if (rslt.ext() != null) {
			code = 500;
			map.put("errors", ERR.getMessagesAsStringWithHead(rslt.ext(), "Executions errors:", true));
		}
		throw CleanDataResponseException.ofJson(code, map);
//		throw CleanDataResponseException.of(code, "Illegal service result for node [%s]", node.id());
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
