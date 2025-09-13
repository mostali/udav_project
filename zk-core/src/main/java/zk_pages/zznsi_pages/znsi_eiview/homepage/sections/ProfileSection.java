package zk_pages.zznsi_pages.znsi_eiview.homepage.sections;

import lombok.RequiredArgsConstructor;
import mpu.core.ARR;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.InputElement;
import zk_com.base_ctr.Div0;
import zk_com.core.IZWin;
import zk_notes.node.NodeDir;
import zk_notes.node.core.NVT;
import zk_page.ZKS_AutoDims;
import zk_pages.zznsi_pages.znsi_eiview.homepage.EiPageSP;
import zk_pages.zznsi_pages.znsi_eiview.homepage.SectionHeader;

import java.util.List;

public class ProfileSection extends CicdSection {

	public static List<String> profiles = ARR.as("profile1", "profile2", "profile3");

	@Override
	protected void init() {
		super.init();

		appendChild(new SectionHeader("Profiles") {
			{
				setSingleBt(true);
			}
		});


		appendChild(new ProfilePanel());

//		appendChild(ImportSection.BOTTOM_SILVER.get());

		EiPageSP.SECTION_BR.apply(this);

	}


	private static class ProfilePanel extends Div0 {

		@Override
		protected void init() {
			super.init();
			flex();

			profiles.forEach(p -> {
				ProfilePanel.ProfileItem div0 = new ProfilePanel.ProfileItem(p);
				appendChild(div0);
			});

		}

		@RequiredArgsConstructor
		private static class ProfileItem extends Div0 {

			final String profile;
//			Bt bt;

			@Override
			protected void init() {
				super.init();

				NodeDir profile1 = NodeDir.ofCurrentPage(profile);
				IZWin form = profile1.createForm(NVT.TEXT);
				form._modal(Window.Mode.EMBEDDED)._showInWindow(this);
				ZKS_AutoDims.initAutoDims((InputElement) form);
//				appendChild((Component) form);

//				bt = new Bt().onCLICK(e -> {
//
////						List<ProfileItem> items = (List) ProfileItem.this.getParent().getChildren().stream().filter(c -> c instanceof ProfileItem).collect(Collectors.toList());
////						items.forEach(i -> ProfileItem.this.cb.setChecked(false));
//
////						Cb this0 = (Cb) e.getTarget();
////
////						this0.setChecked(true);
//					ZKI.infoAfterPointer("Profile active - " + profile, ZKI.Level.INFO);
//				});

//				bt.setLabel(SYMJ.OK_GREEN);
//				appendChild(bt);

//				appendChild((Component) new Tbxm(Tbx.DIMS.BYCONTENT).placeholder("multi map with property"));
			}
		}

	}
}
