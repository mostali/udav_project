package mpe.state_rw;

import mpc.map.UMap;

import java.nio.file.Path;
import java.util.Map;

public interface IMapStateRw extends IStateRw<Map> {


	default String read(Enum entity, String... defRq) {
		return read(entity.name(), defRq);
	}

	default String read(String key, String... defRq) {
		return UMap.get(read(), key, defRq);
	}

	default void write(Enum entity, String value) {
		write(entity.name().toLowerCase(), value);
	}

	default void write(String key, String value) {
		Map props = read();
		props.put(key, value);
		write(props);
	}

	default <T> T readAs(String key, Class<T> asType, T... defRq) {
		return (T) UMap.getAs(read(), key, asType, defRq);
	}
}
