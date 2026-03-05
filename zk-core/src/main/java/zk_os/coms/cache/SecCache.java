package zk_os.coms.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.json.GsonMap;
import mpc.rfl.RFL;
import mpe.NT;
import mpe.UCaffeine;
import mpe.cmsg.ns.NodeID;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Tuple;
import mpu.str.Rt;
import mpu.str.STR;
import mpu.str.Sb;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_os.coms.AFC;
import zk_os.coms.SpaceType;
import zk_os.core.Sdn;
import zk_os.core.Sdnu;
import zk_os.db.net.WebUsr;
import zk_os.sec.SecApp;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;


public class SecCache {

	public static final Logger L = LoggerFactory.getLogger(SecCache.class);
	public static final Logger L2 = mpc.log.L.ofFile("ttt.log");

	private static final LoadingCache<Sdn, Map<String, SecPropsTuple>> CACHE_PLANES_PROPS = UCaffeine.buildCache(new PropsCacheLoader(SpaceType.SPACES), true, SecApp.CACHE_EXPIRE_AFTER_WRITE, SecApp.MAX_CACHE_SIZE);
	private static final LoadingCache<Sdn, Map<String, SecPropsTuple>> CACHE_PAGES_PROPS = UCaffeine.buildCache(new PropsCacheLoader(SpaceType.PAGES), true, SecApp.CACHE_EXPIRE_AFTER_WRITE, SecApp.MAX_CACHE_SIZE);
	private static final LoadingCache<Sdn, Map<String, SecPropsTuple>> CACHE_FORMS_PROPS = UCaffeine.buildCache(new PropsCacheLoader(SpaceType.NODES), true, SecApp.CACHE_EXPIRE_AFTER_WRITE, SecApp.MAX_CACHE_SIZE);

	private static final LoadingCache<Sdnu, Map<String, SecBool>> CACHE_PLANES_BOOL = UCaffeine.buildCache(new BoolCacheLoader(SpaceType.SPACES), true, SecApp.CACHE_EXPIRE_AFTER_WRITE, SecApp.MAX_CACHE_SIZE);
	private static final LoadingCache<Sdnu, Map<String, SecBool>> CACHE_PAGES_BOOL = UCaffeine.buildCache(new BoolCacheLoader(SpaceType.PAGES), true, SecApp.CACHE_EXPIRE_AFTER_WRITE, SecApp.MAX_CACHE_SIZE);
	private static final LoadingCache<Sdnu, Map<String, SecBool>> CACHE_FORMS_BOOL = UCaffeine.buildCache(new BoolCacheLoader(SpaceType.NODES), true, SecApp.CACHE_EXPIRE_AFTER_WRITE, SecApp.MAX_CACHE_SIZE);

	public static void main(String[] args) {
		NT.BEA.set();
		WebUsr fake = WebUsr.fake();
//		Sdn sdn = Sdn.of(NodeID.PLANE_INDEX_ALIAS, NodeID.PAGE_INDEX_ALIAS);
//		Map<String, SecBool> stringSecBoolMap = getCacheBool(SpaceType.SPACES).get(Sdnu.of(fake, sdn));
		Sdn sdn2 = Sdn.of(NodeID.PLANE_INDEX_ALIAS, "tsm");
		Map<String, SecBool> stringSecBoolMap2 = getCacheBool(SpaceType.SPACES).get(Sdnu.of(fake, sdn2));
		toString0(0, L);
	}

	public static String toString0(int lev, Logger... logger) {
		int l0 = lev;
		int l2 = lev + 1;
		Sb sb = new Sb();
		sb.TABNL(l0, RFL.scn(SecCache.class) + ":");
		for (SpaceType value : SpaceType.values()) {
			String dataEnt = toString0(value, l2);
			sb.NL(dataEnt);
		}
		String data = sb.toString();
		ARG.applyVoid((l) -> l.info(sb.toString()), logger);
		return data;
	}

	public static String toString0(SpaceType spaceType, int lev, Logger... logger) {
		int l0 = lev;
		int l2 = lev + 1;
		int l3 = lev + 2;
		ConcurrentMap<Sdn, Map<String, SecPropsTuple>> map0 = getCacheProps(spaceType).asMap();
		Sb sb = new Sb();
		sb.TABNL(lev, STR.repeat(spaceType.toChar(), 20) + spaceType.name() + ":" + X.sizeOf(map0));
		map0.forEach((sdn, map) -> {
			sb.TABNL(l2, sdn + "*" + X.sizeOf(map));
			map.forEach((iName, props) -> {
				sb.TABNL(l3, iName + props);
			});
		});
		String data = sb.toString();
		ARG.applyVoid((l) -> l.info(sb.toString()), logger);
		return data;
	}

	@RequiredArgsConstructor
	private static class BoolCacheLoader implements CacheLoader<Sdnu, Map<String, SecBool>> {
		final SpaceType spaceType;


		@Override
		public @Nullable Map<String, SecBool> load(@NonNull Sdnu sdnu) throws Exception {

			LoadingCache<Sdn, Map<String, SecPropsTuple>> cachePropsImpl = getCacheProps(spaceType);

			Map<String, SecPropsTuple> sdnItemsSecProps = cachePropsImpl.get(sdnu.sdn);

			Map<String, SecBool> stringSecBoolMap = SecBool.loadBoolMap(sdnu, spaceType, sdnItemsSecProps);

			L2.info(sdnu.sdn.nodeId() + ":BOOL:" + X.sizeOf(stringSecBoolMap) + ":::" + stringSecBoolMap.keySet());

			if (L.isDebugEnabled()) {
				L.debug("CacheBool [FRESH] [{}] for '{}', items:\n{}", spaceType, sdnu, Rt.buildReport(stringSecBoolMap));
			} else if (L.isInfoEnabled()) {
				L.info("CacheBool [FRESH] [{}] for '{}' ", spaceType, sdnu);

			}
			return stringSecBoolMap;
		}
	}

	@RequiredArgsConstructor
	private static class PropsCacheLoader implements CacheLoader<Sdn, Map<String, SecPropsTuple>> {

		final SpaceType spaceType;

		@Override
		public @Nullable Map<String, SecPropsTuple> load(@NonNull Sdn sdn) throws Exception {

			Set<Path> paths = getPathsLsClean(spaceType, sdn);
			L2.info(sdn.nodeId() + ":PROPS:" + X.sizeOf(paths));
			Map<String, SecPropsTuple> secPpops = new ConcurrentHashMap<>();
			for (Path path : paths) {
				String iName = UF.fn(path);
				Path statePathProps = getItemPropsPaths(spaceType, sdn, iName);
				if (!UFS.existFile(statePathProps)) {
					secPpops.put(iName, SecPropsTuple.ofEmptyProps());
					continue;
				}
				GsonMap gsonMap = GsonMap.of(statePathProps, false);
				Tuple secPropsItem = gsonMap.getTuple(SecApp.USER, SecApp.SECV, SecApp.SECE, SecApp.SECR);
				secPpops.put(iName, SecPropsTuple.of(secPropsItem));
			}
			if (L.isInfoEnabled()) {
				L.info("CacheProps [FRESH] [{}] for {} ", spaceType, sdn);
			}
			return secPpops;
		}
	}

	//
	//

	public static SecPropsTuple getPropsPages(Sdn sdn) {
		Map<String, SecPropsTuple> props = getCacheProps(SpaceType.PAGES).get(sdn.toSdnPlaneIndex());
		return props.get(sdn.page());
	}

	public static Map<String, SecPropsTuple> getPropsNodes(Sdn sdn) {
		Map<String, SecPropsTuple> props = SecCache.getCacheProps(SpaceType.NODES).get(sdn);
		return props;
	}

	//
	//

	public static LoadingCache<Sdnu, Map<String, SecBool>> getCacheBool(SpaceType spaceType) {
		switch (spaceType) {
			case SPACES:
				return CACHE_PLANES_BOOL;
			case PAGES:
				return CACHE_PAGES_BOOL;
			case NODES:
				return CACHE_FORMS_BOOL;
			default:
				throw new WhatIsTypeException(spaceType);
		}
	}

	private static LoadingCache<Sdn, Map<String, SecPropsTuple>> getCacheProps(SpaceType spaceType) {
		switch (spaceType) {
			case SPACES:
				return CACHE_PLANES_PROPS;
			case PAGES:
				return CACHE_PAGES_PROPS;
			case NODES:
				return CACHE_FORMS_PROPS;
			default:
				throw new WhatIsTypeException(spaceType);
		}
	}

	public static Set<Path> getPathsLsClean(SpaceType spaceType, Sdn sdn, Predicate<Path>... defFilter) {
		switch (spaceType) {
			case SPACES:
				return AFC.PLANES.DIR_PLANES_LS_CLEAN(false);
			case PAGES:
				return AFC.PAGES.DIR_PAGES_LS_CLEAN(sdn.plane());
			case NODES:
				return AFC.FORMS.DIR_FORMS_LS_CLEAN(sdn);
			default:
				throw new WhatIsTypeException(spaceType);
		}
	}

	@Deprecated //TODO/use map
	public static int getSpaceTypesCount(SpaceType spaceType) {
		switch (spaceType) {
			case SPACES:
				return AFC.PLANES.DIR_PLANES_LS_CLEAN(false).size();
			case PAGES:
				return AFC.PAGES.DIR_PAGES_LS_CLEAN(Sdn.planeCurrent()).size();
			case NODES:
				Map<String, SecPropsTuple> formsSecCache = CACHE_FORMS_PROPS.get(Sdn.get());
//				return AFC.FORMS.DIR_FORMS_LS_CLEAN(Sdn.get());
				return formsSecCache.size();
			default:
				throw new WhatIsTypeException(spaceType);
		}
	}

	public static Path getPathProps(SpaceType spaceType, Sdn sdn) {
		return getPathProps(spaceType, sdn, null);
	}

	public static Path getPathProps(SpaceType spaceType, Sdn sdn, String nodeName) {
		switch (spaceType) {
			case SPACES:
				return AFC.PLANES.getStatePath_PROPS(sdn.plane());
			case PAGES:
				return AFC.PAGES.getStatePath_PROPS(sdn.plane(), sdn.page());
			case NODES:
				return AFC.FORMS.getStatePath_PROPS(sdn.plane(), sdn.page(), nodeName);
			default:
				throw new WhatIsTypeException(spaceType);
		}
	}


	static Path getItemPropsPaths(SpaceType spaceType, Sdn sdn, String itemName) {

		switch (spaceType) {
			case SPACES:
				return AFC.PLANES.getStatePath_PROPS(sdn.key());
			case PAGES:
				return AFC.PAGES.getStatePath_PROPS(sdn);
			case NODES:
				return AFC.FORMS.getStatePath_PROPS(sdn.plane(), sdn.page(), itemName);
			default:
				throw new WhatIsTypeException(spaceType);
		}
	}


}


