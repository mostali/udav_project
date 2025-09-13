package zk_pages.zznsi_pages.znsi_eiview.homepage.sections;

import mpu.core.ARR;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import zk_notes.node.NodeDir;
import zk_page.ZKColor;
import zk_page.ZKS;
import zk_pages.zznsi_pages.znsi_eiview.homepage.EiPageSP;
import zk_pages.zznsi_pages.znsi_eiview.homepage.EiRun;
import zk_pages.zznsi_pages.znsi_eiview.homepage.ImportPropsPanel;
import zk_pages.zznsi_pages.znsi_eiview.homepage.IProfilable;
import zk_pages.zznsi_pages.znsi_eiview.homepage.SectionHeader;

import java.util.List;

public class ImportSection extends CicdSection implements IProfilable {

	@Override
	public ZKColor getSectionColor() {
		return ZKColor.BLUE;
	}
	//	Div0 lPan = (Div0) new Div0().width(50.0).height(370).borderSilver().inlineBlock();
//	Div0 rPan = (Div0) new Div0().width(50.0).height(370).borderSilver().inlineBlock();
	//	Tbxm xsdPan = (Tbxm) new Tbxm().placeholder("set xsd").width(50.0).height(770);

//	Tbxm outPan = (Tbxm) new Tbxm().placeholder("Import Response").width(100.0).height(70);


	//	EiPageSP.LpPanel lpPanel;
	ImportPropsPanel importPropsPanel;

	@Override
	public void updatePropsFromProfile(NodeDir nodeDir) {
//		importPropsPanel.update(nodeDir);
		importPropsPanel.lpPanel.updatePropsFromProfile(nodeDir);
	}

	@Override
	protected void init() {
		super.init();

		appendChild(new SectionHeader("Import") {
			EiPageSP.ChoiceMdmStandDd src;

			@Override
			protected List<Component> getEndComs() {
				EiPageSP.BeMdmDataForm beMdmView = new EiPageSP.BeMdmDataForm();
				ZKS.VERT_ALIGN(beMdmView, "middle");
				EiPageSP.BeNifiDataForm beNifiView = new EiPageSP.BeNifiDataForm();
				ZKS.VERT_ALIGN(beNifiView, "middle");
				return ARR.as(beMdmView, beNifiView);
			}

			@Override
			protected List<Component> getBeginComs() {
				return ARR.as(src != null ? src : (src = new EiPageSP.ChoiceMdmStandDd(true)));
			}

			@Override
			protected void onClickStart(Event e) {
				new EiRun(ImportSection.this).doRun();
			}
		});

//		lpPanel = new EiPageSP.LpPanel();
//		appendChild(lpPanel);
		appendChild(importPropsPanel = new ImportPropsPanel());

//		appendChild(EiPageSP.HR_SECTION.get());

//		appendChild(outPan);

		EiPageSP.SECTION_BR.apply(this);

	}

}
