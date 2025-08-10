package mpe.wthttp.core;

import mpc.fs.path.IPath;

public interface INode<T> extends IPath {
	T toNode();

	String toNodeData();

	String toNodeId();
}
