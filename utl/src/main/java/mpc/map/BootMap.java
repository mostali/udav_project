package mpc.map;

import lombok.RequiredArgsConstructor;
import mpc.arr.STREAM;
import mpc.exception.NI;
import mpc.exception.RequiredRuntimeException;
import mpf.contract.IContractBuilder;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.str.SPL;
import mpu.str.UST;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BootMap implements Map<String, String>, IContractBuilder, IGetterAs {

	public static void main(String[] args) {
		BootMap.doAlltest();
		BootContext bootMap = BootContext.ofAll(args, ARR.asLMAP("type", "12"));
		BootMap bootMap0 = bootMap.asMap(true);
		X.exit(bootMap0.getAs("type", Integer.class));
	}

	public final BootContext bootMap;

	private boolean checkAndReplaceSpringPlaceholderWithFind = false;

	public BootMap checkAndReplaceSpringPlaceholderWithFind(boolean... checkAndReplaceSpringPlaceholderWithFind) {
		this.checkAndReplaceSpringPlaceholderWithFind = ARG.isDefNotEqFalse(checkAndReplaceSpringPlaceholderWithFind);
		return this;
	}

	public static BootMap of(BootContext bootMap) {
		return new BootMap(bootMap);
	}

	@Override
	public int size() {
		return bootMap.getBootMap().entrySet().stream().collect(Collectors.summingInt(e -> e.getValue().size()));
	}

	@Override
	public boolean isEmpty() {
		return !bootMap.getBootMap().entrySet().stream().findFirst().isPresent();
	}

	@Override
	public boolean containsKey(Object key) {
		return bootMap.getBootMap().entrySet().stream().anyMatch(e -> e.getValue().containsKey(key));
	}

	@Override
	public boolean containsValue(Object value) {
		return bootMap.getBootMap().entrySet().stream().anyMatch(e -> e.getValue().containsValue(value));
	}

	@Override
	public String get(Object key) {
		return get(key, ARR.EMPTY_ARGS);
	}

	@Override
	public <T> T getAs(String key, Class<T> asType, T... defRq) {
		String strVL = ARG.isDef(defRq) ? get(key, null) : get(key, ARR.EMPTY_ARGS);
		return strVL != null ? UST.strTo(strVL, asType, defRq) : ARG.toDef(defRq);
	}

	public <T> T getAsExt(String key, Class<T> asType, T... defRq) {
		String strVL = ARG.isDef(defRq) ? get(key, null) : get(key, ARR.EMPTY_ARGS);
		return strVL != null ? UST.strToExt(strVL, asType, defRq) : ARG.toDef(defRq);
	}

	public String get(Object key, String... defRq) {
		String vl = bootMap.get((String) key, defRq);
		if (!checkAndReplaceSpringPlaceholderWithFind) {
			return vl;
		}
		List<String> all = ARG.isDef(defRq) ? getAll((String) key, null) : getAll((String) key);
		return X.notEmpty(all) ? ARRi.first(all) : ARG.toDefThrowMsg(() -> X.f("Not found SINGLE value by key '%s'", key), defRq);
	}

	public List<String> getAll(String key, List<String>... defRq) {
		if (!checkAndReplaceSpringPlaceholderWithFind) {
			return bootMap.getAll((String) key, defRq);
		}
		return getAllSPL(key, defRq);
//		return X.notEmpty(allSPL) ? allSPL : ARG.toDefThrowMsg(() -> X.f("Not found ALL values by key '%s'", key), defRq);
	}

	public List<String> getAllSPL(String key, List<String>... defRq) {
		SPL.SplPareBoot root = ARG.isDef(defRq) ? getSPL(key, new HashSet<>(), null) : getSPL(key, new HashSet<>());
		List<String>[] targetValues = null;
		if (root != null) {
			targetValues = root.findTargetValues();
			if (X.notEmpty(targetValues[0])) {
				return targetValues[0];
			} else if (X.notEmpty(targetValues[1])) {
				return targetValues[1];
			}
			return ARG.toDefThrowMsg(() -> X.f("Not found ROOT values '%s'", key), defRq);
		}
		return ARG.toDefThrowMsg(() -> X.f("Not found ROOT key '%s'", key), defRq);
	}

	private SPL.SplPareBoot getSPL(String key, Set<Pare3<BootContext.ApType, String, String>> alreadyChecked, SPL.SplPareBoot... defRq) {

		Map<Pare<BootContext.ApType, String>, List<String>> findedValuesMap = bootMap.findValuesMap(key, false, false);
		if (findedValuesMap == null) {
			return ARG.toDefThrowMsg(() -> X.f("Not found SPL common key '%s'", key), defRq);
		}

		SPL.SplPareBoot root = new SPL.SplPareBoot(key);

		for (Entry<Pare<BootContext.ApType, String>, List<String>> findedValuesMapEntry : findedValuesMap.entrySet()) {

			Pare<BootContext.ApType, String> apKey = findedValuesMapEntry.getKey();

			Pare3<BootContext.ApType, String, String> apKeyProp = Pare3.of(apKey.key(), apKey.val(), key);

			if (alreadyChecked.contains(apKeyProp)) {
				continue;
			}

			List<String> findedValues = findedValuesMapEntry.getValue();

			List<SPL.SplPareBoot> apSpl = STREAM.mapToList(findedValues, s -> SPL.SplPareBoot.of(s, apKey));
			for (SPL.SplPareBoot splToken : apSpl) {
				root.add(splToken);
			}

			if (X.notEmpty(root.resolve0())) {
				alreadyChecked.add(apKeyProp);
				break;
			}

		}
		List<SPL.SplPareBoot> unresolved = root.getUnresolved();
		if (X.empty(unresolved)) {
			return root;
		}
		for (SPL.SplPareBoot unresolvedSpl : unresolved) {

			SPL.SplPareBoot nextSpl = getSPL(unresolvedSpl.name(), alreadyChecked, null);
			if (nextSpl == null) {
				if (!unresolvedSpl.hasDef()) {
					return ARG.toDefThrowMsg(() -> X.f("Not found SPL inner key '%s' apKey '%s'", unresolvedSpl.name(), unresolvedSpl.apKey), defRq);
				}
			} else {
				unresolvedSpl.resolve0().addAll(nextSpl.resolve0());
			}
		}

		return root;
	}

	@Override
	public @Nullable String put(String key, String value) {
		return NI.stop0();
	}

	@Override
	public String remove(Object key) {
		return NI.stop0();
	}

	@Override
	public void putAll(@NotNull Map m) {
		NI.stop0();
	}

	@Override
	public void clear() {
		NI.stop0();
	}

	@Override
	public @NotNull Set keySet() {
		return bootMap.getBootMap().values().stream().map(Map::keySet).flatMap(Set::stream).collect(Collectors.toSet());
	}

	@Override
	public @NotNull Collection values() {
		return NI.stop0();
	}

	@Override
	public @NotNull Set<Entry<String, String>> entrySet() {
		return NI.stop0();
	}

	//
	// --------------------- TEST ----------------------
	//

	public static void test1() {
		String[] args = new String[]{"-file1", "v", "-file1", "v2"};
		BootContext bootMap = BootContext.ofAll(args, ARR.asLMAP("file1", "bb"), ARR.asLMAP("file", "${file:}"));
		BootMap bootMap0 = bootMap.asMap(true);
		List<String> all = bootMap0.getAll("file");
		IT.state(all.equals(ARR.as("")), "test1 fail");
		X.p("test1 is OK");
	}

	public static void test2() {
		String[] args = new String[]{"-file1", "v", "-file1", "v2"};
		BootContext bootMap = BootContext.ofAll(args, ARR.asLMAP("file1", "bb"), ARR.asLMAP("file", "${file:1}"));
		BootMap bootMap0 = bootMap.asMap(true);
		List<String> all = bootMap0.getAll("file");
		IT.state(all.equals(ARR.as("1")), "test2 fail");
		X.p("test2 is OK");
	}

	public static void test3() {
		String[] args = new String[]{"-file", "v", "-file", "v2"};
		BootContext bootMap = BootContext.ofAll(args, ARR.asLMAP("file", "bb"), ARR.asLMAP("file", "${file}"));
		BootMap bootMap0 = bootMap.asMap(true);
		List<String> all = bootMap0.getAll("file");
		IT.state(all.equals(ARR.as("v", "v2")), "test3 fail");
		X.p("test3 is OK");
	}

	public static void test4() {
		String[] args = new String[]{"-file1", "v", "-file2", "v2"};
		BootContext bootMap = BootContext.ofAll(args, ARR.asLMAP("file", "bb"), ARR.asLMAP("file", "${file:1}"));
		BootMap bootMap0 = bootMap.asMap(true);
		List<String> all = bootMap0.getAll("file");
		IT.state(all.equals(ARR.as("bb")), "test4 fail");
		X.p("test4 is OK");
	}

	public static void test5() {
		String[] args = new String[]{"-file1", "v", "-file2", "v2"};
		BootContext bootMap = BootContext.ofAll(args, ARR.asLMAP("file0", "bb"), ARR.asLMAP("file", "${fileNF}"));
		BootMap bootMap0 = bootMap.asMap(true);
		try {
			List<String> all = bootMap0.getAll("noKey");
			NI.wrongLogic("illegal");
		} catch (RequiredRuntimeException ex) {
			IT.state(ex.getMessage().contains("Not found SPL common key 'noKey'"));
			X.p("test5 is OK");
		}
	}

	public static void test6() {
		String[] args = new String[]{"-file1", "v", "-file2", "v2"};
		BootContext bootMap = BootContext.ofAll(args, ARR.asLMAP("file0", "bb"), ARR.asLMAP("file", "${fileNF}"));
		BootMap bootMap0 = bootMap.asMap(true);
		try {
			List<String> all = bootMap0.getAll("file");
			NI.wrongLogic("illegal");
		} catch (RequiredRuntimeException ex) {
			IT.state(ex.getMessage().contains("Not found SPL inner key 'fileNF'"));
			X.p("test6 is OK");
		}
	}

	public static void test7() {
		String[] args = new String[]{"-file", "v", "-file", "v2", "-file", "${file11}"};
		BootContext bootMap = BootContext.ofAll(args, ARR.asLMAP("file11", "bb"), ARR.asLMAP("file", "${file:null}"));
		BootMap bootMap0 = bootMap.asMap(true);
		List<String> all = bootMap0.getAll("file");
		IT.state(all.equals(ARR.as("v", "v2", "bb")), "test7 fail");
		X.p("test7 is OK");
	}

	public static void doAlltest() {
		test1();
		test2();
		test3();
		test4();
		test5();
		test6();
		test7();
	}
}
