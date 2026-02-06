package mpt;

import lombok.SneakyThrows;
import mpu.core.ARR;
import mpu.X;
import mpc.exception.FIllegalStateException;
import mpc.rfl.RFL;
import mpc.rfl.UReflExt;
import mpu.str.Rt;
import mpu.str.Sb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class PackTrmRegistrator {

	public static final Logger L = LoggerFactory.getLogger(PackTrmRegistrator.class);

	public static Class[] scan(String[] scan_packages) {
		return UReflExt.getAllPackageClassess_viaDoubleSearch(true, TrmEntity.class, TrmEntity.class.getClassLoader(), scan_packages).toArray(new Class[0]);
	}

	//	public static Class[] scan2(String[] scan_packages) {
	//		List<Class> allPackageClassViaClassgraph = UReflScanner.getAllPackageClassViaClassgraph(scan_packages);
	//		return allPackageClassViaClassgraph.toArray(new Class[0]);
	//	}


	@SneakyThrows
	static void regTrm(Class... trms) {
		Class<TrmEntity> anoType = TrmEntity.class;
		if (L.isInfoEnabled()) {
			L.info("\nFound Terminal's:" + Rt.buildReport(ARR.as(trms)));
		}
		for (Class t : trms) {
			TrmEntity ano = anoType.cast(t.getAnnotation(anoType));
			boolean isFakeTrm = ITrm.class.isAssignableFrom(t);
			Constructor declaredConstructor = t.getDeclaredConstructor();
			Object inst = declaredConstructor.newInstance();

			String[] keys = ano.value();

			for (String key : keys) {
				TRM._TERMINALS.put(key, isFakeTrm ? (ITrm) inst : null);
			}

			Map<String, ITrmCmd> cmds = findTrmCmds(ano.value()[0], inst);

			for (String key : keys) {
				TRM._CMDS.put(key, cmds);
			}

			if (L.isInfoEnabled()) {
				String msg = X.fl("\nTrm '{}' has '{}' TrmCmd's\n", t.getSimpleName(), X.sizeOf(cmds));
				L.info(msg + Rt.buildReport(cmds.keySet()));
			}
		}
	}

	@SneakyThrows
	static Map<String, ITrmCmd> findTrmCmds(String trmName, Object instance) {
		Class<TrmCmdEntity> ano = TrmCmdEntity.class;
		Map<String, ITrmCmd> cmds = new ConcurrentHashMap<>();
		List<Field> fields_ = RFL.fields(instance.getClass(), ITrmCmd.class, ARR.as(TrmCmdEntity.class), null);
		if (X.empty(fields_)) {
			return cmds;
		}
		fields_.forEach(c -> {
			String[] cmdNames = c.getAnnotation(TrmCmdEntity.class).value();
			try {
				for (String cmdName : cmdNames) {
					if (cmds.containsKey(cmdName)) {
						throw new FIllegalStateException("Trm '%s' already contains cmd '%s' as '%s'", trmName, cmdName, cmds.get(cmdName));
					}
					if (RFL.isStatic(c)) {
						cmds.put(cmdName, (ITrmCmd) c.get(null));
					} else {
						cmds.put(cmdName, (ITrmCmd) c.get(instance));
					}
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		});
		if (L.isInfoEnabled()) {
			Sb rt = Rt.buildReport(cmds.keySet(), "\nTrm:" + trmName + "(" + X.sizeOf(cmds) + ")", 0);
			L.info(rt.toStringLine(" / "));
		}
		return cmds;
	}

}
