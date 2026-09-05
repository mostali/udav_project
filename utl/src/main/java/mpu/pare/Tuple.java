package mpu.pare;

import lombok.RequiredArgsConstructor;
import mpc.map.IGetterAsAny;
import mpc.str.ObjTo;
import mpe.str.ARGS;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.core.EQ;
import mpu.str.SPLIT;
import mpu.str.STR;
import mpu.str.UST;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Tuple<T> implements IGetterAsAny {

	public final T[] objs;

	public static Tuple ofKey(Object key, Object... objs) {
		return new Tuple(ARR.mergeAny(key, objs));
	}

	public static Tuple ofObjs(Object... objs) {
		return new Tuple(objs);
	}

	public static Tuple ofCmd(String cmd, String... del_or_spaceDefault) {
		return new Tuple(SPLIT.argsBy(cmd, ARG.toDefOr(STR.SPACE, del_or_spaceDefault)));
	}

	public static Tuple ofMap(Map map, Object... keys) {
		return new Tuple(Arrays.stream(keys).map(k -> map.get(k)).toArray());
	}

	public List<String> toValuesString() {
		return Arrays.stream(objs).map(String::valueOf).collect(Collectors.toList());
	}

	protected static Tuple ofList(List head) {
		return new Tuple(head.toArray());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "*" + X.sizeOf(objs) + Arrays.toString(objs);
	}

	public T get(int i, T... defRq) {
		return ARR.isIndex(i, objs) ? objs[i] : ARG.throwMsg(() -> X.f("Except obj by index %s, but length is %s", i, objs.length), defRq);
	}

	public Boolean eq(int index, Object obj, Boolean... defRq) {
		if (ARG.isDef(defRq)) {
			return ARR.isIndex(index, objs) ? EQ.equalsUnsafe(get(index), obj) : ARG.toDef(defRq);
		}
		return EQ.equalsUnsafe(get(index), obj);
	}

	public int length() {
		return objs.length;
	}

	@Override
	public <T> T getAs(Object keyIndex, Class<T> asType, T... defRq) {
		T t = (T) get(keyIndex(keyIndex), null);
		return ObjTo.objTo(t, asType, defRq);
	}

	public static Integer keyIndex(Object key) {
		return UST.INT(key.toString());
	}

	public Tuple set(Object key, T value) {
		objs[keyIndex(key)] = value;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tuple)) {
			return false;
		}
		Tuple<?> tuple = (Tuple<?>) o;
		return Objects.deepEquals(objs, tuple.objs);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(objs);
	}

	public boolean hasNN(int i) {
		return get(i, null) != null;
	}

	public boolean hasNotEmptyObj(int i) {
		return X.notEmptyAllObj_Str_Cll_Num(i);
	}

	//

	public String id(String... defRq) {
		return objs.length > 0 ? (String) objs[0] : ARG.throwMsg("Except id", defRq);
	}

	public <T> T key(T... defRq) {
		return (T) ARRi.item(objs, 0, defRq);
	}

	public <K> K keyAs(Class<K> asType, K... defRq) {
		return getAs(0, asType, defRq);
	}

	//

	public <T> T val(T... defRq) {
		return (T) ARRi.item(objs, 1, defRq);
	}

	public <T> T valAs(Class<T> asType, T... defRq) {
		return getAs(1, asType, defRq);
	}

	//

	public <T> T ext(T... defRq) {
		return (T) ARRi.item(objs, 2, defRq);
	}

	public <T> T extAs(Class<T> asType, T... defRq) {
		return getAs(2, asType, defRq);
	}

	//

	public <T> T o1(T... defRq) {
		return (T) ARRi.item(objs, 3, defRq);
	}

	public <T> T o2(T... defRq) {
		return (T) ARRi.item(objs, 4, defRq);
	}

	public <T> T o3(T... defRq) {
		return (T) ARRi.item(objs, 5, defRq);
	}

	public <T> T o4(T... defRq) {
		return (T) ARRi.item(objs, 6, defRq);
	}

	public <T> T o5(T... defRq) {
		return (T) ARRi.item(objs, 7, defRq);
	}
}
