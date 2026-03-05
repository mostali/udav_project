package udav_ai.openai;

import mpc.env.EnvTlp;
import mpc.json.GsonMap;
import mpc.log.L;
import mpc.map.MAP;
import mpc.net.JHttp;
import mpu.X;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.pare.Pare;

import java.io.IOException;
import java.util.List;
import java.util.Map;

//https://platform.openai.com/welcome?step=try
public class UOpenAI {

	public static void main(String[] args) throws IOException {
		String msg = "напиши хоку о работе в IT";
		EnvTlp envTlp = EnvTlp.ofSysAcc("openai");
		String token = envTlp.readLogin();

		Pare<String, GsonMap> rsp = sendMsg2Gpt(token, msg);

		X.p(rsp.key());
//		X.p(rsp.val().toStringPrettyJson());
		X.exit();
	}

	public static Pare<String, GsonMap> sendMsg2Gpt(String token, String msg) throws IOException {
		String url = "https://api.openai.com/v1/chat/completions";
		String[][] headers = JHttp.HEADERS_ARGS_BY_SEMICOLON("Content-Type: application/json", "Authorization: Bearer " + token);
		GsonMap inJson = new GsonMap();
		inJson.put("model", "gpt-4o-mini");
		inJson.put("store", true);
		Map msgJson = MAP.of("role", "user", "content", msg);
		inJson.put("messages", ARR.as(msgJson));

		GsonMap rsp = JHttp.GET_BODY(url, headers, inJson.toStringJson(), GsonMap.class, 200);

		List<GsonMap> choices = rsp.getAsArrayGsonMap("choices");

		GsonMap firstChoice = ARRi.first(choices);
		GsonMap firstMsg = firstChoice.getAsGsonMap("message");

		String content = firstMsg.getAsString("content");

		L.info(msg);
		L.info("----------------ANSWER-----------------");
		L.info(content);

		GsonMap usage = rsp.getAsGsonMap("usage");

		L.info("----------------USAGE-----------------");
		L.info(usage.toStringPrettyJson());

		return Pare.of(content, usage);
	}
}
