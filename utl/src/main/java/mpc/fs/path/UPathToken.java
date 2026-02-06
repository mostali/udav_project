package mpc.fs.path;

import mpu.core.ARG;
import mpu.core.ARRi;
import mpu.IT;
import mpc.exception.RequiredRuntimeException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class UPathToken {

	//	public static void main(String[] args) {
	//		P.exit(pathItems(Paths.get("1/2/3"), -2));
	//		U.exit(last(Paths.get("1/2/3"), "2", true));
	//		U.exit(last(Paths.get("1/2/3"), "2", false));
	//		U.exit(first(Paths.get("1/2/3"), "2", false));
	//		U.exit(lastIndexOf(Paths.get("1/2/3"), "2"));
	//	}

	public static void test() {
		IT.state(first(Paths.get("/1/2/3"), "2", true).equals(Paths.get("/1/2")), "test1");
		IT.state(first(Paths.get("/1/2/3"), "2", true).equals(Paths.get("/1/2/")), "test1-1");
		IT.state(first(Paths.get("1/2/3"), "2", true).equals(Paths.get("1/2")), "test2");

		IT.state(first(Paths.get("/1/2/3"), "2", false).equals(Paths.get("/1")), "test3");
		IT.state(first(Paths.get("1/2/3"), "2", false).equals(Paths.get("1")), "test4");
		//
		//
		IT.state(last(Paths.get("/1/2/3"), "2", true).equals(Paths.get("2/3")), "test-L-1");
		IT.state(last(Paths.get("/1/2/3"), "2", true).equals(Paths.get("2/3/")), "test-L-1-1");
		IT.state(last(Paths.get("1/2/3"), "2", true).equals(Paths.get("2/3")), "test-L-2");

		IT.state(last(Paths.get("/1/2/3"), "2", false).equals(Paths.get("3")), "test-L-3");
		IT.state(last(Paths.get("1/2/3"), "2", false).equals(Paths.get("3")), "test-L-4");
		IT.state(last(Paths.get("1/2/3/"), "2", false).equals(Paths.get("3")), "test-L-4");
		IT.state(last(Paths.get("1/2/3/"), "2", false).equals(Paths.get("3/")), "test-L-4");
	}

	public static Path first(Path path, String name, boolean include, Path... defRq) {
		Integer ind = indexOf(path, name, null);
		if (ind != null) {
			if (!include || ++ind < path.getNameCount()) {
				return swapAbsIf(path.subpath(0, ind), path);
			}
		}
		return ARG.toDefRq(defRq);
	}

	private static Path swapAbsIf(Path target, Path checkWith) {
		if (checkWith.isAbsolute()) {
			if (target.isAbsolute()) {
				return target;
			} else {
				return Paths.get("/").resolve(target);
			}
		} else {
			if (target.isAbsolute()) {
				return target.subpath(0, target.getNameCount() - 1);
			} else {
				return target;
			}
		}
	}

	public static Path last(Path path, String name, boolean include, Path... defRq) {
		Integer ind = lastIndexOf(path, name, null);
		out:
		if (ind != null) {
			if (!include && ++ind >= path.getNameCount()) {
				break out;
			}
			return path.subpath(ind, path.getNameCount());
		}
		return ARG.toDefRq(defRq);
	}

	public static Integer indexOf(Path path, String name, Integer... defRq) {
		for (int i = 0; i < path.getNameCount(); i++) {
			if (name.equals(path.getName(i).toString())) {
				return i;
			}
		}
		return ARG.toDefRq(defRq);
	}

	public static Integer lastIndexOf(Path path, String needle, Integer... defRq) {
		for (int i = path.getNameCount() - 1; i >= 0; i--) {
			if (needle.equals(path.getName(i).toString())) {
				return i;
			}
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Not found lastIndexOf '%s' in path '%s'", needle, path), defRq);
	}

	public static Path pathItems(Path path, int count_start_end, Path... defRq) {
		IT.isNotZero(count_start_end);
		boolean first = count_start_end > 0;
		List<Path> many = first ? ARRi.firstMany(path.iterator(), count_start_end, null) : ARRi.lastMany(path.iterator(), Math.abs(count_start_end), null);
		if (many != null) {
			Path total = many.get(0);
			if (many.size() > 1) {
				for (int i = 1; i < many.size(); i++) {
					total = total.resolve(many.get(i));
				}
			}
			return total;
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDefRq(defRq);
		}
		throw new RequiredRuntimeException("Required '%s' elements of path '%s' (%s)", count_start_end, path, path.getNameCount());
	}
}
