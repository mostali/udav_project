package zk_notes.apiv1;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.env.APP;
import mpc.exception.CleanDataResponseException;
import mpc.exception.NI;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.fs.ext.GEXT;
import mpc.fs.fd.EFT;
import mpc.json.UGson;
import mpc.log.L;
import mpc.net.CON;
import mpc.net.ContentType;
import mpc.net.query.QueryUrl;
import mpe.cmsg.core.INodeType;
import mpe.cmsg.core.NodeSrv;
import mpe.cmsg.ns.NodeID;
import mpe.str.CN;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import mpu.func.FunctionV2;
import mpu.pare.Pare;
import mpu.str.JOIN;
import mpu.str.STR;
import mpu.str.TKN;
import udav_net.apis.zznote.ApiCase;
import udav_net.apis.zznote.NoteApi;
import utl_rest.StatusException;
import zk_notes.apiv1._ati.TreeRestCall;
import zk_notes.apiv1.old.FullDataBuilder;
import zk_notes.node.NodeDir;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.ISecState;
import zk_notes.node_state.ObjState;
import zk_notes.node_state.proxy.NodeProxyRW;
import zk_os.coms.AFC;
import zk_os.db.net.WebUsr;
import zk_os.sec.SecCheck;
import zk_os.sec.UO;
import zk_page.ZKR;
import zk_page.core.PagePathInfoWithQuery;
import zk_page.core.SpVM;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class NoteApiRestHandler {
    final PagePathInfoWithQuery curPPI;

    private static void handleApiDownload(PagePathInfoWithQuery curPPI) throws Exception {

        String targetPath = curPPI.pathWoQuery();

        targetPath = TKN.lastGreedy(UF.normFileStart(targetPath), "/", null);//cut _adi/*

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

        NodeID nodeID = NodeID.of(Paths.get(checkPath), null);
        if (nodeID == null) {
            throwError404.apply(targetPath, X.f("Node not found from path ", file));
        }

        WebUsr usr = WebUsr.get();

        boolean allowedNodeFormView = UO.VIEW.isAllowed(nodeID);
        if (!allowedNodeFormView) {
            String msg = X.f("User [%s] access denied for node [%s]", usr.getAliasOrLogin(), nodeID);
            throwError404.apply(targetPath, msg);
        }

        ZKR.download(file);
//					throw CleanDataResponseException.OK("Downloaded: " + path);
        throw CleanDataResponseException.NOTHING("download:" + targetPath);
    }

    private static void sendFileDataInRsp(String nodeName, Path formStatePath_FormOrProps) {
        boolean existNode = UFS.existFile(formStatePath_FormOrProps);
        if (existNode) {
            throw new CleanDataResponseException(formStatePath_FormOrProps);
        }
        throw StatusException.C404(NoteApi.MSG_404_ITEM_NOTE_FOUND, nodeName);
    }

    private static void checkDstUpdateState(PagePathInfoWithQuery curPPI, Pare<String, String> sdn, String path2_nodeName) {
        QueryUrl queryUrl = curPPI.queryUrl();
        String state = queryUrl.getFirstAsStr(NoteApi.QK_STATE, null);
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

    public static void checkExeParam(Pare sdn, NodeDir nodeDir, PagePathInfoWithQuery curPPI) {

        String exe = curPPI.queryUrl().getFirstAsStr(CN.EXE, null);
        if (exe == null) {
            return;
        }

        INodeType iNodeType = nodeDir.stdType(null);

        boolean hasEvalType = iNodeType != null;
        if (!hasEvalType) {
            throw new CleanDataResponseException(400, "Eval for node not supported");
        }

        String jp = curPPI.queryUrl().getFirstAsStr("jp", null);
        String xp = curPPI.queryUrl().getFirstAsStr("xp", null);
        String async = curPPI.queryUrl().getFirstAsStr("async", null);

        boolean isAsync = "1".equals(async);

        //
        //

        NodeSrv.EvalOpts stdOpts = new NodeSrv.EvalOpts();

        stdOpts.jp = jp;
        stdOpts.xp = xp;
        stdOpts.isAsync = isAsync;
        stdOpts.trackContext = SpVM.getTrackContext(ARR.EMPTY_MAP);

        throw nodeDir.stdSrvAny().evalAsSendResponse(nodeDir, stdOpts);

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

    @SneakyThrows
    public void checkRestCall() {//page/*/note

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


        NodeApiUpDownType nodeApiUpType = NodeApiUpDownType.valueOf(path0_pagename, null);

        if (NodeApiUpDownType.UP.isPatternEq(path0_pagename) || NodeApiUpDownType.DOWN.isPatternEq(path0_pagename)) {
            path0_pagename = NodeID.PAGE_INDEX_ALIAS;
            sdn = Pare.of(sd3, path0_pagename);
            path1_ctrlSym = curPPI.pathStr(1, null);
            path2_nodeName = curPPI.pathStr(2, null);
        }


        if (nodeApiUpType != null) {
            switch (nodeApiUpType) {
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
                    throw StatusException.C404(nodeApiUpType + " for root impossible");
                    //ok - need node ???
            }
        }

        nodeApiUpType = NodeApiUpDownType.valueOf(path1_ctrlSym, null);
        if (nodeApiUpType == null) {

            //it no rest call

            return;
        }

        if (path2_nodeName == null) {

            // it call 'page/*'

            if (nodeApiUpType == NodeApiUpDownType.DOWN) {
                //throw new CleanDataResponseException("Set item name", contentType, videoFile).nothing();
                IT.state(nodeApiUpType == NodeApiUpDownType.UP, "Unsupported %s (only %s)", nodeApiUpType, NodeApiUpDownType.UP);
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

        boolean isApi_COM = nodeApiUpType.isCom();

        Path formStatePath_FormOrProps = isApi_COM ? formState.pathProps() : fileFormStatePath;

        if (nodeApiUpType.isDown()) {// PUT REQUEST
            ISecState.checkIsAllowedEditOr403(formState);

            checkDstUpdateState(curPPI, sdn, path2_nodeName);

            NodeApiUpDownType.handlePostCallWithBody(formStatePath_FormOrProps);

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
            NodeProxyRW proxyRW = nodeDir.getProxyRW();
            boolean hasProxyPath = proxyRW.hasProxyPath_FILE();
            if (hasProxyPath) {
                formStatePath_FormOrProps = proxyRW.getTargetPath_FILE();
            }
        }

        sendFileDataInRsp(formState.objName(), formStatePath_FormOrProps);
    }

}
