package zk_page;

import mpc.exception.WhatIsTypeException;
import mpc.fs.ext.EXT;
import mpc.log.L;
import mpu.core.RW;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zul.Window;
import zk_com.base.Xml;
import zk_form.head.IHeadCom;
import zk_os.AFC;
import zk_os.core.Sdn;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ZkPageAuto {

	public static void initPageHeadLibs(Window window) {
		Sdn sdn = Sdn.getRq();

		List<Path> siteHeads = AFC.HEADS.DIR_HEADS_LS();
		applyPageHeadLibs(window, siteHeads);

		List<Path> sd3Heads = AFC.HEADS.getSd3Dir_LS(sdn.key());
		applyPageHeadLibs(window, sd3Heads);

		List<Path> pageHeads = AFC.HEADS.getPageDir_LS(sdn.key(), sdn.val());
		applyPageHeadLibs(window, pageHeads);
	}

	public static void applyPageHeadLibs(Window window, List<Path> rootLibs) {
		PageCtrl pageCtrl = ZKC.getFirstPageCtrl();
		Map<String, Object> pAttrs = ((Page) pageCtrl).getAttributes();
		for (Path rootLib : rootLibs) {
			if (IHeadCom.isAppendRsrc(pAttrs, rootLib.toString())) {
				continue;
			}
			IHeadCom.updateStateAppendRsrc(pAttrs, rootLib.toString());
			EXT ext = EXT.of(rootLib);
			L.info("Found head data {} -> {}", ext, rootLib);
			switch (ext) {
				case JS:
					pageCtrl.addAfterHeadTags(RW.readContent(rootLib));
					break;
				case HEAD:
					pageCtrl.addBeforeHeadTags(RW.readContent(rootLib));
					break;
				case CSS:
					pageCtrl.addAfterHeadTags(RW.readContent(rootLib));
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
