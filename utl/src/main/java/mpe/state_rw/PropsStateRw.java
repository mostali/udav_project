package mpe.state_rw;

import lombok.SneakyThrows;
import mpu.core.ARG;
import mpc.fs.path.PathEntity;
import mpu.core.RW;
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
		RW.writeRuProps_(fPath(), state);
	}

	transient RuProps ruProps = null;

	@SneakyThrows
	@Override
	public RuProps read(boolean... fresh) {
		if (ruProps == null || ARG.isDefEqTrue(fresh)) {
			return ruProps = RW.readRuProps(fPath());
		}
		return ruProps;
	}
}
