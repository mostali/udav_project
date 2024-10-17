//package zk_core.form;
//
//import mp.core.exception.NI;
//import mp.core.exception.WhatIsTypeException;
//import mp.core.fs.EXT;
//import mp.ext.map.UMap;
//import org.zkoss.video.AVideo;
//import org.zkoss.video.Video;
//import org.zkoss.zk.ui.Component;
//import zk_core.page.FormDirModel;
//import zk_core.srv.ZulLoader;
//
//import java.nio.file.Path;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@Deprecated
//public class SimpleFormBuilder extends FormBuilder {
//
//	public SimpleFormBuilder(FormDirModel formDirModel) {
//		super(formDirModel);
//	}
//
//	@Override
//	public List<Component> buildImpl(boolean editMode) throws Exception {
//		Path formPath = formDirModel.path();
//		CType ctype = formDirModel.getCTypeWithInit();
//		List<Component> dirComs = new ArrayList<>();
//		switch (ctype) {
//			case UNDEFINED: {
//				return null;
//			}
//			case IMG: {
//
//				break;
//			}
//			case IMGW:
//
////			case HTML:
//
//			case MP3: {
//
////				dirComs.add(audio);
//				break;
//			}
//
//			case VIDEO: {
//
//				break;
//			}
//
//			case ZUL:
//			case JS:
//			case CSS: {
////				List<Path> paths = formDirModel.getChilds(EFT.FILE, EXT.buildPredicate(true, ctype));
////				ZKCF.checkNotEmpty(paths, ctype, formPath);
//				break;
//			}
//			default:
//				throw new WhatIsTypeException(ctype);
//
//		}
//
//		return dirComs;
//	}
//}
