package zk_pages.zznsi_pages.znsi_eiview.homepage.sections;

import mpc.str.sym.SYMJ;
import org.zkoss.zk.ui.event.Event;
import zk_notes.node.NodeDir;
import zk_page.ZKColor;
import zk_pages.zznsi_pages.znsi_eiview.homepage.*;

public class NifiExportSection extends CicdSection implements IProfilable {

	public NifiPropsPanel propsPanel;

	@Override
	public ZKColor getSectionColor() {
		return ZKColor.GREEN;
	}

	@Override
	public void updatePropsFromProfile(NodeDir nodeDir) {
		propsPanel.updatePropsFromProfile(nodeDir);
		propsPanel.lpPanel.updatePropsFromProfile(nodeDir);
	}

	@Override
	protected void init() {
		super.init();

		appendChild(new SectionHeader(SYMJ.STAR_SIMPLE_ROUND + " NIFI Export") {

			{
				setMdmOrNifiDdFilter(false);
			}

			@Override
			protected void onClickStart(Event e) {
				new EiRun(NifiExportSection.this).doRun();
			}

			@Override
			protected void onChoiceProfile(Event e) {
				super.onChoiceProfile(e);
			}
		});

		appendChild(propsPanel = new NifiPropsPanel());

		EiPageSP.SECTION_BR.apply(this);

	}

}
