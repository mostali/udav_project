package mpc.types.tks;

import lombok.RequiredArgsConstructor;
import mpc.exception.WhatIsTypeException;
import mpe.str.ARGS;
import mpu.IT;
import mpu.str.SPLIT;
import mpu.str.ToString;
import mpu.str.UST;
import mpu.str.USToken;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class LID {

	public static final String DEL = "/";

	@Override
	public int hashCode() {
		return Objects.hashCode(els);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == null || obj.getClass() != LID.class ? false : Arrays.equals(els, ((LID) obj).els);
	}

	public String del() {
		return DEL;
	}

	final String[] els;

	public LID(Object... els) {
		this.els = Stream.of(els).map(String::valueOf).toArray(String[]::new);
	}

	public static String to(Object o1, Object o2) {
		return ToString.join(o1, DEL, o2);
	}

	public static String to(Object o1, Object o2, Object o3) {
		return ToString.join(o1, DEL, o2, DEL, o3);
	}

	public static <T> T first(String fid, Class<T> asClass) {
		return USToken.first(fid, DEL, asClass);
	}

	public static String first(String fid) {
		return USToken.first(fid, DEL);
	}

	public static LID of(String fid) {
		IT.state(fid.contains(DEL), "wrong fid pattern '%s'", fid);
		return new LID(SPLIT.argsBy(fid, DEL));
	}

	public static LID of(Object first, Object second) {
		return new LID(first, second);
	}

	public String first() {
		return el(0);
	}

	public long fisrtLong() {
		return UST.LONG(first());
	}

	public int firstInt() {
		return UST.INT(first());
	}

	public String second() {
		return el(1);
	}

	public long secondLong() {
		return UST.LONG(second());
	}

	public int secondInt() {
		return UST.INT(second());
	}

	public String third() {
		return el(2);
	}

	public long thirdLong() {
		return UST.LONG(third());
	}

	public int thirdInt() {
		return UST.INT(third());
	}

	public String el(int index, String... defRq) {
		return ARGS.argsAsStr(els, index, defRq);
	}

	public String STR(int index, String... defRq) {
		return ARGS.argsAsStr(els, index, defRq);
	}

	public Long LONG(int index, Long... defRq) {
		return ARGS.argsAsLong(els, index, defRq);
	}

	public Integer INT(int index, Integer... defRq) {
		return ARGS.argsAsInt(els, index, defRq);
	}

	public <T> T el(int index, Class<T> type, T... defRq) {
		if (type == String.class) {
			return (T) ARGS.argsAsStr(els, index, (String[]) defRq);
		} else if (type == Long.class) {
			return (T) ARGS.argsAsLong(els, index, (Long[]) defRq);
		} else if (type == Integer.class) {
			return (T) ARGS.argsAsInt(els, index, (Integer[]) defRq);
		}
		throw new WhatIsTypeException(type);
	}

	public <T> T asType(int index, Class<T> type, T... defRq) {
		return UST.strTo(els[IT.isIndex(index, els)], type, defRq);
	}

	@Override
	public String toString() {
		return toString(els, del());
	}

	public static String toString(String[] els, String del) {
		return Stream.of(els).collect(Collectors.joining(del));
	}

	public <T> T firstAs(Class<T> asType, T... defRq) {
		return asType(0, asType, defRq);
	}

	public <T> T secondAs(Class<T> asType, T... defRq) {
		return asType(1, asType, defRq);
	}
}