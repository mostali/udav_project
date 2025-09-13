package zk_pages.zznsi_pages.znsi_eiview.homepage.sections;

import mpu.core.ARR;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import zk_com.base.Bt;
import zk_com.core.IReRender;
import zk_com.core.IZWin;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Messagebox;
import zk_notes.node.NodeDir;
import zk_notes.node.core.NVT;
import zk_page.ZKCFinder;
import zk_page.ZKCFinderExt;
import zk_pages.zznsi_pages.znsi_eiview.ConturMdm;
import zk_pages.zznsi_pages.znsi_eiview.homepage.EiPageSP;
import zk_pages.zznsi_pages.znsi_eiview.homepage.SectionHeader;

import java.util.Arrays;
import java.util.List;

public class RcStoreSection extends CicdSection implements IReRender {

	public static RcStoreSection findFirst(RcStoreSection... defRq) {
		return ZKCFinderExt.findFirst_inWin0(RcStoreSection.class, false, defRq);
	}

	public static Component rerenderFirst() {
//		ZKC.printAll();
		RcStoreSection notesSpace = ZKCFinderExt.rerenderFirst(RcStoreSection.class, true);
		return notesSpace;
	}

//	public static Component rerenderFirst() {
//		RcStoreSection first1 = findFirst(null);
//		if (first1 != null) {
//			first1.rerender();
//			return first1;
//		}
//		return null;
//	}

	@Override
	public Component rerender() {
		Component parent = getParent();
		RcStoreSection component = ZKCFinder.find_inChilds(parent, RcStoreSection.class, false, true).get(0);
//		RcStoreSection component = (RcStoreSection) parent.getChildren().stream().filter(c -> c instanceof RcStoreSection).findFirst().get();
		component.detach();
		parent.appendChild(new RcStoreSection());
		ZKI.infoAfterPointer("Section` refreshed");
		return this;
	}

	@Override
	protected void init() {
		super.init();

		appendChild(new SectionHeader("RC Store's") {
			{
				setSingleBt(true);
			}

			@Override
			protected void onClickStart(Event e) {
				RcStoreSection.this.rerender();
			}

			@Override
			protected List<Component> getBeginComs() {
				return ARR.as(new Bt("Clean all").onCLICK(e -> {
					ZKI_Messagebox.showMessageBoxBlueYN("Clean dir", "Clean all rc dir?", (yn) -> {
						if (yn) {
							Arrays.stream(ConturMdm.values()).forEach(i -> NodeDir.ofCurrentPage(i.name()).fdRmIfExist());
//							DirViewNode.findFirst().rerender();
							RcStoreSection.this.rerender();
						}
					});
				}));
			}
		});


		for (ConturMdm value : ConturMdm.values()) {
			NodeDir profile1 = NodeDir.ofCurrentPage(value.name()).createIfNotExist();
			IZWin form = profile1.createForm(NVT.DIR);
			appendChild((Component) form);
		}

		EiPageSP.SECTION_BR.apply(this);

	}


//	private static class StorePanels extends Div0 {
//
//		@Override
//		protected void init() {
//			super.init();
//			flex();
//
//		}
//
//
//	}
}
