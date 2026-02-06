package mpc.fs;

import mpu.core.ARG;
import mpu.IT;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.ext.EXT;
import mpc.fs.fd.EFT;
import mpc.fs.path.IPath;
import mpc.fs.path.UPathToken;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

//Namespace
public class Ns implements Serializable, IPath {

	public static final String DEF_ROOT_DIRNAME = ".ns";

	private final String rootDir, namespace, child, path;

	public static Ns findChild(Path path, Ns... defRq) {
		int nameCount = path.getNameCount();
		if (nameCount > 1) {
			Path child = path.getName(nameCount - 1);
			Path ns_name = path.getName(nameCount - 2);
			Path rootDir = nameCount == 2 ? Paths.get("") : UPathToken.pathItems(path, nameCount - 2);
			return Ns.of(rootDir, ns_name.toString(), child.toString());
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Child ns not found '%s'", path), defRq);
	}

	@Override
	public String fName() {
		return namespace;
	}

	public String name(String... child) {
		return ARG.isDef(child) ? UF.normFileEnd(namespace) + "/" + ARG.toDef(child) : namespace;
	}

	public String child() {
		return child;
	}

	public Ns(Path rootDir) {
		this(rootDir.toAbsolutePath().toString(), null);
	}

	public Ns(String rootDir, String namespace) {
		this(rootDir, namespace, null);
	}

	public Ns(String rootDir, String namespace, String child) {
		this.rootDir = rootDir;
		this.namespace = namespace;
		this.child = child;
		if (child != null) {
			IT.NE(namespace, "child without ns", child);
		}
		this.path = child == null ? pathAsString(rootDir, namespace) : pathAsString(rootDir, child_dir(namespace, child));
	}

	public String getRootDir() {
		return rootDir;
	}

	public Ns getNamespaceOfChild(String ns_child) {
		return Ns.of(rootDir, namespace, ns_child);
	}

	public String getNamespaceValue() {
		return namespace;
	}

	public String getNamespaceChildValue() {
		return child;
	}


	public Ns resolve(String child) {
		return Ns.of(rootDir, namespace, child_dir(this.child, child));
	}

	public Path getPathChild() {
		return path(IT.notEmpty(getNamespaceChildValue()));
	}

	public Path path(String... child0) {
		return ARG.isDefNotEmpty(child0) ? Paths.get(path).resolve(child0[0]) : Paths.get(path);
	}

	public String getPathStr(String... child) {
		return ARG.isDefNotEmpty(child) ? Paths.get(path).resolve(child[0]).toString() : path;
	}

	public static String pathAsString(String rootDir, String namespace) {
		IT.notNull(rootDir);
		if (namespace == null) {
			return UF.normDir(rootDir);
		} else if (".".equals(namespace)) {
			return UF.normDir(rootDir) + UF.normDir(DEF_ROOT_DIRNAME);
		}
		return UF.normDir(rootDir) + UF.normDir(namespace);
	}

	public static Ns of(Path rootDir, String namespace) {
		return of(rootDir.toAbsolutePath().toString(), namespace);
	}

	public static Ns of(String rootDir, String namespace) {
		return new Ns(rootDir, namespace);
	}

	public static Ns of(String rootDir, String namespace, String child) {
		return new Ns(rootDir, namespace, child);
	}

	public static Ns of(String namespace) {
		return new Ns(Ns.DEF_ROOT_DIRNAME, namespace);
	}

	public static Ns of(Path rootDir, String namespace, String child) {
		return new Ns(rootDir.toAbsolutePath().toString(), namespace, child);
	}

	public static Ns ofUnsafe(Path rootDir) {
		return new Ns(rootDir.getParent().toString(), rootDir.getFileName().toString());
	}

	public static Ns ofSafeName(Path rootDir) {
		IT.isGE(rootDir.getNameCount(), 2, "Except ns length >=2 ");
		return new Ns(rootDir.getParent().toString(), rootDir.getFileName().toString());
	}

	public static Ns ofSafeChild(Path rootDir) {
		IT.isGE(rootDir.getNameCount(), 3, "Except ns length >=3 ");
		return new Ns(rootDir.getParent().getParent().toString(), rootDir.getParent().getFileName().toString(), rootDir.getFileName().toString());
	}

	public static String child_dir(String child0, String child1) {
		IT.notNull(child1);
		return child0 == null ? child1 : UF.normDir(child0, child1);
	}

	@Override
	public String toString() {
		return "Namespace{" +
				"path='" + path + '\'' +
				", namespace='" + namespace + '\'' +
				", rootDir='" + rootDir + '\'' +
				'}';
	}

	public void createIfNotExist(boolean... mkdirsOrSingleDirOrCheck) {
		UFS.MKDIR.createDirsOrSingleDirOrCheckExist(toPath(), mkdirsOrSingleDirOrCheck);
	}

	public List<Path> dLs(EFT fileType, List<Path>... defRq) {
		Path path = toPath();
		return UFS.existDir(path) ? UDIR.ls(path, fileType) : ARG.toDefThrow(() -> new RequiredRuntimeException("Ns dir '%s' not exist", path), defRq);
	}

	public String fCat(String... defRq) {
		return fRead(defRq);
	}

	public EXT fExt() {
		return EXT.of(toPath());
	}

	@Override
	public Path toPath() {
		return Paths.get(path);
	}

}
