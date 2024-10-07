package zk_notes.apiv1;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.exception.NI;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.RestStatusException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpc.fs.ext.GEXT;
import mpc.fs.fd.EFT;
import mpc.json.UGson;
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
import utl_rest.CleanDataResponseException;
import utl_rest.StatusException;
import utl_web.ContentType;
import utl_web.UWeb;
import zk_os.AFC;
import zk_os.sec.Sec;
import zk_page.ZKR;
import zk_page.core.PagePathInfo;
import zk_page.core.SpVM;
import zk_page.index.RSPath;
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

	public static void checkRestCall(PagePathInfo ppi) {//page/*/note
		Path path0_pagename = ppi.path(0, null);
		Path path1_ctrlSym = ppi.path(1, null);
		Path path2_nodeName = ppi.path(2, null);

		NodeApiCallType nodeApi = NodeApiCallType.valueOf(path0_pagename.getFileName().toString(), null);
		if (nodeApi != null) {
			switch (nodeApi) {
				case UP:
					Sec.checkIsOwnerOr404();
					if (path1_ctrlSym == null) {//need all
						Map map = FullDataBuilder.buildMap_ROOT(null);
						throw new CleanDataResponseException(UGson.toStringPrettyFromObject(map));
					} else {
						NI.stop("ni for pagename");
						return;
					}
				case UP_COM:
				case DOWN:
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
			IT.state(nodeApi == NodeApiCallType.UP, "Unsupported %s (only %s)", nodeApi, NodeApiCallType.UP);
			Path pageComs = AFC.DIR_FORMS(ppi.subdomain3(), ppi.pagename());
			if (X.emptyDir_NotExist(pageComs)) {
				throw StatusException.C404();
			}
			List<Path> ls = EFT.DIR.ls(pageComs);
			String lsPageComs = ls.stream().map(UF::fn).collect(Collectors.joining(STR.NL));
			throw new CleanDataResponseException(lsPageComs);
		}

		String path2_nodeNameStr = path2_nodeName.getFileName().toString();
		Path fileFormStatePath = AFC.getRpaFormStatePath(ppi.subdomain3(), ppi.pagename(), path2_nodeNameStr);
		FormState formState = FormState.ofPathFormFile_orCreate(fileFormStatePath, ppi.sdn());
//		Path fileFormStatePath = nodeApi.isCom() ? ANCS.getComStateDefault(ppi.subdomain3(), ppi.pagename(), path2_nodeName.getFileName().toString()) : formStateDefault;
//		FormState formState = nodeApi.isCom() ? FormState.ofPathComFile_OrCreate(fileFormStatePath, ppi.sdn()) : FormState.ofPathFormFile_orCreate(fileFormStatePath, ppi.sdn());

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

		NodeDir noteDir = NodeDir.ofFile(fileFormStatePath, ppi.sdn());

		//			boolean b = noteDir.hasSingleFile_Interative();
		Path videoFile = noteDir.singleFile(GEXT.VIDEO, null);
		if (videoFile != null) {
			ContentType contentType = ContentType.VIDEO_MP4;
			UWeb.responseContentTypeFromFile(ZKR.getResponse(), contentType, videoFile.toFile());
			throw new CleanDataResponseException("File %s/%s is already write to response", contentType, videoFile).nothing();
		}

//		FormState formPropsApply = FormState.ofFormName(path2_nodeName.getFileName().toString(), ppi.sdn());
//		throw new CleanDataResponseException(formPropsApply.pathComData());

		checkExeParam(formStatePath_FormOrProps, path2_nodeNameStr);
		throw new CleanDataResponseException(formStatePath_FormOrProps);
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
