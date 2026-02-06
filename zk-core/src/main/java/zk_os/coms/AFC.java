package zk_os.coms;

import lombok.SneakyThrows;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.fs.ext.EXT;
import mpc.fs.fd.EFT;
import mpe.call_msg.core.INodeID;
import mpe.call_msg.core.IPageID;
import mpe.call_msg.core.ISpaceID;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.pare.Pare;
import org.jetbrains.annotations.NotNull;
import mpe.call_msg.core.NodeID;
import zk_notes.node.NodeDir;
import zk_os.AppZosCore;
import zk_os.core.Sdn;
import zk_page.ZKColor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

//App File Core
public class AFC {

	public enum SpaceType {
		SPACES, PAGES, NODES;

		public static SpaceType of(ISpaceID objId) {
			if (objId instanceof INodeID) {
				return NODES;
			} else if (objId instanceof IPageID) {
				return PAGES;
			} else if (objId instanceof ISpaceID) {
				return SPACES;
			}
			throw new WhatIsTypeException(objId.getClass());
		}

		public String bgColorNext() {
			return color().nextColor();
		}

		public ZKColor color() {
			switch (this) {
				case SPACES:
					return ZKColor.LBLUE;
				case PAGES:
					return ZKColor.GREEN;
				case NODES:
					return ZKColor.YELLOW;
				default:
					throw new WhatIsTypeException(this);
			}
		}
	}

	//
	//

	public enum AfcEntity {
		PLANE, PAGE, FORM, COM, PAGECOM, EVENTS;

		public static AfcEntity valueOfDirName(String afcEntityName, AfcEntity... defRq) {
			switch (afcEntityName) {
				case AFCC.DIR_FORMS:
					return FORM;
				case AFCC.DIR_COMS:
					return COM;
				case AFCC.DIR_PLANES:
					return PLANE;
				case AFCC.DIR_PAGES:
					return PAGE;
				case AFCC.DIR_PAGECOMS:
					return PAGECOM;
				case AFCC.DIR_EVENTS:
					return EVENTS;
				default:
					return ARG.toDefThrowMsg(() -> X.f("illegal entity name '%s'", afcEntityName), defRq);
			}
		}

		public String toDirName() {
			switch (this) {
				case FORM:
					return AFCC.DIR_FORMS;
				case COM:
					return AFCC.DIR_COMS;
				case PLANE:
					return AFCC.DIR_PLANES;
				case PAGE:
					return AFCC.DIR_PAGES;
				case PAGECOM:
					return AFCC.DIR_PAGECOMS;
				case EVENTS:
					return AFCC.DIR_EVENTS;
				default:
					throw new WhatIsTypeException(this);
			}
		}

	}


	private static class AppNotes {
	}

	public static final Class NODE_FILE_NAME = AppNotes.class;


	@SneakyThrows
	public static void main(String[] args) {
		boolean withUsers = true;
		TreeSet<Path> all = Files.list(PLANES.DIR_PLANES()).filter(p -> {
			if (withUsers) {
				return true;
			} else {
				return !AFCC.Filter.USER_NET_NAMES.test(p.getFileName().toString());
			}
		}).collect(Collectors.toCollection(TreeSet::new));
//		TreeSet<Path> paths =;
//		TreeSet<Path> paths = DIR_PLANES_LS_CLEAN(true);
//		X.exit( DIR_PLANES().toAbsolutePath());
		X.exit(all);
	}

	//
	//

	private static @NotNull String normEntName(String entityname) {
		return UF.clearFilename(entityname);
	}

	public static String toFileName_Default(EXT ext) {
		return toFileName(NODE_FILE_NAME, ext);
	}

	public static String toFileName(Class com, EXT json) {
		return json.toFileName(com.getSimpleName());
	}

	//
	//

	public static class PLANES {

		public static Collection<String> DIR_PLANES_NAMES_CLEAN(boolean withUsers) {
			return DIR_PLANES_LS_CLEAN(withUsers).stream().map(UF::fn).collect(Collectors.toList());
		}

		@SneakyThrows
		public static TreeSet<Path> DIR_PLANES_LS_CLEAN(boolean withUsers) {
			return UFS.lsFilter(DIR_PLANES(), p -> {
				if (!UFS.isDir(p)) {
					return false;
				}
				if (withUsers) {
					return true;
				} else {
					return !AFCC.Filter.USER_NET_NAMES.test(p.getFileName().toString());
				}
			}, ARR.EMPTY_TSET);
		}

		public static Path DIR_PLANES() {
			return AppZosCore.getRpa().resolve(AFCC.DIR_PLANES);
		}

		public static Path getPlaneDir(String sd3) {
			return DIR_PLANES().resolve(IT.NE(sd3));
		}

		public static Path getStatePath_PROPS(String sd3) {
			return getPlaneDir(sd3).resolve(AFCC.FILE_PLANE);
		}

		public static Path DIR_PLANE(String sd3) {
			return DIR_PLANES().resolve(NodeID.wrapPlane(sd3));
		}

	}

	//
	//


	public static class PAGES {

		@SneakyThrows
		public static TreeSet<Path> DIR_PAGES_LS_CLEAN(String sd3) {
			return UFS.lsFilter(PLANES.DIR_PLANE(sd3), p -> UFS.isDir(p), ARR.EMPTY_TSET);
		}

		public static Path DIR_PAGE(String sd3, String pagename) {
			return PLANES.DIR_PLANE(sd3).resolve(pagename);
		}

		//
		//
		public static Path getDir(Pare<String, String> sdn) {
			return getDir(sdn.keyStr(), sdn.valStr());
		}

		public static Path getDirAsZipFile(Pare<String, String> sdn) {
			Path pageDir = getDir(sdn);
			Path pageDirZip = ARCHIVES.DIR_ARCHIVES().resolve(NodeID.wrapPlane(sdn.key())).resolve(pageDir.getFileName() + ".zip");
			return pageDirZip;
		}

		public static Path getDir(String sd3, String pagename) {
			return PLANES.DIR_PLANES().resolve(NodeID.wrapPlane(sd3)).resolve(NodeID.wrapPlane(pagename));
		}

//		public static PageState getStatePathCurentType() {
//			return new PageState(Sdn.get(), getStatePathCurent().toString());
//		}

		public static Path getStatePathCurent() {
			return getStatePath_PROPS(Sdn.get());
		}

		public static Path getStatePath_PROPS(Pare sdn) {
			return getStatePath_PROPS(sdn.keyStr(), sdn.valStr());
		}

		public static Path getStatePath_PROPS(String sd3, String pagename) {
			return PLANES.DIR_PLANES().resolve(NodeID.wrapPlane(sd3)).resolve(normEntName(NodeID.wrapPlane(pagename))).resolve(AFCC.FILE_PAGE);
		}
	}


	//
	//

	public static class HEADS {

		@SneakyThrows
		public static List<Path> RPA_HEADS_LS() {
			return UFS.ls(RPA_HEADS(), ARR.EMPTY_LIST);
		}

		public static Path RPA_HEADS() {
			return AppZosCore.getRpa().resolve(AFCC.DIR_HEADS);
		}

		public static List<Path> getPlaneDir_LS(String sd3) {
			return EFT.FILE.ls(getPlaneDir(sd3), ARR.EMPTY_LIST);
		}

		public static Path getPlaneDir(String sd3) {
			return PLANES.getPlaneDir(sd3).resolve(AFCC.DIR_HEADS);
		}

		public static List<Path> getPageDir_LS(String sd3, String pagename) {
			return EFT.FILE.ls(getPageDir(sd3, pagename), ARR.EMPTY_LIST);
		}

		public static Path getPageDir(String sd3, String pagename) {
			return PAGES.getDir(sd3, pagename).resolve(AFCC.DIR_HEADS);
		}
	}

	//
	//

	public static class FORMS {

		public static TreeSet<Path> DIR_FORMS_LS_CLEAN(Pare<String, String> sdn) {
			return DIR_FORMS_LS_CLEAN(sdn.key(), sdn.val());
		}

//		public static TreeSet<Path> DIR_FORMS_LS_SEC(String sd3, String pagename) {
//			return AFCSec.SEC(sd3, pagename, SpaceType.NODES, DIR_FORMS_LS_CLEAN(sd3, pagename));
//		}

		@SneakyThrows
		public static TreeSet<Path> DIR_FORMS_LS_CLEAN(String sd3, String pagename) {
			return UFS.lsFilter(DIR_FORMS(sd3, pagename), p -> UFS.isDir(p), ARR.EMPTY_TSET);
		}

		public static Path DIR_FORMS(Pare<String, String> sdn) {
			return DIR_FORMS(sdn.key(), sdn.val());
		}

		public static Path DIR_FORMS(String sd3, String pagename) {
			return PLANES.DIR_PLANE(NodeID.wrapPlane(sd3)).resolve(NodeID.wrapPlane(pagename)).resolve(AFCC.DIR_FORMS);
		}

		public static Path getStatePath_DATA(String sd3, String pagename, String formname) {
			return getStatePath(sd3, pagename, (formname), EXT.PROPS);
		}

		public static Path getStatePath_PROPS(String sd3, String pagename, String formname) {
			return getStatePath(sd3, pagename, formname, EXT.PROPS$$);
		}

		public static Path getStatePath(Pare<String, String> sdn, String formname, EXT ext) {
			return getStatePath(sdn.key(), sdn.val(), formname, ext);
		}

		public static Path getStatePath(String sd3, String pagename, String formname, EXT ext) {
			return getParentPath(sd3, pagename, formname).resolve(toFileName_Default(ext));
		}

		public static Path getParentPath(Pare<String, String> sdn, String formname) {
			return getParentPath(sdn.key(), sdn.val(), formname);
		}

		public static Path getParentPath(String sd3, String pagename, String formname) {
			return DIR_FORMS(sd3, pagename).resolve(normEntName(formname));
		}

		public static Path getParentPathCurrent(String newItemName) {
			return getParentPath(Sdn.get(), newItemName);
		}
	}

	//
	//

	public static class COMS {

		@SneakyThrows
		public static TreeSet<Path> DIR_COMS_LS_CLEAN(String sd3, String pagename) {
			return UFS.lsFilter(DIR_COMS(sd3, pagename), p -> UFS.isDir(p), ARR.EMPTY_TSET);
		}

		public static Path DIR_COMS(String sd3, String pagename) {
			return PLANES.DIR_PLANE(sd3).resolve(NodeID.wrapPlane(pagename)).resolve(AFCC.DIR_COMS);
		}

		public static Path getStatePath(String sd3, String pagename, String comname, EXT ext) {
			return DIR_COMS(sd3, pagename).resolve(normEntName(comname)).resolve(toFileName_Default(ext));
		}
	}

	public static class EVENTS {

		public static Path getStatePath(String sd3, String pagename, String eventname, EXT ext) {
			return DIR_EVENTS(sd3, pagename).resolve(normEntName(eventname)).resolve(toFileName_Default(ext));
		}

		public static Path DIR_EVENTS(String sd3, String pagename) {
			return PAGES.DIR_PAGE(sd3, pagename).resolve(AFCC.DIR_EVENTS);
		}

		public static Path getStatePath(NodeDir node) {
			Pare<String, String> sdn = node.sdnPare();
			return getStatePath(sdn.key(), sdn.val(), node.nodeName(), EXT.SQLITE);
		}

		public static Path toPathNodeEventTreePath(Pare<String, String> sdn, String nodeName, boolean... create) {
			Path rpaEventsStatePath = getStatePath(sdn.key(), sdn.val(), nodeName, EXT.SQLITE);
			if (ARG.isDefEqTrue(create)) {
				UFS.MKDIR.createDirs(rpaEventsStatePath);
			}
			return rpaEventsStatePath;
		}

	}

	public static class PAGECOMS {

		public static Path DIR_PAGECOMS(String sd3, String pagename) {
			return PAGES.DIR_PAGE(sd3, pagename).resolve(AFCC.DIR_PAGECOMS);
		}

		public static Path getStatePath(String sd3, String pagename, String comname, EXT ext) {
			return DIR_PAGECOMS(sd3, NodeID.wrapPlane(pagename)).resolve(normEntName(comname)).resolve(toFileName_Default(ext));
		}
	}

	public static class ARCHIVES {

		public static Path DIR_ARCHIVES() {
			return AppZosCore.getRpa().resolve(AFCC.DIR_ARCHIVES);
		}
	}

}
