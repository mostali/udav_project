//package zk_core.form;
//
//import mp.core.U;
//import mp.core.X;
//import mp.core.exception.FIllegalStateException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import zk_core.page.FormDirModel;
//import zk_core.page.PageDirModel;
//
//import java.io.IOException;
//import java.nio.file.Path;
//import java.util.*;
//
///**
// * @author dav 28.03.2021
// */
//public class ZKCF {
//
//	public static final Logger L = LoggerFactory.getLogger(ZKCF.class);
//
//	public static final String HEAD = "head";
//	public static final String UNDEFINED = "undefined";
//	public static final String META = "meta";
//	public static final String BODY = "body";
//	public static final String TTML = "ttml";
//	public static final String HTML = "html";
//
//	public static void main(String[] args) throws IOException {
//
////		String formPath = "/home/dav/.data/bea/rp-index/index/body/102/";
//		String formPath = "/home/dav/.data/bea/rp-index/index/body/100";
//		FormDirModel fdm = FormDirModel.of(formPath);
//		U.exit(fdm.getPropsSync(true, true).setString("go", ""));
////		U.exit(fdm.getComponentsOrBuild());
//		U.exit(fdm.getMapExt());
//
//		String pagePath = "/home/dav/.data/bea/rp-index/index/";
//		PageDirModel pdm = PageDirModel.of(pagePath);
//		U.exit(pdm.getMapExt());
//
//		U.exit(fdm.getCTypeIndex());
//		U.exit(fdm.getCTypeWithInit());
////		DIR of = DIR.of("/home/dav/.data/bea/rp-index/index/body/100");
////		U.exit(of.fmap(null, LS_SORT.NATURAL));
////		U.exit(of.fxmap(null, LS_SORT.NATURAL));
//	}
//
//	public static void checkNotEmpty(List<Path> paths, CType ctype, Path com) {
//		if (X.empty(paths)) {
//			throw new FIllegalStateException("Component '%s' from path %s has empty data", ctype, com);
//		}
//	}
//
//	//	public static List<Component> getComponentsOrBuild(Path form, boolean... edit) throws IOException {
//	//		return getComponentsOrBuild(FormDirModel.of(form), edit);
//	//	}
//
//}
