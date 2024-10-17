package zk_notes.control;

import mpc.fs.path.IPath;
import zk_com.base.Tbx;
import zk_com.base.Tbxm;
import zk_com.core.IZWin;
import zk_page.ZKCF;
import zk_page.ZKS;

import java.nio.file.Path;

public class NoteTbxWin extends Tbxm implements IZWin, IPath {

	private final String nodeName;

	@Override
	public String getFormStateName() {
		return nodeName;
	}

	public NoteTbxWin(String nodeName, Path filePath, Tbx.DIMS... dims) {
		super(filePath, dims);
		this.nodeName = nodeName;
	}

//	public NotesTbxWin(Path filePath, Object width_px_pct, Object height_px_pct) {
//		super(filePath, width_px_pct, height_px_pct);
//	}

	public static NoteTbxWin toggleDnone(NoteTbxWin... defRq) {
		return ZKS.toggleDnoneFirst(NoteTbxWin.class, true, defRq);
	}

	public static NoteTbxWin findFirst(NoteTbxWin... defRq) {
		return ZKCF.findFirstIn_Page(NoteTbxWin.class, true, defRq);
	}


	@Override
	public boolean equals(Object o) {
		return this == o ? true : (o == null ? false : o.equals(fParent()));
	}

}
