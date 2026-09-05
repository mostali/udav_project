package zk_form.tree;

import lombok.RequiredArgsConstructor;
import mpc.exception.WhatIsTypeException;
import mpf.zcall.ZEntity;
import mpf.zcall.ZJar;
import mpf.zcall.ZType;
import mpu.X;
import mpu.pare.Pare3;
import mpu.str.Sb;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.Treeitem;
import zk_com.base_ctr.Div0;
import zk_com.base_ext.Tree0;

import java.util.List;

@RequiredArgsConstructor
public class ZJarViewTree extends Div0 {

	public final String[] pathJar;

	public static class RootTreeNode extends DefaultTreeNode {
		public RootTreeNode(ZJar zType) {
			super(zType.name(), PathTreeNode.toList(zType));
		}

	}

	public static class PathTreeNode extends DefaultTreeNode {
		public final ZEntity zType;

		public static PathTreeNode of(ZEntity zType) {
			if (zType instanceof ZType.ZArg) {
				return new PathTreeNode(zType, zType.name());
			}
			return new PathTreeNode(zType, zType.name(), toList(zType));
		}

		public static PathTreeNode[] toList(ZEntity zType) {
			if (zType instanceof ZJar) {
				List<ZType> zTypes = ((ZJar) zType).getAllZTypes();
				return zTypes.stream().map(PathTreeNode::of).toArray(PathTreeNode[]::new);
			} else if (zType instanceof ZType) {
				List<ZType.ZMethod> zMethods = ((ZType) zType).getAllZMethods();
				return zMethods.stream().map(PathTreeNode::of).toArray(PathTreeNode[]::new);
			} else if (zType instanceof ZType.ZMethod) {
				List<ZType.ZArg> zArgs = ((ZType.ZMethod) zType).getZArgs();
				return zArgs.stream().map(PathTreeNode::of).toArray(PathTreeNode[]::new);
			}
//			return new PathTreeNode[]{new PathTreeNode(zType.name())};
			throw new WhatIsTypeException("What is tree item? " + zType);
		}

		public PathTreeNode(ZEntity zType, String name) {
			super(name);
			this.zType = zType;
		}

		public PathTreeNode(ZEntity zType, String name, PathTreeNode[] children) {
			super(name, children);
			this.zType = zType;
		}

	}

	public void onHappensSelect(SelectEvent event, Pare3<TreeNode, Treeitem, Boolean> selected) {
		Sb sb = Tree0.buildReport(event);
		X.p(sb);
		X.p("----------");
		X.p(selected);
	}

	@Override
	protected void init() {
		super.init();

		Tree0 tree = new Tree0();

		tree.addEventListener(Events.ON_SELECT, (SelectEvent event) -> onHappensSelect(event, Tree0.findSelectedNode(event, false)));

		ZJar zJar = ZJar.of(pathJar);
		DefaultTreeModel treeModel = new DefaultTreeModel(new RootTreeNode(zJar));
		treeModel.setMultiple(true);
		tree.setModel(treeModel);

		appendChild(tree);

	}


}
