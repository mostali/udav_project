package zk_notes.coms;

import mpc.fs.path.IPath;
import mpe.str.CN;
import mpu.core.ARRi;
import zk_com.base.Tbx;
import zk_com.base.Tbxmy;
import zk_com.core.IZWin;
import zk_page.ZKCFinderExt;
import zk_page.ZKS;

import java.nio.file.Path;
import java.util.List;

public class NoteTbxmy extends Tbxmy implements IZWin, IPath {

	private final String nodeName;

	public String fName() {
		return toPath().getFileName().toString();
	}

	//
	//

	@Override
	public String getHeightForm() {
		return getFormState().get(CN.HEIGHT, null);
	}

	@Override
	public String getComName() {
		return nodeName;
	}

	@Override
	public String getFormName() {
		return nodeName;
	}

	@Override
	public Path toPath() {
		return ARRi.first(getPaths0());
	}

	public NoteTbxmy(String nodeName, Path path, Tbx.DIMS... dims) {
		super(path, dims);
		this.nodeName = nodeName;
	}

	public NoteTbxmy(String nodeName, Path path, int size, Tbx.DIMS... dims) {
		super(path, size, dims);
		this.nodeName = nodeName;
	}

	public NoteTbxmy(String nodeName, List<Path> paths, Tbx.DIMS... dims) {
		super(paths, dims);
		this.nodeName = nodeName;
	}

	public static NoteTbxmy toggleDnone(NoteTbxmy... defRq) {
		return ZKS.toggleDnoneFirst(NoteTbxmy.class, true, defRq);
	}

	public static NoteTbxmy findFirst(NoteTbxmy... defRq) {
		return ZKCFinderExt.findFirst_inPage0(NoteTbxmy.class, true, defRq);
	}

	@Override
	public boolean equals(Object o) {
		return this == o ? true : (o == null ? false : o.equals(fParent()));
	}

}
