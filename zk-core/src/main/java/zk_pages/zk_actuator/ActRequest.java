package zk_pages.zk_actuator;

import lombok.SneakyThrows;
import mpc.json.GsonMap;
import mpc.net.JHttp;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ActRequest {
	public static final String ACT_PLACEHOLDER = "{{ACT}}";
	final String url0;

	public static ActRequest of(String urlTemplate) {
		return new ActRequest(urlTemplate);
	}

	private ActRequest(String url) {
		this.url0 = url;
	}

	//
	//
	// ALL

	private GsonMap allGm = null;

	@SneakyThrows
	public GsonMap getAllGm() {
		if (this.allGm != null) {
			return this.allGm;
		}
		String[][] cookie = {{"Cookie", "1"}};
		GsonMap gsonMap = JHttp.GET_BODY(getAllUrl(), cookie, null, GsonMap.class, 200);
		return allGm = gsonMap;
	}

	public @NotNull String getAllUrl() {
		return this.url0.replace(ACT_PLACEHOLDER, "");
	}


	//
	//
	// METRIC'S

	@SneakyThrows
	public List<String> getMetricsNames(List<String>... defRq) {
		GsonMap gsonMap = JHttp.GET_BODY(getMetricsUrl(), null, GsonMap.class, 200);
		return (List<String>) gsonMap.getAs("names", List.class, defRq);
	}

	public @NotNull String getMetricsUrl() {
		return this.url0.replace(ACT_PLACEHOLDER, "metrics");
	}

	private GsonMap metricsMap = null;

	@SneakyThrows
	public GsonMap getMetricsItemGm(String item) {
		if (this.metricsMap != null) {
			return this.metricsMap;
		}
		GsonMap gsonMap = JHttp.GET_BODY(getMetricsUrl(item), null, GsonMap.class, 200);
		return this.metricsMap = gsonMap;
	}

	public @NotNull String getMetricsUrl(String item) {
		return this.url0.replace(ACT_PLACEHOLDER, "metrics/" + item);
	}

	//
	//
	// BEANS

	private GsonMap beansMap = null;

	@SneakyThrows
	public GsonMap getBeansGm() {
		if (this.beansMap != null) {
			return this.beansMap;
		}
		String beansUrl = getBeansUrl();
		GsonMap gsonMap = JHttp.GET_BODY(beansUrl, null, GsonMap.class, 200);
		GsonMap beansMap = gsonMap.getAsGsonMap("contexts").getAsGsonMap("application").getAsGsonMap("beans");
		return this.beansMap = beansMap;
	}

	public String getBeansUrl() {
		return this.url0.replace(ACT_PLACEHOLDER, "beans");
	}

	public Set<String> getBeanNames() {
		return getBeansGm().keySet();
	}

	public GsonMap getBeanItemGm(String beanName) {
		return getBeansGm().child(beanName);
	}

	//
	//
	//

	GsonMap envMap = null;

	@SneakyThrows
	public GsonMap getEnvGm() {
		if (this.envMap != null) {
			return this.envMap;
		}
		//Object get = AHttp.GET(url);
		String[][] cookie = {{"Cookie", "1"}};//hack
		GsonMap gsonMap = JHttp.GET_BODY(getEnvUrl(), cookie, null, GsonMap.class, 200);
		return envMap = gsonMap;
	}

	public String getEnvUrl() {
		return this.url0.replace(ACT_PLACEHOLDER, "env");
	}

	//
	//
	//

	Map<ActType, GsonMap> cacheGm = new HashMap<>();

	@SneakyThrows
	public GsonMap getActGm(ActType actType) {
		GsonMap gm = cacheGm.get(actType);
		if (gm != null) {
			return gm;
		}
		//Object get = AHttp.GET(url);
		String[][] cookie = {{"Cookie", "1"}};//hack
		GsonMap gsonMap = JHttp.GET_BODY(getActUrl(actType), cookie, null, GsonMap.class, 200);
		return envMap = gsonMap;
	}

	public String getActUrl(ActType actType) {
		if (actType == ActType.ALL) {
			return this.url0.replace(ACT_PLACEHOLDER, "");
		}
		return this.url0.replace(ACT_PLACEHOLDER, actType.name().toLowerCase());
	}

}
