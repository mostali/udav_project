package zk_pages.zznsi_pages.znsi_eiview.homepage;

import mpc.types.ruprops.RuProps;
import zk_com.base.Lb;
import zk_com.base.Tbx;
import zk_com.base_ctr.Div0;
import zk_notes.node.NodeDir;

public class NifiPropsPanel extends Div0 implements IProfilable {

	public static final String CK_NIFIR_BUCKET = "nifirBucket";
	public static final String CK_NIFIR_FLOW = "nifirFlow";
	public static final String CK_NIFIR_VERSION = "nifirVersion";

	public EiPageSP.LpPanel lpPanel;

	public Tbx tbxNifirBucket = (Tbx) new Tbx().placeholder(CK_NIFIR_BUCKET).width(300);
	public Tbx tbxNifirFlow = (Tbx) new Tbx().placeholder(CK_NIFIR_FLOW).width(300);
	public Tbx tbxNifirVersion = (Tbx) new Tbx().placeholder(CK_NIFIR_VERSION).width(300);

	@Override
	public void updatePropsFromProfile(NodeDir nodeDir) {

		RuProps ruProps = nodeDir.state().readFcDataAsRuProps();

		String nifirBucket = ruProps.getString(CK_NIFIR_BUCKET, null);
		String nifirFlow = ruProps.getString(CK_NIFIR_FLOW, null);
		String nifirVersion = ruProps.getString(CK_NIFIR_VERSION, null);
		if (nifirBucket != null) {
			tbxNifirBucket.setValue(nifirBucket);
		}
		if (nifirFlow != null) {
			tbxNifirFlow.setValue(nifirFlow);
		}
		if (nifirVersion != null) {
			tbxNifirVersion.setValue(nifirVersion);
		}
	}

	@Override
	protected void init() {
		super.init();

		lpPanel = new EiPageSP.LpPanel();

		Lb lbDocs = (Lb) Lb.of("Set Bucket / Flow / Version : ").bold().block();
//		Lb lbRoles = (Lb) Lb.of("Filter for export roles.json : ").bold().block();
//		Lb lbRoles = (Lb) Lb.of("Filter for export roles.json : ").bold().block();

//			tbxAllowedDocs.setValue("REF_OKVED,REF_OK_LOAD,REF_IPandKFH,REF_EGRIP_20,REF_ProvidersZHKH");
//		tbxAllowedDocs.width(600);
//			tbxAllowedRoles.setValue("Super_user_MDM,Support_MDM,user_MDM,Admin_MDM");
//		tbxAllowedRoles.width(600);

		appendChilds(lbDocs, tbxNifirBucket, tbxNifirFlow, tbxNifirVersion);
//		appendChilds(
//				lpPanel,
//				Xml.BR(), Xml.BR(),
//				lbDocs, tbxNifirBucket, Xml.BR(), Xml.BR(),
//				lbRoles, tbxAllowedRoles
//		);
	}


}
