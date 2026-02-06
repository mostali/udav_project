package zklogapp.merge;

import lombok.SneakyThrows;
import mpc.fs.UFS;
import mpc.fs.ext.EXT;
import mpu.IT;
import mpu.X;
import mpc.fs.UF;
import mpc.fs.ext.GEXT;
import mpc.rfl.RFL;
import mpc.ui.ColorTheme;
import mpu.func.FunctionV2;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Window;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IZComFadeIO;
import zk_form.dirview.DirView;
import zk_form.dirview.FdView;
import zk_com.core.IReRender;
import zk_form.notify.ZKI;
import zk_page.ZKC;
import zk_page.ZKS;
import zklogapp.ALM;
import zklogapp.filter.HeadLogFilter;
import zklogapp.AppLogProps;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class LogDirView extends Div0 implements IReRender {

	public static LogDirView removeMeFirst(LogDirView... defRq) {
//		try{
			return ZKC.removeMeFirst(LogDirView.class, true, defRq);
//		}
	}

	public static void openSingly(String pathDir) {
		UFS.MKDIR.mkdirIfNotExist(Paths.get(pathDir));
//		IT.isDirExist(pathDir);
		Window firstWindow = ZKC.getFirstWindow();
//		LogDirView.removeMeFirst(null);
		firstWindow.appendChild(new LogDirView(pathDir));
	}

	@Override
	public Component newCom() {
		return new LogDirView(IT.NN(newPath, "set new path before build new com"));
	}

	private final String path;

	private String newPath;

	public LogDirView setNewPath(String newPath) {
		this.newPath = newPath;
		return this;
	}

	public LogDirView(String path) {
		super();
		this.path = path;

		LogDirView.removeMeFirst(null);
	}

	private HeadLogFilter headLogFilter;
	private BtMerge btMerge;

	@Override
	protected void init() {
		super.init();

		BtMerge.L = ZKI.ZLOG;

		btMerge = new BtMerge(UF.normDir(path, "merged"));

		appendChild(headLogFilter = new HeadLogFilter(btMerge, path) {
			@Override
			protected void onChangeNewPath(String newPath) {
				LogDirView parent = (LogDirView) getParent();
				parent.setNewPath(newPath);
				parent.rerender();

			}
		});

		{//configure cbOne
			//			headLogFilter.getCbOne().setChecked(false);
			//			headLogFilter.getCbOne().title("Collapse multiline block in result merged file");
			headLogFilter.getCbOne().setVisible(false);
		}

		Predicate<Path> pathArcPredicate = (Path p) -> GEXT.ARC.isNotPath(p);
		Predicate<Path> pathCsvPredicate = (Path p) -> !EXT.CSV.has(p);
		Predicate<Path> filterFiles = AppLogProps
				.APR_DIRVIEW_HIDE_ARC.getValueOrDefault(false) ? pathArcPredicate.and(pathCsvPredicate) : null;
		Boolean openedAll = AppLogProps.APR_DIR_VIEW_ALWAYS_OPENED.getValueOrDefault(false);
		FunctionV2<Menupopup0, Path> apllierDirMenu = (menupopup, dir) -> ALM.applyLogDirWithUtils(menupopup, dir);
		FunctionV2<Menupopup0, Path> apllierFileMenu = (menupopup, file) -> ALM.applyLogFileWithUtils(menupopup, file);
		appendChild(new MergeableDirView(Paths.get(path), true).setBtMerge(btMerge).filterFiles(filterFiles).openedAll(openedAll).applierDirMenu(apllierDirMenu).applierFileMenu(apllierFileMenu));
	}

	public static class MergeableDirView extends DirView implements IZComFadeIO {
		@SneakyThrows
		@Override
		public Component newCom() {
			LogDirView.MergeableDirView newInst = RFL.inst_(LogDirView.MergeableDirView.class, false, new Class[]{Path.class, boolean.class}, new Object[]{path(), open});
			newInst.setBtMerge(btMerge).openedAll(openedAll).filterFiles(filterFiles).applierDirMenu(applierDirMenu).applierFileMenu(applierFileMenu);
			return newInst;
		}

		@Override
		protected void init() {
			addEffectIn(this);
			super.init();
		}

		@Override
		public boolean isRootLevel() {
			return true;
		}

		public MergeableDirView(Path dir, boolean opened) {
			super(dir, opened);
			checkableFd();
			ZKS.BORDER_GRAY(this);
			ZKS.PADDING(this, "20px 5px");
			ZKS.BGCOLOR(this, ColorTheme.WHITE[1]);
		}

		BtMerge btMerge;

		public MergeableDirView setBtMerge(BtMerge btMerge) {
			this.btMerge = btMerge;
			return this;
		}

		@Override
		protected void onClickFdView(Event event, FdView fdView) {
			super.onClickFdView(event, fdView);

			List<String> mergeFiles = btMerge.getMergeFiles();
			mergeFiles.clear();//reset old files

			Map<String, Boolean> checkState = getCheckState();

			for (Map.Entry<String, Boolean> checkableEntry : checkState.entrySet()) {
				if (checkableEntry.getValue() != null && checkableEntry.getValue()) {
					mergeFiles.add(checkableEntry.getKey());
				}
			}

			ZKI.log("Choiced files to merge" + ":" + UF.fn(mergeFiles));

			btMerge.initDisableProperty();

			boolean isUseAllRootDir = X.sizeOf(mergeFiles) == 1 && path().toString().equals(mergeFiles.get(0));
			btMerge.initUseAllRootDir(isUseAllRootDir);

		}
	}

}
