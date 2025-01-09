package mpc.types.opts;

import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpu.str.STR;
import mpu.str.UST;
import mpu.Sys;
import mpu.X;

import java.util.*;
import java.util.stream.Collectors;

public class SeqOptions extends AbsRunOptions {

	public static void main(String[] args) {
		SeqOptions seq = SeqOptions.of("a", "b", "-c", "d", "e", "f", "-g", "--h", "-h", "-1000");
		//seq.removeSingle("h");
		Sys.exit(seq.getSingle("h"));
	}

	public static final String MSGF1_SINGLE_KEY = "Single Arg [%s] is required";
	public static final String MSGF2_SINGLE_KEY_TYPE = "Single Arg [%s], type [%s] is required";
	public final List<String> _args = new ArrayList();

	private List<CmdOption> _opts = null;

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
		return "SeqRunOptions{" +
				"opts=" + (sb.length() == 0 ? "" : sb.substring(0, sb.length() - DEL.length())) +
				'}';
	}

	public static SeqOptions of(String args) {
		return of(args.split("\\s+"));
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
		return ARG.toDefThrow(X.f(MSGF1_SINGLE_KEY, ARR.of(anyKey)), defRq);
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
		return ARG.toDefThrow(X.f(MSGF1_SINGLE_KEY, key), defRq);
	}

	@Override
	public List<String> getSingleAll(String key, List<String>... defRq) {
		List<String> all = getSingleAll(key, false);
		return X.notEmpty(all) ? all : ARG.toDefThrow(X.f("Arg's by key '%s' is required", key), defRq);
	}

	private List<String> getSingleAll(String key, boolean onlyFirst) {
		List<CmdOption> runOpts = getCmdOpts();
		List<String> all = new ArrayList<>();

		out:
		for (int i = 0; i < runOpts.size(); i++) {
			CmdOption optKey = runOpts.get(i);
			switch (optKey.type) {
				case SINGLE:
					if (optKey.key.substring(1).equals(key)) {
						CmdOption optVal = ARRi.item(runOpts, i + 1, null);
						if (optVal == null) {
							//end
							break out;
						}
						switch (optVal.type) {
							case CHAR:
							case SIMPLE:
							case NUM:
								all.add(optVal.key);
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

}
