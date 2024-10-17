package mpe.state_rw;

import lombok.SneakyThrows;
import mpu.core.ARG;
import mpc.env.Env;
import mpc.fs.path.PathEntity;
import mpu.core.RW;
import mpc.types.ruprops.RuProps;
import mpc.json.GsonMap;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class MapStateRw extends PathEntity implements IMapStateRw {
	public static IMapStateRw loadStateRw(Path fileState, boolean json, boolean createIfNotExist) {
		return new MapStateRw(fileState, json, createIfNotExist);
	}

	public MapStateRw(Path path) {
		this(path, false, false);
	}

	public final boolean json, createIfNotExist;


	public MapStateRw(Path filePath, boolean json, boolean createIfNotExist) {
		super(filePath);
		this.json = json;
		this.createIfNotExist = createIfNotExist;
	}

	public static IMapStateRw tmp(String rs_name, boolean json) {
		Path fileState = Paths.get(Env.PD_RS + rs_name + "." + (json ? "json" : "props"));
		return MapStateRw.loadStateRw(fileState, true, true);
	}

	@Override
	public String toString() {
		if (props == null) {
			return RW.readContent(fPath());
		}
		if (json) {
			return GsonMap.of(props).toString();
		} else {
			return RuProps.toStringFromMap(props).toString();
		}
	}

	@SneakyThrows
	@Override
	public void write(Map state) {
		if (json) {
			RW.writeGsonMap(fPath(), GsonMap.of(state), createIfNotExist);
		} else {
			RW.writeRuProps(fPath(), state, true);
		}
		props = null;
	}

	transient Map props = null;

	@SneakyThrows
	@Override
	public Map read(boolean... fresh) {
		if (props == null || ARG.isDefEqTrue(fresh)) {
			if (json) {
				props = RW.readGsonMap(fPath(), createIfNotExist);
			} else {
				props = RW.readRuProps(fPath()).readMap();
			}
		}
		return props;
	}
}
