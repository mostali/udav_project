package mpc.arr;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.TreeSet;

public final class EmptyTreeSet extends TreeSet {
	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean containsAll(@NotNull Collection c) {
		return false;
	}

	@Override
	public boolean contains(Object o) {
		return false;
	}

	@Override
	public boolean add(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection c) {
		throw new UnsupportedOperationException();
	}

}
