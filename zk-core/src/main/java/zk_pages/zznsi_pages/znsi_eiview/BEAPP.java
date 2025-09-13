package zk_pages.zznsi_pages.znsi_eiview;

import mpu.core.ARR;
import mpu.core.ENUM;
import mpu.func.Function2;
import mpu.str.STR;

import java.util.List;
import java.util.function.Function;

public enum BEAPP {
	MDM, NIFI, NIFIR;

	public static Function<Boolean, String> funcEiType = (isEi0) -> isEi0 ? "export" : "import";
	public static Function2<Boolean, Enum, String> funcEiEnumPfx = (isEi0, item) -> "--" + funcEiType.apply(isEi0) + STR.capitalizeLC(item.name());


	public enum BEMDM {
		ROLES, PIPES, ENUMS, JOBS, LIBS, DQ, MODEL;

		BEMDM() {
		}

		public static List<BEMDM> noRoleFilter() {
			return ENUM.getValuesWoExclude(BEMDM.ROLES);
		}

		public String toDblKey(boolean isEi) {
			return funcEiEnumPfx.apply(isEi, this);
		}

		public boolean hasDblKey(String[] args, boolean isEi) {
			return ARR.containsString(args, toDblKey(isEi), true);
		}
	}

	public enum BENIFI {
		NIFI,
		;//NIFIR;

		BENIFI() {
		}

		public boolean hasDblKey(String[] args, boolean isEi) {
			return ARR.containsString(args, toDblKey(isEi), true);
		}

		public String toDblKey(boolean isEi) {
			return funcEiEnumPfx.apply(isEi, this);
		}
	}


	//	public static class SO2Ei extends SO2 {
//		final Boolean isEi;
//
//		public SO2Ei(String key, Boolean val, Boolean isEi) {
//			super(key, val);
//			this.isEi = isEi;
//		}
//
//		public static String toDblKey(Boolean isEi, String name) {
//			return SO2.wrap(isEi ? "export" : "import" + STR.capitalizeLC(name));
//		}
//
//	}

}
