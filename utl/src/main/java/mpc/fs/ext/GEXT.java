package mpc.fs.ext;

import mpu.core.ARG;
import mpu.core.ARR;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum GEXT {
	UNDEFINED, RUNNABLE, EDITABLE, MUSIC, IMG, GIF, VIDEO, XML, HTML, DOC, XLS, DB, ARC, CSV;

	public static final List<String> EXT_IMGS = Arrays.asList("jpg", "png", "bmp", "jpeg", "webp");
	public static final List<String> EXT_VIDEOS = Arrays.asList("avi", "mp4", "3gp", "mpeg", "mov", "flv", "wmv");
	public static final List<String> EXT_MUSIC = Arrays.asList("mp3");
	public static final EXT[] SET_VIDEO = ARR.of(EXT.MP4, EXT._3GP);
	public static final GEXT[] MEDIA_GEXT = {GEXT.MUSIC, GEXT.VIDEO, GEXT.IMG, GEXT.GIF};

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

	public static boolean has(Path path) {
		return EXT.of(path).hasIn(GEXT.IMG);
	}

	public static boolean isMediaFile(Path file) {
		GEXT gext = GEXT.of(file, null);
		return gext == null ? false : ARR.contains(GEXT.MEDIA_GEXT, gext);
	}

	public boolean notHasIn(Path path) {
		return !hasIn(path);
	}

	public boolean hasIn(Path path) {
		return has(EXT.of(path));
	}

	public boolean has(EXT ext) {
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
			case XML:
			case HTML:
				switch (ext) {
					case HTML:
					case XML:
						return true;
					default:
						return false;
				}
			case DOC:
				switch (ext) {
					case DOC:
					case DOCX:
					case ODT:
					case PDF:
						return true;
					default:
						return false;
				}
			case XLS:
				switch (ext) {
					case XLS:
					case XLSX:
					case ODS:
						return true;
					default:
						return false;
				}
			case DB:
				switch (ext) {
					case SQLITE:
						return true;
					default:
						return false;
				}
			case UNDEFINED:
				return false;
			default:
				throw new WhatIsTypeException(this + " has ext '" + ext + "'?");
		}
	}

	public static GEXT of(String path, GEXT... defRq) {
		return of(EXT.of(path), defRq);
	}

	public static GEXT of(Path file, GEXT... defRq) {
		return of(EXT.of(file), defRq);
	}

	public static GEXT of(EXT ext, GEXT... defRq) {
		if (ext != null) {
			for (GEXT gExt : GEXT.values()) {
				if (gExt.has(ext)) {
					return gExt;
				}
			}
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("GExt not found from EXT '%s'", ext), defRq);
	}

	public List<Path> ls_filter(List<Path> ls) {
		return ls.stream().filter(this::hasIn).collect(Collectors.toList());
	}


	public boolean isInteractive() {
		switch (this) {
			case IMG:
			case GIF:
			case VIDEO:
			case MUSIC:
				return true;
			default:
				return false;
		}

//		switch (this) {
//			case ARC:
//			case DOC:
//			case GIF:
//			case IMG:
//			case XLS:
//			case MUSIC:
//			case VIDEO:
//			case DB:
//				return true;
//			default:
//				return false;
//		}
	}
}
