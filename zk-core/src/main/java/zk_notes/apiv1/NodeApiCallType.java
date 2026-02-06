package zk_notes.apiv1;

import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import mpc.env.APP;
import mpc.exception.CleanDataResponseException;
import mpc.exception.NI;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.RestStatusException;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.fs.ext.GEXT;
import mpc.fs.fd.EFT;
import mpc.html.UXPath;
import mpc.json.UGson;
import mpc.log.L;
import mpc.net.CON;
import mpc.net.ContentType;
import mpc.net.query.QueryUrl;
import mpe.call_msg.CallMsg;
import mpe.call_msg.core.NodeID;
import mpe.core.ERR;
import mpe.str.CN;
import mpu.IT;
import mpu.X;
import mpu.core.*;
import mpu.func.Function3;
import mpu.func.FunctionV2;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.str.JOIN;
import mpu.str.STR;
import mpu.str.TKN;
import org.apache.commons.io.IOUtils;
import udav_net.apis.zznote.ApiCase;
import udav_net.apis.zznote.NoteApi;
import utl_rest.StatusException;
import zk_notes.apiv1._ati.TreeRestCall;
import zk_notes.apiv1.old.FullDataBuilder;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.EvalService;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.ISecState;
import zk_notes.node_state.ObjState;
import zk_notes.node_state.ProxyRW;
import zk_os.coms.AFC;
import zk_os.db.net.WebUsr;
import zk_os.sec.SecCheck;
import zk_os.sec.UO;
import zk_os.tasks.TaskManager;
import zk_page.ZKR;
import zk_page.core.PagePathInfoWithQuery;
import zk_page.core.SpVM;

import javax.servlet.ServletInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
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

	@SneakyThrows
	public static void handlePostCallWithBody(Path formStatePath) {
		ServletInputStream inputStream = ZKR.getRequest().getInputStream();
		String inData;
		if (inputStream.available() == 0) {
			inData = ZKR.getRequestQueryParamAsStr(NoteApi.PK_V, null);
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

	public static CleanDataResponseException toCleanDataResponseEvalException(Pare3<Object, Throwable, String> rsp, String name) {
		if (rsp.val() != null) {
			X.throwException(rsp.val());
		}
		L.info("toCleanDataResponseEvalException:{}:\n{}", name, rsp.ext());
		throw CleanDataResponseException.ofNotEmptyRsp_or400(rsp.key() + "", name);
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

	@SneakyThrows
	public static void checkRestCall(PagePathInfoWithQuery curPPI) {//page/*/note

//		boolean allowedEditPlane = SecMan2.isAllowedEditPlane(curPPI.sdn());
//		if (!allowedEditPlane) {
//			L.info("Not ");
//			return;
//		}

		String pagename = curPPI.pagename0();

		ApiCase apiCase = ApiCase.valueOf(pagename, null);
		if (apiCase != null) {
			switch (apiCase) {

				case _ati://tree
					TreeRestCall treeRest = TreeRestCall.ofPPI(curPPI);
					Pare<Integer, String> rspPare = treeRest.apply();
					//throw CleanDataResponseException.ofNotEmptyRsp_or400(val, treeRest.getItemname());
					throw CleanDataResponseException.ofRspPare(rspPare);

				case _adi://download
					handleApiDownload(curPPI);
					break;
				default:
					//ok
			}
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

		String sd3 = curPPI.planeRq();

		String path0_pagename = curPPI.pathStr(1, null);
		String path1_ctrlSym = curPPI.pathStr(2, null);
		String path2_nodeName = curPPI.pathStr(3, null);

		Pare<String, String> sdn = Pare.of(sd3, path0_pagename); //shift _api


		NodeApiCallType nodeApi = NodeApiCallType.valueOf(path0_pagename, null);

		if (NodeApiCallType.UP.isPatternEq(path0_pagename) || NodeApiCallType.DOWN.isPatternEq(path0_pagename)) {
			path0_pagename = NodeID.PAGE_INDEX_ALIAS;
			sdn = Pare.of(sd3, path0_pagename);
			path1_ctrlSym = curPPI.pathStr(1, null);
			path2_nodeName = curPPI.pathStr(2, null);
		}


		if (nodeApi != null) {
			switch (nodeApi) {
				case UP:
				case DOWN:
					SecCheck.checkIsAdminOrOwnerOr404();
					if (path1_ctrlSym == null) {//need all
						NI.stop0("old way");
						Map map = FullDataBuilder.buildMap_ROOT(null);
						throw new CleanDataResponseException(UGson.toStringPrettyFromObject(map));
					}
					//ok
					break;
				case UP_COM:
				case DOWN_COM:
				default:
					SecCheck.checkIsOwnerOr404();
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

			// it call 'page/*'

			if (nodeApi == NodeApiCallType.DOWN) {
				//throw new CleanDataResponseException("Set item name", contentType, videoFile).nothing();
				IT.state(nodeApi == NodeApiCallType.UP, "Unsupported %s (only %s)", nodeApi, NodeApiCallType.UP);
			}

			Path pageComs = AFC.FORMS.DIR_FORMS(sd3, path0_pagename);
			if (X.emptyDir_NotExist(pageComs)) {
				throw StatusException.C404();
			}
			List<Path> ls = EFT.DIR.ls(pageComs);
			String lsPageComs = ls.stream().map(UF::fn).collect(Collectors.joining(STR.NL));
			throw new CleanDataResponseException(lsPageComs);
		}

		//has path2_nodeName

		Path fileFormStatePath = AFC.FORMS.getStatePath_DATA(sd3, path0_pagename, path2_nodeName);

		ObjState formState = AppStateFactory.ofPath_EntityFile_orCreate(sdn, fileFormStatePath, AFC.AfcEntity.FORM);

		boolean isApi_COM = nodeApi.isCom();

		Path formStatePath_FormOrProps = isApi_COM ? formState.pathProps() : fileFormStatePath;

		if (nodeApi.isDown()) {// PUT REQUEST
			ISecState.checkIsAllowedEditOr403(formState);

			checkDstUpdateState(curPPI, sdn, path2_nodeName);

			handlePostCallWithBody(formStatePath_FormOrProps);

			return;
		}

		// GET REQUEST
		ISecState.checkIsAllowedViewOr403(formState);

		if (isApi_COM) {
			throw new CleanDataResponseException(formStatePath_FormOrProps);
		}

		NodeDir nodeDir = NodeDir.ofFile(sdn, fileFormStatePath);

		Path videoFile = nodeDir.firstFile(GEXT.VIDEO, null);
		if (videoFile != null) {
			ContentType contentType = ContentType.VIDEO_MP4;
//			UWeb.sendResponseContentType_FromFile(ZKR.getResponse(), contentType, videoFile.toFile());
//			throw new CleanDataResponseException("File %s/%s is already write to response", contentType, videoFile).nothing();
			throw new CleanDataResponseException("File %s/%s is already write to response", contentType, videoFile).setContentFile(contentType, videoFile);
		}

		{//CHECK EXE PARAM
			checkExeParam(sdn, nodeDir, curPPI);
		}

		if (!isApi_COM) {
			ProxyRW.NodeProxyRW proxyRW = nodeDir.getProxyRW();
			boolean hasProxyPath = proxyRW.hasProxyPath();
			if (hasProxyPath) {
				formStatePath_FormOrProps = proxyRW.getTargetAnyPath_READ();
			}
		}

		sendFileDataInRsp(formState.objName(), formStatePath_FormOrProps);
	}

	private static void sendFileDataInRsp(String nodeName, Path formStatePath_FormOrProps) {
		boolean existNode = UFS.existFile(formStatePath_FormOrProps);
		if (existNode) {
			throw new CleanDataResponseException(formStatePath_FormOrProps);
		}
		throw StatusException.C404(NoteApi.MSG_404_ITEM_NOTE_FOUND, nodeName);
	}

	private static void handleApiDownload(PagePathInfoWithQuery curPPI) throws Exception {

		String targetPath = curPPI.pathWoQuery();

		targetPath = TKN.lastGreedy(UF.normFileStart(targetPath), "/", null);//cut _adi/*

//		String plane = TKN.lastGreedy(UF.normFileStart(targetPath), "/", null);//cut _adi/*

		FunctionV2<String, String> throwError404 = (path, msgCause) -> {
			String msgErr = "Illegal request to download file:" + path + " (" + msgCause + ")";
			if (L.isWarnEnabled()) {
				L.warn(msgErr);
			}
			if (APP.IS_DEBUG_ENABLE) {
				throw CleanDataResponseException.C400(msgErr);
			} else {
				throw CleanDataResponseException.C404("Resource not found: " + path);
			}
		};

		if (targetPath == null) {
			throwError404.apply(targetPath, "Set file path for download");
		}

		String checkPath = TKN.lastGreedy(UF.normFileStart(targetPath), "/", null);//cut _adi/.planes
		if (checkPath == null) {
			throwError404.apply(targetPath, "Set full file path for download");
		}
//					IT.isChildOfParent(AFC.PLANES.DIR_PLANES().toAbsolutePath(), Paths.get(path));
		Path dirPlanes = AFC.PLANES.DIR_PLANES().toAbsolutePath();
		Pare<String, Path> checkSecureParentPath = UFS.checkSecureParentPathAndGet(dirPlanes, checkPath, true, true);
		if (checkSecureParentPath.hasKey()) {//has error msg?
			throwError404.apply(targetPath, checkSecureParentPath.key());
		}

		Path file = checkSecureParentPath.val();

//		String planeFromFile = file.getName(0).toString();
		NodeID nodeID = NodeID.of(Paths.get(checkPath), null);
		if (nodeID == null) {
			throwError404.apply(targetPath, X.f("Node not found from path ", file));
		}

		WebUsr usr = WebUsr.get();

//		boolean allowedNodeFormView = SecMan.isAllowedNode_FORM_VIEW(usr, nodeID);
		boolean allowedNodeFormView = UO.VIEW.isAllowed(nodeID);
		if (!allowedNodeFormView) {
			String msg = X.f("User [%s] access denied for node [%s]", usr.getAliasOrLogin(), nodeID);
			throwError404.apply(targetPath, msg);
		}

		ZKR.download(file);
//					throw CleanDataResponseException.OK("Downloaded: " + path);
		throw CleanDataResponseException.NOTHING("download:" + targetPath);
	}

	private static void checkDstUpdateState(PagePathInfoWithQuery curPPI, Pare<String, String> sdn, String path2_nodeName) {
		QueryUrl queryUrl = curPPI.queryUrl();
		String state = queryUrl.getFirstAsStr(NoteApi.PK_STATE, null);
		if (state == null) {
			return;
		}
		state = "." + state;
		String key = queryUrl.getFirstAsStr(NoteApi.PK_K);
		String val = queryUrl.getFirstAsStr(NoteApi.PK_V);

		ObjState stateDst = AppStateFactory.ofState_OrCreate(sdn, AFC.AfcEntity.valueOfDirName(state), path2_nodeName);
		if (stateDst == null) {
			throw StatusException.C404("state '%s' for update not found", state);
		}

		stateDst.set(key, val);

		throw new CleanDataResponseException("Updated");

	}

	private boolean isPatternEq(String path) {
		return ctrlSym.equals(path);
	}

	public static Function3<String, String, String, String> handlerRspViaJpOrXp = (rsp, jp, xp) -> {
		if (jp != null) {
			Object read = JsonPath.read(rsp, jp);
			return read + "";
		} else if (xp != null) {
			Object read = UXPath.parseString(rsp, xp);
			return read + "";
		}
		return rsp;
	};

	public static void checkExeParam(Pare sdn, NodeDir nodeDir, PagePathInfoWithQuery curPPI) {
		String exe = curPPI.queryUrl().getFirstAsStr(CN.EXE, null);
		if (exe == null) {
			return;
		}
		boolean hasEvalType = nodeDir.evalType(false, null) != null;
		if (!hasEvalType) {
//			boolean hasIO = "".equals(exe) &&;
//			if (!hasIO) {
//			L.warn("Apply exe logic illegal for node {}", nodeDir);
			throw new CleanDataResponseException(400, "Eval for node not supported");
//			return;
//			}
		}
//		else {
//			//TODO need rm this after regres atsm
//			L.warn("Use EXE arg in query is DEPRECATED:" + exe);
//			P.warnBig("Use EXE arg in query is DEPRECATED:" + exe);
//			//return;
//		}

		String jp = curPPI.queryUrl().getFirstAsStr("jp", null);
		String xp = curPPI.queryUrl().getFirstAsStr("xp", null);
		String async = curPPI.queryUrl().getFirstAsStr("async", null);

		boolean withOuterJp = jp != null;
		boolean withOuterXp = xp != null;
		boolean isAsync = "1".equals(async);

//		NodeData inject = NodeData.of(nodeDir);
//		NodeData injected = nodeDir.inject(true);

		if (!isAsync) {

			String rsp = EvalService.evalNode_NULL_QUERY(nodeDir, withOuterJp, withOuterXp);

			rsp = handlerRspViaJpOrXp.apply(rsp, jp, xp);

			throw CleanDataResponseException.ofNotEmptyRsp_or400(rsp, nodeDir.nodeName());
		}

		String taskName = "async-" + nodeDir.nodeName() + "-" + QDate.now().mono4_h2m2();
		//!! init track context in current thread
		Map trackContext = SpVM.getTrackContext(ARR.EMPTY_MAP);

		TaskManager.addTaskAsync(taskName, () -> EvalService.evalNode_NULL_QUERY(nodeDir, withOuterJp, withOuterXp, trackContext));

		throw CleanDataResponseException.OK("RunAsync=" + taskName);
	}


	public static CleanDataResponseException toCleanDataResponseException(Pare3<CallMsg, Object, Throwable> rslt, NodeDir node) {
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

	public static CleanDataResponseException sendCleanResponse(Pare<Integer, List<String>> rslt) {
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
