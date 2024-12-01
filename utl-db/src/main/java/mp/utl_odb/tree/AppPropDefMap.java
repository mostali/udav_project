package mp.utl_odb.tree;

import mpc.json.GsonMap;

public class AppPropDefMap extends AppPropDef<GsonMap> {

	private final String[] keys;

	public AppPropDefMap(String key, String... keys) {
		super(key, null, GsonMap.class);
		this.keys = keys;
	}

	@Override
	public GsonMap getValueOrDefault(GsonMap... defRq) {
		return super.getValueOrDefault(defRq);
	}
}
