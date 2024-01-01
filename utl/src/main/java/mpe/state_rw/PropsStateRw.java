package mpe.state_rw;

import lombok.SneakyThrows;
import mpc.args.ARG;
import mpc.fs.path.PathEntity;
import mpc.fs.RW;
import mpc.types.ruprops.RuProps;

import java.nio.file.Path;

@Deprecated
public class PropsStateRw extends PathEntity implements IStateRw<RuProps> {
	public PropsStateRw(Path path) {
		this(path, false);
	}

	private PropsStateRw(Path path, boolean json) {
		super(path);
	}

	@SneakyThrows
	@Override
	public void write(RuProps state) {
		RW.writeRuProps_(path(), state);
	}

	transient RuProps ruProps = null;

	@SneakyThrows
	@Override
	public RuProps read(boolean... fresh) {
		if (ruProps == null || ARG.isDefEqTrue(fresh)) {
			return ruProps = RW.readRuProps(path());
		}
		return ruProps;
	}
}
