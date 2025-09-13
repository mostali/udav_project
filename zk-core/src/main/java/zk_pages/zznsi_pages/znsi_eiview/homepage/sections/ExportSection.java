package zk_pages.zznsi_pages.znsi_eiview.homepage.sections;

import mpu.core.ARR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import zk_notes.node.NodeDir;
import zk_page.ZKColor;
import zk_page.ZKS;
import zk_pages.zznsi_pages.znsi_eiview.homepage.EiPageSP;
import zk_pages.zznsi_pages.znsi_eiview.homepage.EiRun;
import zk_pages.zznsi_pages.znsi_eiview.homepage.ExportPropsPanel;
import zk_pages.zznsi_pages.znsi_eiview.homepage.IProfilable;
import zk_pages.zznsi_pages.znsi_eiview.homepage.SectionHeader;

import java.util.List;

public class ExportSection extends CicdSection implements IProfilable {

	public static final Logger L = LoggerFactory.getLogger(ExportSection.class);

	@Override
	public ZKColor getSectionColor() {
		return ZKColor.LBLUE;
	}

	public ExportPropsPanel propsPanel;

	@Override
	public void updatePropsFromProfile(NodeDir nodeDir) {
		propsPanel.updatePropsFromProfile(nodeDir);
		propsPanel.lpPanel.updatePropsFromProfile(nodeDir);
	}

	@Override
	protected void init() {
		super.init();

		appendChild(new SectionHeader("Export") {
//			EiPageSP.BeAppDataForm beAppDataForm;

			@Override
			protected List<Component> getEndComs() {
				EiPageSP.BeMdmDataForm beMdmView = new EiPageSP.BeMdmDataForm();
				ZKS.VERT_ALIGN(beMdmView, "middle");
				EiPageSP.BeNifiDataForm beNifiView = new EiPageSP.BeNifiDataForm();
				ZKS.VERT_ALIGN(beNifiView, "middle");
				return ARR.as(beMdmView, beNifiView);
			}

			@Override
			protected void onClickStart(Event e) {
				new EiRun(ExportSection.this).doRun();

			}

			@Override
			protected void onChoiceContur(Event e) {
				super.onChoiceContur(e);
			}

			@Override
			protected void onChoiceProfile(Event e) {
				super.onChoiceProfile(e);
			}
		});

//		appendChild(EiPageSP.HR_SECTION.get());

		appendChild(propsPanel = new ExportPropsPanel());

		EiPageSP.SECTION_BR.apply(this);


	}

}
