package zklogapp.docker;

import com.github.dockerjava.api.model.Image;
import lombok.RequiredArgsConstructor;
import mpc.types.tks.FID;
import mpu.X;
import mpu.core.ARR;
import zk_com.base.Ln;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_form.events.DefAction;
import zk_form.notify.ZKI;
import zk_notes.apiv1.treenode.DockerSrv;
import zk_page.ZKColor;
import zk_page.ZKME;

import java.util.List;

public class DockerImages extends Div0 {
//	public static final int HEIGHT = 1200;

	@Override
	protected void init() {
		super.init();

		appendH(2, "Image's");

		List<Image> allImages = DockerSrv.getAllImages();
		allImages.forEach(c -> appendChild(new DockerImageView(c)));

		width(45.0);
		padding(10);
		inlineBlock();
		borderSilver();

	}

	@RequiredArgsConstructor
	public static class DockerImageView extends Div0 {
		final Image c;

		@Override
		protected void init() {
			super.init();

			L.info("Create Image Div\n" + c);
			String[] collect = ARR.as(c.getRepoTags()).stream().flatMap(is -> ARR.as(is).stream()).filter(X::NE).toArray(String[]::new);

			String name = FID.ofArgs(collect) + "";
			appendLbBlock(name);
//			appendLbBlock(c.getId());

			Ln ln = appendLn((DefAction) e -> ZKME.textReadonly("Docker Image..", c.toString(), true), name);

			Menupopup0 orCreateMenupopup = ln.getOrCreateMenupopup(this);

			orCreateMenupopup.addMI("Remove image []", (e) -> {
				ZKI.infoAfterPointer(DockerSrv.removeImage(c.getId()));
				DockerImageView toRm = (DockerImageView) getParent().getChildren().stream().
						filter(i -> i instanceof DockerImageView).
						filter(i -> ((DockerImageView) i).c.getId().equals(c.getId())).
						findFirst().get();
				toRm.detach();
			});

//			orCreateMenupopup.addMI("Stop", (e) -> ZKI.infoAfterPointer(DockerSrv.removeContainer(c.getId())));

//				appendLbBlock(c.getState());
//				appendLbBlock(c.getStatus());
//				appendLbBlock(c.getCommand());
//				appendLbBlock(c.getCommand());

//			absolute();
//			applyState_RandomOrTopLeft(name);

//			ZKS.APPLY_RANDOM_TOPLEFT(this, HEIGHT);
			bgcolor(ZKColor.ORANGE.nextColor());
		}
	}

}
