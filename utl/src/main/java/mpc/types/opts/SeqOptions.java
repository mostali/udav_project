package mpc.types.opts;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import mpc.exception.FIllegalArgumentException;
import mpc.fs.UF;
import mpc.types.ruprops.URuProps;
import mpf.contract.IContractBuilder;
import mpu.IT;
import mpu.core.*;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpu.str.SPL;
import mpu.str.SPLIT;
import mpu.str.STR;
import mpu.str.UST;
import mpu.Sys;
import mpu.X;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SeqOptions extends AbsRunOptions implements IContractBuilder {

	public static final String MSGF1_SINGLE_KEY = "Single Arg [%s] is required";
	public static final String MSGF2_SINGLE_KEY_TYPE = "Single Arg [%s], type [%s] is required";

	private boolean checkAndReplaceSpringPlaceholderWithDefault = false;

	public final List<String> _args = new ArrayList();
	private List<CmdOption> _opts = null;


	public static SeqOptions ofMMap(Map<String, Object> mmap) {
		return SeqOptions.of(URuProps.toLinesMultimapAsSeq(mmap));
	}

	public static boolean startsWithMulti(String str, List<String> startPrefix1, List<String> startPrefix2) {
		for (String _startPrefix1 : startPrefix1) {
			for (String _startPrefix2 : startPrefix2) {
				if (STR.startsWith(str, _startPrefix1 + _startPrefix2)) {
					return true;
				}
			}
		}
		return false;
	}

	public SeqOptions checkAndReplaceSpringPlaceholderWithDefault(boolean... checkAndReplaceSpringPlaceholderWithDefault) {
		this.checkAndReplaceSpringPlaceholderWithDefault = ARG.isDefNotEqFalse(checkAndReplaceSpringPlaceholderWithDefault);
		return this;
	}

	@Override
	public Map getContractDataMap() {
		return asMultiMap(false).asMap();
	}

	public static void main(String[] args) {
//		SeqOptions seq = SeqOptions.ofStrictKeys("a", "b", "-c", "d", "e", "f", "-g", "--h", "-h", "-1000");
		SeqOptions seq = SeqOptions.ofStrictKeys("a", "b", "-c", "d", "e", "f", "-g", "--h0", "-h", "-1000", "-h", "-1000", "-h", "-1001");
		//seq.removeSingle("h");
		Sys.exit(seq.asMultiMap().asMap());
		Sys.exit(seq.getSingle("h"));
	}

	public Multimap asMultiMap() {
		return asMultiMap(true);
	}

	public Multimap asMultiMap(boolean asLinkedHashMultimap) {
		Multimap<String, String> map = ARG.isDefEqTrue(asLinkedHashMultimap) ? LinkedHashMultimap.create() : ArrayListMultimap.create();
		List<CmdOption> cmdOpts = getCmdOpts();
		for (int i = 0; i < cmdOpts.size(); i++) {
			CmdOption cmdOpt = cmdOpts.get(i);
			switch (cmdOpt.type()) {
				case SINGLE:
					if (map.containsKey(cmdOpt.keyName())) {
						continue;
					}
					List<String> singleAll = getSingleAll(cmdOpt.keyName(), null);
					if (singleAll != null) {
						if (ARG.isDefEqTrue(checkAndReplaceSpringPlaceholderWithDefault)) {
							singleAll = SPL.replaceSpringPlacholderWithDefaultOr(singleAll, false);
						}
						map.putAll(cmdOpt.keyName(), singleAll);
					}
					continue;
				case DOUBLE:
					if (map.containsKey(cmdOpt.keyName())) {
						continue;
					}
					map.put(cmdOpt.keyName(), "true");
					continue;
			}
		}
		return map;
	}

	public Map<String, String> getSingleAsMap(boolean required, String... keys) {
		return getSingleAsMap(required, keys);
	}

	public static Map<String, String> getSingleAsMap(SeqOptions opts, boolean required, String... keys) {
		Map map = new LinkedHashMap();
		for (String k : keys) {
			if (!opts.hasSingleNotEmpty(k)) {
				continue;
			}
			map.put(k, required ? opts.getSingle(k) : opts.getSingle(k, null));
		}
		return map;
	}

	public List<String> getArgs() {
		return _args;
	}

	public void removeSingle(String opt) {
		List<String> args = getArgs();
		List<String> newArgs = new LinkedList();
		boolean found = false;
		for (int i = 0; i < args.size(); i++) {
			String val = args.get(i);
			CmdOption arg = CmdOption.of(val);
			switch (arg.type) {
				case SINGLE:
					if (arg.eq(opt)) {
						found = true;
					} else {
						newArgs.add(val);
					}
					break;
				case CHAR:
				case SIMPLE:
					//skip value
					if (found) {
						found = false;
					} else {
						newArgs.add(val);
					}
					break;
				default:
					newArgs.add(val);
			}
		}
		_args.clear();
		_args.addAll(newArgs);
		_opts = null;
	}

	public SeqOptions addOpts(String... args) {
		this._args.addAll(ARR.as(args));
		_opts = null;
		return this;
	}

	public List<CmdOption> getCmdOpts() {
		return _opts == null ? _opts = _args.stream().map(CmdOption::of).collect(Collectors.toList()) : _opts;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean next = false;
		String DEL = ", ";
		for (String opt : getArgs()) {
			if (next) {
				next = false;
				opt = "********";
			} else if (!X.empty(getProtectedKeys()) && startsWithMulti(opt, ARR.as("", "-", "--", "---"), getProtectedKeys())) {
				next = true;
			}
			sb.append(opt).append(DEL);
		}
		return "SeqRunOptions{" + "opts=" + (sb.length() == 0 ? "" : sb.substring(0, sb.length() - DEL.length())) + '}';
	}

	public static SeqOptions of(String args) {
		return of(args.split("\\s+"));
	}

	public static SeqOptions ofPath(Path pathOpts) {
		return ofStrictKeys(SPLIT.argsBySpace(RW.readString(IT.isFileExist(pathOpts))));
	}

	public static SeqOptions ofStrictKeys(String... args) {
		SeqOptions seqOptions = new SeqOptions().initRunOptions(args);
		List<CmdOption> cmdOpts = seqOptions.getCmdOpts();
		Optional<CmdOption> any = cmdOpts.stream().filter(dblCmd -> dblCmd.type == OptType.DOUBLE).filter(c -> {//
			return cmdOpts.stream().filter(sc -> sc.type == OptType.SINGLE).anyMatch(snglCmd -> snglCmd.keyName().equals(c.keyName()));
		}).findAny();
		if (any.isPresent()) {
			throw new FIllegalArgumentException("Found collision single and double keys '%s'", any.get().keyName());
		}
		return seqOptions;
	}

	public static SeqOptions of(String... args) {
		return new SeqOptions().initRunOptions(args);
	}

	public SeqOptions initRunOptions(String[] args) {
		this._args.addAll(Arrays.asList(args));
		return this;
	}

	@Override
	public boolean hasAnyToken(String opt) {
		return this._args.contains(opt);
	}

	/**
	 * *************************************************************
	 * --------------------------- Simple --------------------------
	 * *************************************************************
	 */

	@Override
	public boolean hasSimple(String key) {
		return _args.stream().anyMatch(e -> e.equals(key));
	}

	/**
	 * *************************************************************
	 * --------------------------- Single --------------------------
	 * *************************************************************
	 */
	@Override
	public boolean hasSingleNotEmpty(String key) {
		return X.notEmpty(getSingle(key, null));
	}

	public String getSingle(String[] anyKey, String... defRq) {
		for (String key : anyKey) {
			String val = getSingle(key, null);
			if (val != null) {
				return val;
			}
		}
		return ARG.toDefThrowMsg(() -> X.f(MSGF1_SINGLE_KEY, ARR.of(anyKey)), defRq);
	}

	public Boolean getSingleAsBool(String key, Boolean... defRq) {
		return getSingleAs(key, Boolean.class, defRq);
	}

	@Override
	public <T> T getSingleAs(String key, Class<T> type, T... defRq) {
		String val = getSingle(key, null);
		if (val != null) {
			return UST.strTo(val, type, defRq);
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException(MSGF2_SINGLE_KEY_TYPE, key, type);
	}

	public String getSingleOrFile(String key, String... defRq) {
		try {
			String single = getSingle(key);
			if (!STR.startsWith(key, UF.PFX_FILE)) {
				return single;
			}
			String file = key.substring(UF.PFX_FILE.length());
			IT.isFileExist(file, "File '%s' not found", file);
			return RW.readString(Paths.get(file));
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "getSingleOrFile throw error with key '%s'", key), defRq);
		}
	}

	@Override
	public String getSingle(String key, String... defRq) {
		List<String> all = getSingleAll(key, true);
		if (!all.isEmpty()) {
			return all.get(0);
		}
		return ARG.toDefThrowMsg(() -> X.f(MSGF1_SINGLE_KEY, key), defRq);
	}


	@Override
	public List<String> getSingleAll(String key, List<String>... defRq) {
		List<String> all = getSingleAll(key, false);
		return X.notEmpty(all) ? all : ARG.toDefThrowMsg(() -> X.f("Arg's by key '%s' is required", key), defRq);
	}

	private List<String> getSingleAll(String key, boolean onlyFirst) {
		List<CmdOption> runOpts = getCmdOpts();
		List<String> all = new ArrayList<>();

		out:
		for (int i = 0; i < runOpts.size(); i++) {
			CmdOption optKey = runOpts.get(i);
			switch (optKey.type) {
				case SINGLE:
					if (optKey.keyOrg.substring(1).equals(key)) {
						CmdOption optVal = ARRi.item(runOpts, i + 1, null);
						if (optVal == null) {
							//end
							break out;
						}
						switch (optVal.type) {
							case CHAR:
							case SIMPLE:
							case NUM:
								all.add(optVal.keyOrg);
								if (onlyFirst) {
									return all;
								} else {
									break;
								}
							case SINGLE:
								all.add(optVal.keyOrg);
								break;
							case DOUBLE:
							case EMPTY:
							case DASH2:
							case DASH:
								continue out;

							default:
								throw new WhatIsTypeException(optVal.type);
						}
					}
					break;
			}
		}
		return all;
	}

	/**
	 * *************************************************************
	 * --------------------------- Double --------------------------
	 * *************************************************************
	 */

//	@Deprecated
	@Override
	public Boolean hasDouble(String key, Boolean... defRq) {
		key = "--" + IT.NE(key);
		for (String opt : _args) {
			if (key.equals(opt)) {
				return true;
			}
		}
		return ARG.toDefThrow(new RequiredRuntimeException("Double Arg [%s] is required", key), defRq);
	}

	@Override
	public boolean hasDoubleQk(String key) {
		key = "--" + IT.NE(key);
		for (String opt : _args) {
			if (key.equals(opt)) {
				return true;
			}
		}
		return false;
	}

	public Boolean hasDoubleIgnoreCase(String key, Boolean... defRq) {
		key = "--" + IT.NE(key);
		for (String opt : _args) {
			if (key.equalsIgnoreCase(opt)) {
				return true;
			}
		}
		return ARG.toDefThrow(new RequiredRuntimeException("Double Arg [%s] is required, ignoreCase", key), defRq);
	}

	public String[] getArgsArray() {
		return getArgs().toArray(new String[0]);
	}

	public <T> List<T> getDoubleAll(T... values) {
		return Stream.of(values).filter(t -> hasDoubleIgnoreCase(t.toString(), true, false)).collect(Collectors.toList());
	}

}
