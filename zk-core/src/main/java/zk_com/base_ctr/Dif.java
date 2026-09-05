package zk_com.base_ctr;

import mpe.cmsg.core.CallMsg;
import mpe.cmsg.core.ICallMsg;
import mpe.cmsg.core.INodeType;
import mpu.core.ARRi;
import mpu.core.RW;
import mpu.pare.Pare;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;
import zk_com.base.Lb;
import zk_com.core.IZWin;
import zk_notes.factory.NFForm;
import zk_notes.node.NodeDir;

import java.nio.file.Path;

public class Dif extends Div0 {
	public class DifOpts {

	}

	public static Dif of(Path path) {
		String line = ARRi.firstLine(path);
		INodeType nodeTypeByLine0 = INodeType.findNodeTypeByLine0(line);
		ICallMsg iCallMsg = nodeTypeByLine0.stdDesc().newInstanceCallMsg(path);
		NodeDir node = NodeDir.ofFile(Pare.of("tmp", ""), path);
//		nodeTypeByLine0.stdType().
//		IZWin form = node.createForm();


//		Window window = NFForm.openForm(node);
//		return new Dif(coms);
		return null;
	}

	public static Dif of(Component... coms) {
		return new Dif(coms);
	}

	public static Dif of(String content, DifOpts... opts) {
		return new Dif(buildDifCom(content, opts));
	}

	private static Component buildDifCom(String content, DifOpts... opts) {
		return Lb.of(content);
	}

	public Dif(Component... coms) {
		super(coms);
	}


	@Override
	protected void init() {
		super.init();

	}
}
