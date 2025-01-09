package zk_page.node;

import mpc.fs.path.IPath;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.pare.Pare;
import zk_page.node_state.FileState;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class FileSiteEntity<FORMSTATE extends FileState> extends SitePersEntity<FORMSTATE> implements IPath {

	public FileSiteEntity(Pare sdn) {
		super(sdn);
	}

	public FileSiteEntity(Path siteDir, Pare sdn) {
		super(siteDir, sdn);
	}

	public Long formSize(Long... defRq) {
		return X.sizeOfFile(getPathFc(), defRq);
	}

	public Path getPathFc() {
		return state().pathFc();
	}


	public Path fPath() {
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
		String head = getClass().getSimpleName() + SYMJ.ARROW_RIGHT_SPEC + sdn().key() + "/" + sdn().val() + SYMJ.ARROW_RIGHT_SPEC + fPath();
		return head;
	}

}
