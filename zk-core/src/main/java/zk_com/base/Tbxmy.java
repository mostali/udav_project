package zk_com.base;

import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARGn;
import mpu.core.ARRi;
import mpu.core.RW;
import mpu.str.UST;
import mpu.str.USToken0;
import org.zkoss.zk.ui.Component;
import zk_com.base_ctr.Div0;
import zk_os.coms.AFCC;
import zk_os.sec.SecMan;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Tbxmy extends Div0 {

	int size = 2;

	private boolean saveOnShortCut;

	public Tbxmy saveOnShortCut(boolean... saveOnShortCut) {
		this.saveOnShortCut = ARG.isDefNotEqFalse(saveOnShortCut);
		return this;
	}

	private List<String> paths;
	private transient List<Path> paths0;

	public List<Path> getPaths0() {
		return paths0 != null ? paths0 : (paths0 = paths.stream().map(Paths::get).collect(Collectors.toList()));
	}

	public Tbxmy(Path path, Tbx.DIMS... dims) {
		this(AFCC.getAllExistParts(path), dims);
	}

	public Tbxmy(Path path, int size, Tbx.DIMS... dims) {
		this(AFCC.getAllPathParts(path, size), dims);
	}

	public Tbxmy(List<Path> paths, Tbx.DIMS... dims) {
		super();

		this.size = paths.size();

		this.paths = paths.stream().map(Path::toString).collect(Collectors.toList());
		this.paths0 = paths;

		if (ARG.isDef(dims)) {
			Tbx.DIMS def = ARG.toDef(dims);
			IT.state(def != Tbx.DIMS.BYCONTENT, "unsupported");
			def.init(this, null);
		}
	}

	protected String readFileContent(int... index) {
		return RW.readString(ARRi.first(getPaths0(), ARGn.toDefOr(0, index)), "");
	}

//		public Tbx onCheckFileExist() {
//			if (checkFileExist) {
//				IT.isFileExist(path);
//				IT.isFileExist(path2);
//			}
//			return this;
//		}

	public boolean isPersist() {
		return X.notEmpty(paths);
	}


	public static Tbxmy two(String data1, String data2) {
		Tbxm yPan1 = (Tbxm) new Tbxm().placeholder("set data1").width(50.0);
		Tbxm yPan2 = (Tbxm) new Tbxm().placeholder("set data2").width(50.0);
		if (data1 != null) {
			yPan1.setValue(data1);
		}
		if (yPan2 != null) {
			yPan2.setValue(data2);
		}
		return new Tbxmy(yPan1, yPan2);
	}

	public Tbxmy(Component... coms) {
		super(coms);
	}


	public String getHeightForm() {
		return getHeight();
	}

	@Override
	protected void init() {
		super.init();

//		int diff = size;

		String heightForm = getHeightForm();

		Integer tbxmHeight = 200;
		if (heightForm != null && heightForm.endsWith("px")) {
			String h = USToken0.firstNum(heightForm, null);
			if (h != null) {
				Integer px = UST.INT(h);

				tbxmHeight = px / size - SameRows.correctAutoTbxHeight(size);
			}
		}

		List<Path> paths0 = getPaths0();

		for (int i = 0; i < paths0.size(); i++) {
			Path path = paths0.get(i);
			Tbxm tbxm = new Tbxm(path);
			tbxm.width(100.0);
			tbxm.height(tbxmHeight);
			tbxm.placeholder("data" + (i == 0 ? "" : i));
			tbxm.block();
			appendChild(tbxm);
//			diff--;
		}

//		Path first = ARRi.first(paths0);
//		int ctr = 1;
//		while (diff-- > 0) {
//			Path nextPart = first.getParent().resolve(FileState.partName(first.getFileName().toString(), ctr++));
//			paths0.add(nextPart);
//			Tbxm tbxm = new Tbxm(nextPart);
//			tbxm.width(100.0);
//			tbxm.height(tbxmHeight);
//			tbxm.block();
//			appendChild(tbxm);
//		}

		boolean isEditorAdminOwner = SecMan.isAllowedEditPlane();

		if (isEditorAdminOwner) {
			getComsAsTbxm().stream().forEach(t -> {
				if (saveOnShortCut) {
					t.saveOnShortCut();
				}
			});
		}
	}

	public static class SameRows {

		public static int correctAutoTbxHeight(int size) {
			if (size < 2) {
				return 0;
			} else if (size < 3) {
				return 36;
			} else if (size < 4) {
				return 26;
			} else if (size < 7) {
				return 16;
			} else if (size < 12) {
				return 10;
			} else if (size < 20) {
				return 6;
			}
			return 3;
		}
	}

	public List<Tbxm> getComsAsTbxm() {
		return getComs().stream().map(i -> i instanceof Tbxm ? (Tbxm) i : null).filter(i -> i != null).collect(Collectors.toList());
	}
}
