package zk_pages.zznsi_pages.znsi_eiview.homepage;

import mpc.types.ruprops.RuProps;
import zk_com.base.Lb;
import zk_com.base.Tbx;
import zk_com.base.Tbxm;
import zk_com.base.Xml;
import zk_com.base_ctr.Div0;
import zk_notes.node.NodeDir;

public class ExportPropsPanel extends Div0 implements IProfilable {

	public EiPageSP.LpPanel lpPanel;

	public Tbxm tbxAllowedDocs = (Tbxm) new Tbxm().placeholder("allowedDocs - REF_OKVED,REF_OK_LOAD,REF_IPandKFH,REF_EGRIP_20,REF_ProvidersZHKH").width(600);
	public Tbx tbxAllowedRoles = (Tbx) new Tbx().placeholder("allowedRoles - Super_user_MDM,Support_MDM,user_MDM,Admin_MDM").width(800);

	@Override
	public void updatePropsFromProfile(NodeDir nodeDir) {
//			FormState state = nodeDir.state();
		RuProps ruProps = nodeDir.state().readFcDataAsRuProps();

		String allowedDocs = ruProps.getString("allowedDocs", null);
		String allowedRoles = ruProps.getString("allowedRoles", null);
		if (allowedDocs != null) {
			tbxAllowedDocs.setValue(allowedDocs);
		}
		if (allowedRoles != null) {
			tbxAllowedRoles.setValue(allowedRoles);
		}
	}

	@Override
	protected void init() {
		super.init();

		lpPanel = new EiPageSP.LpPanel();

		Lb lbDocs = (Lb) Lb.of("Filter for export model.xml : ").bold().block();
		Lb lbRoles = (Lb) Lb.of("Filter for export roles.json : ").bold().block();

//			tbxAllowedDocs.setValue("REF_OKVED,REF_OK_LOAD,REF_IPandKFH,REF_EGRIP_20,REF_ProvidersZHKH");
		tbxAllowedDocs.width(600);
//			tbxAllowedRoles.setValue("Super_user_MDM,Support_MDM,user_MDM,Admin_MDM");
		tbxAllowedRoles.width(600);

		appendChilds(
				lpPanel,
				Xml.BR(), Xml.BR(),
				lbDocs, tbxAllowedDocs, Xml.BR(), Xml.BR(),
				lbRoles, tbxAllowedRoles
		);
	}


}
