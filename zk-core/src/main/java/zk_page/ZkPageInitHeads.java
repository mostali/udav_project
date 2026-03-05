package zk_page;

import mpc.fs.ext.EXT;
import mpu.core.RW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zul.Window;
import zk_com.base.Xml;
import zk_form.head.IHeadCom;
import zk_os.coms.AFC;
import zk_os.core.Sdn;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ZkPageInitHeads {
	public static final Logger L = LoggerFactory.getLogger(ZkPageInitHeads.class);

	public static void initPageHeadLibs(Window window) {
		Sdn sdn = Sdn.get();

		PageCtrl pageCtrl = ZKC.getFirstPageCtrl();

		List<Path> siteHeads = AFC.HEADS.RPA_HEADS_LS();
		applyPageHeadLibs(pageCtrl, window, siteHeads);

		List<Path> sd3Heads = AFC.HEADS.getPlaneDir_LS(sdn.key());
		applyPageHeadLibs(pageCtrl, window, sd3Heads);

		List<Path> pageHeads = AFC.HEADS.getPageDir_LS(sdn.key(), sdn.val());
		applyPageHeadLibs(pageCtrl, window, pageHeads);
	}

	public static void applyPageHeadLibs(PageCtrl pageCtrl, Window window, List<Path> rootLibs) {
		Map<String, Object> pAttrs = ((Page) pageCtrl).getAttributes();
		for (Path rootLib : rootLibs) {
			if (IHeadCom.isAppendRsrc(pAttrs, rootLib.toString())) {
				continue;
			}
			IHeadCom.updateStateAppendRsrc(pAttrs, rootLib.toString());
			EXT ext = EXT.of(rootLib);
			if (L.isInfoEnabled()) {
				L.info("Found head data {} -> {}", ext, rootLib);
			}
			switch (ext) {
				case JS:
					pageCtrl.addAfterHeadTags(RW.readString(rootLib));
					break;
				case HEAD:
					pageCtrl.addBeforeHeadTags(RW.readString(rootLib));
					break;
				case CSS:
					pageCtrl.addAfterHeadTags(RW.readString(rootLib));
					//					ZScript sd=new ZScript(key,content);
					//					ZKC.getFirstPageCtrl().addDeferredZScript();
					break;
				case XML:
				case HTML:
					window.appendChild(Xml.ofFile(rootLib));
					break;

				default:
					L.info("Found UNDEFINED head data {} -> {}", ext, rootLib);
					continue;
			}
		}
	}
}
