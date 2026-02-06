package botcore;


import java.util.concurrent.ConcurrentHashMap;

public abstract class BotUpdate implements IBotUpdate {
	private ConcurrentHashMap meta_data;

	@Override
	public void setMetaData(String key, Object value) {
		if (meta_data == null) {
			meta_data = new ConcurrentHashMap();
		}
		meta_data.put(key, value);
	}

	@Override
	public Object getMetaData(String key, Object... defRq) {
		return meta_data == null ? null : meta_data.get(key);
	}

}
