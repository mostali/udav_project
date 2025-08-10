package udav_net.apis.zznote;

import mpc.exception.FIllegalStateException;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ItemPath {

	public static final String ITEM_INDEX_ALIAS = ".index";
	public static final String PAGE_INDEX_ALIAS = ".index";
	public static final String SD3_INDEX_ALIAS = ".index";

	public static ItemPath getPathRelativeWithIndex(ItemPath itemPath) {
		switch (itemPath.mode) {
			case SINGLY:
				return rootParent().resolve(itemPath.nodeName());
			case PARE:
				return of(Paths.get(SD3_INDEX_ALIAS).resolve(itemPath.pageName()).resolve(itemPath.nodeName()));
			case ALL:
				return itemPath;
			default:
				throw new WhatIsTypeException(itemPath.mode);
		}
	}

	public static String wrapSd3(String... sd3) {
		return sd3 == null || sd3.length == 0 || sd3[0] == null || sd3[0].length() == 0 ? SD3_INDEX_ALIAS : sd3[0];
	}

	public static String unwrapSd3(String... sd3) {
		return sd3 == null || sd3.length == 0 || sd3[0] == null ? "" : (sd3[0].equals(SD3_INDEX_ALIAS) ? "" : sd3[0]);
	}

	public static String unwrapPage(String... sd3) {
		return sd3 == null || sd3.length == 0 || sd3[0] == null ? "" : (sd3[0].equals(PAGE_INDEX_ALIAS) ? "" : sd3[0]);
	}

	public static String unwrapSd3(Pare<String, String> sd3pn) {
		return unwrapSd3(sd3pn.key());
	}

	public static boolean isAliasIndexPlane(String sd3) {
		return SD3_INDEX_ALIAS.equals(sd3);
	}

	public static boolean isAliasIndexOrEmpty(String name) {
		return X.empty(name) || SD3_INDEX_ALIAS.equals(name);
	}

	public static Pare<String, String> unwrapSdn(Pare<String, String> sdn) {
		return Pare.of(unwrapSd3(sdn.key()), unwrapPage(sdn.val()));
	}

	public NodeID toNodeID() {
		return new NodeID(subdomain() + "/" + pageName() + "/" + nodeName());
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
		return of(Paths.get(wrapSd3(sdn.key()), wrapSd3(sdn.val()), IT.NE(itemName)));
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
		return ItemPath.of(Paths.get(SD3_INDEX_ALIAS).resolve(PAGE_INDEX_ALIAS));
	}

	public ItemPath resolve(String name) {
		return ItemPath.of(path.resolve(name));
	}

	public Pare<String, String> sdn() {
		return Pare.of(subdomain(), pageName());
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
		return getSubdomainAsUrlPart(subdomain());
	}

	public static String getSubdomainAsUrlPart(String sd3) {
		return isAliasIndexPlane(sd3) ? "" : sd3 + ".";
	}

	public String pageAsUrlPart() {
		return getPageAsUrlPart(pageName());
	}

	public static String getPageAsUrlPart(String pagename) {
		return PAGE_INDEX_ALIAS.equals(pagename) ? "" : "/" + pagename;
	}

	public String subdomain(String... defRq) {
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
