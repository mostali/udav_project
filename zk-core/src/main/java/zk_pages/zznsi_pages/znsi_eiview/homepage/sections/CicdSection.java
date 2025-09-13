package zk_pages.zznsi_pages.znsi_eiview.homepage.sections;

import mpu.IT;
import mpu.str.TKN;
import zk_com.base_ctr.Div0;
import zk_page.ZKCFinder;
import zk_page.ZKColor;
import zk_pages.zznsi_pages.znsi_eiview.homepage.ExportPropsPanel;
import zk_pages.zznsi_pages.znsi_eiview.homepage.ImportPropsPanel;
import zk_pages.zznsi_pages.znsi_eiview.homepage.SectionHeader;

public class CicdSection extends Div0 {

	public String getSectionName() {
		String simpleName = getClass().getSimpleName();
		return TKN.firstGreedy(simpleName, "Section", simpleName);
	}

	public ZKColor getSectionColor() {
		return ZKColor.GRAY;
	}

	public SectionHeader getSectionHeader() {
		return ZKCFinder.find_inChilds(this, SectionHeader.class, false, true).get(0);
//		return getChildren().stream().filter(c->c instanceof SectionHeader)
	}

	public ExportPropsPanel getExportPropsPanel() {
		IT.state(this instanceof ExportSection, "except ExportSection");
		return ZKCFinder.find_inChilds(this, ExportPropsPanel.class, false, true).get(0);
//		return getChildren().stream().filter(c->c instanceof SectionHeader)
	}

	public ImportPropsPanel getImportPropsPanel() {
		IT.state(this instanceof ImportSection);
		return ZKCFinder.find_inChilds(this, ImportPropsPanel.class, false, true).get(0);
//		return getChildren().stream().filter(c->c instanceof SectionHeader)
	}
}
