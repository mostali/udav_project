package mpe.cmsg.core;

import mpc.str.sym.SYMJ;
import mpc.ui.ColorTheme;
import mpu.core.ENUM;

import java.util.function.Supplier;


public interface INodeTypeProps {
	//regstd

	String stdType();

	default StdType stdTypeSys(StdType... defRq) {
		return ENUM.valueOf(stdType(), StdType.class, defRq);
	}

	static INodeTypeProps of(String stdType) {
		return () -> stdType;
	}

	default String loadPropsOrDefault(String propKey, Supplier<String> defOrThrow) {
		return NodeDescCache.loadPropsValueOrDefault(stdType(), propKey, defOrThrow);
	}

	default String actionTitle() {
		return loadPropsOrDefault("actionTitle", null);
	}

	default String titleWithIcon() {
		return icon() + " " + actionTitle();
	}

	default String iconLight() {
		StdType stdType = stdTypeSys(null);
		if (stdType == null) {
			return icon();
		}
		switch (stdType) {
			case NODE:
				return SYMJ.FILE3_WL;
			case JARTASK:
				return SYMJ.JAVA_JAR_LIGHT;
			case HTTP:
//				return SYMJ.THINK2;
//				return SYMJ.STAR_SIMPLE2;
				return SYMJ.STAR_COM;
			case SENDMSG:
				return SYMJ.EMAIL2;
			case QZEVAL:

			default:
				return icon();
		}
	}

	default String icon() {
//		return loadPropsOrDefault("icon", () -> "x");
		return loadPropsOrDefault("icon", () -> null);
	}

//	public static String iconSys(StdType stdType) {
//		if (stdType == null) {
//			return null;
//		}
//		switch (stdType) {
//			case HTTP:
//				return SYMJ.JET;
//			case SQL:
//				return SYMJ.TARGET;
//			case JARTASK:
//				return SYMJ.JAVA_JAR;
//			case PYTHON:
//			case MVEL:
//			case GROOVY:
//			case SHTASK:
//				return SYMJ.TRACK_PLAY;
//			case KAFKA:
//				return SYMJ.ROCKET;
//			case QZEVAL:
//				return SYMJ.TIME_R_CLOCK;
//			case IIPROMPT:
//				return SYMJ.EYE;
//			case NODE:
//				return SYMJ.FILE2;
//				return SYMJ.FILE_HTML;
//				return SYMJ.THINK_BLACK;
//				return SYMJ.MONEY_STATS;
//			case SENDMSG:
//				return SYMJ.EMAIL;

//			case PUBL:
//				return SYMJ.PUZZLE;
//			case JQL:
//				return SYMJ.TOOLS;

//			default:
//				return null;
//		}
//
//	}

	default String[] toColor() {
//		return loadPropsOrDefault("toColor", () -> toColorSys(stdTypeSys()));
		return toColorSys(stdTypeSys(null));
	}

	public static String[] toColorSys(StdType stdType) {
		if (stdType == null) {
			return ColorTheme.WHITE;
		}

		switch (stdType) {
			case IIPROMPT:
				return ColorTheme.LBLUE;
			case SENDMSG:
			case KAFKA:
			case HTTP:
				return ColorTheme.GREEN;

			case QZEVAL:
				return ColorTheme.BLUE;
			case PYTHON:
			case MVEL:
			case GROOVY:
				return ColorTheme.YELLOW;
			case SHTASK:
				return ColorTheme.BLUE;
			case JARTASK:
				return ColorTheme.YELLOW;
			case NODE:
				return ColorTheme.GRAY;
			case SQL:
				return ColorTheme.BLACK;

//			case PUBL:
//				return ColorTheme.ORANGE;
//			case JQL:
//				return SYMJ.TOOLS;

			default:
				return ColorTheme.WHITE;


		}
	}

	default String shortName() {
		return loadPropsOrDefault("shortName", () -> shortNameSys(stdTypeSys(null)));
	}

	public static String shortNameSys(StdType stdType) {
		if (stdType == null) {
			return stdType.stdTypeUC().toLowerCase();
		}
		switch (stdType) {
			case SENDMSG:
				return "smsg";
			case QZEVAL:
				return "qz";
			case JARTASK:
				return "jar";
			case NODE:
				return "note";
			default:
				return stdType.stdTypeUC().toLowerCase();

		}
	}

	default String shortNameRu() {
		return loadPropsOrDefault("shortNameRu", null);
	}

}
