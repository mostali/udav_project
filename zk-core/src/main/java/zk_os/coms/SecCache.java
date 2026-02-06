package zk_os.coms;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.json.GsonMap;
import mpc.log.L;
import mpc.str.sym.SYMJ;
import mpe.UCaffeine;
import mpu.X;
import mpu.core.ARR;
import mpu.pare.Tuple;
import mpu.str.JOIN;
import mpu.str.Rt;
import mpu.str.SPLIT;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_os.core.Sdn;
import zk_os.core.Sdnu;
import zk_os.db.net.WebUsr;
import zk_os.sec.SecApp;
import zk_os.sec.UO;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class SecCache {

	public static final Logger L = LoggerFactory.getLogger(SecCache.class);

	public static Set<Path> getPathsLsClean(AFC.SpaceType spaceType, Sdn sdn) {
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
	public static int getSpaceTypesCount(AFC.SpaceType spaceType) {
		switch (spaceType) {
			case SPACES:
				return AFC.PLANES.DIR_PLANES_LS_CLEAN(false).size();
			case PAGES:
				return AFC.PAGES.DIR_PAGES_LS_CLEAN(Sdn.planeCurrent()).size();
			case NODES:
				Map<String, SecProps> formsSecCache = CACHE_FORMS_PROPS.get(Sdn.get());
//				return AFC.FORMS.DIR_FORMS_LS_CLEAN(Sdn.get());
				return formsSecCache.size();
			default:
				throw new WhatIsTypeException(spaceType);
		}
	}

	public static Path getPathProps(AFC.SpaceType spaceType, Sdn sdn) {
		return getPathProps(spaceType, sdn, null);
	}

	public static Path getPathProps(AFC.SpaceType spaceType, Sdn sdn, String nodeName) {
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

	public static LoadingCache<Sdnu, Map<String, SecBool>> getCacheBool(AFC.SpaceType spaceType) {
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

	private static Path getItemPropsPaths(AFC.SpaceType spaceType, Sdn sdn, String itemName) {

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


	public static LoadingCache<Sdn, Map<String, SecProps>> getCacheProps(AFC.SpaceType spaceType) {
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

	public static class SecBool extends Tuple<Boolean> {

		@Override
		public String toString() {
			String usr = SecProps.ICON_USER + SYMJ.toBoolOrStop(isAllowedSecBy(UO.USR));
			String view = SecProps.ICON_VIEW + SYMJ.toBoolOrStop(isAllowedSecBy(UO.VIEW));
			String edit = SecProps.ICON_EDIT + SYMJ.toBoolOrStop(isAllowedSecBy(UO.EDIT));
			String run = SecProps.ICON_RUN + SYMJ.toBoolOrStop(isAllowedSecBy(UO.RUN));
			return JOIN.argsBySpace(usr, view, edit, run);
		}

		public SecBool() {
			this(new Boolean[4]);
		}

		private SecBool(Boolean[] objects) {
			super(objects);
		}

		public static @Nullable Map<String, SecBool> loadBoolMap(Sdnu sdnu, AFC.SpaceType spaceType, Map<String, SecProps> sdnItemsSecProps) throws Exception {
			Map<String, SecBool> boolMap = new ConcurrentHashMap<>();
			WebUsr usr = sdnu.getWebUsr();

			for (Map.Entry<String, SecProps> items : sdnItemsSecProps.entrySet()) {

				SecProps secProps = items.getValue();

				SecBool secBool = secProps.toNewSecBool(usr);

				boolMap.put(items.getKey(), secBool);
			}
			//		if (L.isInfoEnabled()) {
			//			L.info("CacheBool [FRESH] [{}] for {} ", spaceType, sdnu);
			//		}
			return boolMap;
		}

		public boolean isAllowedSecBy(UO oper) {
			return get(oper.index());
		}

//		public boolean isAllowedSecBy(int iSecProp) {
//			switch (iSecProp) {
//				case SecApp.I_USR:
//					return isAllowedUsr();
//				case SecApp.I_SECV:
//					return isAllowedSecV();
//				case SecApp.I_SECE:
//					return isAllowedSecE();
//				case SecApp.I_SECR:
//					return isAllowedSecR();
//				default:
//					throw new WhatIsTypeException(iSecProp);
//
//			}
//		}

		private static boolean isAllowed(WebUsr usr, SecProps secTuple, UO oper) {
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
//			List<String> ids = SPLIT.allByComma(propValue);
			String[]ids = SPLIT.argsByComma(propValue);
			return usr.isEqualsByIds(ids);
//			Set<String> allLoginValues = usr.getAllLoginValues();
//			for (String id : ids) {
//				if (allLoginValues.contains(id)) {
//					return true;
//				}
//			}
//			return false;
//			return ARR.containsAny(ids, allLoginValues.toArray(new String[allLoginValues.size()]));
		}
	}

	public static class SecProps extends Tuple {


		public static final String ICON_RUN = SYMJ.ARROW_REPEAT_TRIANGLE_GREEN;
		//		public static final String ICON_EDIT = SYMJ.EDIT;
		public static final String ICON_EDIT = SYMJ.GRID_CORNER;
		public static final String ICON_VIEW = SYMJ.EYE;
		public static final String ICON_USER = SYMJ.USER;

		@Override
		public String toString() {
			String usr = ICON_USER + SYMJ.toStringOrFail(usrName(null));
			String view = ICON_VIEW + SYMJ.toStringOrFail(secv(null));
			String edit = ICON_EDIT + SYMJ.toStringOrFail(sece(null));
			String run = ICON_RUN + SYMJ.toStringOrFail(secr(null));
			return JOIN.argsBySpace(usr, view, edit, run);
		}

		public static SecProps of(Tuple tuple) {
			return new SecProps(tuple.objects);
		}

		public SecProps(Object[] objects) {
			super(objects);
		}

		public String usrName(String... defRq) {
			return getAsString(SecApp.I_USR, defRq);
		}

		public String secv(String... defRq) {
			return getAsString(SecApp.I_SECV, defRq);
		}

		public String sece(String... defRq) {
			return getAsString(SecApp.I_SECE, defRq);
		}

		public String secr(String... defRq) {
			return getAsString(SecApp.I_SECR, defRq);
		}

		public SecBool toNewSecBool(WebUsr usr) {
			return toNewSecBool(usr, this);
		}

		private static SecBool toNewSecBool(WebUsr usr, SecProps secProps) {
			SecBool secBool = new SecBool();

			for (UO oper : UO.values()) {
				secBool.set(oper.index(), SecBool.isAllowed(usr, secProps, oper));
			}
//			secBool.set(SecApp.I_USR, SecBool.isAllowed(usr, secProps, UO.USR));
//			secBool.set(SecApp.I_SECV, SecBool.isAllowed(usr, secProps, SecApp.I_SECV));
//			secBool.set(SecApp.I_SECE, SecBool.isAllowed(usr, secProps, SecApp.I_SECE));
//			secBool.set(SecApp.I_SECR, SecBool.isAllowed(usr, secProps, SecApp.I_SECR));
			return secBool;
		}
	}

	public static final LoadingCache<Sdn, Map<String, SecProps>> CACHE_PLANES_PROPS = UCaffeine.buildCache(new PropsCacheLoader(AFC.SpaceType.SPACES), true, SecApp.CACHE_EXPIRE_AFTER_WRITE, SecApp.MAX_CACHE_SIZE);
	public static final LoadingCache<Sdn, Map<String, SecProps>> CACHE_PAGES_PROPS = UCaffeine.buildCache(new PropsCacheLoader(AFC.SpaceType.PAGES), true, SecApp.CACHE_EXPIRE_AFTER_WRITE, SecApp.MAX_CACHE_SIZE);
	public static final LoadingCache<Sdn, Map<String, SecProps>> CACHE_FORMS_PROPS = UCaffeine.buildCache(new PropsCacheLoader(AFC.SpaceType.NODES), true, SecApp.CACHE_EXPIRE_AFTER_WRITE, SecApp.MAX_CACHE_SIZE);

	public static final LoadingCache<Sdnu, Map<String, SecBool>> CACHE_PLANES_BOOL = UCaffeine.buildCache(new BoolCacheLoader(AFC.SpaceType.SPACES), true, SecApp.CACHE_EXPIRE_AFTER_WRITE, SecApp.MAX_CACHE_SIZE);
	public static final LoadingCache<Sdnu, Map<String, SecBool>> CACHE_PAGES_BOOL = UCaffeine.buildCache(new BoolCacheLoader(AFC.SpaceType.PAGES), true, SecApp.CACHE_EXPIRE_AFTER_WRITE, SecApp.MAX_CACHE_SIZE);
	public static final LoadingCache<Sdnu, Map<String, SecBool>> CACHE_FORMS_BOOL = UCaffeine.buildCache(new BoolCacheLoader(AFC.SpaceType.NODES), true, SecApp.CACHE_EXPIRE_AFTER_WRITE, SecApp.MAX_CACHE_SIZE);


	@RequiredArgsConstructor
	private static class PropsCacheLoader implements CacheLoader<Sdn, Map<String, SecProps>> {

		final AFC.SpaceType spaceType;

		@Override
		public @Nullable Map<String, SecProps> load(@NonNull Sdn sdn) throws Exception {
			Set<Path> paths = getPathsLsClean(spaceType, sdn);
			Map<String, SecProps> secPpops = new ConcurrentHashMap<>();
			for (Path path : paths) {
				String iName = UF.fn(path);
				Path statePathProps = getItemPropsPaths(spaceType, sdn, iName);
				if (!UFS.existFile(statePathProps)) {
					continue;
				}
				GsonMap gsonMap = GsonMap.of(statePathProps, false);
				Tuple secPropsItem = gsonMap.getTuple(SecApp.USER, SecApp.SECV, SecApp.SECE, SecApp.SECR);
				secPpops.put(iName, SecProps.of(secPropsItem));
			}
			if (L.isInfoEnabled()) {
				L.info("CacheProps [FRESH] [{}] for {} ", spaceType, sdn);
			}
			return secPpops;
		}
	}

	@RequiredArgsConstructor
	private static class BoolCacheLoader implements CacheLoader<Sdnu, Map<String, SecBool>> {
		final AFC.SpaceType spaceType;

		@Override
		public @Nullable Map<String, SecBool> load(@NonNull Sdnu sdnu) throws Exception {

			LoadingCache<Sdn, Map<String, SecProps>> cachePropsImpl = getCacheProps(spaceType);

			Map<String, SecProps> sdnItemsSecProps = cachePropsImpl.get(sdnu.sdn);

			Map<String, SecBool> stringSecBoolMap = SecBool.loadBoolMap(sdnu, spaceType, sdnItemsSecProps);
			if (L.isDebugEnabled()) {
				L.debug("CacheBool [FRESH] [{}] for '{}', items:\n{}", spaceType, sdnu, Rt.buildReport(stringSecBoolMap));
			} else if (L.isInfoEnabled()) {
				L.info("CacheBool [FRESH] [{}] for '{}' ", spaceType, sdnu);

			}
			return stringSecBoolMap;
		}
	}


}


