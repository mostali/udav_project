package zk_os.walkers;

import lombok.RequiredArgsConstructor;
import mpu.pare.Pare;
import zk_os.AFC;
import zk_os.sec.SecMan;
import zk_notes.node.NodeDir;

import java.nio.file.Path;
import java.util.Set;

@RequiredArgsConstructor
public abstract class FormsWalker {

	public final Pare<String, String> sdn;

	protected abstract Boolean walkForm(NodeDir nodeDir);

	public FormsWalker doWalk() {

		Set<Path> formsDirs = AFC.FORMS.DIR_FORMS_LS_CLEAN(sdn);

		for (Path formDir : formsDirs) {
			NodeDir nodeDir = NodeDir.ofDir(sdn, formDir);
			if (!SecMan.isAllowedView(nodeDir, false)) {
				continue;
			}
			if (!walkForm(nodeDir)) {
				break;
			}
		}

		return this;

	}
}
