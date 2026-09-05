package zk_os.coms.cache;

import mpc.exception.WhatIsTypeException;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.pare.Tuple;
import mpu.str.JOIN;
import mpu.str.SPLIT;
import org.checkerframework.checker.nullness.qual.Nullable;
import zk_os.coms.SpaceType;
import zk_os.core.Sdnu;
import zk_os.db.net.WebUsr;
import zk_os.sec.SecApp;
import zk_os.sec.UO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SecBool extends Tuple<Boolean> {

	@Override
	public String toString() {
		String usr = SecPropsTuple.ICON_USER + SYMJ.toBoolOrStop(isAllowedSecBy(UO.USR));
		String view = SecPropsTuple.ICON_VIEW + SYMJ.toBoolOrStop(isAllowedSecBy(UO.VIEW));
		String edit = SecPropsTuple.ICON_EDIT + SYMJ.toBoolOrStop(isAllowedSecBy(UO.EDIT));
		String run = SecPropsTuple.ICON_RUN + SYMJ.toBoolOrStop(isAllowedSecBy(UO.RUN));
		return JOIN.argsBySpace(usr, view, edit, run);
	}

	public SecBool() {
		this(new Boolean[4]);
	}

	private SecBool(Boolean[] objects) {
		super(objects);
	}

	public static @Nullable Map<String, SecBool> loadBoolMap(Sdnu sdnu, SpaceType spaceType, Map<String, SecPropsTuple> sdnItemsSecProps) throws Exception {

		Map<String, SecBool> boolMap = new ConcurrentHashMap<>();

		WebUsr usr = sdnu.getWebUsr();

		for (Map.Entry<String, SecPropsTuple> items : sdnItemsSecProps.entrySet()) {

			SecPropsTuple secProps = items.getValue();

			SecBool secBool = secProps.toNewSecBool(usr);

			boolMap.put(items.getKey(), secBool);
		}

		return boolMap;
	}

	public boolean isAllowedSecBy(UO oper) {
		return get(oper.index());
	}

	static boolean isAllowed(WebUsr usr, SecPropsTuple secTuple, UO oper) {
		String propValue = secTuple.getAsString(oper.index(), null);
		switch (oper) {
			case USR:
				return propValue == null ? SecApp.IS_ALLOWED_IFEMPTY__USR : usr.isEqualsUserByLoginOrAlias(propValue);

			case VIEW:
			case EDIT:
			case RUN:
				if (X.empty(propValue)) {
					return secTuple.usrName(null) == null ? SecApp.IS_ALLOWED_IFEMPTY__USR : false;
				}
				switch (propValue) {
					case SecApp.SECFORALL:
						return true;
					case SecApp.SECFORUSER:
						return usr.isMainRole_USER();
				}
				return isAllowedProp_ByDirtyLoginEntity(usr, propValue);

			default:
				throw new WhatIsTypeException(oper);

		}
	}

	private static boolean isAllowedProp_ByDirtyLoginEntity(WebUsr usr, String propValue) {
		String[] ids = SPLIT.argsByComma(propValue);
		return usr.isEqualsByIds(ids);
	}
}
