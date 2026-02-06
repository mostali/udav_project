package mpe;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalListener;
import mpc.exception.WhatIsTypeException;
import mpu.core.ARGn;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class UCaffeine {

//	public static final ConcurrentMap<Object, Object> _SOFT_BOTS_CACHE = CacheBuilder.newBuilder().softValues().<Object, Object>build().asMap();

//	public static <I, O> LoadingCache<I, O> buildCacheOLD(long[] config, CacheLoader<I, O> loader) {
//		return buildCacheOLD(config).build(loader);
//	}

	public static final Long STRONG = 0l;
	public static final Long SOFT = 1l;
	public static final Long WEAK = 2l;


	public static <I, O> LoadingCache<I, O> buildCache(CacheLoader<I, O> cacheLoader, Boolean soft_weak_not, int expireAfterWriteSec, int... maxSize) {
		Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder();
		if (soft_weak_not != null) {
			cacheBuilder = soft_weak_not ? cacheBuilder.softValues() : cacheBuilder.weakValues();
		}
		if (ARGn.isDef(maxSize)) {
			cacheBuilder = cacheBuilder.maximumSize(ARGn.toDef(maxSize));
		}
		if (expireAfterWriteSec > 0) {
			cacheBuilder = cacheBuilder.expireAfterWrite(expireAfterWriteSec, TimeUnit.SECONDS);
		}
		return cacheBuilder.build(cacheLoader);
	}

	public static <I, O> Caffeine<I, O> buildCacheOLD(long[] config) {
		Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder();
		switch ((int) config[2]) {
			case 0:
				//ok
				break;
			case 1:
				cacheBuilder = cacheBuilder.softValues();
				break;
			case 2:
				cacheBuilder = cacheBuilder.weakValues();
				break;
			default:
				throw new WhatIsTypeException(config[2]);
		}
		if (config[1] > 0) {
			cacheBuilder = cacheBuilder.maximumSize(config[1]);
		}
		return (Caffeine<I, O>) cacheBuilder.expireAfterWrite(config[0], TimeUnit.SECONDS);
	}

	public static <K, V> ConcurrentMap<K, V> asMapSoft() {
		return Caffeine.newBuilder().softValues().<K, V>build().asMap();
	}

	public static <K, V> ConcurrentMap<K, V> asMapSoft(int max) {
		return Caffeine.newBuilder().maximumSize(max).softValues().<K, V>build().asMap();
	}

	public static <K, V> ConcurrentMap<K, V> asMapSoft(int max, RemovalListener<K, V> removeListener) {
		return Caffeine.newBuilder().maximumSize(max).softValues().removalListener(removeListener).<K, V>build().asMap();
	}
}
