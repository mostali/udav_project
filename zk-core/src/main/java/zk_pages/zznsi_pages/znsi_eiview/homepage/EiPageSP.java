package zk_pages.zznsi_pages.znsi_eiview.homepage;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.types.ruprops.RuProps;
import mpu.core.ARR;
import mpu.func.FunctionV1;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;
import zk_com.base.*;
import zk_com.base_ctr.Div0;
import zk_com.sun_editor.IPerPage;
import zk_form.WithLogo;
import zk_form.events.IBoolEvent;
import zk_form.head.StdHeadLib;
import zk_notes.control.NoteLogo;
import zk_notes.node.NodeDir;
import zk_os.sec.ROLE;
import zk_os.sec.Sec;
import zk_page.ZKPage;
import zk_page.ZKS;
import zk_page.core.PageRoute;
import zk_page.core.PageSP;
import zk_page.core.SpVM;
import zk_page.core.WithSearch;
import zk_page.index.control.TopFixedPanel;
import zk_pages.zznsi_pages.znsi_eiview.homepage.sections.*;
import zklogapp.header.BottomHistoryPanel;
import zk_pages.zznsi_pages.znsi_eiview.BEAPP;
import zk_pages.zznsi_pages.znsi_eiview.UMdmStand;

import java.util.function.Supplier;

@PageRoute(pagename = "eiview", role = ROLE.ANONIM)
public class EiPageSP extends PageSP implements IPerPage, WithLogo, WithSearch {

	public static FunctionV1<Component> SECTION_BR = (window0) -> {
		window0.appendChild(Xml.BR());
		window0.appendChild(Xml.BR());
		window0.appendChild(Xml.HR());
		window0.appendChild(Xml.BR());
		window0.appendChild(Xml.BR());
	};

	public static final Supplier<Component> SECTION_HR = () -> {
		return Xml.ofXml("");
//		return Xml.HR(16, ZKColor.BLUE.nextColor());
	};//, WithLogo

	public EiPageSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

	@Override
	public LogoCom getLogoDefault() {
		LogoCom first = LogoCom.findFirst(null);
		if (first != null) {
			return first;
		}
		NoteLogo noteLogo = new NoteLogo(null, "/img/xnr16.png");
		return noteLogo;
	}

	ProfileSection profileSection;

	@SneakyThrows
	public void buildPageImpl() {

		ZKS.PADDING0(window);
		ZKS.MARGIN(window, "30px 0 0 0");

		window.appendChild(getLogoOrAdd());

		ZKPage.renderHeadRsrcs(window, StdHeadLib.JS_CODEMIRROR_6_65_7);
		ZKPage.renderHeadRsrcs(window, StdHeadLib.JS_CODEMIRROR_6_65_7_PERL);
		ZKPage.renderHeadRsrcs(window, StdHeadLib.CSS_CODEMIRROR_6_65_7);
		ZKPage.renderHeadRsrcs(window, StdHeadLib.CSS_CODEMIRROR_6_65_7_THEME_ABBOTT);

		IBoolEvent.initNewAndAppend(window);
//		window.appendChild(new LogPageHeader());

		window.appendChild(new BottomHistoryPanel());

		if (Sec.isEditorAdminOwner()) {
			window.appendChild(new TopFixedPanel());
//			window.appendChild(new RightControlPanel());
		}

		window.appendChild(profileSection = new ProfileSection());

		window.appendChild(new ExportSection());

		window.appendChild(new ImportSection());

		window.appendChild(new NifiExportSection());

		window.appendChild(new NifiImportSection());

//		window.appendChild(new ConturSection());
//		br.apply();

		//
		//

		window.appendChild(new RcStoreSection());

		//
		//

//		window.appendChild(Div0.of(new Bt("Import").on CLICK(e -> update(true))).addSTYLE("text-align:center"));
//		window.appendChild(Div0.of(new Bt("ToHtml").onCLICK(e -> updateReverse(true))).addSTYLE("text-align:center"));

	}


	//
	//                     FORM with checkbox
	//


	public static class BeMdmDataForm extends Div0 {
		@Override
		protected void init() {
			super.init();
			inlineBlock();
			for (BEAPP.BEMDM value : BEAPP.BEMDM.values()) {
				BeMdmDataCb child = new BeMdmDataCb(value);
				child.block();
				appendChild(child);
			}

		}

		public boolean isChecked(BEAPP.BEMDM beapp) {
			return getChildren().stream().filter(c -> c instanceof BeMdmDataCb).anyMatch(c -> ((BeMdmDataCb) c).be == beapp && ((BeMdmDataCb) c).isChecked());
		}
	}

	public static class BeAppDataForm extends Div0 {
		@Override
		protected void init() {
			super.init();
			inlineBlock();
			for (BEAPP value : BEAPP.values()) {
				BeAppDataCb child = new BeAppDataCb(value);
				child.block();
				appendChild(child);
			}
		}

		public boolean isChecked(BEAPP beapp) {
			return getChildren().stream().filter(c -> c instanceof BeAppDataCb).anyMatch(c -> ((BeAppDataCb) c).be == beapp && ((BeAppDataCb) c).isChecked());
		}
	}

	public static class BeNifiDataForm extends Div0 {
		@Override
		protected void init() {
			super.init();
			inlineBlock();
			for (BEAPP.BENIFI value : BEAPP.BENIFI.values()) {
				BeNifiDataCb child = new BeNifiDataCb(value);
				child.block();
				appendChild(child);
			}
		}

		public boolean isChecked(BEAPP.BENIFI beapp) {
			return getChildren().stream().filter(c -> c instanceof BeNifiDataCb).anyMatch(c -> ((BeNifiDataCb) c).be == beapp && ((BeNifiDataCb) c).isChecked());
		}
	}


	//
	//                     ITEM-checkbox  for FORM
	//


	@RequiredArgsConstructor
	static class BeMdmDataCb extends Cb {
		final BEAPP.BEMDM be;

		@Override
		public void init() {
			super.init();
			setLabel(be.name());
		}

		public boolean isChecked(BEAPP.BEMDM beapp) {
			return getChildren().stream().filter(c -> c instanceof BeMdmDataCb).anyMatch(c -> ((BeMdmDataCb) c).be == beapp && ((BeMdmDataCb) c).isChecked());
		}
	}

	@RequiredArgsConstructor
	static class BeAppDataCb extends Cb {
		final BEAPP be;

		@Override
		public void init() {
			super.init();
			setLabel(be.name());
		}
	}

	@RequiredArgsConstructor
	static class BeNifiDataCb extends Cb {
		final BEAPP.BENIFI be;

		@Override
		public void init() {
			super.init();
			setLabel(be.name());
		}
	}

	//
	//
	//

	public static class LpPanel extends Div0 implements IProfilable {

		Tbx tbxLogin = (Tbx) new Tbx().placeholder("MDM login");
		Tbx tbxPass = (Tbx) new Tbx().placeholder("MDM pass");
		Tbx tbxToken = (Tbx) new Tbx().placeholder("MDM token");

		@Override
		protected void init() {
			super.init();

			Lb lbMdmCreds = Lb.of("Set Login & Pass | Token for Mdm API : ").bold();

			appendChilds(lbMdmCreds, tbxLogin, tbxPass, tbxToken);
		}

		@Override
		public void updatePropsFromProfile(NodeDir nodeDir) {

			RuProps ruProps = nodeDir.state().readFcDataAsRuProps();
			String login = ruProps.getString("login", null);
			String pass = ruProps.getString("pass", null);
			String token = ruProps.getString("token", null);
			if (login != null) {
				tbxLogin.setValue(login);
			}
			if (pass != null) {
				tbxPass.setValue(pass);
			}
			if (token != null) {
				tbxToken.setValue(token);
			}
		}
	}

	//
	//
	//

	public static class ChoiceMdmStandDd extends Dd {

		public ChoiceMdmStandDd(boolean isMdmOrNifi) {
			super(isMdmOrNifi ? ARR.toListString(UMdmStand.getValuesMdm()) : ARR.toListString(UMdmStand.getValuesNifi()));
		}

		@Override
		protected void init() {
			super.init();
			width(150);
		}
	}

	public static class ChoiceProfileDd extends Dd {
		public ChoiceProfileDd() {
			super("", ProfileSection.profiles);
		}

		@Override
		protected void init() {
			super.init();
			width(150);
		}
	}

}
