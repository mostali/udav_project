package mpf.ns.space.core;

import mpc.args.ARG;
import mpc.X;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.path.IPath;
import mpc.fs.fd.EFT;
import mpc.fs.Ns;
import mpf.ns.space.ST;
import mpf.ns.space.Src;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class SpaceFd implements IPath, ISs {
	public static final String FIRST_DIR_NAME = "_";
	final Ns ns;

	@Override
	public EFT ft() {
		return this instanceof Src ? EFT.FILE : EFT.DIR;
	}

	public static <S extends SpaceFd> List<S> sortFiles(List<S> fdItems) {
		if (X.empty(fdItems)) {
			return fdItems;
		}
		Comparator<S> sComparator = (hm1, hm2) -> {
			Integer index1 = hm1.indexCached(null);
			Integer index2 = hm2.indexCached(null);
			if (index1 == null) {
				return 1;
			} else if (index2 == null) {
				return -1;
			}
			return index1 == index2 ? 0 : (index1 > index2 ? 1 : -1);
		};
		List<S> sortedItems = fdItems.stream().sorted(sComparator).collect(Collectors.toList());
		fdItems.clear();
		fdItems.addAll(sortedItems);
		return fdItems;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof SpaceFd)) {
			return false;
		}
		SpaceFd spaceFd = (SpaceFd) o;
		return Objects.equals(ns, spaceFd.ns);
	}

	@Override
	public int hashCode() {
		return Objects.hash(ns, index(Integer.MAX_VALUE));
	}

	@Override
	public Path path() {
		return ns.path();
	}

	private SpaceHomeMap _spaceHomeMap;
	private final ST spaceSrcType;

	public SpaceFd(Ns ns) {
		this(ns, ST.SRC);
	}

	public SpaceFd(Ns ns, ST spaceSrcType) {
		this.ns = ns;
		this.spaceSrcType = spaceSrcType;
	}

	public SpaceHomeMap homeMap(SpaceHomeMap... defRq) {
		try {
			return _spaceHomeMap = srcType().homeMap(path());
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "SpaceSrcMap invalid from path '%s'", path()), defRq);
		}
	}

	public Ns ns() {
		return Ns.ofUnsafe(path());
	}

	public List<? extends SpaceFd> getChilds() {
		throw new UnsupportedOperationException("child ni:" + scn());
	}


	public Integer index(Integer... defRq) {
		SpaceHomeMap homeMap = homeMap(null);
		return homeMap == null ? ARG.toDefThrow(() -> new RequiredRuntimeException("Property 'index' not found"), defRq) : homeMap.index(defRq);
	}

	private Integer indexCached;

	public Integer indexCached(Integer... defRq) {
		return indexCached != null ? indexCached : (indexCached = index(defRq));
	}

	@Override
	public String toString() {
		return "SpaceFd{" + srcType(null) + ":" + ns + '}';
	}

}
