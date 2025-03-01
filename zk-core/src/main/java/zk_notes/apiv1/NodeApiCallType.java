package zk_notes.apiv1;

import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import mp.utl_odb.tree.UTree;
import mp.utl_odb.tree.trees.api_expiremental.UTreeRest;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.RestStatusException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.fs.ext.EXT;
import mpc.fs.ext.GEXT;
import mpc.fs.fd.EFT;
import mpc.html.UXPath;
import mpc.json.UGson;
import mpc.log.L;
import mpc.net.CON;
import mpc.net.query.QueryUrl;
import mpe.core.ERR;
import mpe.core.U;
import mpe.str.CN;
import mpe.wthttp.*;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpu.core.RW;
import mpu.func.Function3;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.str.JOIN;
import mpu.str.STR;
import org.apache.commons.io.IOUtils;
import udav_net.apis.zznote.NodeID;
import utl_rest.StatusException;
import udav_net.apis.zznote.NoteApi;
import zk_notes.node_srv.*;
import zk_os.AFC;
import udav_net.apis.zznote.ItemPath;
import zk_os.core.NodeData;
import zk_os.sec.Sec;
import zk_page.ZKR;
import zk_page.core.PagePathInfoWithQuery;
import zk_notes.node_state.FormState;
import zk_notes.node.NodeDir;
import zk_notes.node_state.ISecState;

import javax.servlet.ServletInputStream;
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

	public static class TreeRest {

		final PagePathInfoWithQuery curPPI;

		public static TreeRest ofPPI(PagePathInfoWithQuery curPPI) {
			return new TreeRest(curPPI);
		}

		public TreeRest(PagePathInfoWithQuery curPPI) {
			this.curPPI = curPPI;
			this.nodeID = null;
			this.oper = null;
		}

		final NodeID nodeID;
		final Pare3<String, String, String> oper;

		public TreeRest(NodeID nodeID, Pare3<String, String, String> oper) {
			this.nodeID = nodeID;
			this.oper = oper;
			IT.NE(oper.key(), "oper");
			IT.NE(oper.val(), "val");

			curPPI = null;
		}

		public static boolean isValidPath(String path) {
			return NoteApi._ATI.equals(path);
		}

		@SneakyThrows
		public String apply() {
			UTree tree = loadTreeFromPath();
			String rslt = UTreeRest.apply(tree, getOper(), getKey(), getVal());
			return rslt;
		}

		private UTree loadTreeFromPath() {
			Path atiTree = AFC.EVENTS.getRpaEventsStatePath(getSd3(), getPagename(), getItemname(), EXT.SQLITE);
			return UTree.tree(atiTree);
		}

		private String getSd3() {
			return curPPI != null ? curPPI.subdomain3Rq() : nodeID.sd3Rq();
		}

		private String getPagename() {
			return curPPI != null ? curPPI.pathStr(1) : nodeID.pageRq();
		}

		private String getItemname() {
			return curPPI != null ? curPPI.pathStr(2) : nodeID.itemRq();
		}

		private String getOper() {
			return curPPI != null ? curPPI.pathStr(3) : oper.key();
		}

		private String getVal() {
			return curPPI != null ? curPPI.queryUrl().getFirstAsStr("v", null) : oper.ext();
		}

		private String getKey() {
			if (curPPI == null) {
				return oper.val();
			}
			String key = curPPI.queryUrl().getFirstAsStr("k", null);
			if (key == null) {
				throw CleanDataResponseException.C400("set key value arg <k>");
			} else if (U.__NULL__.equals(key)) {
				key = null;
			}
			return key;
		}

	}

	public enum ApiCase {
		_api, _ati;

		public static ApiCase valueOf(String pagename, ApiCase... defRq) {
			if (pagename.startsWith("_a")) {
				switch (pagename) {
					case NoteApi._API:
						return _api;
					case NoteApi._ATI:
						return _ati;
				}
			}
			return ARG.toDefThrowMsg(() -> X.f("Except API case of " + pagename), defRq);
		}

		public static ApiCase ofServletPath(String servletPath) {
			if (servletPath.startsWith(NoteApi._API_PARTURL_)) {
				return _api;
			} else if (servletPath.startsWith(NoteApi._ATI_PARTURL_)) {
				return _ati;
			}
			return null;
		}

		public CharSequence apiName() {
			switch (this) {
				case _api:
					return NoteApi._API;
				case _ati:
					return NoteApi._ATI;
				default:
					throw new WhatIsTypeException(this);
			}
		}
	}

	@SneakyThrows
	public static void checkRestCall(PagePathInfoWithQuery curPPI) {//page/*/note

		String pagename = curPPI.pagename0();

		ApiCase apiCase = ApiCase.valueOf(pagename, null);
		if (apiCase == ApiCase._ati) {
			TreeRest treeRest = new TreeRest(curPPI);
			String val = treeRest.apply();
//			String nodeName = curPPI.queryUrl().getFirstAsStr("n");
			throw CleanDataResponseException.ofNotEmptyRsp_or400(val, treeRest.getItemname());
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

		String sd3 = curPPI.subdomain3Rq();

		String path0_pagename = curPPI.pathStr(1, null);
		String path1_ctrlSym = curPPI.pathStr(2, null);
		String path2_nodeName = curPPI.pathStr(3, null);

		Pare<String, String> sdn = Pare.of(sd3, path0_pagename); //shift _api


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

		FormState stateDst = FormState.ofState_OrCreate(sdn, state, path2_nodeName);//null
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

		if (X.empty(exe)) {
			return;
		}

		String jp = curPPI.queryUrl().getFirstAsStr("jp", null);
		String xp = curPPI.queryUrl().getFirstAsStr("xp", null);

		boolean withOuterJp = jp != null;
		boolean withOuterXp = xp != null;

		NodeData inject = NodeData.of(nodeDir, null);

		String rsp = EvalService.evalNode(inject, withOuterJp, withOuterXp);

		rsp = handlerRspViaJpOrXp.apply(rsp, jp, xp);

		throw CleanDataResponseException.ofNotEmptyRsp_or400(rsp, nodeDir.nodeName());
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
