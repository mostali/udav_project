package mpe;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Queues;
import mpc.Sys;
import mpc.X;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class UL {

	//			public static void main(String[] args) throws InterruptedException {
	//				test_queuesafe_FILO();
	//				test_map_size_FILO();
	//				QueueUtils.synchronizedQueue();
	//				Queue<Integer> qq = (Queue<Integer>) cache_queue_sync_FILO(2);
	//			}


	public static Map cache_map_sync_FILO(int MAX) {
		return Collections.synchronizedMap(cache_map_FILO(MAX));
	}

	public static Map cache_map_FILO(int MAX) {
		return new LinkedHashMap(MAX) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(Map.Entry eldest) {
				return size() > MAX;
			}
		};
	}

	/**
	 * Not supported null elements
	 */
	public static Queue<?> cache_queue_sync_FILO(int MAX) {
		return Queues.synchronizedQueue(cache_queue_FILO(MAX));
	}

	public static QueueSafe<?> cache_queuesafe_sync_FILO(int MAX) {
		return new QueueSafe(MAX);
	}

	public static Queue<?> cache_queue_FILO(int MAX) {
		return EvictingQueue.create(MAX);
	}

	private static void test_queuesafe_FILO() {
		QueueSafe map = cache_queuesafe_sync_FILO(3); //NOT SUPPORTED NULL
		map.add("el1");
		Sys.p("1:" + map);

		map.add("el2");
		Sys.p("2:" + map);

		map.add("el3");
		Sys.p("3:" + map);


		Sys.p("1:" + map.poll());
		Sys.p("2:" + map.poll());
		Sys.p("3:" + map.poll());

		try {
			Sys.p("4:" + map.poll());
			Sys.p("5:" + map.poll());


		} catch (NoSuchElementException ex) {
			Sys.p("Queue is end");
		}
		Sys.exit();
	}

	private static void test_queue_FILO() {
		Queue map = cache_queue_sync_FILO(3); //NOT SUPPORTED NULL
		map.add("el1");
		Sys.p("1:" + map);

		map.add("el2");
		Sys.p("2:" + map);

		map.add("el3");
		Sys.p("3:" + map);


		Sys.p("1:" + map.poll());
		Sys.p("2:" + map.poll());
		Sys.p("3:" + map.poll());

		try {
			Sys.p("4:" + map.poll());
			Sys.p("5:" + map.poll());


		} catch (NoSuchElementException ex) {
			Sys.p("Queue is end");
		}
		Sys.exit();
	}

	private static void test_map_size_FILO() {
		Map map = cache_map_FILO(3);
		map.put("on1", "on1v");
		Sys.p("1:" + map);
		map.put("on2", "on2v");
		Sys.p("2:" + map);

		map.put("on3", "on3v");
		Sys.p("3:" + map);

		map.put("on4", "on4v");
		Sys.p("4:" + map);

		Sys.exit();
	}

	public static ConcurrentHashMap cmap() {
		return new ConcurrentHashMap();
	}

	public static Optional<String> randomElement(Collection map) {
		return map.stream().skip((int) (map.size() * Math.random())).findFirst();
	}

	public static boolean containElementOnlyStartWith(Collection<String> coll, String... prefix) {
		for (String e : coll) {
			if (!containElementStartWith(Arrays.asList(e), prefix)) {
				return false;
			}
		}
		return true;
	}

	public static boolean containElementStartWith(Collection<String> coll, String... prefix) {
		for (String e : coll) {
			for (String p : prefix) {
				if (e.startsWith(p)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean containElementIgnoreCase(Collection<String> coll, String... prefix) {
		for (String e : coll) {
			for (String p : prefix) {
				if (p.equalsIgnoreCase(e)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean contains(List list, Object ownerIdRq) {
		return X.empty(list) ? false : list.contains(ownerIdRq);
	}

	/**
	 * Will happens Exception if will be max
	 */
	public static class QueueSafe<T> {
		private Queue<T> queue_updates;
		private final int max;

		public QueueSafe(int max) {
			queue_updates = (Queue<T>) UL.cache_queue_sync_FILO(max);
			this.max = max;

		}

		public T pollNotEmpty() {
			T update = poll();
			if (update == null) {
				throw new NoSuchElementException();
			}
			return update;
		}

		public T poll() {
			return queue_updates.poll();
		}

		public QueueSafe<T> add(T update) {
			queue_updates.add(update);
			return this;
		}

		public QueueSafe<T> add(T update, Consumer<T> consumer) {
			if (queue_updates.size() == max) {
				consumer.accept(queue_updates.poll());
			}
			queue_updates.add(update);
			return this;
		}
	}
}
