package zk_pages.zk_actuator;

import com.jayway.jsonpath.JsonPath;
import mpc.json.GsonMap;
import mpc.net.JHttp;
import mpu.X;

import java.io.IOException;
import java.util.List;

public class Actuator {
	public static void main(String[] args) throws IOException {

		String url = "http://q.com:8080/actuator/threaddump";
//		String jpUfosGuid = "$.payload.['org.unidata.mdm.rest.v2.data'].etalon.simpleAttributes[?(@.name == 'GUID')].value";
//		String jpUfosGuid = "$.threads[?(@.threadName)]";
		String jpUfosGuid = "$.threads[*].threadName";

		String jsonStr = JHttp.GET_BODY(url, null, String.class, 200);
		Object read = JsonPath.read(jsonStr, jpUfosGuid);
		X.exit(read);

		url = "http://fk-eb-arp-dev-impz-ufos.otr.ru:8090/ai-portlet/actuator/metrics/jetty.threads.busy?user_name=50eb9491-876b-480e-b71f-f835e37f9bd1&p=661";
		GsonMap gsonMap = JHttp.GET_BODY(url, null, GsonMap.class, 200);
		X.exit(gsonMap);

		String url0 = "http://fk-eb-arp-dev-impz-ufos.otr.ru:8090/ai-portlet/actuator/{{ACT}}?user_name=50eb9491-876b-480e-b71f-f835e37f9bd1";
//		String url0 = "http://fk-eb-arp-dev-impz-ufos.otr.ru:8090/ai-portlet/actuator/{{ACT}}?user_name=RevenkoMV";
		ActRequest actRequest = ActRequest.of(url0);
		X.exit(actRequest.getAllGm());
		X.exit(actRequest.getEnvGm());
		X.exit(actRequest.getBeanNames());
		List<String> metricsNames = actRequest.getMetricsNames();
		X.exit(actRequest.getMetricsItemGm(metricsNames.get(0)));
		X.exit(metricsNames);
//		GsonMap beans = beans();
//		X.exit(beans);


//		List list = (List) gsonMap.getAs("measurements", List.class);
//		Map map = (Map) list.get(0);
//		Number value = (Number) map.get("value");
//		X.exit(value.doubleValue());

	}


}
