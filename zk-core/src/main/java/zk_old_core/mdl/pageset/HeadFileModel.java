package zk_old_core.mdl.pageset;

import mpc.fs.ext.EXT;
import mpc.fs.ext.MapExt;
import mpc.fs.path.UPath;
import zk_old_core.mdl.FdModel;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HeadFileModel extends FdModel {

	public HeadFileModel(Path file) {
		super(file);
	}

	public static List<HeadFileModel> extractDir(Path dir) {
		return extractDir(MapExt.ofNatural(dir));
	}

	public static List<HeadFileModel> extractDirs(List<MapExt> dirs) {
		List<HeadFileModel> heads = new ArrayList<>();
		for (MapExt dir : dirs) {
			if (dir.isEmpty()) {
				continue;
			}
			List<HeadFileModel> all = HeadFileModel.extractDir(dir);
			heads.addAll(all);
		}
		return heads;
	}

	public static List<HeadFileModel> extractDir(MapExt dir) {
		List<HeadFileModel> heads = new ArrayList<>();
		if (dir.isEmpty()) {
			return heads;
		}
		for (Map.Entry<Path, EXT> pathExt : dir.map().entrySet()) {
			Path path = pathExt.getKey();
			if (UPath.startsWith(path, SKIP_PFX)) {
				continue;
			}
			if (pathExt.getValue() == null) {
				List<HeadFileModel> headsChilds = extractDir(path);
				heads.addAll(headsChilds);
				continue;
			}
			HeadFileType headFileType = HeadFileType.of(pathExt.getValue(), null);
			if (headFileType == null) {
				if (L.isWarnEnabled()) {
					L.warn("SKIP. HeadType is null, from {}", path);
				}
				continue;
			}
			heads.add(new HeadFileModel(path) {
				@Override
				public HeadFileType getHeadType() {
					return headFileType;
				}
			});
		}
		return heads;
	}

	@Override
	public Path getFileRootProps() {
		throw new UnsupportedOperationException("HeadFileModel is file");
	}

	public static HeadFileModel of(String pathUsrDir) {
		return of(Paths.get(pathUsrDir));
	}

	public static HeadFileModel of(File file) {
		return of(file.toPath());
	}

	public static HeadFileModel of(Path file) {
		return new HeadFileModel(file);
	}

	private HeadFileType hft;

	public HeadFileType getHeadType() {
		return hft == null ? (hft = HeadFileType.of(path(), HeadFileType.UNDEFINED)) : hft;
	}

	;
}
