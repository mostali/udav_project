package zk_notes.coms;

import mpc.fs.path.IPath;
import zk_com.base.Tbxm;
import zk_com.core.IZWin;
import zk_page.ZKCFinder;
import zk_page.ZKS;

import java.nio.file.Path;

public class NoteTbxm extends Tbxm implements IZWin, IPath {

	private final String nodeName;

	@Override
	public String getFormName() {
		return nodeName;
	}

	public NoteTbxm(String nodeName, Path filePath, DIMS... dims) {
		super(filePath, dims);
		this.nodeName = nodeName;
	}

	public static NoteTbxm toggleDnone(NoteTbxm... defRq) {
		return ZKS.toggleDnoneFirst(NoteTbxm.class, true, defRq);
	}

	public static NoteTbxm findFirst(NoteTbxm... defRq) {
		return ZKCFinder.findFirstIn_Page(NoteTbxm.class, true, defRq);
	}

	@Override
	public boolean equals(Object o) {
		return this == o ? true : (o == null ? false : o.equals(fParent()));
	}

}
