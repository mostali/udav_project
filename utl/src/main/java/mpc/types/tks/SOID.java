package mpc.types.tks;

import lombok.RequiredArgsConstructor;
import mpc.exception.WhatIsTypeException;
import mpe.str.ARGS;
import mpu.IT;
import mpu.pare.Pare;
import mpu.str.JOIN;
import mpu.str.SPLIT;
import mpu.str.TKN;
import mpu.str.UST;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// SeqOptionsID '-arg val'  - param in command
public class SOID extends Pare<String, String> {

	public static final String SFX = "-";

	public SOID(String key, String val) {
		super(key, val);
	}

	public static String toKey(String name) {
		return SFX + name;
	}

	public static String toVal(String name, String val) {
		return toKey(name) + " " + IT.NE(val, "set val");
	}

//
//	@Override
//	public int hashCode() {
//		return Objects.hashCode(els);
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		return obj == null || obj.getClass() != SOID.class ? false : Arrays.equals(els, ((SOID) obj).els);
//	}

}
