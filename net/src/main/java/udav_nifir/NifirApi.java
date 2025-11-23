package udav_nifir;

import com.google.gson.JsonElement;
import lombok.SneakyThrows;
import mpc.url.UUrl;
import mpc.json.GsonMap;
import mpc.net.CON;
import mpc.net.JHttp;
import mpu.pare.Pare;

import java.io.IOException;
import java.util.List;

public class NifirApi {


	public static String getFlowAsString(String host, String bucket, String flow) throws IOException {
		return JHttp.GET_BODY(UUrl.normUrl(host, "/nifi-registry-api/buckets/") + bucket + "/flows/" + flow + "/versions/latest", String.class, 200);
	}

	@SneakyThrows
	public static String setFlow(String host, String bucket, String flow, String data) throws IOException {
		String url = UUrl.normUrl(host, "/nifi-registry-api/buckets/" + bucket + "/flows/" + flow + "/versions/import");
		String[][] headers = CON.HEADERS_ARGS_BY_SEMICOLON("Content-Type:application/json");
		String s = JHttp.POST_BODY(url, headers, data, String.class, 200, 201);
		return s;
	}

	public static List<GsonMap> getFlow(String host, String bucket, String flow) throws IOException {
		JsonElement s = JHttp.GET_BODY(UUrl.normUrl(host, "/nifi-registry-api/buckets/") + bucket + "/flows/" + flow + "/versions/latest", JsonElement.class, 200);
		List<GsonMap> maps = GsonMap.ofLinentJson(s);
		return maps;
	}

	public static Pare<String, List<GsonMap>> getVersions(String host, String bucket, String flow) throws IOException {
		JsonElement s = JHttp.GET_BODY(UUrl.normUrl(host, "/nifi-registry-api/buckets/") + bucket + "/flows/" + flow + "/versions", JsonElement.class, 200);
		List<GsonMap> maps = GsonMap.ofLinentJson(s);
		return Pare.of(s.toString(), maps);
	}

	public static String getVersionsAsString(String host, String bucket, String flow) throws IOException {
		return JHttp.GET_BODY(UUrl.normUrl(host, "/nifi-registry-api/buckets/") + bucket + "/flows/" + flow + "/versions", String.class, 200);
	}

	public static List<GsonMap> getBucketItems(String host) throws IOException {
		JsonElement s = JHttp.GET_BODY(UUrl.normUrl(host, "nifi-registry-api/items"), JsonElement.class, 200);
		List<GsonMap> maps = GsonMap.ofLinentJson(s);
		return maps;
	}

	public static List<GsonMap> getBuckets(String host) throws IOException {
		JsonElement s = JHttp.GET_BODY(UUrl.normUrl(host, "nifi-registry-api/buckets"), JsonElement.class, 200);
		List<GsonMap> maps = GsonMap.ofLinentJson(s);
		return maps;
	}


}
