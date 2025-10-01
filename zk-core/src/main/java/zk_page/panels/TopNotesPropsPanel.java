package zk_page.panels;

import lombok.RequiredArgsConstructor;
import mp.utl_odb.tree.AppPropDef;
import mp.utl_odb.tree.IAppProps;
import zk_com.base_ctr.Div0;
import zk_os.AppZosProps;
import zk_page.ZKC;
import zk_page.ZKCFinderExt;
import zk_page.ZKS;
import zk_notes.node_state.PropFormItem;
import zk_notes.AppNotesCore;
import zk_notes.AppNotesProps;

import java.util.List;

@RequiredArgsConstructor
public class TopNotesPropsPanel extends Div0 {

	public static TopNotesPropsPanel removeMeFirst(TopNotesPropsPanel... defRq) {
		return ZKC.removeMeFirst(TopNotesPropsPanel.class, true, defRq);
	}

	public static TopNotesPropsPanel findFirst(TopNotesPropsPanel... defRq) {
		return ZKCFinderExt.findFirst_InPage(TopNotesPropsPanel.class, true, defRq);
	}

	public static void openSimple() {
		ZKC.getFirstWindow().appendChild(new TopNotesPropsPanel());
	}

	@Override
	protected void init() {
		super.init();

		ZKS.ABSOLUTE(this);
		ZKS.WIDTH(this, 100.0);

		List<AppPropDef> allProps = IAppProps.getAllProps(AppZosProps.class, AppNotesCore.TREE_PROPS, AppNotesProps.class, AppNotesCore.TREE_PROPS);

		allProps.forEach(appPropDef -> appendChild(new PropFormItem(appPropDef).posRightTop(true)));

	}

}
