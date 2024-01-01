package mpf.ns.space;

import lombok.SneakyThrows;
import mpc.args.ARG;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS_BASE;
import mpc.fs.Ns;
import mpf.ns.space.core.SpaceFd;
import mpf.ns.space.core.SpaceHomeMap;
import mpc.str.sym.SYMJ;

import java.nio.file.Files;
import java.nio.file.Path;

//SrcType
public enum ST {
	GX, SPACE, TOPIC, SRC;

	public static final String PROPS_SFX = "..";
	public static final String GS_SFX = "._";

	public String getIconEmoj(boolean... isTrue) {
		switch (this) {
			case GX:
				return SYMJ.GALAXY;
			case SPACE:
				return SYMJ.MAP_ISLAND;
			case TOPIC:
				return ARG.isDefEqTrue(isTrue) ? SYMJ.SQUARE_DBL : SYMJ.SQUARE;
			case SRC:
				return ARG.isDefEqTrue(isTrue) ? SYMJ.ROUND_DBL : SYMJ.POINT_EMPTY;// SYMJ.POINT_MINI2
			default:
				throw new WhatIsTypeException(this);
		}
	}

//	public static ST of(Path pathSs, ST... defRq) {
//		if (Files.isRegularFile(pathSs)) {
//			return SRC;
//		} else if (!Files.isDirectory(pathSs)) {
//			return ARG.toDefThrow(() -> new RequiredRuntimeException("Sts not found, path '%s'", pathSs), defRq);
//		}
//		return Env.SPACE.equals(pathSs) ? SPACE : TOPIC;
//	}

	public Path homeChildPath(Path pathSs, String child) {
		return homeMapPathBlank(pathSs).getParent().resolve(child);
	}

	public SpaceHomeMap homeMapOrCreate(Path pathSs, SpaceHomeMap... defRq) {
		Path pathHomeMap = homeMapPathOrCreate(pathSs, null);
		if (pathHomeMap == null) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Home Map '%s' not create", pathSs), defRq);
		}
		return SpaceHomeMap.ofFileMap(this, pathHomeMap, defRq);
	}

	public SpaceHomeMap homeMap(Path pathSs, SpaceHomeMap... defRq) {
		Path pathHomeMap = homeMapPathBlank(pathSs);
		if (Files.isRegularFile(pathHomeMap)) {
			return SpaceHomeMap.ofFileMap(this, pathHomeMap, defRq);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("HomeMap '%s' not exist", pathSs), defRq);
	}

	@SneakyThrows
	public Path homeMapPathOrCreate(Path pathSs, Path... defRq) {
		Path homeMapPathBlank = homeMapPathBlank(pathSs);
		if (Files.isRegularFile(homeMapPathBlank)) {
			return homeMapPathBlank;
		}
		if (Files.exists(homeMapPathBlank)) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Except FILE (but it not file) '%s'", homeMapPathBlank), defRq);
		}
		boolean isHmFile = pathSs.getFileName().toString().endsWith(PROPS_SFX);
		if (isHmFile) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Except FILE (but it HM file with suffix '..') '%s'", homeMapPathBlank), defRq);
		}
		UFS_BASE.MKDIR.mkdirsIfNotExistForParent(homeMapPathBlank);
		UFS_BASE.MKFILE.createFileIfNotExistWithEmptyJson_(homeMapPathBlank);
		return homeMapPathBlank;

	}

	// .gx
	// any.space
	// .space/topic
	// .space/topic/src.sh
	public Path home(Path pathSs) {
		switch (this) {
			case TOPIC:
			case SRC:
				return pathSs.getParent();
			case SPACE:
			case GX:
				return pathSs;
			default:
				throw new WhatIsTypeException(this);
		}
	}

	public Path homeMapPathBlank(Path pathSs) {
		Path home = home(pathSs);
		String fn = pathSs.getFileName().toString();
		switch (this) {
			case SRC:
				return home.resolve(PROPS_SFX + fn);// .space/topic/abc.sh >> .space/topic/abc.sh..
			case TOPIC:
				return home.resolve(PROPS_SFX + fn);// .space/abc >> .space/abc..
			case SPACE:
				return home.resolve(GS_SFX);// .space >> .space/._
			case GX:
				return home.resolve(GS_SFX);// .gx >> .gx/._
			default:
				throw new WhatIsTypeException(this);
		}
	}

	public <T extends SpaceFd> T buildType(Path pathSs) {
		Ns namespace = Ns.ofSafeChild(pathSs);
		switch (this) {
			case SPACE:
				return (T) new Space(namespace);
			case TOPIC:
				return (T) new Topic(namespace);
			case SRC:
				return (T) new Src(namespace);
			case GX:
				return (T) new Gx(namespace);
			default:
				throw new WhatIsTypeException(this);
		}
	}

	public void mkdirs(Path pathSs) {
		UFS_BASE.MKDIR.mkdirsIfNotExist(pathSs);
	}
}

