package zk_old_core.old.per_win;

import mpe.state_rw.PropsStateRw;

import java.nio.file.Path;

public class PerWinStateRw extends PropsStateRw implements IPerWinStateRw {
	public PerWinStateRw(Path path) {
		super(path);
	}

}
