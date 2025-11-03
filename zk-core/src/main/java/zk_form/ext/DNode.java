package zk_form.ext;

import lombok.RequiredArgsConstructor;
import org.zkoss.zul.Window;
import zk_com.base_ctr.Div0;
import zk_notes.factory.NFOpen;
import zk_notes.node.NodeDir;
import zk_page.ZKS;

@RequiredArgsConstructor
public class DNode extends Div0 {
	final NodeDir nodeLeft, nodeRight;

	public static DNode of(NodeDir nodeLeft, NodeDir nodeRight) {
		return new DNode(nodeLeft, nodeRight);
	}

	@Override
	protected void init() {
		super.init();

//		ZKS.WIDTH(this, 100.0);

		Window leftWin = NFOpen.openFormRequired(nodeLeft);
		Window rightWin = NFOpen.openFormRequired(nodeRight, leftWin);


//		ZKS.PADDING_WIN(IT.NN(leftWin), 0, 0);
//		ZKS.PADDING_TOP_WIN(IT.NN(leftWin), 0, 100);
//		ZKS.PADDING_WIN(leftWin, 0, 0);
//		ZKS.MARGIN_LEFT(leftWin, 180);

		leftWin.doEmbedded();
		ZKS.CENTER(leftWin);

		rightWin.doEmbedded();


		ZKS.INLINE_BLOCK(rightWin);

//		Component com = leftWin.getChildren().get(0);
//		ZKS.WIDTH((HtmlBasedComponent) com, 40.0);

		ZKS.WIDTH(rightWin, 40.0);

//		ZKS.BORDER_GRAY(leftWin);
//		ZKS.BORDER_GRAY(rightWin);
	}

}
