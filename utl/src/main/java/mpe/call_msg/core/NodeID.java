package mpe.call_msg.core;

import mpc.exception.IErrorsCollector;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;
import mpu.str.SPLIT;
import mpu.str.STR;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class NodeID implements IErrorsCollector, INodeID {

	public static final String KEY = "nodeID";
	public static final String NODE_PARENT_DIR = ".forms";
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
		testSingle("spaceName//", State.FULL);
		testSingle("spaceName/pagename/", State.FULL);
		testSingle("planeName//singleNodeName", State.FULL); // Need .index?
		X.p("-------------");

		testSingle("/pageName/", State.FULL);
		testSingle("pageName/nodeName", State.PAGED);
		testSingle("planeName/pageName/singleNodeName", State.FULL);

		X.p("-------------");
		testSingle("planeName/pageName/singleNodeName/", State.BIG);
		testSingle("/planeName/pageName/singleNodeName/", State.BIG);
		testSingle("//planeName/pageName/singleNodeName", State.BIG);
		X.p("-------------");
		testSingle(" ", State.EMPTY);
	}

	private static void testSingle(String str, State state) {
		NodeID nodeID = NodeID.ofQk(str);
		System.out.print("CHECK >>> " + (nodeID.isValid() ? "OK" : "ERROR") + "::*" + X.sizeOf0(nodeID.getErrors()) + "::" + nodeID.state + "[" + str + "] >>> " + nodeID);
		IT.state(nodeID.state == state);
		System.out.println(" <<< " + " = PLANE:" + nodeID.plane() + " | PAGE:" + nodeID.page() + " | ITEM:" + nodeID.item());
	}

	public static final String DEL = "/";

	private final String nodeIdStr;
	private final String _item;
	private final String _page;
	private final String _plane;

	public final State state;

	private List<Throwable> _errors;

	public static String wrapPlane(String plane) {
		return X.empty(plane) ? PLANE_INDEX_ALIAS : plane;
	}

	public static String unwrapPlane(String plane) {
		return PLANE_INDEX_ALIAS.equals(plane) ? "" : plane;
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

	public static boolean isPlaneAliasIndex(String plane) {
		return PLANE_INDEX_ALIAS.equals(plane);
	}

	public static boolean isPlaneAliasIndexOrEmpty(String name) {
		return X.empty(name) || isPlaneAliasIndex(name);
	}

	public static Pare<String, String> unwrapSdn(Pare<String, String> sdn) {
		return Pare.of(unwrapPlane(sdn.key()), unwrapPage(sdn.val()));
	}

	public String plane() {
		return _plane;
	}

	public String planeRq() {
		String plane = plane();
		if (plane == null) {
			throw new RequiredRuntimeException("NodeID without plane");
		}
		return "".equals(plane) ? PLANE_INDEX_ALIAS : plane;
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
				return Pare.of(planeRq(), pageRq());
			default:
				throw new WhatIsTypeException("Illegal state for prepare sdn:" + state);
		}
	}

	@Override
	public String nodeName() {
		return item();
	}

	@Override
	public String toObjId() {
		return toString0();
	}

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
				_page = wrapPlane(paths[0]);
				_item = paths[1];
				break;
			case 3:
				state = State.FULL;
				_plane = wrapPlane(paths[0]);
				_page = wrapPlane(paths[1]);
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

	public static NodeID of(Path file, NodeID... defRq) {
		int nameCount = file.getNameCount();
		if (nameCount > 3) {
			String formsDir = UF.name(file, 2);
			if (NODE_PARENT_DIR.equals(formsDir)) {
				NodeID nodeID = new NodeID(Pare.of(UF.name(file, 0), UF.name(file, 1)), UF.name(file, 3));
				if (nodeID.isValid()) {
					return nodeID;
				}
				return ARG.toDefThrowMsg(() -> nodeID.getMultiOrSingleErrorOrNullAsString(), defRq);
			}
			return ARG.toDefThrowMsg(() -> X.f("Illegal path [%s] item for defined NodeID", formsDir), defRq);
		}
		return ARG.toDefThrowMsg(() -> "Except path count >=4", defRq);

//		String err;
//		switch (state) {
//			case FULL:
//				if (nameCount > 2) {
//					return new NodeID(Pare.of(UF.fn(file, 0), UF.fn(file, 1)), UF.fn(file, 2));
//				}
//				err = "Path name count less that 3";
//				break;
//			case PAGED:
//				if (nameCount > 1) {
//					return new NodeID(Pare.of(UF.fn(file, 0), UF.fn(file, 1)), UF.fn(file, 2));
//				}
//				err = "Path name count less that 3";
//				break;
//			default:
//				err = "Illegal nodeId state '" + state + "'";
//		}
//		return ARG.toDefThrowMsg(() -> err, defRq);
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
