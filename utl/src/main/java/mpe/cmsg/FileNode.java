package mpe.cmsg;

import mpc.fs.path.IPath;
import mpe.cmsg.core.INode;
import mpe.cmsg.ns.NodeID;
import mpu.pare.Pare;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileNode implements INode {

	public static FileNode of(String file) {
		return new FileNode(Paths.get(file));
	}

	private final String pathFile;
	private transient Path pathFile0;

	public FileNode(Path pathFile) {
		this.pathFile0 = pathFile;
		this.pathFile = pathFile.toString();
	}

//	public FileNode(String pathFile) {
//		this.pathFile = pathFile;
//		this.pathFile0 = Paths.get(pathFile);
//	}

	@Override
	public Pare<String, String> sdn() {
		return NodeID.of(toPath()).sdn();
	}

	@Override
	public String nodeName() {
		return toPath().getParent().getFileName().toString();
	}

	@Override
	public IPath toNodeImpl() {
		return IPath.of(toPath());
	}

	@Override
	public String readNodeDataStr(String... defRq) {
		return toNodeImpl().fCat(defRq);
	}

	@Override
	public String line0(String... defRq) {
		return toNodeImpl().fLine0(defRq);
	}

	@Override
	public String toObjId() {
		return toNodeImpl().toPath().toString();
	}

	@Override
	public Path toPath() {
		return pathFile0 != null ? pathFile0 : (pathFile0 = Paths.get(pathFile));
	}
}
