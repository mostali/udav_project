package zk_notes.apiv1;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.env.APP;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.RestStatusException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.fs.ext.GEXT;
import mpc.fs.fd.EFT;
import mpc.json.UGson;
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
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Node;
import utl_rest.CleanDataResponseException;
import utl_rest.StatusException;
import utl_web.ContentType;
import utl_web.UWeb;
import zk_os.AFC;
import zk_os.AFCC;
import zk_os.sec.Sec;
import zk_page.ZKR;
import zk_page.core.PagePathInfo;
import zk_page.core.SpVM;
import zk_page.index.RSPath;
import zk_page.node.fsman.NodeFileTransferMan;
import zk_page.node_state.FormState;
import zk_page.node.NodeDir;
import zk_page.node_state.ISecState;

import javax.servlet.ServletInputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum NodeApiCallType {
	UP(NodeApiChars.UP), UP_COM(NodeApiChars.UP_COM), DOWN(NodeApiChars.DOWN), DOWN_COM(NodeApiChars.DOWN_COM);


	public final String ctrlSym;

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

	public static void checkRestCall(PagePathInfo curPPI) {//page/*/note

		if (!"_api".equals(curPPI.pagename())) {
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
					Sec.checkIsOwnerOr404();
					if (path1_ctrlSym == null) {//need all
						Map map = FullDataBuilder.buildMap_ROOT(null);
						throw new CleanDataResponseException(UGson.toStringPrettyFromObject(map));
					}
//					else {
//						NI.stop("ni for pagename:" + path1_ctrlSym);
//						return;
//					}
					break;
				case DOWN:
					Sec.checkIsOwnerOr404();

					if (method == CON.Method.DELETE) {
						NodeFileTransferMan.deleteItem(NodeDir.ofNodeName(path2_nodeName, sdn));
						return;
					}

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

		if (nodeApi.isDown()) {
			ISecState.checkIsAllowedEditOr403(formState);
			postCallWithBody(formStatePath_FormOrProps);
			return;
		}
		ISecState.checkIsAllowedViewOr403(formState);

		if (nodeApi.isCom()) {
			throw new CleanDataResponseException(formStatePath_FormOrProps);
		}

		NodeDir noteDir = NodeDir.ofFile(fileFormStatePath, sdn);

		Path videoFile = noteDir.singleFile(GEXT.VIDEO, null);
		if (videoFile != null) {
			ContentType contentType = ContentType.VIDEO_MP4;
			UWeb.responseContentTypeFromFile(ZKR.getResponse(), contentType, videoFile.toFile());
			throw new CleanDataResponseException("File %s/%s is already write to response", contentType, videoFile).nothing();
		}

		checkExeParam(formStatePath_FormOrProps, path2_nodeName);

		boolean exxist = UFS.existFile(formStatePath_FormOrProps);

		if (exxist) {
			throw new CleanDataResponseException(formStatePath_FormOrProps);
		}

		if (APP.IS_PROM_ENABLE) {
			throw new CleanDataResponseException("not found " + formState.formName());
		}

		throw StatusException.C400("item not found '" + formState.formName() + "' from " + formStatePath_FormOrProps);
	}

	private boolean isPatternEq(String path) {
		return ctrlSym.equals(path);
	}

	private static void checkExeParam(Path formStatePath_FormOrProps, String path2_nodeName) {
		String exe = ZKR.getRequestQueryParamAsStr("bash", null);
		if (exe != null) {
			executeBashAndGet(formStatePath_FormOrProps, "bash", path2_nodeName);
			throw new IllegalStateException();
		}
		exe = ZKR.getRequestQueryParamAsStr("bash*", null);
		if (exe != null) {
			executeBashAndGet(formStatePath_FormOrProps, "bash*", path2_nodeName);
			throw new IllegalStateException();
		}
		exe = ZKR.getRequestQueryParamAsStr("python3", null);
		if (exe != null) {
			executeBashAndGet(formStatePath_FormOrProps, "python3", path2_nodeName);
			throw new IllegalStateException();
		}
		exe = ZKR.getRequestQueryParamAsStr("python3*", null);
		if (exe != null) {
			executeBashAndGet(formStatePath_FormOrProps, "python3*", path2_nodeName);
			throw new IllegalStateException();
		}
	}

	@SneakyThrows
	public static void postCallWithBody(Path formStatePath) {
		ServletInputStream inputStream = ZKR.getRequest().getInputStream();
		String inData;
		if (inputStream.available() == 0) {
			inData = ZKR.getRequestQueryParamAsStr("v", null);
			if (inData == null) {
				throw RestStatusException.C400("Set body or key 'v' for request");
			}
		} else {
			inData = IOUtils.toString(inputStream);
		}
		RW.write(formStatePath, inData, true);
		throw new CleanDataResponseException(CN.OK + ":" + inData.length());
	}

	@SneakyThrows
	public static void executeBashAndGet(Path formStatePath, String cmdKey, String path2_nodeName) {
		switch (cmdKey) {
			case "bash":
			case "python3":
				break;
			case "bash*":
			case "python3*":
				String vl = SpVM.get().getUrlTo(RSPath.PAGE, UP.ctrlSym + "/" + path2_nodeName);
				String callCurl = X.f("curl -s '%s' | ", vl) + STR.substr(cmdKey, -1);
				throw new CleanDataResponseException(callCurl);
			default:
				throw new WhatIsTypeException(cmdKey);
		}

		Pare<Integer, List<String>> rslt = Sys.exec_filetmp(cmdKey, RW.readContent(formStatePath), null, false);
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
