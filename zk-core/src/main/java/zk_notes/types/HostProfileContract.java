package zk_notes.types;

import com.google.gson.JsonObject;
import mpu.core.ARG;
import mpe.core.P;
import mpc.exception.RequiredRuntimeException;
import mpc.json.UGson;
import mpc.map.MapTableContract;

public interface HostProfileContract {

	Integer getPort();

	String getHost();

	String getLogin();

	String getPass();

	public static void main(String[] args) {
		P.p(HostProfileContract.of("{\"login\":\"123\",\"pass\":\"123\",\"host\":\"123\",\"port\":22}"));
	}

	static HostProfileContract of(String json, HostProfileContract... defRq) {
		try {
			HostProfileContract hostProfileContract = of(UGson.JO(json));
			if (ARG.isDef(defRq)) {//doValid
				hostProfileContract.toString();
			}
			return hostProfileContract;
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "HostProfileContract not loaded from data\n" + json), defRq);
		}
	}

	static HostProfileContract of(JsonObject issueJsonObject) {
		return MapTableContract.buildContract_MarkNotRq(UGson.toMapFromJO(issueJsonObject), HostProfileContract.class);
	}
}
