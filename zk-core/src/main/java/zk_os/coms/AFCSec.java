package zk_os.coms;

import com.github.benmanes.caffeine.cache.LoadingCache;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpe.call_msg.core.INodeID;
import mpe.call_msg.core.IPageID;
import mpe.call_msg.core.ISpaceID;
import mpu.core.ARR;
import zk_os.core.Sdn;
import zk_os.core.Sdnu;
import zk_os.db.net.AnonimAppWebUsr;
import zk_os.db.net.WebUsr;
import zk_os.sec.ROLE;
import zk_os.sec.UO;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;


//						SecState.getSecState()
//						SecMan.isAllowedEdit()
//						SecPropChecker.ofSecProp()
//						SecApp.ofSecProp()


public class AFCSec extends SecCache {

	public static Set<Path> getItemPaths(WebUsr usr, AFC.SpaceType spaceType, Sdn sdn, UO... opers) {
		Set<Path> paths = getPathsLsClean(spaceType, sdn);
		ROLE usrRole = usr.getMainRoleType();
		switch (usrRole) {
			case ADMIN:
			case OWNER:
				return paths;
		}

		switch (spaceType) {
			case SPACES: {
//				TreeSet<Path> paths = AFC.PLANES.DIR_PLANES_LS_CLEAN(false);
				return ARR.EMPTY_SET;
			}

			case PAGES: {
//				TreeSet<Path> paths = AFC.PAGES.DIR_PAGES_LS_CLEAN(sdn.plane());
				return ARR.EMPTY_SET;
			}

			case NODES:
				switch (usrRole) {
					case ANONIM:
					case EDITOR:
						boolean isAnonim = usrRole == ROLE.ANONIM;
						if (!isAnonim && usr.isEditorFor(sdn)) {
							return paths;
						}
						Long sid = isAnonim ? AnonimAppWebUsr.SID : usr.getSid();
						Sdnu sdnu = Sdnu.of(sid, sdn);
						Map<String, SecCache.SecBool> secTupleStateMap = getCacheBool(spaceType).get(sdnu);
						Predicate<Path> pathFilter;
//						if (secTupleStateMap.isEmpty()) {
//							paths.forEach(p -> {
//								String itemName = UF.fn(p);
//								SecProps secProps = mapSecProps.get(itemName);
//								SecBool.initSecBool(usr, secProps, secTupleStateMap, itemName);
//							});
//						}
						pathFilter = p -> {
							SecCache.SecBool secBool = secTupleStateMap.get(UF.fn(p));
							if (secBool == null) {
								return false;
							}
							for (UO oper : opers) {
								if (secBool.isAllowedSecBy(oper)) {
									return true;
								}
							}
							return false;
						};
						return paths.stream().filter(pathFilter).collect(Collectors.toCollection(LinkedHashSet::new));
					default:
						throw new WhatIsTypeException(usrRole);

				}

			default:
				throw new WhatIsTypeException(spaceType);
		}

	}

//	public static boolean isAllowed(WebUsr usr, AFC.SpaceType spaceType, INodeID node, int iSece) {
//		Map<String, AFCSecBase.SecBool> boolMap = getCacheBool(spaceType).get(Sdn.of(node.sdn()).withUser(usr));
//		AFCSecBase.SecBool secBool = boolMap.get(node.nodeName());
//		return secBool.isAllowedSecBy(iSece);
//	}

	public static boolean isAllowed(WebUsr usr, AFC.SpaceType spaceType, ISpaceID itemId, UO oper) {
		LoadingCache<Sdnu, Map<String, SecBool>> cacheBool = getCacheBool(spaceType);
		Map<String, SecCache.SecBool> boolMap;
		String eName;
		switch (spaceType) {
			case SPACES:
				boolMap = cacheBool.get(Sdn.ofPlane(itemId.spaceName()).withUser(usr));
				eName = itemId.spaceName();
				break;
			case PAGES:
				IPageID pageID = (IPageID) itemId;
				boolMap = cacheBool.get(Sdn.of(pageID.sdn()).withUser(usr));
				eName = pageID.pageName();
				break;
			case NODES:
				INodeID nodeID = (INodeID) itemId;
				boolMap = cacheBool.get(Sdn.of(nodeID.sdn()).withUser(usr));
				eName = nodeID.nodeName();
				break;
			default:
				throw new WhatIsTypeException(spaceType);

		}
		SecCache.SecBool secBool = boolMap.get(eName);
		if (secBool == null) {
			//TODO???
			return oper.isAllowedIfEmpty();
		}
		return secBool.isAllowedSecBy(oper);

	}

}


