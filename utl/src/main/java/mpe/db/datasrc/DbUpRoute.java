package mpe.db.datasrc;

import mpu.str.SPLIT;

public enum DbUpRoute {
	PG_SL, PG_GS, //
	SL_PG, SL_GS, //
	GS_PG, GS_SL, //
	; //

//	public String nameHu() {
//		return name().replace("_", "->");
//	}

	final DbType src, dst;

	DbUpRoute() {
		String[] srcDst = SPLIT.argsBy(name(), "_");
		this.src = DbType.valueOf(srcDst[0]);
		this.dst = DbType.valueOf(srcDst[1]);
	}

}
