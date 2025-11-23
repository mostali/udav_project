package zklogapp;

import lombok.RequiredArgsConstructor;
import mp.utl_odb.tree.IAppProps;
import zk_com.base_ctr.Div0;
import zk_notes.AppNotesCore;
import zk_os.AppZosProps;
import zk_page.ZKCFinderExt;
import zk_page.ZKS;
import zk_notes.node_state.PropFormItem;
import mp.utl_odb.tree.AppPropDef;

import java.util.List;

@RequiredArgsConstructor
public class AppLogSettingsPanel extends Div0 {

	public static AppLogSettingsPanel findFirst(AppLogSettingsPanel... defRq) {
		return ZKCFinderExt.findFirst_InPage(AppLogSettingsPanel.class, true, defRq);
	}

//	final Object[] treeConfigs;

	@Override
	protected void init() {
		super.init();

		ZKS.toggleDnone(this);
		ZKS.ABSOLUTE(this);
		ZKS.WIDTH(this, 100.0);

		List<AppPropDef> allProps = IAppProps.getAllProps(AppZosProps.class, AppNotesCore.TREE_PROPS, AppLogProps.class, AppLogCore.TREE_PROPS);
		allProps.forEach(i -> appendChild(new PropFormItem(i)));

	}

}
