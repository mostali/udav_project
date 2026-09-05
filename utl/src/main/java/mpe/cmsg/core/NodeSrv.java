package mpe.cmsg.core;

import com.jayway.jsonpath.JsonPath;
import mpc.html.UXPath;
import mpe.cmsg.TrackMap;
import mpu.func.Function3;

import java.util.Map;

public interface NodeSrv<R> {

    Function3<String, String, String, String> handlerRspViaJpOrXp = (rsp, jp, xp) -> {
        if (jp != null) {
            Object read = JsonPath.read(rsp, jp);
            return read + "";
        } else if (xp != null) {
            Object read = UXPath.parseString(rsp, xp);
            return read + "";
        }
        return rsp;
    };

	class StdOpts {
        public Map trackContext = null;
        //        public INodeType iNodeType = null;
        public TrackMap.TrackId track = null;

        public boolean isAsync=false;
    }

    class EvalOpts extends StdOpts {
        public String jp = null;
        public String xp = null;
//        public boolean withOuterJp = false;
//        public boolean withOuterXp = false;

        public boolean withOuterJp() {
            return jp != null;
        }

        public boolean withOuterXp() {
            return xp != null;
        }

        public String applyJpXp(String rsp) {
           return NodeSrv.handlerRspViaJpOrXp.apply(rsp, jp, xp);
        }
    }


}
