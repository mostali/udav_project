package mpe.tpl_engine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mpc.fs.UF;
import mpc.fs.ext.EXT;
import mpc.types.opts.Cmmd;
import mpe.str.IRegex;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.core.RW;
import mpu.str.SPLIT;
import mpu.str.STR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TplEngine {

	public static final Logger L = LoggerFactory.getLogger(TplEngine.class);

	public static void main(String[] args) throws IOException {

		IRegex regex = TplEngineEnv.get().getREGEX();

		if (true) {
//			String cmd = "::ls";
			String cmd = "::*";

//			new LsTplCmd()
//			new TplCmds.LsTxtTplCmd();
//			new TplCmds();
			Object run = AppCmds.findCmd(cmd).run(cmd);
			X.exit("ok:" + run);
		}

		if (true) {
//			Map<TplSrc, List<Tpl>> tplsByExt = findTplsSql();
			Map<TplSrc, List<Tpl>> tplsByExt = findTplsWith_Sql_And_EmptyArgs();
			X.exit(tplsByExt);
		}

		TplSrc tplEngine = new TplSrc(Paths.get("/tmp/notes-demo629542066907736157"), regex);

		if (true) {
			List<Tpl> tplCntAll = tplEngine.findTplCntAll();
			X.exit(tplCntAll);
			String cmd = "::123 -name nm -email em";
			List<Tpl> tpl = tplEngine.findTpl(NeedleMode.STRICT, true, Cmmd.of(cmd));
			Tpl tpl1 = tpl.get(0);
//			X.exit(tpl1.getContentReplaced());

		}
		Map keysData = Map.of("name", "naaame", "email", "maaaaaaail");

		List<TplCnt> tpl = tplEngine.findTplRequired(NeedleMode.STRICT, true, (String[]) keysData.keySet().toArray(String[]::new));

		TplCnt first = ARRi.first(tpl);

		String replacedData = regex.findAndReplaceAll_Get(first.data, keysData::get);

		X.exit(replacedData);

	}


	public static AppRun.Out runSql(Cmmd cmmd, TplEngine.Tpl tpl) {
		Function<AppRun.In, AppRun.Out> runner = AppRun.RUNNERS.get(EXT.SQL);
		tpl.checkIsAplicableTplGroups(cmmd, true);
		String dataIn = tpl.getContentReplacedWith(cmmd);
		AppRun.In in = AppRun.In.ofString(dataIn);
		AppRun.Out out = runner.apply(in);
		return out;
	}

	//
	//

	public static Map<TplEngine.TplSrc, List<TplEngine.Tpl>> findTplsWith_Sql_And_EmptyArgs() {
		Predicate<Tpl> tplPredicate1 = t -> Tpl.SQL_EXT_TYPES.contains(t.getFileExt());
		Predicate<Tpl> tplPredicate2 = t -> t.getTplCnt().isEmptyGroups();
		return findTplsWith(tplPredicate1.and(tplPredicate2));
	}

	public static List<Tpl> findTplsWith_Sql_LIST() {
		return findTplsWith_Sql_LIST(null);
	}

	public static List<Tpl> findTplsWith_Sql_LIST(Predicate<Tpl> with) {
		return Tpl.toListTpl(findTplsWith_Sql(with));
	}

	public static Map<TplEngine.TplSrc, List<TplEngine.Tpl>> findTplsWith_Sql(Predicate<Tpl> with) {
		Predicate<Tpl> tplPredicate1 = t -> Tpl.SQL_EXT_TYPES.contains(t.getFileExt());
		return findTplsWith(with == null ? tplPredicate1 : with.and(tplPredicate1));
	}

	public static Map<TplEngine.TplSrc, List<TplEngine.Tpl>> findTplsWith(Predicate<Tpl> with) {

		with = with == null ? t -> true : with;

		Map<TplEngine.TplSrc, List<TplEngine.Tpl>> allTpls = new HashMap<>();
		for (Path store : TplEngineEnv.get().getSTORES()) {
			TplEngine.TplSrc tplSrc = TplEngine.TplSrc.of(store);
			List<TplEngine.Tpl> tpl = tplSrc.findTpl(TplEngine.NeedleMode.ANY, false, null, null);
			if (X.notEmpty(tpl)) {
				tpl = tpl.stream().filter(with).collect(Collectors.toList());
				if (X.notEmpty(tpl)) {
					allTpls.put(tplSrc, tpl);
				}
			}
		}
		return allTpls;
	}

	//
	//

	@RequiredArgsConstructor
	public static class Tpl {
		public static final Set<EXT> SQL_EXT_TYPES = ARR.asHSET(EXT.SQL);

		private final @Getter TplSrc tplSrc;
		private final @Getter TplCnt tplCnt;
		private final @Getter IRegex regex;

		public static List<Tpl> toListTpl(Map<TplSrc, List<Tpl>> tplsWithSqlAndEmptyArgs) {
			return tplsWithSqlAndEmptyArgs.entrySet().stream().flatMap(e -> e.getValue().stream()).collect(Collectors.toList());
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + "::" + getFileExt() + "::" + tplCnt;//+ " << " + getCmd0();
		}

		public String getTplFilename() {
			return tplCnt.file.getFileName().toString();
		}

		public EXT getFileExt() {
			return tplCnt.getFileExt();
		}

		public String getContentTemplate() {
			return tplCnt.data;
		}

		public String getContentReplacedWith(Cmmd cmmd) {
			Map map = toMapSimple(cmmd.asMap(), regex);
			String s = IRegex.fillGroupsStrictMode(getContentTemplate(), regex.regex(), regex.groupNum(), map, true);
			return s;
		}

		public boolean checkIsAplicableTplGroups(Cmmd cmmd, boolean RETURN) {
//			Map map = toMapSimple(cmmd.asMap());
			NeedleMode needleMode = NeedleMode.STRICT_OUT;
			Set<String> keysIn = cmmd.keysSingly();
			List<String> groups = getTplCnt().getGroups();
			if (RETURN) {
				return needleMode.validate(keysIn, groups, true);
			}
			needleMode.throwIsNot(keysIn, groups, true);
			return true;
		}

		public static Map<String, Object> toMapSimple(Map<String, Collection<Object>> map, IRegex regex) {
			return map.entrySet().stream().collect(Collectors.toMap(e -> regex.wrapOneKey(e.getKey()), v -> ARRi.first(v.getValue())));
		}

		private static String getKeyFromGroup(String group) {
			return STR.substrCount(group, 2, 2);
		}

		//выводим только шапку с комментами
		public List<String> toStringComments() {
			String prefix = "--";
			List<String> comment = new LinkedList<>();
			List<String> lines = SPLIT.allByNL(getContentTemplate());
			boolean firstCommentFound = false;
			for (String line : lines) {
				line = line.trim();
				if (line.startsWith(prefix)) {
					String cleanComment = line.substring(prefix.length());
					comment.add(cleanComment);
					firstCommentFound = true;
				} else if (firstCommentFound) {
					break;
				}
			}
			return comment;
		}


	}

	@RequiredArgsConstructor
	@Getter
	public static class TplCnt {
		final Path file;
		final String data;
		final List<String> groups;

		@Override
		public String toString() {
			return getClass().getSimpleName() + "::" + UF.ln(getFile()) + "::" + getGroups();
		}

		public boolean isEmptyGroups() {
			return X.empty(groups);
		}

		public EXT getFileExt() {
			return EXT.of(file, null);
		}
	}

	@RequiredArgsConstructor
	public static class TplSrc {

		final Path dir;
		final IRegex iRegex;

		public static TplSrc of(Path dir) {
			return new TplSrc(dir, TplEngineEnv.get().getREGEX());
		}

		public List<Tpl> findTplCntAll() {
			List<TplCnt> tplAll = findTplAll(NeedleMode.ANY, false);
			return tplAll.stream().map(tplCnt -> new Tpl(this, tplCnt, iRegex)).collect(Collectors.toList());
		}

		public List<Tpl> findTpl(NeedleMode eNeedle, boolean findFirst, Cmmd cmd, List<Tpl>... defRq) {
			String[] keys = NeedleMode.ANY == eNeedle ? null : ARR.toArgsString(cmd.keysSingly());
			List<TplCnt> tpl = findTplAll(eNeedle, findFirst, keys);
			List<Tpl> collect = tpl.stream().map(tplCnt -> new Tpl(this, tplCnt, iRegex)).collect(Collectors.toList());
			if (!collect.isEmpty()) {
				return collect;
			}
			return ARG.throwMsg(() -> X.f("Except tpl's in %s by cmd [%s]. Used regex [%s]", UF.ln(dir), cmd.getCommand(), iRegex), defRq);
		}

		public List<TplCnt> findTplRequired(NeedleMode eNeedle, boolean findFirst, String... keys) {
			List<TplCnt> tplAll = findTplAll(eNeedle, findFirst, keys);
			IT.notEmpty(tplAll, "Except tpl's in %s by keys %s. Used regex [%s]", UF.ln(dir), ARR.as(keys), iRegex);
			return tplAll;
		}

		public List<TplCnt> findTplAll(NeedleMode eNeedle, boolean findFirst, String... keys) {
			if (eNeedle != NeedleMode.ANY) {
				IT.state(keys != null && X.notEmptyAll(keys), "Except not empty keys");
			}
			try (Stream<Path> files = Files.walk(dir)) {
				Stream<TplCnt> pareStream = files //
						.filter(Files::isRegularFile) //
						.filter(Files::isReadable).map(path -> {
							TplCnt pathParePare = readContentByAllKeys(path, eNeedle, iRegex, true, keys);
							return pathParePare;
						}).filter(X::NN);
				if (findFirst) {
					Optional<TplCnt> first = pareStream.findFirst();
					return first.isPresent() ? ARR.as(first.get()) : ARR.EMPTY_LIST;
				}
				return pareStream
//						.sorted()
						.collect(Collectors.toList());
			} catch (IOException e) {
				throw new UncheckedIOException("Failed to scan notes directory: " + dir, e);
			}
		}

		public static TplCnt readContentByAllKeys(Path file, NeedleMode needleMode, IRegex iRegex, boolean returnNull, String... keys) {
			String dataTpl = RW.readContent(file);
			List<String> allGroup = iRegex.findAllGroupUnwraped(dataTpl);
			if (returnNull) {
				boolean validate = true;
				if (NeedleMode.ANY != needleMode) {
					validate = needleMode.validate(ARR.as(keys), allGroup, true);
				}
				return validate ? new TplCnt(file, dataTpl, allGroup) : null;
			}
			needleMode.throwIsNot(ARR.as(keys), allGroup, true);
			return new TplCnt(file, dataTpl, allGroup);
		}

		public String getTplSrcFilename() {
			return dir.getFileName().toString();
		}
	}

	public enum NeedleMode {
		ANY, //
		STRICT, //Строгое равенство ключей
		STRICT_IN, //Все ключи IN найдены в OUT
		STRICT_OUT //Все Ключи OUT найдены в IN
		;

		public void throwIsNot(Collection<String> keysIn, Collection<String> keysOut, boolean ignoreCase) {
			boolean validate = validate(keysIn, keysOut, ignoreCase);
			IT.state(validate, "Except needle MODE:%s IN %s <-> OUT %s", this, keysIn, keysOut);
		}

		public boolean validate(Collection<String> keysIn, Collection<String> keysOut, boolean ignoreCase) {
			if (keysIn == null || keysOut == null) {
				return false;
			}
			switch (this) {
				case ANY:
					return true;
				case STRICT: {
					if (keysIn.size() != keysOut.size()) {
						return false;
					}
					return ignoreCase ? containsAllIgnoreCase(keysIn, keysOut) : keysIn.containsAll(keysOut);
				}
				case STRICT_IN: {
					// Все ключи IN найдены в OUT -> OUT должен содержать все элементы IN
					if (keysOut.size() < keysIn.size()) {
						return false;
					}
					return ignoreCase ? containsAllIgnoreCase(keysOut, keysIn) : keysOut.containsAll(keysIn);
				}
				case STRICT_OUT: {
					// Все ключи OUT найдены в IN -> IN должен содержать все элементы OUT
					if (keysIn.size() < keysOut.size()) {
						return false;
					}
					return ignoreCase ? containsAllIgnoreCase(keysIn, keysOut) : keysIn.containsAll(keysOut);
				}
				default:
					return false;
			}
		}

		private boolean containsAllIgnoreCase(Collection<String> container, Collection<String> items) {
			for (String item : items) {
				if (item == null) {
					if (!container.contains(null)) {
						return false;
					}
				} else {
					boolean found = false;
					for (String candidate : container) {
						if (item.equalsIgnoreCase(candidate)) {
							found = true;
							break;
						}
					}
					if (!found) {
						return false;
					}
				}
			}
			return true;
		}

	}
}