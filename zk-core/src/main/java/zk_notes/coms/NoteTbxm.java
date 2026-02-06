package zk_notes.coms;

import lombok.Getter;
import mpc.fs.path.IPath;
import org.zkoss.zk.ui.Page;
import zk_com.base.Tbxm;
import zk_com.core.IZWin;
import zk_notes.node.NodeDir;
import zk_page.ZKCFinderExt;
import zk_page.ZKS;

import java.nio.file.Path;

public class NoteTbxm extends Tbxm implements IZWin, IPath {//, IZComFadeIO

	private final String nodeName;
	private final @Getter NodeDir nodeDir;

	@Override
	public String getFormName() {
		return nodeName;
	}

	public NoteTbxm(NodeDir nodeDir, Path filePath, DIMS... dims) {
		super(filePath, dims);
		this.nodeDir = nodeDir;
		this.nodeName = nodeDir.nodeName();

	}

	@Override
	public void onPageDetached(Page page) {
		super.onPageDetached(page);
	}

	public static NoteTbxm toggleDnone(NoteTbxm... defRq) {
		return ZKS.toggleDnoneFirst(NoteTbxm.class, true, defRq);
	}

	public static NoteTbxm findFirst(NoteTbxm... defRq) {
		return ZKCFinderExt.findFirst_inPage0(NoteTbxm.class, true, defRq);
	}

	@Override
	public boolean equals(Object o) {
		return this == o ? true : (o == null ? false : o.equals(fParent()));
	}

	public void detachNodeCom(boolean persist) {
		getParent().detach();
		if (persist) {
			getNodeDir().state().set_STATE(null);
		}
	}
}
