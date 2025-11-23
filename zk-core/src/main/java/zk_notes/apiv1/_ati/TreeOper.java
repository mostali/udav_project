package zk_notes.apiv1._ati;

import lombok.RequiredArgsConstructor;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpc.exception.WhatIsTypeException;
import mpc.fs.ext.EXT;
import mpu.pare.Pare;
import org.jetbrains.annotations.NotNull;
import mpe.call_msg.core.NodeID;
import zk_os.coms.AFC;

import java.nio.file.Path;

@RequiredArgsConstructor
public abstract class TreeOper {

	public static final int CODE_EMPTY_TREE = 400;

	public static final int CODE_TREE_NOT_FOUND = 404;

	public static final int CODE_SUCCESS_PUT = 200;
	public static final int CODE_SUCCES_GET = 200;

	public static final int CODE_ROOT = 400;
	public static final int CODE_WRONG_LOGIC = 400;
	public static final int CODE_NI = 400;

	final TreeRestCall treeRestCall;

	final int level;

	public abstract Pare<Integer, String> apply();

	public abstract ICtxDb tree(ICtxDb... defRq);

	public abstract Pare<Integer, String> applyRoot();

	public Path pathKey(int level, Path... defRq) {
		return path(level + 1, defRq);
	}

	public Path path(int level, Path... defRq) {
		return treeRestCall.curPPI.path(1 + level, defRq);
	}

	public String getSd3() {
		return treeRestCall.getSd3();
	}

	public @NotNull Path getAtiTreePath() {
		String sd3 = getSd3();
		switch (level) {
			case 0: //sd
				return AFC.EVENTS.getStatePath(sd3, NodeID.PAGE_INDEX_ALIAS, NodeID.ITEM_INDEX_ALIAS, EXT.SQLITE);
			case 1: //page
			{
				String page = path(0).toString();
				return AFC.EVENTS.getStatePath(sd3, page, NodeID.ITEM_INDEX_ALIAS, EXT.SQLITE);
			}
			case 2: //item
			{
				String page = path(0).toString();
				String item = path(1).toString();
				return AFC.EVENTS.getStatePath(sd3, page, item, EXT.SQLITE);
			}
		}
		throw new WhatIsTypeException(level);
	}


}
