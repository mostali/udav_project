package zk_form.dirview;

import lombok.SneakyThrows;
import mpu.core.ARG;
import mpu.core.ARR;
import mpc.arr.STREAM;
import mpc.fs.UDIR;
import mpc.fs.fd.EFT;
import mpc.rfl.RFL;
import mpc.str.sym.FD_ICON;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Lb;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IReRender;
import zk_os.sec.SecMan;
import zk_page.ZKCFinder;
import zk_page.ZKCFinderExt;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class DirView extends FdView implements IReRender {

	public static DirView findFirst() {
		return ZKCFinderExt.findFirst_InPage(DirView.class, true);
	}

	@SneakyThrows
	@Override
	public Component newCom() {
		DirView newInst = RFL.inst_(getClass(), false, new Class[]{Path.class, boolean.class}, new Object[]{path(), open});
		newInst.openedAll(openedAll).filterFiles(filterFiles).applierDirMenu(applierDirMenu);
		return newInst;
	}

	protected final boolean open;

	protected boolean openedAll = false;
	protected Predicate<Path> filterFiles = null;

	public DirView filterFiles(Predicate<Path> filterFiles) {
		this.filterFiles = filterFiles;
		return this;
	}

	public DirView openedAll(boolean... openedAll) {
		this.openedAll = ARG.isDefNotEqFalse(openedAll);
		return this;
	}

	public DirView(Path dir) {
		this(dir, true);
	}

	public DirView(Path dir, boolean open) {
		this(dir, 0, open);
	}

	public DirView(Path dir, int level, boolean open) {
		super(dir, level);
		this.open = open;
	}

	@Override
	protected void init() {
		super.init();

		Path dirPath = path();

		Lb dirRoot = appendLb(filePfx(level) + FD_ICON.toNameDirQk(dirPath));

		if (applierDirMenu != null && SecMan.isOwnerOrAdmin()) {
			Menupopup0 menupopup = Menupopup0.createMenupopup(this, dirRoot, null);
			applierDirMenu.apply(menupopup, dirPath);
		}

		appendCbFdView();

		dirRoot.onDBLCLICK((SerializableEventListener<Event>) event -> {//DIR dbl-click
			DirView divViewParent = DirView.this;
			DirView newSwap = (DirView) new DirView(dirPath, level, !open) {
				@Override
				protected void onClickFdView(Event event, FdView fdView) {
					DirView.this.onHookClickParentFdView(event, fdView);
				}
			}.openedAll(openedAll).filterFiles(filterFiles).applierDirMenu(applierDirMenu).applierFileMenu(applierFileMenu);
			divViewParent.clear();
			divViewParent.appendChild(newSwap);
		});

		if (!open) {
			return;
		}

		int nextLevel = this.level + 1;

		List<Path> dirs = UDIR.ls(dirPath, EFT.DIR, Collections.EMPTY_LIST);
		List<Path> files = UDIR.ls(dirPath, EFT.FILE, Collections.EMPTY_LIST);

		for (Path dir : dirs) {
			DirView dirViewChild = (DirView) new DirView(dir, nextLevel, openedAll) {
				@Override
				protected void onClickFdView(Event event, FdView fdView) {
					onHookClickParentFdView(event, fdView);
				}
			}.openedAll(openedAll).filterFiles(filterFiles).applierDirMenu(applierDirMenu).applierFileMenu(applierFileMenu);
			appendChild(dirViewChild);
		}
		if (filterFiles != null) {
			files = STREAM.filterToAll(files, filterFiles);
		}
		for (Path file : files) {
			FdView fileViewChild = new FileView(file, nextLevel) {
				@Override
				protected void onClickFdView(Event event, FdView fdView) {
					onHookClickParentFdView(event, fdView);
				}
			}.applierFileMenu(applierFileMenu);
			appendChild(fileViewChild);
		}
	}

	private void onHookClickParentFdView(Event event, FdView fdView) {
		onClickFdView(event, fdView);
	}

	public <T extends FdView> List<T> getFdViews(EFT eft) {
		Class<? extends FdView> fdVieClass = eft == EFT.FILE ? FileView.class : DirView.class;
		List all = ZKCFinder.find_inChilds(this, fdVieClass, false, ARR.EMPTY_LIST);
		return all;
	}


}
