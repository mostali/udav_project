//package nettm.client;
//
//import mpc.exception.FIllegalStateException;
//import mpc.exception.RequiredRuntimeException;
//import mpu.IT;
//import mpu.core.ARG;
//import mpu.pare.Pare;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//public class PartPath {
//
//
////	public static ItemPath getPathRelativeWithIndex(ItemPath itemPath) {
////		switch (itemPath.mode) {
////			case SINGLY:
////				return rootParent().resolve(itemPath.name());
////			case PARE:
////				return of(Paths.get(AFCC.SD3_INDEX_ALIAS).resolve(itemPath.page()).resolve(itemPath.name()));
////			case ALL:
////				return itemPath;
////			default:
////				throw new WhatIsTypeException(itemPath.mode);
////		}
////	}
//
//	public enum Mode {
//		SINGLY, PARE, ITEM, CHILD
//	}
//
//
//	public final java.nio.file.Path path;
//	public final Mode mode;
//
//	@Override
//	public String toString() {
//		return mode + ":" + path;
//	}
//
//	public static PartPath of(String path) {
//		return of(Paths.get(path));
//	}
//
//	public static PartPath of(java.nio.file.Path path) {
//		return new PartPath(path);
//	}
//
//	public PartPath(Path path) {
//		this.path = path;
//		switch (path.getNameCount()) {
//			case 1:
//				mode = Mode.SINGLY;
//				break;
//			case 2:
//				mode = Mode.PARE;
//				break;
//			case 3:
//				mode = Mode.ITEM;
//				break;
//			case 4:
//				mode = Mode.CHILD;
//				break;
//			default:
//				throw new FIllegalStateException("except 1,2 or path items from path '%s'", path);
//		}
//	}
//
//	public PartPath resolve(String name) {
//		return PartPath.of(path.resolve(name));
//	}
//
//
//	public PartPath throwIsNot(Mode mode) {
//		IT.state(this.mode == mode, "except whole path, no %s", mode);
//		return this;
//	}
//
////
////	public String name(String... defRq) {
////		switch (mode) {
////			case SINGLY:
////				return path.getName(0).toString();
////			case PARE:
////				return path.getName(1).toString();
////			case ALL:
////				return path.getName(2).toString();
////			default:
////				return ARG.toDefThrow(() -> new RequiredRuntimeException("Except name from mode %s :", path), defRq);
////		}
////	}
//
//}
