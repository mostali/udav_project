package mpu.pare;

import lombok.RequiredArgsConstructor;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.EQ;

import java.util.Arrays;
import java.util.Map;

@RequiredArgsConstructor
public class Tuple<T> {

	public final T[] objects;

	public static Tuple valueOfObjs(Object... objs) {
		return new Tuple(objs);
	}

	public static Tuple valueOfMap(Map map, Object... keys) {
		return new Tuple(Arrays.stream(keys).map(k -> map.get(k)).toArray());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "*" + X.sizeOf(objects) + Arrays.toString(objects);
	}

	public T get(int i, T... defRq) {
		return ARR.isIndex(i, objects) ? objects[i] : ARG.toDefThrowMsg(() -> X.f("Except obj by index %s, but length is %s", i, objects.length), defRq);
	}

	public Boolean eq(int index, Object obj, Boolean... defRq) {
		if (ARG.isDef(defRq)) {
			return ARR.isIndex(index, objects) ? EQ.equalsUnsafe(get(index), obj) : ARG.toDef(defRq);
		}
		return EQ.equalsUnsafe(get(index), obj);
	}
}
