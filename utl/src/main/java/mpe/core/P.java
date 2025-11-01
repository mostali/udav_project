package mpe.core;


import lombok.SneakyThrows;
import mpu.Sys;
import mpu.X;
import mpu.core.ARR;
import mpu.core.ARG;
import mpu.str.Rt;
import mpc.str.sym.SEP;
import mpu.str.Sb;
import mpu.str.ToString;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//Print
public class P extends Sys {

	public static final String TAB = "    ";
	public static final String TAB2 = TAB + TAB;
	public static final String TAB3 = TAB2 + TAB;
	public static final String TAB4 = TAB3 + TAB;

	public static final String NL = System.lineSeparator();

	public static void t1(Object s) {
		Sys.p(TAB + s);
	}

	public static void t2(Object s) {
		Sys.p(TAB2 + s);
	}

	public static void t3(Object s) {
		Sys.p(TAB3 + s);
	}

	public static void pl(String s, Object... args) {
		System.out.println(X.fl(s, args));
	}

	public static void p(Iterator it) {
		while (it.hasNext()) {
			Sys.p(it.next());
		}
	}

	public static void p(Iterable s) {
		if (s == null) {
			Sys.p("" + s);
			return;
		}
		for (Object o : s) {
			pnice(o);
		}
	}

	public static void pnice(Object obj) {
		if (obj == null) {
			Sys.p((Object) null);
			return;
		}
		if (Collection.class.isInstance(obj)) {
			Collection collection = (Collection) obj;
			if (X.empty(collection)) {
				Sys.p("empty collection");
			}
			if (collection.size() == 1) {
				Sys.p(ToString.toNiceStringLine(collection));
			} else {
				Sys.p(ToString.toNiceStringCompact(collection));
			}
		} else if (Map.class.isInstance(obj)) {
			Sys.p(Rt.buildReport((Map) obj, obj.getClass().getSimpleName(), 0));
		} else if (Path.class.isInstance(obj) || File.class.isInstance(obj)) {
			Sys.p("file://" + obj);
		} else if (obj.getClass().isArray()) {
			Object[] objects = (Object[]) obj;
			String s = ToString.toNiceStringCompact(Arrays.asList(objects));
			Sys.p(s);
		} else {
			Sys.p(obj);
		}
	}

	public static Logger F() {
		return mpc.log.L.toFile();
	}

	@SneakyThrows
	public static void p(InputStream s) {
		System.out.println(IOUtils.toString(s, Charset.defaultCharset()));
	}

	public static void pobj(Object s) {
		System.out.println(s);
	}

	public static void p(Object... args) {
		if (args == null) {
			System.out.println("null");
		} else {
			System.out.println(Arrays.asList(args));
		}
	}

	public static void pdump(byte[] array) {
		//https://stackoverflow.com/questions/32459683/in-java-8-is-there-a-bytestream-class
		IntStream intStream = IntStream.range(0, array.length).map(idx -> array[idx]);
		System.out.println(intStream.mapToObj(e -> String.valueOf(array)).collect(Collectors.joining()));
	}

	public static void p(Map map) {
		if (map == null) {
			Sys.p("Map is null");
			return;
		}
		Sys.p(map.toString());
		//		for (Object param : map.keySet()) {
		//			p(param + " ::: " + map.get(param));
		//		}
	}

	public static void pfile(String file) {
		pfile(new File(file));
	}

	public static void pfile(File file) {
		Sys.p("file://" + file.getAbsolutePath());
	}

	public static void pfile(Path file) {
		Sys.p("file://" + file.toAbsolutePath());
	}

	public static void w(String msg, Logger... logger) {
		p("WARN", msg, logger);
	}

	public static void w(Throwable t, Logger... logger) {
		p("WARN", t.getMessage(), X.empty(logger) ? "[]" : logger);
		p(ERR.getStackTrace(t));
	}

	public static void warnBig(String msg, Logger... logger) {
		Sb sb = new Sb();
		sb.NL("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		sb.NL("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		sb.NL("!!!!!!!!!!!!!!!!!! " + msg + " !!!!!!!!!!!!!!!!!!!!!!!!!");
		sb.NL("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		sb.NL("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		if (ARG.isDef(logger)) {
			ARG.toDef(logger).warn(msg);
		} else {
			Sys.p(sb);
		}
	}

	public static void exit(Object obj) {
		pnice(obj);
		Sys.p("exit(0)");
		System.exit(0);
	}

	public static void info(Object obj) {
		if (obj == null) {
			Sys.p("Object is null");
			return;
		}
		Sys.p(obj.getClass());
		Sys.p(obj);
	}

	public static void phead(String headMsg, Object... args) {
		SEP.EQ__(X.f(headMsg, args));
	}

	public static void rt(String[] test, String... header) {
		Sys.p(Rt.buildReport(ARR.as(test), ARG.toDefOr("array:" + X.sizeOf(test), header)));
	}

	public static void rt(List list, String... header) {
		Sys.p(Rt.buildReport(list, ARG.toDefOr("list:" + X.sizeOf(list), header)));
	}

	public static void exit0(String msg) {
		Sys.p(msg);
		System.exit(0);
	}
}
