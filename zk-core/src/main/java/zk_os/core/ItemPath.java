package zk_os.core;

import mpc.exception.FIllegalStateException;
import mpc.exception.RequiredRuntimeException;
import mpu.IT;
import mpu.core.ARG;
import mpu.pare.Pare;
import zk_os.AFCC;

import java.nio.file.Paths;

public class ItemPath {


	public enum Mode {
		SINGLY, PARE, ALL
	}


	public final java.nio.file.Path path;
	public final Mode mode;

	@Override
	public String toString() {
		return mode + ":" + path;
	}

	public static ItemPath of(String path) {
		return of(Paths.get(path));
	}

	public static ItemPath of(java.nio.file.Path path) {
		return new ItemPath(path);
	}

	public ItemPath(java.nio.file.Path path) {
		this.path = path;
		switch (path.getNameCount()) {
			case 1:
				mode = Mode.SINGLY;
				break;
			case 2:
				mode = Mode.PARE;
				break;
			case 3:
				mode = Mode.ALL;
				break;
			default:
				throw new FIllegalStateException("except 1,2 or path items from path '%s'", path);
		}
	}

	public static ItemPath rootParent() {
		return ItemPath.of(Paths.get(AFCC.SD3_INDEX_ALIAS).resolve(AFCC.PAGE_INDEX_ALIAS));
	}

	public ItemPath resolve(String name) {
		return ItemPath.of(path.resolve(name));
	}

	public Pare<String, String> sdn() {
		return Pare.of(subdomain(), page());
	}

	public ItemPath throwIsNotWhole() {
		IT.state(mode == Mode.ALL, "except whole path, no %s", mode);
		return this;
	}

	public ItemPath throwIsNotPare() {
		IT.state(mode == Mode.PARE, "except pare path, no %s", mode);
		return this;
	}

	public String nameAsPart() {
		return "/" + name();
	}

	public String subdomainAsUrlPart() {
		return AFCC.SD3_INDEX_ALIAS.equals(subdomain()) ? "" : subdomain() + ".";
	}

	public String pageAsUrlPart() {
		return AFCC.PAGE_INDEX_ALIAS.equals(page()) ? "" : "/" + page();
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

	public String page(String... defRq) {
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

	public String name(String... defRq) {
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
