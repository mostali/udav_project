package mpf.ns.space.core;


import mpu.core.ARG;
import mpc.rfl.IRfl;
import mpu.IT;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.path.IPath;
import mpf.ns.space.Space;
import mpf.ns.space.ST;

import java.nio.file.Path;

//SpaceSrc
public interface ISs extends IPath, IRfl {
	ST srcType(ST... defRq);

	default SpaceHomeMap props(SpaceHomeMap... defRq) {
		ST st = srcType(null);
		return st != null ? st.homeMap(fPath(), defRq) : ARG.toDefThrow(() -> new RequiredRuntimeException("Home Map '%s' not found from path '%s'", st, fPath()), defRq);
	}

	default Space spaceType() {
		return Space.of(this);
	}

	default SpaceFd spaceFd(SpaceFd... defRq) {
		ST ST = srcType(null);
		if (ST != null) {
			IT.state(this instanceof IPath);
			Path path = ((IPath) this).fPath();
			return ST.buildType(path);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Src type not found from '%s'", fPath()), defRq);
	}

	default Path home(Path... defRq) {
		ST ST = srcType(null);
		if (ST != null && this instanceof IPath) {
			Path path = ((IPath) this).fPath();
			return ST.home(path);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Home for '%s' not found, from %s", ST, getClass().getSimpleName()), defRq);
	}

	default boolean isHM() {
		return fName().endsWith(ST.PROPS_SFX);
	}
}
