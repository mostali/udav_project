package udav_net.apis;


import mpu.Sys;
import mpu.X;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import mpu.IT;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//https://docs.gitlab.com/ee/api/pipeline_schedules.html
public class UGitLab {

//	public static final String CI_AI_TOKEN = "";
//	public static final String CI_AI_PROJECT = "358";
//
//	public static void main(String[] args) {
////			String[] arg = new String[]{"PUID", all};
//		String[] arg = new String[0];
//		String status = UGitLab.runPipelineByApi(CI_AI_PROJECT, CI_AI_TOKEN, "eb-dev-pg", arg);
//
//
//	}
	public static String runPipelineByApi(String project_id, String token, String ref, String[]... vars) throws IOException {
		//- curl --request POST --form "token=$CI_JOB_TOKEN" --form ref=$CI_COMMIT_REF_NAME --form "variables[TCBP]=SelenoidTests" --form "variables[BUILD_VERSION]=$BUILD_VERSION_NUMBER" "https://GL_URL.ru/api/v4/projects/$CI_PROJECT_ID/trigger/pipeline"
		HttpClient httpclient = HttpClients.createDefault();
		String url = X.f("https://gitlab.com/api/v4/projects/%s/trigger/pipeline", project_id);
		Sys.p("Call to:" + url);
		Sys.p("Call to ref:" + ref);

		HttpPost httppost = new HttpPost(url);

		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("token", token));
		params.add(new BasicNameValuePair("ref", ref));
		for (String[] var : vars) {
			IT.notEmpty(var);
			String varName = X.f("variables[%s]", var[0]);
			switch (var.length) {
				case 1:
					params.add(new BasicNameValuePair(varName, ""));
					continue;
				case 2:
					params.add(new BasicNameValuePair(varName, var[1]));
					continue;
				default:
					throw new IllegalStateException("Var length must be 2:::" + Arrays.asList(var));
			}
		}
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();

		if (entity != null) {
			try (InputStream instream = entity.getContent()) {
				Sys.p(response.getStatusLine());
				return IOUtils.toString(instream, Charset.defaultCharset());
			}
		}

		throw new IllegalStateException("Response = null");
	}


	public static void download(String[] args) throws IOException, InterruptedException {
		// curl -o ff --header 'Private-Token: vdCxSBmzBzFoL9C2h' https://GL_URL.ru/api/v4/projects/69/repository/files/pom.xml\?ref\=master
		//		int projectId = 69;
		//		String filePath = "pom/xml";
		//		String glPrivatetoken = "";
		//		HttpRequest postRequest = HttpRequest.newBuilder()
		//				.uri(URI.create(U.f("https://GL_URL.ru/api/v4/projects/%s/repository/files/%s\\?ref\\=master", projectId, filePath)))
		//				.header("Private-Token", glPrivatetoken)
		//				.GET()
		//				.build();
		//		HttpClient client = HttpClient.newHttpClient();
		//		HttpResponse.BodyHandler<String> asString = HttpResponse.BodyHandlers.ofString();
		//		HttpResponse<String> response = client.send(postRequest, asString);
		//		response.body();

		// use the client to send the request
		//		var response = client.send(request, new JsonBodyHandler<>(APOD.class));
		//
		// the response:
		//		System.out.println(response.body().get().title);
	}

}
