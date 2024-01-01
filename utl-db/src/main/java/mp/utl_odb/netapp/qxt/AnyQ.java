package mp.utl_odb.netapp.qxt;

import mpc.X;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

//Abstract AnyContext - Use ThreadLocal Store
public interface AnyQ {
	public final static Map<Class, List<ThreadLocal>> _MAPQ = new ConcurrentHashMap<>();

	static ThreadLocal reg(Class host) {
		List ls = _MAPQ.get(host);
		if (ls == null) {
			_MAPQ.put(host, ls = new CopyOnWriteArrayList());
		}
		ThreadLocal tl = new ThreadLocal<>();
		ls.add(tl);
		return tl;
	}

	public static void clear(Class host) {
		List<ThreadLocal> threadLocals = _MAPQ.get(host);
		if (X.notEmpty(threadLocals)) {
			threadLocals.stream().forEach(tl -> tl.remove());
		}
	}

	public static void clearAll() {
		for (Class cls : _MAPQ.keySet()) {
			clear(cls);
		}
	}
}
