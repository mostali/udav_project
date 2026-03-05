package mpe.cmsg;

import lombok.RequiredArgsConstructor;
import mpc.fs.path.IPath;
import mpe.cmsg.core.INode;
import mpe.cmsg.ns.NodeID;
import mpu.pare.Pare;

import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
public class FileNode implements INode {

	public static FileNode of(String file) {
		return new FileNode(file);
	}

	final String pathFile;

	@Override
	public Pare<String, String> sdn() {
		return NodeID.of(Paths.get(pathFile)).sdn();
	}

	@Override
	public String nodeName() {
		return toPath().getParent().getFileName().toString();
	}

	@Override
	public IPath toNodeImpl() {
		return IPath.of(Paths.get(pathFile));
	}

	@Override
	public String readNodeDataStr(String...defRq) {
		return toNodeImpl().fCat(defRq);
	}

	@Override
	public String toObjId() {
		return toNodeImpl().toPath().toString();
	}

	@Override
	public Path toPath() {
		return Paths.get(pathFile);
	}
}
