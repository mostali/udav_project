package zk_pages.zznsi_pages.znsi_eiview.homepage;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import mpc.exception.NI;
import mpu.X;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import zk_com.base.Bt;
import zk_com.base_ctr.Div0;
import zk_form.notify.ZKI;
import zk_notes.node.NodeDir;
import zk_page.ZKCFinder;
import zk_pages.zznsi_pages.znsi_eiview.homepage.sections.CicdSection;

import java.util.List;

@RequiredArgsConstructor
public class SectionHeader extends Div0 {

	final String name;

	@Setter
	boolean isMdmOrNifiDdFilter = true;
	@Setter
	boolean isSingleBt = false;


	EiPageSP.ChoiceMdmStandDd choiceConturDd;
	EiPageSP.ChoiceProfileDd choiceProfileDd;

//	public EiPageSP.BeAppDataForm getBeAppDataForm() {
//		return ZKCFinder.findAllFromParent(this, EiPageSP.BeAppDataForm.class, false, true).get(0);
//	}

	public EiPageSP.BeMdmDataForm getBeMdmDataForm() {
		return ZKCFinder.find_inChilds(this, EiPageSP.BeMdmDataForm.class, false, true).get(0);
	}

	public EiPageSP.BeNifiDataForm getBeaNifiDataForm() {
		return ZKCFinder.find_inChilds(this, EiPageSP.BeNifiDataForm.class, false, true).get(0);
	}

	protected List<Component> getEndComs() {
		return null;
	}

	protected List<Component> getBeginComs() {
		return null;
	}

	protected void onClickStart(Event e) {
		throw new NI(getParentClassSimpleName());
	}

	protected void onChoiceContur(Event e) {

	}

	public String getParentClassSimpleName() {
		return getParent().getClass().getSimpleName();
	}

	public String getChoicedProfileName() {
		return choiceProfileDd.getValue();
	}

	public String getChoicedConturName() {
		return choiceConturDd.getValue();
	}

	protected void onChoiceProfile(Event e) {

		Component parent = getParent();
		if (!(parent instanceof IProfilable)) {
			return;
		}
		IProfilable profilable = (IProfilable) parent;

		EiPageSP.ChoiceProfileDd choiceProfileDd = (EiPageSP.ChoiceProfileDd) e.getTarget();
		String value = choiceProfileDd.getValue();

		ZKI.infoAfterPointer("Choiced profile:" + value);

		profilable.updatePropsFromProfile(NodeDir.ofCurrentPage(value));
//			profilable.update();
	}

	public CicdSection getParentSection() {
		return (CicdSection) getParent();
	}

	@Override
	protected void init() {
		super.init();

		bgcolor(getParentSection().getSectionColor().nextColor());

		padding(20);

		Bt exportBt = new Bt(name);
		exportBt.onCLICK(e -> onClickStart(e));

		choiceConturDd = new EiPageSP.ChoiceMdmStandDd(isMdmOrNifiDdFilter);
		choiceConturDd.onCHANGE(e -> onChoiceContur(e));

		choiceProfileDd = new EiPageSP.ChoiceProfileDd();
		choiceProfileDd.onCHANGE(e -> onChoiceProfile(e));

		List<Component> headerComs = getBeginComs();
		if (X.notEmpty(headerComs)) {
			appendChilds(headerComs);
		}

		appendChilds(exportBt);

		if (!isSingleBt) {
			appendChilds(choiceConturDd, choiceProfileDd);
		}

		List<Component> footerComs = getEndComs();
		if (X.notEmpty(footerComs)) {
			appendChilds(footerComs);
		}

		addSTYLE("text-align:center");
//			Div0 header = (Div0) div0.addSTYLE("text-align:center");


//			appendChild(Xml.HR());
	}
}
