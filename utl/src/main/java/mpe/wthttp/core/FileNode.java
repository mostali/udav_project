package mpe.wthttp.core;

import lombok.RequiredArgsConstructor;
import mpc.fs.path.IPath;

import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
public class FileNode implements INode {

	public static FileNode of(String file) {
		return new FileNode(file);
	}

	final String pathFile;

	@Override
	public IPath toNode() {
		return IPath.of(Paths.get(pathFile));
	}

	@Override
	public String toNodeData() {
		return toNode().fCat();
	}

	@Override
	public String toNodeId() {
		return toNode().toPath().toString();
	}

	@Override
	public Path toPath() {
		return Paths.get(pathFile);
	}
}
