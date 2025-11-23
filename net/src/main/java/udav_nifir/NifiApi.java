package udav_nifir;

import mpc.json.GsonMap;
import mpc.net.JHttp;
import mpc.url.UUrl;
import mpu.core.ARR;
import mpu.pare.Pare;

import java.io.IOException;
import java.util.List;

public class NifiApi {

	public static Pare<GsonMap, List<GsonMap>> getPgVersions(String host) throws IOException {
		GsonMap rsp = JHttp.GET_BODY(UUrl.normUrl(host, "/nifi-api/process-groups/root/process-groups"), GsonMap.class, 200);
		List<GsonMap> processGroups = rsp.getAsArrayGsonMap("processGroups", ARR.EMPTY_LIST);
//		List<GsonMap> maps = GsonMap.ofLinentJson(s);
		return Pare.of(rsp, processGroups);
	}
}
