package zk_notes.coms;

import lombok.RequiredArgsConstructor;
import mpc.fs.path.IPath;
import zk_com.core.IZWin;
import zk_com.sun_editor.SeTbxm;
import zk_page.ZKCFinderExt;
import zk_page.ZKS;

import java.nio.file.Path;

@RequiredArgsConstructor
public class SeNoteTbxm extends SeTbxm implements IZWin, IPath {

	public SeNoteTbxm(Path filePath, DIMS... dims) {
		super(filePath, dims);
	}

	public SeNoteTbxm(Path filePath, Object width_px_pct, Object height_px_pct) {
		super(filePath, width_px_pct, height_px_pct);
	}

	public static SeNoteTbxm toggleDnone(SeNoteTbxm... defRq) {
		return ZKS.toggleDnoneFirst(SeNoteTbxm.class, true, defRq);
	}

	public static <V extends IPath> V findFirst(Class<V> openViewClass, V... defRq) {
		return (V) ZKCFinderExt.findFirst_inPage0((Class) openViewClass, true, defRq);
	}

}
