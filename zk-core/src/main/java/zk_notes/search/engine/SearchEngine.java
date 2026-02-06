package zk_notes.search.engine;

import lombok.Getter;
import lombok.Setter;
import mpc.fs.UFS;
import mpc.log.L;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public abstract class SearchEngine<T> {

	@Setter
	private @Getter Enum searchMode;

	public final String srcPath;

	public Path pathSrcSearch() {
		return Paths.get(srcPath);
	}

	public SearchEngine() {
		this.srcPath = null;
	}

	public SearchEngine(String srcPath) {
		this.srcPath = srcPath;
	}

	protected abstract Collection<T> searchImpl(String part, int count);

	protected abstract Collection<T> searchImpl(Predicate<T> part, int count);

	public Collection<T> search(Predicate<T> predicateItem, int count) {
		return search(null, predicateItem, count);
	}

	public Collection<T> search(String part, int count) {
		return search(part, null, count);
	}

	private Collection<T> search(String part, Predicate<T> predicateItem, int count) {

		IT.isPosNotZero(count);

		Collection<T> items = predicateItem == null ? searchImpl(part, count) : searchImpl(predicateItem, count);

		//TODO
		if (X.sizeOf(items) > 100) {
			List all = new ArrayList<>(items);
			items = ARR.sublist(all, 0, count - 1, all);
		}

		if (X.empty(items)) {
			if (srcPath == null) {
//				L.info("Empty (no path) items:" + part);
			} else {
//				L.info("Empty path items:" + srcPath + "(" + UFS.ls(pathSrcSearch()).size() + ") <<< " + part);
			}
		}

		return items;
	}

}
