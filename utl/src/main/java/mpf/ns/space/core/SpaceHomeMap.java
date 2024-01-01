package mpf.ns.space.core;

import mpc.args.ARG;
import mpc.ERR;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.RW;
import mpc.json.GsonMap;
import mpc.json.PerGsonMap;
import mpc.json.UGson;
import mpc.map.UMap;
import mpf.ns.space.ST;
import mpe.str.CN;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class SpaceHomeMap extends PerGsonMap implements ISs {

	//	public static final SpaceHomeMap EMPTY = new SpaceHomeMap(LINKED_HASH_MAP(), null);//throw ERROR NoClssDefFounde -- need refactoring

	public static LinkedHashMap LINKED_HASH_MAP() {
		return new LinkedHashMap();
	}

	@Override
	public String toString() {
		return "SpaceHomeMap{ ns=" + path() + "}";
	}

	public static SpaceHomeMap ofFileMap(ST ST, Path pathHomeMap, SpaceHomeMap... defRq) {
		Exception err = null;
		if (Files.isRegularFile(pathHomeMap)) {
			try {
				return new SpaceHomeMap(pathHomeMap, RW.readContent(pathHomeMap)) {
					@Override
					public ST srcType(ST... defRq) {
						return ST;
					}
				};
			} catch (Exception ex) {
				err = ex;
			}
		} else if (Files.notExists(pathHomeMap)) {
			return new SpaceHomeMap(pathHomeMap, LINKED_HASH_MAP()) {
				@Override
				public ST srcType(ST... defRq) {
					return ST;
				}
			};
		}
		Exception finalErr = err;
		return ARG.toDefThrow(() -> finalErr == null ? new RequiredRuntimeException("Path '%s' is not file", pathHomeMap) : new RequiredRuntimeException(finalErr, "Path '%s' is not correct file", pathHomeMap), defRq);
	}

	public SpaceHomeMap(Path pathJson) {
		super(Files.isRegularFile(pathJson) ? UGson.toMapFromString(pathJson) : LINKED_HASH_MAP(), pathJson);
	}

	public SpaceHomeMap(Path pathJson, Map gson) {
		super(gson, ERR.isFileOrNotExist(pathJson));
	}


	public SpaceHomeMap(Path pathJson, String json) {
		super(Files.isRegularFile(pathJson) ? UGson.toMapFromString(json) : LINKED_HASH_MAP(), pathJson);
	}

	public static void writeProfileHomeMap(Path rsPath, ST srcType, String profile, GsonMap profileMap) {
		SpaceHomeMap spaceHomeMap = (SpaceHomeMap) srcType.homeMapOrCreate(rsPath).writable();
		spaceHomeMap.put(profile, profileMap);
	}

	public Integer index(Integer... defRq) {
		return UMap.getAsInt(map(), CN.INDEX, defRq);
	}

	public SpaceHomeMap index_write(Integer newIndex) {
		writable().put(CN.INDEX, newIndex);
		return this;
	}

	@Deprecated
	public SpaceHomeMap index_write_steps_safety(int steps) {
		if (steps == 0) {
			return this;
		}
		Integer index = index(0);
		if (index == 0) {
			return this;
		}
		int newIndex = index + steps;
		if (newIndex < 0) {
			newIndex = 0;
		}
		return index_write(newIndex);
	}

	@Nullable
	@Override
	public Object put(Object key, Object value) {
		return null;
	}

	@Override
	public ST srcType(ST... defRq) {
		return ARG.toDefThrow(() -> new RequiredRuntimeException("override srcType"), defRq);
	}

}
