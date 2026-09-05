package mp.jira;

import mpc.env.EnvTlp;
import mpu.X;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.util.EntityUtils;

public class JiraHttpClientExample {


	static String[] hlp = EnvTlp.ofSysAcc("jira", "dav").readAsHLP3();

	public static void main(String[] args) {
		try {
			String issueKey = "SUP-1495556"; // Замените на ваш ключ задачи
			getIssue(issueKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getIssue(String issueKey) throws Exception {

		BasicCredentialsProvider provider = new BasicCredentialsProvider();
		String userName = hlp[0];
		String password = hlp[1];
		String uri = hlp[2] + "/rest/api/2/issue/" + issueKey;

		X.exit(userName, password, uri);
		provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));

		try (CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultCredentialsProvider(provider)
				.build()) {

			HttpGet httpGet = new HttpGet(uri);
			httpGet.setHeader("Content-Type", "application/json");

			HttpResponse response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			System.out.println("Response Code : " + statusCode);

			if (statusCode == 200) { // success
				String jsonResponse = EntityUtils.toString(response.getEntity());
				System.out.println("Response: " + jsonResponse);
			} else {
				System.out.println("GET request failed");
			}
		}
	}
}
