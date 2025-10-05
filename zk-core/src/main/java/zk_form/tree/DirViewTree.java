package zk_form.tree;

import lombok.RequiredArgsConstructor;
import mpu.Sys;
import mpu.X;
import mpu.core.ARR;
import mpu.pare.Pare3;
import mpu.str.Sb;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.*;
import zk_com.base_ctr.Div0;
import zk_com.base_ext.Tree0;
import zk_page.ZKS;

import java.io.File;
import java.util.Arrays;

import static com.google.common.collect.Lists.newArrayList;

@RequiredArgsConstructor
public class DirViewTree extends Div0 {

	final String pathDir;

	public static class RootTreeNode extends DefaultTreeNode {
		public RootTreeNode(File dir) {
			super(dir.getName(), PathTreeNode.toList(dir, true));
		}
	}

	public static class PathTreeNode extends DefaultTreeNode {
		//		private boolean loadChildren = false;
		//private List<TreeNode<T>> children = newArrayList();

		public static PathTreeNode of(File file) {
			if (file.isDirectory()) {
				return new PathTreeNode(file.getName(), toList(file, false));
			}
			return new PathTreeNode(file.getName());
		}

		public static PathTreeNode[] toList(File file, boolean root) {
			File[] array = root ? ARR.of(file) : file.listFiles();
			array = array == null ? new File[0] : array;
			return Arrays.stream(array).map(PathTreeNode::of).toArray(PathTreeNode[]::new);
		}

		public PathTreeNode(String name) {
			super(name);
		}

		public PathTreeNode(String name, PathTreeNode[] children) {
			super(name, children);
		}

	}

	@Override
	protected void init() {
		super.init();

//		appendHtml("<hr/>");
		ZKS.WIDTH_HEIGHT100(this);

		appendBt((SerializableEventListener) (Event) -> {
			Sys.say("ok");
		}, "Go");

		Tree0 tree = new Tree0();

		tree.addEventListener(Events.ON_SELECT, (SelectEvent event) -> {
			Sb sb = Tree0.buildReport(event);
			X.p(sb);
			X.p("----------");
			Pare3<TreeNode, Treeitem, Boolean> selected = Tree0.findSelectedNode(event, tree.isMultiple());
			X.p(selected);
			Tree0.swapAllChilds(tree, event, true, true);

		});
		if (true) {
			DefaultTreeModel treeModel = new DefaultTreeModel(new RootTreeNode(new File(pathDir)));
			RootTreeNode root = (RootTreeNode) treeModel.getRoot();
			treeModel.setMultiple(true);
			tree.setModel(treeModel);
			tree.setCheckmark(true);
		}

		appendChild(tree);

//		appendBr();
		appendHtml("<hr/>");
	}


}
