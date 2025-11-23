package zk_notes.node;

import mpc.fs.UFS;
import mpc.fs.path.IPath;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.pare.Pare;
import zk_notes.node_state.FileState;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public abstract class FileSiteEntity<FORMSTATE extends FileState> extends SitePersEntity<FORMSTATE> implements IPath {

	public FileSiteEntity(Pare sdn) {
		super(sdn);
	}

	public FileSiteEntity(Path siteDir, Pare sdn) {
		super(siteDir, sdn);
	}

	public Long formSize(Long... defRq) {
		return X.sizeOf(getPathFc(), defRq);
	}

	public List<Path> getPathFcParentLs() {
		return UFS.ls(getPathFc().getParent());
	}

	public Path getPathFc() {
		return state().pathFc();
	}

	public Path toPath() {
		if (this.siteDirPath0 != null) {
			return siteDirPath0;
		} else if (siteDirPath != null) {
			return siteDirPath0 = Paths.get(siteDirPath);
		}
		siteDirPath0 = state().pathPropsNodeDir();
		siteDirPath = siteDirPath0.toString();
		return siteDirPath0;
	}

	@Override
	public String toString() {
		String head = SYMJ.ARROW_RIGHT_SPEC + sdnPare().key() + "/" + sdnPare().val() + SYMJ.ARROW_RIGHT_SPEC + toPath();
		return head;
	}

	public boolean existNode(boolean checkExistCom) {
		return checkExistCom ? UFS.existFile(getPathFc()) : UFS.existDir(toPath());
	}
}
