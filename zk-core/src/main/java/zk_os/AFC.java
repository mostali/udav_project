package zk_os;

import lombok.SneakyThrows;
import mpc.fs.LS_SORT;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.fs.ext.EXT;
import mpc.fs.fd.EFT;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import mpu.pare.Pare;
import org.jetbrains.annotations.NotNull;
import udav_net.apis.zznote.ItemPath;
import zk_notes.AppNotes;
import zk_notes.node.NodeDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

//App File Core
public class AFC {

	public static final Class NODE_FILE_NAME = AppNotes.class;

	private static @NotNull String normEntName(String entityname) {
		return UF.clearFilename(entityname);
	}

	public static String toFileName_Default(EXT ext) {
		return toFileName(NODE_FILE_NAME, ext);
	}

	public static String toFileName(Class com, EXT json) {
		return json.toFileName(com.getSimpleName());
	}

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

//	public static class TREES {
//		public static Path DIR_TREES(String sd3, String pagename) {
//			return PLANES.DIR_PLANE(sd3).resolve(ItemPath.wrapSd3(pagename)).resolve(AFCC.DIR_COMS);a
//		}
//
//		@SneakyThrows
//		public static TreeSet<Path> DIR_COMS_LS_CLEAN(String sd3, String pagename) {
//			return UFS.lsSorted(DIR_TREES(sd3, pagename), p -> UFS.isDir(p), ARR.EMPTY_TSET);
//		}
//
//		public static Path getRpaComStatePath(String sd3, String pagename, String comname, EXT ext) {
//			return DIR_COMS(sd3, pagename).resolve(normEntName(comname)).resolve(toFileName_Default(ext));
//		}
//	}

	public static class COMS {

		@SneakyThrows
		public static TreeSet<Path> DIR_COMS_LS_CLEAN(String sd3, String pagename) {
			return UFS.lsSorted(DIR_COMS(sd3, pagename), p -> UFS.isDir(p), ARR.EMPTY_TSET);
		}

		public static Path DIR_COMS(String sd3, String pagename) {
			return PLANES.DIR_PLANE(sd3).resolve(ItemPath.wrapSd3(pagename)).resolve(AFCC.DIR_COMS);
		}

		public static Path getRpaComStatePath(String sd3, String pagename, String comname, EXT ext) {
			return DIR_COMS(sd3, pagename).resolve(normEntName(comname)).resolve(toFileName_Default(ext));
		}
	}

	public static class FORMS {

		public static Set<Path> DIR_FORMS_LS_CLEAN(Pare<String, String> sdn) {
			return DIR_FORMS_LS_CLEAN(sdn.key(), sdn.val());
		}

		@SneakyThrows
		public static TreeSet<Path> DIR_FORMS_LS_CLEAN(String sd3, String pagename) {
			return UFS.lsSorted(DIR_FORMS(sd3, pagename), p -> UFS.isDir(p), ARR.EMPTY_TSET);
		}

		public static Path DIR_FORMS(Pare<String, String> sdn) {
			return DIR_FORMS(sdn.key(), sdn.val());
		}

		public static Path DIR_FORMS(String sd3, String pagename) {
			return PLANES.DIR_PLANE(ItemPath.wrapSd3(sd3)).resolve(ItemPath.wrapSd3(pagename)).resolve(AFCC.DIR_FORMS);
		}

		public static Path getRpaFormStatePath(Pare<String, String> sdn, String formname) {
			return getRpaFormStatePath(sdn.key(), sdn.val(), formname);
		}

		public static Path getRpaFormStatePath(String sd3, String pagename, String formname) {
			return getRpaFormStatePath(sd3, pagename, (formname), EXT.PROPS);
		}

		public static Path getRpaFormStatePath(String sd3, String pagename, String formname, EXT ext) {
			return DIR_FORMS(sd3, pagename).resolve(normEntName(formname)).resolve(toFileName_Default(ext));
		}
	}

	public static class EVENTS {

		public static Path getRpaEventsStatePath(String sd3, String pagename, String eventname, EXT ext) {
			return DIR_EVENTS(sd3, pagename).resolve(normEntName(eventname)).resolve(toFileName_Default(ext));
		}

		public static Path DIR_EVENTS(String sd3, String pagename) {
			return PAGES.DIR_PAGE(sd3, pagename).resolve(AFCC.DIR_EVENTS);
		}

		public static Path getRpaEventsStatePath(NodeDir node) {
			Pare<String, String> sdn = node.sdn();
			return getRpaEventsStatePath(sdn.key(), sdn.val(), node.nodeName(), EXT.SQLITE);
		}
	}

	public static class PAGECOMS {

		public static Path DIR_PAGECOMS(String sd3, String pagename) {
			return PAGES.DIR_PAGE(sd3, pagename).resolve(AFCC.DIR_PAGECOMS);
		}

		public static Path getRpaPageComStatePath(String sd3, String pagename, String comname, EXT ext) {
			return DIR_PAGECOMS(sd3, ItemPath.wrapSd3(pagename)).resolve(normEntName(comname)).resolve(toFileName_Default(ext));
		}
	}

	public static class HEADS {

		@SneakyThrows
		public static List<Path> DIR_HEADS_LS() {
			return UFS.ls(DIR_HEADS(), ARR.EMPTY_LIST);
		}

		public static Path DIR_HEADS() {
			return AppZosCore.getRpa().resolve(AFCC.DIR_HEADS);
		}

		public static List<Path> getRpaHeadsStatePathLs(String sd3) {
			return EFT.DIR.ls(getRpaHeadsStatePath(sd3), ARR.EMPTY_LIST);
		}

		public static Path getRpaHeadsStatePath(String sd3) {
			return PLANES.getRpaPlaneDir(sd3).resolve(AFCC.DIR_HEADS);
		}
	}


	public static class PAGES {

		@SneakyThrows
		public static TreeSet<Path> DIR_PAGES_LS_CLEAN(String sd3) {
			return UFS.lsSorted(PLANES.DIR_PLANE(sd3), p -> UFS.isDir(p), ARR.EMPTY_TSET);
		}

		public static Path DIR_PAGE(Pare<String, String> sdn) {
			return DIR_PAGE(ItemPath.wrapSd3(sdn.key()), ItemPath.wrapSd3(sdn.val()));
		}

		public static Path DIR_PAGE(String sd3, String pagename) {
			return PLANES.DIR_PLANE(sd3).resolve(pagename);
		}

		//
		//
		public static Path getRpaPageDir(Pare<String, String> sdn) {
			return getRpaPageDir(sdn.keyStr(), sdn.valStr());
		}

		public static Path getRpaPageDirAsZipFile(Pare<String, String> sdn) {
			Path pageDir = getRpaPageDir(sdn);
			Path pageDirZip = ARCHIVES.DIR_ARCHIVES().resolve(ItemPath.wrapSd3(sdn.key())).resolve(pageDir.getFileName() + ".zip");
			return pageDirZip;
		}

		public static Path getRpaPageDir(String sd3, String pagename) {
			return PLANES.DIR_PLANES().resolve(ItemPath.wrapSd3(sd3)).resolve(ItemPath.wrapSd3(pagename));
		}

		public static Path getRpaPageStatePath(String sd3, String pagename) {
			return PLANES.DIR_PLANES().resolve(ItemPath.wrapSd3(sd3)).resolve(normEntName(ItemPath.wrapSd3(pagename))).resolve(AFCC.FILE_PAGE);
		}

		public static Path getRpaPageStatePath(Pare sdn) {
			return getRpaPageStatePath(sdn.keyStr(), sdn.valStr());
		}
	}

	public static class PLANES {

		@SneakyThrows
		public static TreeSet<Path> DIR_PLANES_LS_CLEAN(boolean withUsers) {
			return UFS.lsSorted(DIR_PLANES(), p -> {
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

		public static Path getRpaPlaneDir(String sd3) {
			return DIR_PLANES().resolve(IT.NE(sd3));
		}

		public static Path getRpaPlaneStatePath(String sd3) {
			return getRpaPlaneDir(sd3).resolve(AFCC.FILE_PLANE);
		}

		public static Path DIR_PLANE(String sd3) {
			return DIR_PLANES().resolve(ItemPath.wrapSd3(sd3));
		}

		public static Path getRpaPlaneDirAsZipFile(String sd3) {
			Path pageDirZip = ARCHIVES.DIR_ARCHIVES().resolve(ItemPath.wrapSd3(sd3) + ".zip");
			return pageDirZip;
		}
	}

	public static class ARCHIVES {

		public static Path DIR_ARCHIVES() {
			return AppZosCore.getRpa().resolve(AFCC.DIR_ARCHIVES);
		}
	}

}
