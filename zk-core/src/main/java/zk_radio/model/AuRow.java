package zk_radio.model;

import lombok.RequiredArgsConstructor;
import mp.utl_odb.tree.ctxdb.Ctx3Db;
import mpc.exception.WhatIsTypeException;
import mpc.json.GsonMap;
import mpe.str.CN;
import mpu.str.STR;
import zk_os.db.net.WebUsr;
import zk_radio.ZkAudio;

@RequiredArgsConstructor
public class AuRow {

	public final Ctx3Db.CtxModelCtr rowDb;

	protected static String toSimpleKey() {
		return WebUsr.login();
	}

	public void __setAndWriteExt(String key, String val, String cKey) {
		GsonMap extAs = rowDb.getExtAs(GsonMap.class, GsonMap.EMPTYMAP);
		extAs.put(key, val);
		String stringPrettyJson = extAs.toStringPrettyJson();
		switch (cKey) {
			case CN.EXT:
				rowDb.setExt(stringPrettyJson);
				break;
			default:
				throw new WhatIsTypeException(cKey);

		}
		ZkAudio.getDb().saveModelAsUpdate(rowDb);
	}

	public boolean __addValueLine(String lineWraped, boolean uniq) {
		String value = rowDb.getValue();
		if (value == null) {
			rowDb.setValue(lineWraped);
		} else {
			if (uniq && value.contains(lineWraped)) {
				return false;
			} else {
				rowDb.setValue(value + STR.NL + lineWraped);
			}
		}
		ZkAudio.getDb().saveModelAsUpdate(rowDb);

		return true;
	}


}
