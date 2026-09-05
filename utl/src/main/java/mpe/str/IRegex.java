package mpe.str;

import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.core.RW;
import mpu.pare.Pare;
import mpu.str.SPLIT;
import mpu.str.UST;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public interface IRegex {

	String name();

	String regex();


	String wrapOneKey(String key);

	String unwrapOneKey(String pattern);

	String[] unwrapManyKeys(String pattern);

	default int groupNum() {
		return -1;
	}

	default String findAndReplaceAll_Get(String pattern, Function<String, Object> kevToValueResolver) {
		return findAndReplace(pattern, this, kevToValueResolver).key();
	}

	default Pare<String, Boolean> findAndReplace(String pattern, Function<String, Object> kevToValueResolver) {
		return findAndReplace(pattern, this, kevToValueResolver);
	}

	default List<String> findAllGroupUnwraped(String pattern) {
		return findGroups_ALL(pattern).stream().map(this::unwrapOneKey).collect(Collectors.toList());
	}

	default List<String> findGroups_ALL(String pattern) {
		return findGroups(pattern, regex());
	}

	private static Pare<String, Boolean> findAndReplace(String pattern, IRegex iRegex, Function<String, Object> kevToValueResolver) {
		List<String> allFoundedGroup = iRegex.findGroups_ALL(pattern);
		if (X.empty(allFoundedGroup)) {
			return Pare.of(pattern, false);
		}

		Map map = new LinkedHashMap<>();

		for (String injectPl : allFoundedGroup) {
			String[] nameWithKey = iRegex.unwrapManyKeys(injectPl);
			String name = nameWithKey[0];
			IT.NE(name, "Empty key from placeholder '%s'", injectPl);
			Object vl = kevToValueResolver.apply(name);
			IT.NN(vl, "Not found value by placeholder key  %s", name);
			if (nameWithKey.length == 2) {
				vl = cutValueBy(vl + "", nameWithKey[1]);
			}
			map.put(injectPl, vl);
		}

		String newNodeData = fillGroupsUniMode(pattern, iRegex.regex(), iRegex.groupNum(), map, true);

		return Pare.of(newNodeData, true);

	}

	private static String cutValueBy(String data, String byKey, String... defRq) {
		if (byKey.startsWith("::") || byKey.startsWith("line:")) {
			int beginIndex = byKey.startsWith("::") ? 2 : 5;
			Integer id = UST.INT(byKey.substring(beginIndex), null);
			if (id != null) {
				List<String> lines = SPLIT.allByNL(data);
				IT.hasLength(lines, id);
				return lines.get(id);
			}
			return ARG.throwMsg(() -> X.f("Not found cut data by key %s. Line number except", byKey), defRq);
		} else if (byKey.startsWith("file:")) {
			Path file = UST.PATH(byKey.substring(5), null);
			IT.isFileExist(file);
			return RW.readString(file);
		}
//			String val=appReesolver.apply(byKey);
//			else if (byKey.startsWith("tree:")) {
//				String treePattern = byKey.substring(5);
//			}

		return ARG.throwMsg(() -> X.f("Except data handler by KeyHolder %s, data\n{}", byKey, data), defRq);

	}

	public static String fillGroupsStrictMode(String str, String regex, int groupNum, Map<String, String> vars, boolean required) {
		return fillGroups(str, regex, groupNum, vars, required, false);
	}

	public static String fillGroupsUniMode(String str, String regex, int groupNum, Map<String, String> vars, boolean required) {
		return fillGroups(str, regex, groupNum, vars, required, true);
	}

	public static String fillGroups(String str, String regex, int groupNum, Map<String, String> vars, boolean required, boolean allowedCollection) {
		List<String> allGroup = groupNum < 0 ? findGroups(str, regex) : findGroups(str, regex, groupNum);
		for (String gr : allGroup) {
			boolean varsContains = vars.containsKey(gr);
			boolean strContains = str.contains(gr);
			if (required) {
				IT.state(varsContains, "vars not contain '%s'", gr);
				IT.state(strContains, "Pattern not contain '%s'", gr);
			}
			if (!varsContains || !strContains) {
				continue;
			}
			Object replacement = vars.get(gr);
			if (replacement instanceof CharSequence) {
				str = str.replace(gr, (CharSequence) replacement);
			} else if (replacement instanceof Number || replacement instanceof Boolean) {
				str = str.replace(gr, replacement.toString());
			}
			if (allowedCollection && replacement instanceof Collection) {
				Collection<String> multiValue = (Collection) replacement;
				str = str.replace(gr, ARRi.first(multiValue));
			}
		}
		return str;
	}


	//
	//

	public static List<String> findGroups(String str, String regex) {
		return findGroups(str, Pattern.compile(regex));
	}

	public static List<String> findGroups(String str, Pattern regex) {
		return findGroups(str, regex, -1);
	}

	public static List<String> findGroups(String str, String regex, int groupNum) {
		return findGroups(str, Pattern.compile(regex), groupNum);
	}

	public static List<String> findGroups(String str, Pattern regex, int groupNum) {
		Set<String> l = new LinkedHashSet<>();
		Matcher matcher = regex.matcher(str);
		while (matcher.find()) {
			if (groupNum < 0) {
				for (int j = 0; j <= matcher.groupCount(); j++) {
					String group = matcher.group(j);
					l.add(group);
				}
			} else {
				l.add(matcher.group(groupNum));
			}
		}
		return ARR.toList(l);
	}

}
