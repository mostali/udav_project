package zklogapp.otr.jmx;

import lombok.SneakyThrows;
import mpc.fs.UF;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public enum JmxHikariPoolAttrs {
	ActiveConnections, IdleConnections, ThreadsAwaitingConnection, TotalConnections;

	public static final String URL_POOL_PATH_PFX = "jmx/servers/0/domains/com.zaxxer.hikari/mbeans/type%3DPool+%28main%29/attributes";

	public static Map<JmxHikariPoolAttrs, Integer> getGroupStats(String hostUrl) {
		LinkedHashMap<JmxHikariPoolAttrs, Integer> collect = Arrays.stream(values()).collect(Collectors.toMap(t -> t, t -> t.getSinlgeValue(hostUrl), (e1, e2) -> e1, LinkedHashMap::new));
		return collect;
	}

	@SneakyThrows
	public int getSinlgeValue(String hostUrl) {
		return JmxPropertyCollector.parseIntValue(buildUrlToTargetAttribute(hostUrl));
	}

	private String buildUrlToTargetAttribute(String hostUrl) {
		return UF.normFile(hostUrl, URL_POOL_PATH_PFX, name());
	}
}
