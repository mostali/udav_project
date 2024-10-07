package zk_old_core.old.per_win;

import mpc.types.ruprops.RuProps;
import mpe.state_rw.IStateRw;

import java.nio.file.Path;

public interface IPerWinStateRw extends IStateRw<RuProps> {
	static IPerWinStateRw loadStateRw(Path fileState) {
		return new PerWinStateRw(fileState);
	}

	default void touchIfNotExist() {
		RuProps file = read();
		if (!file.existsFile()) {
			file.mkfileIfNotExist(true);
		}
	}

	enum LTWH {
		LEFT, TOP, WIDTH, HEIGHT
	}

	enum STATE {
		state, bgcolor, edit_form, edit_form_com
	}

	enum STATE_VALUE {
		def, min,
	}

	default String read(String key, String... defRq) {
		return read().getString(key, defRq);
	}

	default String read(Enum entity, String... defRq) {
		return read().getString(entity.name().toLowerCase(), defRq);
	}

	default void write(Enum entity, String value) {
		write(entity.name().toLowerCase(), value);
	}

	default void write(String key, String value) {
		RuProps props = read();
		props.setString(key, value);
		write(props);
	}
}
