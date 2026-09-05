package zk_os.walkers;

import lombok.RequiredArgsConstructor;
import mpu.core.ARG;
import mpu.pare.Pare;
import zk_os.coms.AFC;
import zk_os.core.Sdn;
import zk_os.sec.UO;
import zk_notes.node.NodeDir;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@RequiredArgsConstructor
public abstract class NoteWalker {

	public final Pare<String, String> sdn;

	public static List<NodeDir> toList(Sdn sdn, Predicate<NodeDir>... filter) {
		Predicate<NodeDir> predicate = ARG.toDefOrNull(filter);
		List<NodeDir> nodes = new LinkedList();
		new NoteWalker(sdn) {
			@Override
			protected Boolean walkForm(NodeDir nodeDir) {
				if (predicate == null || predicate.test(nodeDir)) {
					nodes.add(nodeDir);
				}
				return true;
			}
		}.doWalk();
		return nodes;
	}

	protected abstract Boolean walkForm(NodeDir nodeDir);

	public NoteWalker doWalk() {

		Set<Path> formsDirs = AFC.FORMS.DIR_FORMS_LS_CLEAN(sdn);

		for (Path formDir : formsDirs) {
			NodeDir nodeDir = NodeDir.ofDir(sdn, formDir);
			if (!UO.isAllowed_VIEW(nodeDir, false)) {
				continue;
			}
			if (!walkForm(nodeDir)) {
				break;
			}
		}

		return this;

	}
}
