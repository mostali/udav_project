package zk_com.base_ext;

import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.pare.Pare3;
import mpu.str.Sb;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.*;
import zk_page.ZKC;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Tree0 extends Tree {

	public Tree0() {
		super();
	}

	public static Sb buildReport(SelectEvent event) {

		int lvl0 = 0;
		int lvl1 = lvl0 + 1;
		int lvl2 = lvl0 + 2;

		Sb sb = new Sb();

		Component reference = event.getReference();
		int keys = event.getKeys();
		sb.TABNL(lvl0, "Tree:" + keys + "/" + reference);

		Set selectedObjects = event.getSelectedObjects();
		sb.TABNL(lvl1, "selectedObjects*" + X.sizeOf0(selectedObjects) + "/" + selectedObjects);

		Set unselectedObjects = event.getUnselectedObjects();
		sb.TABNL(lvl1, "unselectedObjects*" + X.sizeOf0(unselectedObjects) + "/" + unselectedObjects);

		Set previousSelectedObjects = event.getPreviousSelectedObjects();
		sb.TABNL(lvl1, "previousSelectedObjects*" + X.sizeOf0(previousSelectedObjects) + "/" + previousSelectedObjects);

		//

		Set selectedItems = X.toObjOr(event.getSelectedItems(), ARR.EMPTY_SET);
		sb.TABNL(lvl1, "selectedItems*" + X.sizeOf0(selectedItems) + "/" + selectedItems.stream().map(c -> ZKC.toStringLog((Component) c)).collect(Collectors.toList()));

		Set unselectedItems = event.getUnselectedItems();
		sb.TABNL(lvl1, "unselectedItems*" + X.sizeOf0(unselectedItems) + "/" + unselectedItems.stream().map(c -> ZKC.toStringLog((Component) c)).collect(Collectors.toList()));

		Set previousSelectedItems = event.getPreviousSelectedItems();
		sb.TABNL(lvl1, "previousSelectedItems*" + X.sizeOf0(previousSelectedItems) + "/" + previousSelectedItems.stream().map(c -> ZKC.toStringLog((Component) c)).collect(Collectors.toList()));

		return sb;
	}

	public static Pare3<TreeNode, Treeitem, Boolean> findSelectedNode(SelectEvent event, boolean multiChoice) {
		Set<TreeNode> selectedObjects = event.getSelectedObjects();
		Set<Treeitem> selectedItems = event.getSelectedItems();
		Set<TreeNode> previousSelectedObjects = event.getPreviousSelectedObjects();
		Set<Treeitem> previousSelectedItems = event.getPreviousSelectedItems();

		int sizeSelected = X.sizeOf(selectedItems);
		int sizeSelectedPrev = X.sizeOf(previousSelectedItems);

		if (multiChoice) {
			IT.state(sizeSelected != sizeSelectedPrev, "except selected");
		}
		HashSet<TreeNode> hashSetObjs;
		HashSet<Treeitem> hashSetItems;
		Pare3<TreeNode, Treeitem, Boolean> selectedNode;
		Boolean isSelected;
		if (sizeSelected > sizeSelectedPrev) {
			hashSetObjs = new LinkedHashSet(selectedObjects);
			hashSetItems = new LinkedHashSet(selectedItems);
			if (X.sizeOf(previousSelectedObjects) > 0) {
				if (previousSelectedObjects.size() > 0) {
					boolean state = hashSetObjs.removeAll(previousSelectedObjects);
					if (multiChoice) {
						IT.state(state, "except remove obj (selected)");
					}
				}
				if (previousSelectedItems.size() > 0) {
					boolean state = hashSetItems.removeAll(previousSelectedItems);
					if (multiChoice) {
						IT.state(state, "except remove item (selected)");
					}
				}
			}
			isSelected = true;
		} else {
			hashSetObjs = new LinkedHashSet<>(previousSelectedObjects);
			hashSetItems = new LinkedHashSet(previousSelectedItems);
			if (X.sizeOf(previousSelectedObjects) > 1) {
				if (selectedObjects.size() > 0) {
					boolean state = hashSetObjs.removeAll(selectedObjects);
					if (multiChoice) {
						IT.state(state, "except remove obj (unselected)");
					}
				}
				if (selectedItems.size() > 0) {
					boolean state = hashSetItems.removeAll(selectedItems);
					if (multiChoice) {
						IT.state(state, "except remove item (unselected)");
					}
				}
			}
			isSelected = false;
		}
		selectedNode = Pare3.of(ARRi.first(hashSetObjs), ARRi.first(hashSetItems), isSelected);
		return selectedNode;

	}

	public static void swapAllChilds(Tree tree, SelectEvent event, Boolean recursive, boolean multi) {
		Pare3<TreeNode, Treeitem, Boolean> nodeItem = findSelectedNode(event, multi);
		TreeNode node = nodeItem.key();
		Treeitem item = nodeItem.val();
		boolean whatIsNeed_SelOrUnsel = nodeItem.ext();

//		List<Component> children = item.getChildren();
		List<TreeNode> nodes = node.getChildren();
		if (X.empty(nodes)) {
			return;
		}

		AbstractTreeModel<TreeNode> model = (AbstractTreeModel) tree.getModel();

//		X.p("Found child's:" + children);
		X.p("Found node's:" + nodes);

		nodes.forEach(childNode -> swapNode(model, childNode, whatIsNeed_SelOrUnsel, recursive));

//		children.forEach(c -> {
//			if (c instanceof Treerow) {
//				Treerow treerow = (Treerow) c;
////				Treecell treecell = ARRi.first(treerow.getChildren());
////				treecell.getTreecol().getChildren()
//				X.p("Treerow:" + treerow.getChildren());
//				return;
//			} else if (c instanceof Treechildren) {
//				Treechildren treechildren = (Treechildren) c;
//				X.p("Treechildren:" + treechildren.getChildren());
////				model.addToSelection(treechildren.getChildren());
//				return;
//			} else {
//				X.p("Undefined tree child:" + c);
//			}
//		});

	}

	public static void swapNode(AbstractTreeModel<TreeNode> model, TreeNode node, boolean whatIsNeed_SelOrUnsel, Boolean recursive) {
		if (recursive == null) { //simple all (include leaf and parent's)
			onActionNode(model, node, whatIsNeed_SelOrUnsel);
		} else if (!recursive) {
			if (node.isLeaf()) {
				onActionNode(model, node, whatIsNeed_SelOrUnsel);
			} else {
				//parent stay without change
				X.nothing();
			}
		} else {
			if (node.isLeaf()) {
				onActionNode(model, node, whatIsNeed_SelOrUnsel);
			} else {
				List<TreeNode> children = node.getChildren();
				onActionNode(model, node, whatIsNeed_SelOrUnsel);//swap self
				children.forEach(childNode -> swapNode(model, childNode, whatIsNeed_SelOrUnsel, recursive));
			}
		}
	}

	public static void onActionNode(AbstractTreeModel<TreeNode> model, TreeNode node, boolean whatIsNeed_SelOrUnsel) {
		if (whatIsNeed_SelOrUnsel) {
			model.addToSelection(node);
		} else {
			model.removeFromSelection(node);
		}
	}
}
