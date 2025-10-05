package zklogapp.docker;

import com.github.dockerjava.api.model.Container;
import lombok.RequiredArgsConstructor;
import mpc.types.tks.FID;
import zk_com.base.Ln;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_form.events.DefAction;
import zk_form.notify.ZKI;
import zk_notes.apiv1.treenode.DockerSrv;
import zk_page.ZKColor;
import zk_page.ZKME;

import java.util.List;

public class DockerCtrs extends Div0 {
//	public static final int HEIGHT = 1200;

	@Override
	protected void init() {
		super.init();

		appendH(2, "Container's");

		List<Container> allContainers = DockerSrv.getAllContainers();
		allContainers.forEach(c -> appendChild(new DockerContainerView(c)));

		width(45.0);
		padding(10);

		inlineBlock();
		borderSilver();
	}

	@RequiredArgsConstructor
	public static class DockerContainerView extends Div0 {
		final Container c;

		@Override
		protected void init() {
			super.init();

			String name = FID.ofArgs(c.getNames()) + "";

			Ln ln = appendLn((DefAction) e -> ZKME.textReadonly("Docker Image..", c.toString(), true), name);

			Menupopup0 orCreateMenupopup = ln.getOrCreateMenupopup(this);
			orCreateMenupopup.addMI("Start Container", (e) -> ZKI.infoAfterPointer(DockerSrv.startContainer(c.getId())));
			orCreateMenupopup.addMI("Stop Container", (e) -> ZKI.infoAfterPointer(DockerSrv.stopContainer(c.getId())));
			appendLbBlock(c.getId());
			appendLbBlock(c.getState());
//				appendLbBlock(c.getStatus());
			appendLbBlock(c.getCommand());
//				appendLbBlock(c.getCommand());

//			absolute();
//			applyState_RandomOrTopLeft(name);

//			ZKS.APPLY_RANDOM_TOPLEFT(this, 100);

			bgcolor(ZKColor.GREEN.nextColor());

		}
	}


}
