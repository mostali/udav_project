package zk_notes.apiv1._ati;

import mpe.core.U;
import mpc.exception.CleanDataResponseException;
import mpu.IT;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpe.call_msg.core.NodeID;
import udav_net.apis.zznote.NoteApi;
import zk_page.core.PagePathInfoWithQuery;

import java.nio.file.Path;
import java.util.Optional;

public class TreeRestCall {

	final PagePathInfoWithQuery curPPI;

	public static TreeRestCall ofPPI(PagePathInfoWithQuery curPPI) {
		return new TreeRestCall(curPPI);
	}

	public TreeRestCall(PagePathInfoWithQuery curPPI) {
		this.curPPI = curPPI;
		this.nodeID = null;
		this.oper = null;
	}

	final NodeID nodeID;
	final Pare3<String, String, String> oper;

	public TreeRestCall(NodeID nodeID, Pare3<String, String, String> oper) {
		this.nodeID = nodeID;
		this.oper = oper;
		IT.NE(oper.key(), "oper");
		IT.NE(oper.val(), "val");

		curPPI = null;
	}

	public Optional<TreeOper> of(int level) {
		Path path = path(level, null);
		if (path == null) {
			return null;
		}
		switch (path.toString()) {
			case "*":
				return Optional.of(new StarOperation(this, level));
			case "!":
				return Optional.of(new ClaimOperation(this, level));
			default:
				return Optional.empty();
		}
	}

	public Pare<Integer, String> apply() {

		for (int level = 0; level <= 2; level++) {
			Optional<TreeOper> partLevel = of(level);
			if (partLevel == null) {
				return new StarOperation(this, level).applyRoot();
			} else if (partLevel.isPresent()) {
				return partLevel.get().apply();
			}
		}

		Optional<TreeOper> partLevel = of(3);
		if (partLevel == null) {
			return new StarOperation(this, 2).apply();
		}

		return Pare.of(TreeOper.CODE_WRONG_LOGIC, "bad");

	}

	public String getSd3() {
		return curPPI != null ? curPPI.planeRq() : nodeID.planeRq();
	}

	private String getPagename() {
		return curPPI != null ? curPPI.pathStr(1) : nodeID.pageRq();
	}

	private String getItemname() {
		return curPPI != null ? curPPI.pathStr(2) : nodeID.itemRq();
	}

	public Path path(int level, Path... defRq) {
		return curPPI.path(1 + level, defRq);
	}

	private String getOper() {
		return curPPI != null ? curPPI.pathStr(3) : oper.key();
	}

	private String getVal() {
		return curPPI != null ? curPPI.queryUrl().getFirstAsStr(NoteApi.PK_V, null) : oper.ext();
	}

	private String getKey() {
		if (curPPI == null) {
			return oper.val();
		}
		String key = curPPI.queryUrl().getFirstAsStr(NoteApi.PK_K, null);
		if (key == null) {
			throw CleanDataResponseException.C400("set key value arg <k>");
		} else if (U.__NULL__.equals(key)) {
			key = null;
		}
		return key;
	}

}
