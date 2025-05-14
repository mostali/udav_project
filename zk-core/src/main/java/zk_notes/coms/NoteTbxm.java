package zk_notes.coms;

import mpc.fs.path.IPath;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Events;
import zk_com.base.Tbxm;
import zk_com.core.IZComFadeIO;
import zk_com.core.IZWin;
import zk_page.ZKCFinder;
import zk_page.ZKS;

import java.nio.file.Path;

public class NoteTbxm extends Tbxm implements IZWin, IPath {//, IZComFadeIO

	private final String nodeName;

	@Override
	public String getFormName() {
		return nodeName;
	}

	public NoteTbxm(String nodeName, Path filePath, DIMS... dims) {
		super(filePath, dims);
		this.nodeName = nodeName;
//		addEffectIn(this);
//		addEffectOut(this, Events.ON);

	}

	@Override
	public void onPageDetached(Page page) {
		super.onPageDetached(page);
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
