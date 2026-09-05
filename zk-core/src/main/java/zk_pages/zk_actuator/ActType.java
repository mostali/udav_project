package zk_pages.zk_actuator;

public enum ActType {
	ALL, BEANS, CACHES, CACHES__, HEALTH, HEALTH__, INFO, CONDITIONS, CONFIGPROPS, ENV, ENV__, FLYWAY, LOGGERS, LOGGERS__, HEAPDUMP, THREADDUMP, METRICS, METRICS__, SCHEDULEDTASKS, QUARTZ, MAPPINGS;

	public boolean isHeapDump() {
		return this == HEAPDUMP;
	}

	public boolean isWithArgs() {
		return name().endsWith("__");
	}
}
