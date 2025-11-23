package udav_net.apis.zznote;

import mpc.arr.STREAM;
import mpc.exception.FIllegalStateException;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpe.call_msg.core.NodeID;
import mpu.IT;
import mpu.core.ARG;
import mpu.pare.Pare;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

public class ItemPath {

	public static ItemPath getPathRelativeWithIndex(ItemPath itemPath) {
		switch (itemPath.mode) {
			case SINGLY:
				return rootParent().resolve(itemPath.nodeName());
			case PARE:
				return of(Paths.get(NodeID.PLANE_INDEX_ALIAS).resolve(itemPath.pageName()).resolve(itemPath.nodeName()));
			case ALL:
				return itemPath;
			default:
				throw new WhatIsTypeException(itemPath.mode);
		}
	}

	//
	//


	public static Collection filterNoIndexPath(List<String> fileNames) {
		return STREAM.filterToAll(fileNames, i -> !NodeID.ANY_INDEX_ALIAS.equals(i));
	}

	public static ItemPath of(NodeID nodeID) {
		switch (nodeID.state) {
			case FULL:
				return of(nodeID.sdn(), nodeID.item());
			default:
				throw new WhatIsTypeException("Illegal state for prepare ItemPath:" + nodeID.state);
		}
	}

	public NodeID toNodeID() {
		return new NodeID(Pare.of(planeName(), pageName()), nodeName());
	}

	public enum State {
		SINGLY, PARE, ALL
	}


	public final Path path;
	public final State mode;

	@Override
	public String toString() {
		return mode + ":" + path;
	}

	public static ItemPath of(String path) {
		return of(Paths.get(path));
	}

	public static ItemPath of(Pare<String, String> sdn, String itemName) {
		return of(Paths.get(NodeID.wrapPlane(sdn.key()), NodeID.wrapPlane(sdn.val()), IT.NE(itemName)));
	}

	public static ItemPath of(Path path) {
		return new ItemPath(path);
	}

	public ItemPath(Path path) {
		this.path = path;
		switch (path.getNameCount()) {
			case 1:
				mode = State.SINGLY;
				break;
			case 2:
				mode = State.PARE;
				break;
			case 3:
				mode = State.ALL;
				break;
			default:
				throw new FIllegalStateException("except 1,2 or 3 items from path '%s'", path);
		}
	}

	public static ItemPath rootParent() {
		return ItemPath.of(Paths.get(NodeID.PLANE_INDEX_ALIAS).resolve(NodeID.PAGE_INDEX_ALIAS));
	}

	public ItemPath resolve(String name) {
		return ItemPath.of(path.resolve(name));
	}

	public Pare<String, String> sdn() {
		return Pare.of(planeName(), pageName());
	}

	public ItemPath throwIsNotWhole() {
		IT.state(mode == State.ALL, "except whole path, no %s", mode);
		return this;
	}

	public ItemPath throwIsNotPare() {
		IT.state(mode == State.PARE, "except pare path, no %s", mode);
		return this;
	}

	public String nameAsPart() {
		return "/" + nodeName();
	}

	public String subdomainAsUrlPart() {
		return getSubdomainAsUrlPart(planeName());
	}

	public static String getSubdomainAsUrlPart(String sd3) {
		return NodeID.isPlaneAliasIndex(sd3) ? "" : sd3 + ".";
	}

	public String pageAsUrlPart() {
		return getPageAsUrlPart(pageName());
	}

	public static String getPageAsUrlPart(String pagename) {
		return NodeID.PAGE_INDEX_ALIAS.equals(pagename) ? "" : "/" + pagename;
	}

	public String planeName(String... defRq) {
		switch (mode) {
			case SINGLY:
			case PARE:
				throw new UnsupportedOperationException("subdomain only ALL mode");
			case ALL:
				return path.getName(0).toString();
			default:
				return ARG.toDefThrow(() -> new RequiredRuntimeException("Except name from mode %s :", path), defRq);
		}
	}

	public String pageName(String... defRq) {
		switch (mode) {
			case SINGLY:
				throw new UnsupportedOperationException("pagename only for PARE mode");
			case PARE:
			case ALL:
				return path.getName(1).toString();
			default:
				return ARG.toDefThrow(() -> new RequiredRuntimeException("Except name from mode %s :", path), defRq);
		}
	}

	public String nodeName(String... defRq) {
		switch (mode) {
			case SINGLY:
				return path.getName(0).toString();
			case PARE:
				return path.getName(1).toString();
			case ALL:
				return path.getName(2).toString();
			default:
				return ARG.toDefThrow(() -> new RequiredRuntimeException("Except name from mode %s :", path), defRq);
		}
	}

}
