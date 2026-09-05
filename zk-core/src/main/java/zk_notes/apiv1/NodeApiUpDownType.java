package zk_notes.apiv1;

import lombok.SneakyThrows;
import mpc.exception.CleanDataResponseException;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.RestStatusException;
import mpe.str.CN;
import mpu.core.*;
import mpu.str.STR;
import org.apache.commons.io.IOUtils;
import udav_net.apis.zznote.NoteApi;
import zk_page.ZKR;

import javax.servlet.ServletInputStream;
import java.nio.file.Path;
import java.util.*;

public enum NodeApiUpDownType {
    UP(NodeApiChars.UP), UP_COM(NodeApiChars.UP_COM), DOWN(NodeApiChars.DOWN), DOWN_COM(NodeApiChars.DOWN_COM);

    NodeApiUpDownType(String ctrlSymPart) {
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

    public boolean isDown() {
        return ctrlSym.charAt(0) == NodeApiChars.DOWN_CHAR;
    }

    public boolean isCom() {
        return ctrlSym.length() > 1 && ARRi.last(ctrlSym) == NodeApiChars.COM_CHAR;
    }

    public static NodeApiUpDownType valueOf(Path key, NodeApiUpDownType... defRq) {
        return key != null ? valueOf(key.getFileName().toString(), defRq) : ARG.throwErr(() -> new RequiredRuntimeException("Path is null"), defRq);
    }

    public static NodeApiUpDownType valueOf(String key, NodeApiUpDownType... defRq) {
        Optional<NodeApiUpDownType> findFirst = Arrays.stream(values()).filter(en -> en.ctrlSym.equals(key)).findFirst();
        return ARG.throwErrOpt(() -> new RequiredRuntimeException("NodeApi not found by key %s", key), findFirst, defRq);
    }

    public boolean isPatternEq(String path) {
        return ctrlSym.equals(path);
    }

}
