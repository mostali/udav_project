package udav_net.apis.zznote;

import mpc.exception.IErrorsCollector;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;
import mpu.str.SPLIT;
import mpu.str.STR;

import java.util.*;

public class NodeID implements IErrorsCollector {

	public static final String KEY = "nodeID";

	public static void main(String[] args) {
//		NodeID.of("/page")
		testJUnit();
	}

	public static void testJUnit() {
		testSingle("singleNodeName", State.SINGLE);
		testSingle("/nodeName", State.PAGED);
		testSingle("//fullNodeName", State.FULL);
		X.p("-------------");
		testSingle("/pageName/", State.FULL);
		testSingle("pageName/nodeName", State.PAGED);
		testSingle("sd3/pageName/singleNodeName", State.FULL);
		testSingle("sd3//singleNodeName", State.FULL); // Need .index?

		X.p("-------------");
		testSingle("sd3/pageName/singleNodeName/", State.BIG);
		testSingle("/sd3/pageName/singleNodeName/", State.BIG);
		testSingle("//sd3/pageName/singleNodeName", State.BIG);
		X.p("-------------");
		testSingle(" ", State.EMPTY);
	}

	private static void testSingle(String str, State state) {
		NodeID nodeID = NodeID.ofQk(str);
		System.out.print("CHECK >>> " + (nodeID.isValid() ? "OK" : "ERROR") + "::*" + X.sizeOf0(nodeID.getErrors()) + "::" + nodeID.state + "[" + str + "] >>> " + nodeID);
		IT.state(nodeID.state == state);
		System.out.println(" <<< " + " = item:" + nodeID._item + ", page:" + nodeID.page() + ", sd3:" + nodeID.sd3() + " ");
	}

	public static final String DEL = "/";

	private final String nodeIdStr;
	private final String _item;
	private final String _page;
	private final String _sd3;

	public final State state;

	private List<Throwable> _errors;

	public String sd3() {
		return _sd3;
	}

	public String sd3Rq() {
		String sd3 = sd3();
		if (sd3 == null) {
			throw new RequiredRuntimeException("NodeID without sd3");
		}
		return "".equals(sd3) ? ItemPath.SD3_INDEX_ALIAS : sd3;
	}

	public String page() {
		return _page;
	}

	public String pageRq() {
		String pn = page();
		if (pn == null) {
			throw new RequiredRuntimeException("NodeID without page");
		}
		return "".equals(pn) ? ItemPath.PAGE_INDEX_ALIAS : pn;
	}

	public String item() {
		return _item;
	}

	public String itemRq() {
		return IT.NE(_item);
	}

	public Pare<String, String> sdn() {
		switch (state) {
			case FULL:
				return Pare.of(sd3Rq(), pageRq());
			default:
				throw new WhatIsTypeException("Illegal state for prepare sdn:" + state);
		}
	}

	public ItemPath toItemPath() {
		switch (state) {
			case FULL:
				return ItemPath.of(sdn(), item());
			default:
				throw new WhatIsTypeException("Illegal state for prepare ItemPath:" + state);
		}
	}

	public NodeID newNodeID(String itemName) {
		return (NodeID) new NodeID(itemName, sdn()).throwIsErr();
	}

	public enum State {
		EMPTY, SINGLE, PAGED, FULL, BIG
	}


	public NodeID(String nodeName, Pare<String, String> sdn) {
		if (X.blank(nodeName)) {
			addError("Empty node name");
			this.state = State.EMPTY;
			this.nodeIdStr = null;
			this._item = null;
			_sd3 = sdn.key();
			_page = sdn.val();

			return;
		}
		this._item = nodeName = nodeName.trim();
//		this.sdn = sdn = Pare.of(sdn.key().trim(), sdn.val().trim());
		this.nodeIdStr = sdn.key() + DEL + sdn.val() + DEL + nodeName;

		this.state = State.FULL;
		_sd3 = sdn.key();
		_page = sdn.val();

//		if (X.blank(sdn.val())) {
//			IT.isEmpty(sdn.key(), "set page (because sd3 is present)");
//			this.state = State.SINGLE;
//			_sd3 = null;
//			_page = null;
//		} else if (X.blank(sdn.key())) {
//			this.state = State.PAGED;
//			_sd3 = null;
//			_page = sdn.val();
//		} else {
//			this.state = State.FULL;
//			_sd3 = sdn.key();
//			_page = sdn.val();
//		}
	}

	public NodeID(String nodeIdStr) {
		this.nodeIdStr = nodeIdStr;
		if (X.blank(nodeIdStr)) {
			addError("Empty node name");
			state = State.EMPTY;
			_page = null;
			_sd3 = null;
			_item = null;
//			sdn = null;
			return;
		}
		String[] paths = SPLIT.argsByPreserve(nodeIdStr, "/");
		paths = STR.trimAll(paths);
		switch (paths.length) {
			case 1:
				state = State.SINGLE;
				_sd3 = null;
				_page = null;
				_item = nodeIdStr;
				break;
			case 2:
				state = State.PAGED;
				_sd3 = null;
				_page = ItemPath.wrapSd3(paths[0]);
				_item = paths[1];
				break;
			case 3:
				state = State.FULL;
				_sd3 = ItemPath.wrapSd3(paths[0]);
				_page = ItemPath.wrapSd3(paths[1]);
				_item = X.empty(paths[2]) ? null : paths[2];
				break;
			default:
				addError("too many paths '%s'", nodeIdStr);
				state = State.BIG;
				_sd3 = null;
				_page = null;
				_item = null;
				break;
		}

	}

	@Override
	public List<Throwable> getErrors() {
		return _errors;
	}

	@Override
	public void addError(Throwable... ex) {
		if (_errors == null) {
			_errors = new LinkedList<>();
		}
		for (Throwable e : ex) {
			_errors.add(e);
		}
	}

	public static NodeID of(String nodeID) {
		return (NodeID) ofQk(nodeID).throwIsErr();
	}

	public static NodeID ofQk(String nodeID) {
		return new NodeID(nodeID);
	}

	@Override
	public String toString() {
		return string();
	}

	public String string() {
		return nodeIdStr;
	}

	public String toStringLog() {
		return "NodeID{" +
				"nodeIdStr='" + nodeIdStr + '\'' +
				", nodeName='" + _item + '\'' +
				", state=" + state +
				", _errors=" + _errors +
				'}';
	}


}
