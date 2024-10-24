package zk_notes.control;

import lombok.RequiredArgsConstructor;
import mpc.fs.path.IPath;
import zk_com.core.IZWin;
import zk_com.sun_editor.SeTbxm;
import zk_page.ZKCFinder;
import zk_page.ZKS;

import java.nio.file.Path;

@RequiredArgsConstructor
public class SeTbxWin extends SeTbxm implements IZWin, IPath {

	public SeTbxWin(Path filePath, DIMS... dims) {
		super(filePath, dims);
	}

	public SeTbxWin(Path filePath, Object width_px_pct, Object height_px_pct) {
		super(filePath, width_px_pct, height_px_pct);
	}

	public static SeTbxWin toggleDnone(SeTbxWin... defRq) {
		return ZKS.toggleDnoneFirst(SeTbxWin.class, true, defRq);
	}

	public static <V extends IPath> V findFirst(Class<V> openViewClass, V... defRq) {
		return (V) ZKCFinder.findFirstIn_Page((Class) openViewClass, true, defRq);
	}

}
