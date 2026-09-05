package zk_notes.types;

import com.google.gson.JsonObject;
import mpc.json.UGson;
import mpc.map.MapTableContract;
import mpe.NT;

public interface AuthRspContract {

	NT getNet(NT... defRq);

	Long getNid(Long... defRq);

	String getN(String... defRq);

	Integer getExp();

	static AuthRspContract of(String json) {
		return of(UGson.JO(json));
	}

	static AuthRspContract of(JsonObject issueJsonObject) {
		return MapTableContract.buildContract_DefRq(UGson.toMapFromJO(issueJsonObject), AuthRspContract.class);
	}

	String getAlias();
}
