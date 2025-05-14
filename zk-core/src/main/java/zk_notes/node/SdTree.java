package zk_notes.node;

import mpu.pare.Pare;
import zk_notes.node_state.TreeState;

@Deprecated
public class SdTree extends SitePersEntity<TreeState> {

	//
	//
	public static SdTree of(String sd3) {
		return of(Pare.of(sd3));
	}

	public static SdTree of(Pare sdn) {
		return new SdTree(sdn);
	}

	//
	//
	@Override
	public TreeState newState() {
		return new TreeState(sdn(), true);
	}

	//
	//
	public SdTree(Pare sdn) {
		super(sdn);
	}

}
