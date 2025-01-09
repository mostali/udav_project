package mpc.fs.ext;

import mpc.fs.LS_SORT;
import mpc.fs.UDIR;
import mpc.fs.fd.EFT;
import mpu.IT;
import mpu.core.ARR;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class MapExt {

	private final Map<Path, EXT> mapExt;

	@Override
	public String toString() {
		return "MapExt:" + map().size() + ">>>" + mapExt;
	}

	public Map<Path, EXT> map() {
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

	public static MapExt of(Path path) {
		return of(path, null, null, null);
	}

	public static MapExt ofNatural(Path path) {
		return of(path, null, LS_SORT.NATURAL, null);
	}

	public static MapExt of(Path path, EFT fileType, LS_SORT lsSort, Predicate<Path> filter) {
		return new MapExt(getMapExt(path, fileType, lsSort, filter));
	}

	public static Map<Path, EXT> getMapExt(Path path, EFT fileType, LS_SORT lsSort) {
		return getMapExt(path, fileType, lsSort, null);
	}

	public static Map<Path, EXT> getMapExt(Path dir, EFT fileType, LS_SORT lsSort, Predicate<Path> filter) {
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
		return map().isEmpty();
	}

	public Map<GEXT, List<Path>> gmap() {
		Map<GEXT, List<Path>> gmap = new LinkedHashMap<>();
		Map<Path, EXT> mapExt = map();
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
}
