package mpe.cmsg;

import mpc.fs.path.IPath;
import mpe.cmsg.core.INode;
import mpe.cmsg.ns.NodeID;
import mpu.core.ARRi;
import mpu.pare.Pare;

import java.nio.file.Path;
import java.util.UUID;

//@RequiredArgsConstructor
public class StringNode implements INode {

	final String name;
	final String data;

	public StringNode(String data) {
		this(UUID.randomUUID().toString(), data);
	}

	public StringNode(String name, String data) {
		this.name = name;
		this.data = data;
	}

	public static StringNode of(String file) {
		return new StringNode(file);
	}


	@Override
	public Pare<String, String> sdn() {
		return Pare.of(NodeID.PLANE_INDEX_ALIAS, NodeID.PAGE_INDEX_ALIAS);
	}

	@Override
	public String nodeName() {
		return name;
	}

	@Override
	public IPath toNodeImpl() {
		throw new UnsupportedOperationException();
//		return IPath.of(Paths.get(data));
	}

	@Override
	public String readNodeDataStr(String... defRq) {
		return data;
	}

	@Override
	public String line0(String... defRq) {
		return ARRi.firstLine(data);
	}

	@Override
	public String toObjId() {
		return NodeID.of(sdn(), name).toObjId();
	}

	@Override
	public Path toPath() {
		throw new UnsupportedOperationException();
	}
}
