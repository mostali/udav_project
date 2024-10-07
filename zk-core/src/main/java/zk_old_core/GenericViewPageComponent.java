package zk_old_core;

import lombok.RequiredArgsConstructor;
import mpu.str.Rt;
import mpu.X;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.*;
import zk_com.base_ctr.Div0;
import zk_page.core.ISpPageCom;
import zk_pages.GenericPageSP;
import zk_old_core.mdl.PageDirModel;
import zk_old_core.std.AbsVF;
import zk_page.ZKR;
import zk_page.ZkPage;
import zk_old_core.mdl.pageset.HeadFileModel;
import zk_old_core.mdl.pageset.IFormModel;
import zk_old_core.mdl.pageset.PageSet;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class GenericViewPageComponent extends Div0 implements ISpPageCom {

	public static final Logger L = LoggerFactory.getLogger(GenericViewPageComponent.class);

	protected final Window window;
	protected final PageDirModel pdm;
	protected final boolean rebuild;

	@Override
	protected void init() {

		PageSet pageSet = pdm.getPageSet();

		List<IFormModel> iForms = pageSet.getIForms();
		List<HeadFileModel> iHeads = pageSet.getIStaticHeads();
		if (L.isDebugEnabled()) {
			L.debug(">>>Build '{}' head-model's:\n" + Rt.buildReport(iHeads), X.sizeOf(iHeads));
		}
		List<Component> pageForms = GenericPageSP.getOrBuildComponents(iForms, AbsVF.ViewMode.view);

		ZkPage.renderHeadRsrcs_Forms(window, pageForms);
		ZkPage.renderHeadPage_Static(window, iHeads);

		String cookieValue = ZKR.getCookieValue("anous", null);

		if (L.isInfoEnabled()) {
			L.info("Page created m:{}/c:{} from pdm: {}", X.sizeOf(iForms), X.sizeOf(pageForms), pdm);
		}

		Map<String, ?> props = pdm.getRootProps();

		GenericPageSP.applyHeadMetaPage(window, props);

		HtmlBasedComponent pageLayoutContainer = GenericPageSP.buildLayoutContainer(props, pageForms);
		appendChild(pageLayoutContainer);

		GenericPageSP.applyPageControl(pdm, pageForms);

		GenericPageSP.openStatableForms(pdm,rebuild);

	}


}
