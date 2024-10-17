//package zk_core.form;
//
//import lombok.RequiredArgsConstructor;
//import mp.core.ARG;
//import org.zkoss.zk.ui.Component;
//import zk_core.page.FormDirModel;
//
//import java.util.List;
//
//@Deprecated
//
//@RequiredArgsConstructor
//public abstract class FormBuilder {
//	public final FormDirModel formDirModel;
//
//	public List<Component> build(boolean editMode, List<Component>... defRq) {
//		try {
//			return buildImpl(editMode);
//		} catch (Exception e) {
//			return ARG.toDefOrThrow(e, defRq);
//		}
//	}
//
//	public abstract List<Component> buildImpl(boolean editMode) throws Exception;
//}
