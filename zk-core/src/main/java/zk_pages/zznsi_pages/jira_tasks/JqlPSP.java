package zk_pages.zznsi_pages.jira_tasks;

import lombok.SneakyThrows;
import mpc.json.GsonMap;
import mpc.str.condition.StringConditionType;
import mpu.X;
import org.zkoss.zul.Window;
import zk_com.core.IZState;
import zk_com.sun_editor.IPerPage;
import zk_form.WithLogo;
import zk_notes.node.NodeDir;
import zk_notes.node_state.impl.FormState;
import zk_notes.node_state.proxy.StateProxyRW;
import zk_os.sec.ROLE;
import zk_os.walkers.NoteWalker;
import zk_page.core.PageRoute;
import zk_page.core.PageSP;
import zk_page.core.SpVM;
import zk_page.zpage.ZPage;
import zk_page.with_com.WithSearch;
import zk_pages.zznsi_pages.jira_tasks.core.JqlPageView;
import zk_pages.zznsi_pages.jira_tasks.core.RecoveryState;
import zk_pages.zznsi_pages.jira_tasks.core.RecoveryStateSerialize;

@PageRoute(pagename = JqlPSP.KEY, role = ROLE.ANONIM, eqt = StringConditionType.REGEX)
public class JqlPSP extends PageSP implements IPerPage, IZState, WithLogo, WithSearch {

	public static final String KEY = "@@tasks@[a-zA-Z@\\d.]+";

	public static void main(String[] args) {
		X.exit("tasks_daaa.aaa2@asd.ru".matches(KEY));
	}

	public JqlPSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

	@SneakyThrows
	public void buildPageImpl() {

		ZPage zPage = ZPage.of(sdn(), window);

		JqlPageView jqlPageView = new JqlPageView(zPage);

		window.appendChild(jqlPageView);

		jqlPageView.checkAndOpenUpdateHLP();

		NoteWalker.toList(sdn()).forEach(n -> {
			StateProxyRW.CACHE_STORE_LISNNER.put(n.toObjId(), new StateProxyRW.UpdListener() {
				@Override
				public void up(NodeDir nodeDir) {
					FormState state = nodeDir.state();
					String val = state.readFcData(1);
					GsonMap gsonMap = state.readFcDataAsGsonMap(null);
//					new RecoveryStateSerialize(state).serialize();

					RecoveryState.RecModel newRecModel = RecoveryState.RecModel.createNew(nodeDir.nodeName(), gsonMap == null ? null : gsonMap.toStringPrettyJson(), val);

					L.info("Node '{}' update recovery state:{}\n--{}", nodeDir.nodeId(), val,newRecModel);
				}
			});
		});

	}


}
