package zk_pages.zznsi_pages.znsi_eiview.homepage.sections;

import lombok.RequiredArgsConstructor;
import mpc.str.sym.SYMJ;
import mpu.core.ARR;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.impl.InputElement;
import zk_com.base.Bt;
import zk_com.base.Tbxm;
import zk_com.base_ctr.Div0;
import zk_com.core.IZCom;
import zk_com.core.IZWin;
import zk_form.notify.ZKI;
import zk_notes.node.NodeDir;
import zk_notes.node.core.NVT;
import zk_page.ZKColor;
import zk_page.ZKS_AutoDims;
import zk_pages.zznsi_pages.znsi_eiview.homepage.EiPageSP;
import zk_pages.zznsi_pages.znsi_eiview.homepage.EiRun;
import zk_pages.zznsi_pages.znsi_eiview.homepage.SectionHeader;

import java.util.List;

public class NifiImportSection extends CicdSection {

	@Override
	public ZKColor getSectionColor() {
		return ZKColor.GREEN;
	}

	@Override
	protected void init() {
		super.init();

		appendChild(new SectionHeader(SYMJ.STAR_SIMPLE_ROUND + " NIFI Import") {
			EiPageSP.ChoiceMdmStandDd src;

			{
				setMdmOrNifiDdFilter(false);
			}

			@Override
			protected List<Component> getBeginComs() {
				return ARR.as(src != null ? src : (src = new EiPageSP.ChoiceMdmStandDd(false)));
			}

			@Override
			protected void onClickStart(Event e) {
				new EiRun(NifiImportSection.this).doRun();
			}
		});

//		appendChild(EiPageSP.HR_SECTION.get());

		appendChild(new NifiPanel());

		EiPageSP.SECTION_BR.apply(this);

	}


	private static class NifiPanel extends Div0 {

		@Override
		protected void init() {
			super.init();


//			ProfileItem div0 = new ProfileItem("profile1");
//			appendChild(div0);
//			ProfileItem div2 = new ProfileItem("profile2");
//			appendChild(div2);

		}

		@RequiredArgsConstructor
		private static class ProfileItem extends Div0 {

			final String profile;
			Bt cb;

			@Override
			protected void init() {
				super.init();

				NodeDir profile1 = NodeDir.ofCurrentPage(profile);
				IZWin form = profile1.createForm(NVT.TEXT);
				appendChild((Component) form);

				cb = new Bt().onCLICK(e -> {

//						List<ProfileItem> items = (List) ProfileItem.this.getParent().getChildren().stream().filter(c -> c instanceof ProfileItem).collect(Collectors.toList());
//						items.forEach(i -> ProfileItem.this.cb.setChecked(false));

//						Cb this0 = (Cb) e.getTarget();
//
//						this0.setChecked(true);
					ZKI.infoAfterPointer("Profile active - " + profile, ZKI.Level.INFO);
				});

				cb.setLabel(SYMJ.OK_GREEN);
				appendChild(cb);

				IZCom multiMapWithProperty = new Tbxm().placeholder("multi map with property");
				appendChild((Component) multiMapWithProperty);

				ZKS_AutoDims.initAutoDims((InputElement) multiMapWithProperty, true);
			}
		}

	}
}
