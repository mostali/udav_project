package mpe.call_msg.core;

import mpc.exception.IErrorsCollector;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpu.IT;
import mpu.X;
import mpu.pare.Pare;
import mpu.str.SPLIT;
import mpu.str.STR;

import java.util.LinkedList;
import java.util.List;

public class NodeID implements IErrorsCollector {

	public static final String KEY = "nodeID";
	public static final String ANY_INDEX_ALIAS = ".index";
	public static final String ITEM_INDEX_ALIAS = ".index";
	public static final String PAGE_INDEX_ALIAS = ".index";
	public static final String PLANE_INDEX_ALIAS = ".index";

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
	private final String _plane;

	public final State state;

	private List<Throwable> _errors;

	public static String wrapSd3(String sd3) {
		return X.empty(sd3) ? PLANE_INDEX_ALIAS : sd3;
	}

	public static String unwrapSd3(String sd3) {
		return PLANE_INDEX_ALIAS.equals(sd3) ? "" : sd3;
	}

	public static String wrapPage(String pagename) {
		return X.empty(pagename) ? PAGE_INDEX_ALIAS : pagename;
	}

	public static String unwrapPage(String pagename) {
		return PAGE_INDEX_ALIAS.equals(pagename) ? "" : pagename;
	}

	public static String unwrapIndexPath(String path) {
//		return path == null || path.length == 0 || path[0] == null ? "" : (path[0].equals(PAGE_INDEX_ALIAS) ? "" : path[0]);
		return unwrapPage(path);
	}

	public static boolean isPlaneAliasIndex(String sd3) {
		return PLANE_INDEX_ALIAS.equals(sd3);
	}

	public static boolean isPlaneAliasIndexOrEmpty(String name) {
		return X.empty(name) || isPlaneAliasIndex(name);
	}

	public static Pare<String, String> unwrapSdn(Pare<String, String> sdn) {
		return Pare.of(unwrapSd3(sdn.key()), unwrapPage(sdn.val()));
	}

	public String sd3() {
		return _plane;
	}

	public String sd3Rq() {
		String sd3 = sd3();
		if (sd3 == null) {
			throw new RequiredRuntimeException("NodeID without sd3");
		}
		return "".equals(sd3) ? PLANE_INDEX_ALIAS : sd3;
	}

	public String page() {
		return _page;
	}

	public String pageRq() {
		String pn = page();
		if (pn == null) {
			throw new RequiredRuntimeException("NodeID without page");
		}
		return "".equals(pn) ? PAGE_INDEX_ALIAS : pn;
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

//	public ItemPath toItemPath() {
//		switch (state) {
//			case FULL:
//				return ItemPath.of(sdn(), item());
//			default:
//				throw new WhatIsTypeException("Illegal state for prepare ItemPath:" + state);
//		}
//	}

	public NodeID newNodeID(String itemName) {
		return (NodeID) new NodeID(sdn(), itemName).throwIsErr();
	}

	public enum State {
		EMPTY, SINGLE, PAGED, FULL, BIG
	}

//	public NodeID(String plane, String page, String item) {
//		this._plane = plane;
//		this._page = page;
//		this._item = item;
//	}

	public NodeID(Pare<String, String> sdn, String nodeName) {
		if (X.blank(nodeName)) {
			addError("Empty node name");
			this.state = State.EMPTY;
			this.nodeIdStr = null;
			this._item = null;
			_plane = sdn.key();
			_page = sdn.val();

			return;
		}
		this._item = nodeName = nodeName.trim();
//		this.sdn = sdn = Pare.of(sdn.key().trim(), sdn.val().trim());
		this.nodeIdStr = sdn.key() + DEL + sdn.val() + DEL + nodeName;

		this.state = State.FULL;
		_plane = sdn.key();
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
			_plane = null;
			_item = null;
//			sdn = null;
			return;
		}
		String[] paths = SPLIT.argsByPreserve(nodeIdStr, "/");
		paths = STR.trimAll(paths);
		switch (paths.length) {
			case 1:
				state = State.SINGLE;
				_plane = null;
				_page = null;
				_item = nodeIdStr;
				break;
			case 2:
				state = State.PAGED;
				_plane = null;
				_page = wrapSd3(paths[0]);
				_item = paths[1];
				break;
			case 3:
				state = State.FULL;
				_plane = wrapSd3(paths[0]);
				_page = wrapSd3(paths[1]);
				_item = X.empty(paths[2]) ? null : paths[2];
				break;
			default:
				addError("too many paths '%s'", nodeIdStr);
				state = State.BIG;
				_plane = null;
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

	public static NodeID of(Pare<String, String> sdn, String item) {
		return new NodeID(sdn, item);
	}

	public static NodeID ofQk(String nodeID) {
		return new NodeID(nodeID);
	}

	@Override
	public String toString() {
		return toString0();
	}

	public String toString0() {
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
