package mpu.pare;

import lombok.RequiredArgsConstructor;
import mpc.map.IGetterAsAny;
import mpc.str.ObjTo;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.EQ;
import mpu.str.SPLIT;
import mpu.str.STR;
import mpu.str.UST;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class Tuple<T> implements IGetterAsAny {

	public final T[] objects;

	public static Tuple ofObjs(Object... objs) {
		return new Tuple(objs);
	}

	public static Tuple ofCmd(String cmd, String... del_or_spaceDefault) {
		return new Tuple(SPLIT.argsBy(cmd, ARG.toDefOr(STR.SPACE, del_or_spaceDefault)));
	}

	public static Tuple ofMap(Map map, Object... keys) {
		return new Tuple(Arrays.stream(keys).map(k -> map.get(k)).toArray());
	}

	protected static Tuple ofList(List head) {
		return new Tuple(head.toArray());
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

	public int length() {
		return objects.length;
	}

	@Override
	public <T> T getAs(Object key, Class<T> asType, T... defRq) {
		T t = (T) get(keyIndex(key), null);
		return ObjTo.objTo(t, asType, defRq);
	}

	public static Integer keyIndex(Object key) {
		return UST.INT(key.toString());
	}

	public Tuple set(Object key, T value) {
		objects[keyIndex(key)] = value;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tuple)) {
			return false;
		}
		Tuple<?> tuple = (Tuple<?>) o;
		return Objects.deepEquals(objects, tuple.objects);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(objects);
	}
}
