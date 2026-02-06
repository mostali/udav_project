package zk_notes.apiv1._ati;

import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpc.exception.NI;
import mpc.exception.WhatIsTypeException;
import mpu.X;
import mpu.pare.Pare;

public class ClaimOperation extends TreeOper {


	public ClaimOperation(TreeRestCall treeRestCall, int level) {
		super(treeRestCall, level);
	}

	@Override
	public Pare<Integer, String> apply() {
		if (getClass() != ClaimOperation.class) {
			return Pare.of(CODE_ROOT, "root claim^" + level);
		}
		switch (level) {
			case 0: //sd
//				return new StarOperation.SdStarOperation(treeRestCall, level).apply();
			case 1: //page
//				return new StarOperation.PageStarOperation(treeRestCall, level).apply();
			case 2: //item
//				return new StarOperation.ItemStarOperation(treeRestCall, level).apply();
				return new ClaimOperation0(treeRestCall, level).apply();
		}
		throw new WhatIsTypeException(level);
	}

	@Override
	public ICtxDb tree(ICtxDb... defRq) {
		return null;
	}

	@Override
	public Pare<Integer, String> applyRoot() {
		NI.stop();
		return null;
	}


	class ClaimOperation0 extends StarOperation {

		public ClaimOperation0(TreeRestCall treeRestCall, int level) {
			super(treeRestCall, level);
		}

		@Override
		public Pare<Integer, String> apply() {
			String key = getKeyItem(null);
			if (X.notEmpty(key)) {
				ICtxDb tree = treeOrCreate();
				ICtxDb.CtxModel put = tree.put(key, getValItem());
				return Pare.of(CODE_SUCCESS_PUT, put.getValue());
			}
			return super.apply();//default
		}
	}

}
