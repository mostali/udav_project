package zk_page.node_state;

import mp.utl_odb.tree.UTree;
import mpc.json.GsonMap;
import mpu.IT;
import mpu.core.ARG;
import mpu.pare.Pare;
import zk_old_core.AppZosCore_Old;

public class TreeState extends EntityState implements ISecState {// implements IPath

	protected final boolean sd_or_page;

	public UTree getTreeDb() {
		return sd_or_page ? AppZosCore_Old.TREE_SD3 : AppZosCore_Old.TREE_PAGES;
	}

	public String key() {
		return sd_or_page ? sdn.key() : sdn.val();
	}

	public TreeState(Pare sdn, boolean sd_or_page) {
		super(sdn, false);
		this.sd_or_page = sd_or_page;
	}

	@Override
	public void updatePropSingle(String prop, Object value) {
		GsonMap map = getMap(null);
		if (map == null) {
			map = new GsonMap();
		}
		map.put(prop, IT.NN(value));
		getTreeDb().put(key(), map.toStringJson());
	}

	@Override
	public String get(String prop, String... defRq) {
		GsonMap map = ARG.isDef(defRq) ? getMap(GsonMap.EMPTYMAP) : getMap();
		return map.getAsStr(prop, defRq);
	}

	@Override
	public <T> T getAs(String prop, Class<T> asType, T... defRq) {
		GsonMap map = ARG.isDef(defRq) ? getMap(GsonMap.EMPTYMAP) : getMap();
		return (T) map.getAs(prop, asType, defRq);
	}

	public GsonMap getMap(GsonMap... defRq) {
		return getTreeDb().getAs(key(), GsonMap.class, defRq);
	}

	@Override
	public Boolean hasPropEnable(String prop, Boolean... defRq) {
		try {
			return getMap().containsKey(prop);
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}
}
