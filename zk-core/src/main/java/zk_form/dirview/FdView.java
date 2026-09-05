package zk_form.dirview;

import lombok.Getter;
import lombok.Setter;
import mpc.exception.WhatIsTypeException;
import mpc.fs.fd.EFT;
import mpu.str.STR;
import mpc.str.sym.FD_ICON;
import mpu.func.FunctionV2;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Cb;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FdView extends Div0 {
	final int level;
	private final @Getter String fd;

	private transient Path path;

	protected FunctionV2<Menupopup0, Path> applierDirMenu = null;

	public FdView applierDirMenu(FunctionV2<Menupopup0, Path> applierDirMenu) {
		this.applierDirMenu = applierDirMenu;
		return this;
	}

	protected FunctionV2<Menupopup0, Path> applierFileMenu = null;

	public FdView applierFileMenu(FunctionV2<Menupopup0, Path> applierFileMenu) {
		this.applierFileMenu = applierFileMenu;
		return this;
	}

	protected Cb checkbox;
	@Getter
	@Setter
	private Map<String, Boolean> checkState = null;

	public boolean isRootLevel() {
		return level == 0;
	}


	@NotNull
	private static String spaceDel() {
		return STR.repeat(".", 2);
	}

	public Path path() {
		return path == null ? (path = Paths.get(fd)) : path;
	}

	public String name() {
		return path().getFileName().toString();
	}

	public EFT eft(EFT... defRq) {
		return EFT.of(path(), defRq);
	}



	protected void appendCbFdView() {
		checkbox = (Cb) new Cb() {
			@Override
			public void onDefaultActionEvent(Event event) {
				onClickFdView(event, FdView.this);
			}
		}.defaultAction();
		appendChild(checkbox);
	}

	public static String filePfx(int level) {
		if (level == 0) {
			return "";
		}
		String pfx = "";
		do {
			pfx += "|" + spaceDel();
		} while (--level > 0);
		return pfx;
	}

	public FdView(Path dir, int level) {
		this.fd = dir.toString();
		this.path = dir;
		this.level = level;
	}

	@Override
	protected void init() {
		super.init();
		if (this instanceof FileView || this instanceof DirView) {
			// already append
		} else {
			appendLbBlock(FD_ICON.FD.toNameFd(name()));
		}

		onCLICK((SerializableEventListener<Event>) event -> {
			onClickFdView(event, this);
		});
	}

	protected void onClickFdView(Event event, FdView fdView) {
		if (checkState == null) {
			return;
		}
		Boolean checked = fdView.isChecked();
		if (checked == null) {
			return;
		}
		EFT eft = fdView.eft(null);
		if (eft == null) {
			L.warn("FilType is null : " + fdView.path());
		} else {
			switch (eft) {
				case FILE:
					checkState.put(fdView.path().toString(), checked);
					break;
				case DIR:
					DirView dirView = (DirView) fdView;
					List<FileView> files = dirView.getFdViews(EFT.FILE);
					files.forEach(fileView -> {
						fileView.checkbox().setChecked(checked);
						onClickFdView(event, fileView);
					});
					break;
				default:
					throw new WhatIsTypeException(eft);
			}
		}
	}

	protected Cb checkbox() {
		return checkbox;
	}

	protected Boolean isChecked() {
		Cb cb = checkbox();
		if (cb == null) {
			return null;
		}
		return cb.isChecked();
	}

	public FdView checkableFd() {
		checkState = new ConcurrentHashMap<>();
		return this;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + eft() + ")" + fd;
	}
}
