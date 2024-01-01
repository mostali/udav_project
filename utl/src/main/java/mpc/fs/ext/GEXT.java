package mpc.fs.ext;

import mpc.args.ARG;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum GEXT {
	UNDEFINED, RUNNABLE, EDITABLE, MUSIC, IMG, GIF, VIDEO, JSON, XML, HTML, DOC, XLS, PDF, SQLITE, ARC,CSV;

	public static final List<String> EXT_IMGS = Arrays.asList("jpg", "png", "bmp", "jpeg", "webp");
	public static final List<String> EXT_VIDEOS = Arrays.asList("avi", "mp4", "3gp", "mpeg", "mov", "flv", "wmv");
	public static final List<String> EXT_MUSIC = Arrays.asList("mp3");

	@Deprecated //old way
	public static GEXT getTypeFromUrl(String url) {
		String ext = UF.getExtFromDirtyUrl(url);
		if (ext == null) {
			return GEXT.UNDEFINED;
		}
		ext = ext.toLowerCase();
		if ("gif".equals(ext)) {
			return GIF;
		} else if (EXT_IMGS.contains(ext)) {
			return IMG;
		} else if (EXT_MUSIC.contains(ext)) {
			return MUSIC;
		} else if (EXT_VIDEOS.contains(ext)) {
			return VIDEO;
		}
		return UNDEFINED;
	}

	public boolean isNot(Path path) {
		return !is(path);
	}

	public boolean is(Path path) {
		return is(EXT.of(path));
	}

	public boolean is(EXT ext) {
		switch (this) {
			case CSV:
				switch (ext) {
					case CSV:
						return true;
					default:
						return false;
				}
			case IMG:
				switch (ext) {
					case JPG:
					case PNG:
					case BMP:
					case JPEG:
					case WEBP:
						return true;
					default:
						return false;
				}
			case GIF:
				return ext == EXT.GIF;
			case RUNNABLE:
				switch (ext) {
					case GROOVY:
					case JAVA:
					case JS:
					case SH:
					case PY:
						return true;
				}
			case EDITABLE:
				switch (ext) {
					case GROOVY:
					case JAVA:
					case JS:
					case SH:
					case PY:
						return true;
					case CSS:
						return true;
					case ZUL:
					case HTML:
						return true;
					case PROPS:
					case PROPERTIES:
					case XML:
					case JSON:
					case YAML:
						return true;
					case LOG:
					case TXT:
					case MD:
						return true;
					default:
						return false;
				}
			case VIDEO:
				switch (ext) {
					case MP4:
					case AVI:
					case _3GP:
					case MPEG:
					case MOV:
					case FLV:
					case WMV:
						return true;
					default:
						return false;
				}
			case ARC:
				switch (ext) {
					case ZIP:
					case RAR:
					case _7Z:
					case GZ:
						return true;
					default:
						return false;
				}
			case MUSIC:
				switch (ext) {
					case MP3:
						return true;
					default:
						return false;
				}
			default:
				throw new WhatIsTypeException(this);
		}
	}

	public static GEXT of(EXT ext, GEXT... defRq) {
		if (ext != null) {
			for (GEXT gExt : GEXT.values()) {
				if (gExt.is(ext)) {
					return gExt;
				}
			}
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("GExt not found from EXT '%s'", ext), defRq);
	}

	public List<Path> ls_filter(List<Path> ls) {
		return ls.stream().filter(this::is).collect(Collectors.toList());
	}
}
