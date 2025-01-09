package lifebeat.mod;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ExecutionError;
import lifebeat.HeapDumpPrinter;
import lifebeat.MemPrinter;
import mpu.Sys;
import mpe.rt.SLEEP;
import mpu.X;
import mpc.env.PidUtils;
import mpc.types.opts.SeqOptions;
import mpc.log.LoggerToSystemOut;
import mpu.str.STR;

import java.io.IOException;

/**
 * Mod для генерации ООМ
 */
//java -Xmx112M -jar utl-SNAPSHOT-shad.jar -strong 10000 -mp 1000
// mvn install -Poom_mod
//java -XX:+HeapDumpOnOutOfMemoryError  -Xmx112M -jar utl-SNAPSHOT-shaded.jar -mp 1000 -strong 1000000
//java -XX:OnOutOfMemoryError="./oom.sh" -XX:+HeapDumpOnOutOfMemoryError  -Xmx112M -jar utl-SNAPSHOT-shaded.jar -mp 1000 -strong 10000000 --noomx
//jmap -dump:live,format=b,file=dump.hprof 24201
//jcmd <pid> GC.heap_dump <file-path>

public class OomMod {
	public static SeqOptions _OPTS;

	public static void main(String[] args) throws IOException {
		main_impl(args);
	}

	public static void main_impl(String[] args) throws IOException {
		_OPTS = SeqOptions.of(args);

		Sys.p("PID:" + PidUtils.getPid_v0());

		Integer strong = _OPTS.getSingleAs("strong", Integer.class, null);
		Integer soft = _OPTS.getSingleAs("soft", Integer.class, null);
		Integer weak = _OPTS.getSingleAs("weak", Integer.class, null);
		Integer mp = _OPTS.getSingleAs("mp", Integer.class, null);
		Integer sleep = _OPTS.getSingleAs("sleep", Integer.class, 1000);

		boolean noom = _OPTS.hasDouble("noom", false);
		boolean noomx = _OPTS.hasDouble("noomx", false);
		boolean noomd = _OPTS.hasDouble("noomd", false);

		if (X.nullAll(strong, soft, weak)) {
			throw new NullPointerException("set arg [strong|soft|weak]");
		}

		if (mp != null) {
			MemPrinter.RUN(mp, new LoggerToSystemOut());
		}

		int dump = 0;
		while (true) {
			try {
				RUN(sleep, strong, soft, weak);
			} catch (Throwable t) {
				if (t instanceof ExecutionError) {
					t = t.getCause();
				}
				Sys.p("Happens ERROR:" + t.getClass() + "::" + t.getMessage());
				if (!noomd && t instanceof OutOfMemoryError) {
					Sys.p("Happens OOM. Build Dump");
					if (dump++ > 0) {
						Sys.p("Write AppHeapDump:::" + HeapDumpPrinter.writeDump());
					}
				}
				if (noom || noomx) {
					Sys.p("No OOM is active. Simple Exit.");
					if (noomx) {
						Sys.exit();
					}
				} else {
					X.throwException(t);
				}
			}
		}
	}

	static LoadingCache<Integer, String> strongCache = CacheBuilder.newBuilder().build(new CacheLoader<Integer, String>() {
		@Override
		public String load(Integer o) throws Exception {
			return new StringBuilder(STR.repeat("1", o)).toString();
		}
	});
	static LoadingCache<Integer, String> softCache = CacheBuilder.newBuilder().softValues().build(new CacheLoader<Integer, String>() {
		@Override
		public String load(Integer o) throws Exception {
			return new StringBuilder(STR.repeat("1", o)).toString();
		}
	});
	static LoadingCache<Integer, String> weakCache = CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<Integer, String>() {
		@Override
		public String load(Integer o) throws Exception {
			return new StringBuilder(STR.repeat("1", o)).toString();
		}
	});

	public static void RUN(Integer sleep, Integer strong, Integer soft, Integer weak) {
//		int ctr = 10_000_000;
//		int minClean = 5;
		while (true) {
			if (strong != null) {
				strongCache.getUnchecked(strong++);
			}
			if (soft != null) {
				softCache.getUnchecked(soft++);
			}
			if (weak != null) {
				weakCache.getUnchecked(soft++);
			}
			Sys.pf("GO >>> Strong:%s(%s),Soft:%s(%s),Weak:%s(%s)", strongCache.size(), strong, softCache.size(), soft, weakCache.size(), weak);
			SLEEP.sleep(sleep);
//			P.p("CacheSoft:" + softCache.size());
//			if (minClean-- < 0) {
//				minClean = 5;
//				weakCache.cleanUp();
//				P.p("CacheWeakCLEAN:" + weakCache.size());
//
//			} else {
////				P.p("CacheWeak:" + weakCache.size());
//
//			}
		}
	}
}
