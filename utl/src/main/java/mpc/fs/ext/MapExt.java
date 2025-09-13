package mpc.fs.ext;

import mpc.exception.RequiredRuntimeException;
import mpc.fs.LS_SORT;
import mpc.fs.UDIR;
import mpc.fs.UFS;
import mpc.fs.fd.EFT;
import mpu.IT;
import mpu.core.ARG;
import mpu.core.ARR;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class MapExt {

	private final Map<Path, EXT> mapExt;

	@Override
	public String toString() {
		return "MapExt:" + getMap().size() + ">>>" + mapExt;
	}

	public Map<Path, EXT> getMap() {
		return mapExt;
	}

	public MapExt() {
		this(new HashMap<>());
	}

	public MapExt(Map<Path, EXT> mapExt) {
		this.mapExt = mapExt;
	}

	public static MapExt of(Map<Path, EXT> mapExt) {
		return new MapExt(mapExt);
	}

	public static MapExt of(Path path, Map<Path, EXT>... defRq) {
		return of(path, null, null, null, defRq);
	}

	public static MapExt ofNatural(Path path) {
		return of(path, null, LS_SORT.NATURAL, null);
	}

	public static MapExt of(Path path, EFT fileType, LS_SORT lsSort, Predicate<Path> filter, Map<Path, EXT>... defRq) {
		return new MapExt(getMapExt(path, fileType, lsSort, filter, defRq));
	}

	public static Map<Path, EXT> getMapExt(Path path, EFT fileType, LS_SORT lsSort, Map<Path, EXT>... defRq) {
		return getMapExt(path, fileType, lsSort, null, defRq);
	}

	public static Map<Path, EXT> getMapExt(Path dir, EFT fileType, LS_SORT lsSort, Predicate<Path> filter, Map<Path, EXT>... defRq) {
		if (UFS.isDirWoContent(dir, false)) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Dir [%s] not exist", dir), defRq);
		}
		List<Path> files = UDIR.ls(IT.isDirExist(dir), fileType, lsSort, filter);
		LinkedHashMap mapExt = new LinkedHashMap();
		for (Path path : files) {
			if ((fileType != null && EFT.DIR == fileType) || Files.isDirectory(path)) {
				mapExt.put(path, null);
				continue;
			}
			EXT extType = EXT.of(path, null);
			mapExt.put(path, extType);
		}
		return (Map<Path, EXT>) mapExt;
	}

//	public static Map<Path, String> fmap(Path rootPath, EFT fileType, LS_SORT lsSort, Predicate<Path> filter) {
//		List<Path> files = UDIR.ls(rootPath, fileType, lsSort);
//		LinkedHashMap lhm = new LinkedHashMap();
//		for (Path path : files) {
//			if ((fileType != null && EFT.DIR == fileType) || Files.isDirectory(path)) {
//				lhm.put(path, null);
//				continue;
//			}
//			EXT extType = EXT.of(path);
//			lhm.put(path, extType == null ? "" : extType.name());
//		}
//		return lhm;
//	}

	public boolean isEmpty() {
		return getMap().isEmpty();
	}

	public Map<GEXT, List<Path>> getGMap() {
		Map<GEXT, List<Path>> gmap = new LinkedHashMap<>();
		Map<Path, EXT> mapExt = getMap();
		for (Map.Entry<Path, EXT> entry : mapExt.entrySet()) {
			GEXT gExt = GEXT.of(entry.getValue(), null);
			if (gExt == null) {
				continue;
			}
			BiFunction<? super GEXT, ? super List<Path>, ? extends List<Path>> f = (gext, vls) -> {
				if (vls == null) {
					return ARR.asAL(entry.getKey());
				}
				vls.add(entry.getKey());
				return vls;
			};
			gmap.compute(gExt, f);
		}
		return gmap;
	}

	public boolean hasOnly(EXT... ext) {
		Collection<EXT> values = new HashSet<>(getMap().values());
		values.removeAll(ARR.as(ext));
		return values.isEmpty();
	}
}
