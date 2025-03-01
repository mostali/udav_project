package mpc.types.opts;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import mpc.exception.FIllegalArgumentException;
import mpc.types.ruprops.URuProps;
import mpf.contract.IContractBuilder;
import mpu.IT;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpu.core.RW;
import mpu.str.SPL;
import mpu.str.SPLIT;
import mpu.str.STR;
import mpu.str.UST;
import mpu.Sys;
import mpu.X;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class SeqOptions extends AbsRunOptions implements IContractBuilder {

	private boolean checkAndReplaceSpringPlaceholderWithDefault = false;

	public static SeqOptions ofMMap(Map<String, Object> mmap) {
		return SeqOptions.of(URuProps.toLinesMultimapAsSeq(mmap));
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

	public static final String MSGF1_SINGLE_KEY = "Single Arg [%s] is required";
	public static final String MSGF2_SINGLE_KEY_TYPE = "Single Arg [%s], type [%s] is required";
	public final List<String> _args = new ArrayList();

	private List<CmdOption> _opts = null;

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

	public static Map<String, String> getSingleAsMap(SeqOptions opts, String[] keys, Map... defRq) {
		Map map = new LinkedHashMap();
		for (String k : keys) {
			if (!opts.hasSingleNotEmpty(k)) {
				continue;
			}
			map.put(k, opts.getSingle(k));
		}
		if (!map.isEmpty()) {
			return map;
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
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
			} else if (!X.empty(getProtectedKeys()) && STR.startsWithMulti(opt, ARR.as("", "-", "--", "---"), getProtectedKeys())) {
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
		return ofStrictKeys(SPLIT.argsBySpace(RW.readContent(IT.isFileExist(pathOpts))));
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

	@Override
	public Boolean hasDouble(String key, Boolean... defRq) {
		key = "--" + key;
		for (String opt : _args) {
			if (Objects.equals(opt, key)) {
				return true;
			}
		}
		return ARG.toDefThrow(new RequiredRuntimeException("Double Arg [%s] is required", key), defRq);
	}

	public String[] getArgsArray() {
		return getArgs().toArray(new String[0]);
	}
}
