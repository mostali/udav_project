package zk_os.coms;

import lombok.RequiredArgsConstructor;
import mpc.arr.STREAM;
import mpc.env.APP;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.UFS;
import mpe.NT;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;
import mpu.str.RANDOM;
import mpu.str.TKN;
import zk_notes.node.NodeDir;
import zk_page.core.FinderPSP;
import zk_notes.node_state.FileState;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

//App File Core Com's
public class AFCC {

	//	public static final String DIR_PAGES_FC = ".pages.fc";
	public static final String DIR_EVENTS = ".events";
	public static final String DIR_COMS = ".coms";
	public static final String DIR_FORMS = ".forms";
	public static final String DIR_PLANES = ".planes";
	public static final String DIR_PAGES = ".pages";
	public static final String FILE_PAGE = ".page";
	public static final String DIR_PAGECOMS = ".pagecoms";
//	public static final String DIR_PAGECOM = ".pagecom";
	public static final String FILE_PLANE = ".plane";
	public static final String DIR_HEADS = ".heads";
	public static final String DIR_ARCHIVES = ".archives";

	public static final String PROPS_FILE_EXT = "..";
	public static final Pattern PATTERN_USER_NETID = Pattern.compile("[t|v]\\d++$");

	public static List<String> relativizeAppFile(Collection<Path> paths) {
		return STREAM.mapToList(paths, AFCC::relativizeAppFile);
	}

	public static String relativizeAppFile(Path path, String... defRq) {
		try {
			return APP.relativizeAppFile(path, AFCC.DIR_PLANES);
		} catch (Exception ex) {
			return ARG.toDefThrowMsg(() -> X.f("RelativizeAppFile from .planes : %s", path), defRq);
		}
	}

	public static Path getPageOfNodeFileCom(Path fileCom) {
		return fileCom.getParent().getParent();
	}

	public static Path getPageOfNode(NodeDir srcNode) {
		return srcNode.fParent().getParent();
	}

	public static Path getFormDirBlank(Pare sdn, String name, Integer... randomSfxLen) {
		String namePfx = ARG.isDefNNF(randomSfxLen) && randomSfxLen[0] > 0 ? RANDOM.alpha(randomSfxLen[0]) : "";
		return AFC.FORMS.DIR_FORMS(sdn).resolve(name + namePfx);
	}


	@RequiredArgsConstructor
	public static class PredicatePages implements Predicate<String> {
		public final String sd3;

		@Override
		public boolean test(String name) {
			return FinderPSP.getAllBusyPages(sd3).contains(name);
		}
	}

	public static class Filter {

		public static final Predicate<String> USER_NET_NAMES = (fn) -> //
				isMatchesUserNetId(fn, NT.TG) || //
						isMatchesUserNetId(fn, NT.VK);

//		public static final Predicate<String> NO_PLANE_DIR = (fn) -> //
//				USER_NET_NAMES.test(fn);
//
//		public static final Predicate<String> NO_PAGE_DIR = (fn) -> //
//				true;


		public static final Predicate<String> IS_APP_COM_FN = (fn) -> //
//				ItemPath.SD3_INDEX_ALIAS.equalsIgnoreCase(fn) || //
				DIR_COMS.equalsIgnoreCase(fn) || //
						DIR_FORMS.equalsIgnoreCase(fn) || //
						DIR_PAGES.equalsIgnoreCase(fn) || //
						DIR_PAGECOMS.equalsIgnoreCase(fn) || //
						DIR_PLANES.equalsIgnoreCase(fn);

		public static final Predicate<String> IS_SYS_SD3 = name -> FinderPSP.getAllBusySd3().contains(name);
		//

//		static Predicate<String> DIR_PLANES(boolean showDirUsers) {
//			return s -> showDirUsers ? true : !USER_NET_NAMES.test(s);
//		}

//		static Predicate<String> DIR_PAGES() {
//			return s -> true;
//		}
//
//		static Predicate<String> DIR_PAGE() {
//			return s -> true;
//		}
//
//		static Predicate<String> DIR_FORMS() {
//			return s -> true;
//		}
//
//		static Predicate<String> DIR_COMS() {
//			return s -> true;
//		}


	}

	public static boolean isMatchesUserNetId(String name, NT... net) {
		if (net.length == 0) {
			return false;
		}
		for (NT nt : net) {
			if (isUserNetId(name, nt.shortPfx())) {
				return true;
			}
		}
		return false;
	}

	public static Long getUserNetId(String fn, String pfxNet, Long... defRq) {
		return TKN.lastGreedy(fn, pfxNet, Long.class, defRq);
	}

	public static boolean isUserNetId(String fn, String pfxNet) {
		return getUserNetId(fn, pfxNet, null) != null;
	}


	//
	//
	//

	public static List<Path> getAllExistParts(Path pathForm) {
		List<Path> parts = new ArrayList<>();
		parts.add(pathForm);
		int next = 1;
		Path nextPart;
		while (UFS.existFile(nextPart = FileState.getPartPathFor(pathForm, next++))) {
			parts.add(nextPart);
		}
		return parts;
	}

	public static List<Path> getAllPathParts(Path pathForm, int size) {
		List<Path> parts = new ArrayList<>();
		parts.add(pathForm);
		for (int i = 1; i < size; i++) {
			parts.add(FileState.getPartPathFor(pathForm, i));
		}
		return parts;
	}

	public enum FileType {
		XML, HEAD_BEFORE, HEAD_AFTER;

		public static FileType of(String filename, FileType... defRq) {
			if (isHtmlNote(filename)) {
				return XML;
			} else if (filename.endsWith("$$HEAD")) {
				return HEAD_AFTER;
			} else if (filename.endsWith("$$head")) {
				return HEAD_BEFORE;
			}
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Except Afcc File Type from '%s'", filename), defRq);
		}
	}

	public static boolean isHtmlNote(String noteName) {
		return noteName.endsWith("$$HTML") || noteName.endsWith("$$html");
	}

	public static boolean isHtmlHead(String noteName) {
		return noteName.endsWith("$$HEAD") || noteName.endsWith("$$head");
	}
}
