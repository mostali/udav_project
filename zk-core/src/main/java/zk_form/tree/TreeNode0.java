package zk_form.tree;

import mpu.IT;
import mpu.X;
import mpu.str.STR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.TreeNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TreeNode0<T> implements TreeNode<T>, Serializable {

	private static final Logger log = LoggerFactory.getLogger(TreeNode0.class);

	private T data;

	private final List<TreeNode<T>> children = new ArrayList<>();
	private DefaultTreeModel<T> model;
	private TreeNode<T> parent;

	@Override
	public String toString() {
		return getClass().getSimpleName() + "::" + X.toString(data) + STR.ARR_DEL + parent;
	}

	public TreeNode0() {
	}

	public TreeNode0(T data, TreeNode<T> parent) {
		IT.notNull(data);
		IT.notNull(parent);
		this.data = data;
		this.parent = parent;
	}

	@Override
	public boolean isLeaf() {
		return children == null || children.isEmpty();
	}

	@Override
	public DefaultTreeModel<T> getModel() {
		return model;
	}

	@Override
	public void setModel(DefaultTreeModel<T> model) {
		this.model = model;
	}

	@Override
	public T getData() {
		return data;
	}

	@Override
	public void setData(T data) {
		this.data = data;
	}

	@Override
	public List<TreeNode<T>> getChildren() {
		return children;
	}

	@Override
	public TreeNode<T> getChildAt(int childIndex) {
		return children.get(childIndex);
	}

	@Override
	public int getChildCount() {
		return children.size();
	}

	@Override
	public TreeNode<T> getParent() {
		return parent;
	}

	@Override
	public int getIndex(TreeNode<T> node) {
		return children.indexOf(node);
	}

	@Override
	public void insert(TreeNode<T> child, int index) {
		children.add(index, child);
	}

	@Override
	public void add(TreeNode<T> child) {
		children.add(child);
	}

	@Override
	public void remove(int index) {
		children.remove(index);
	}

	@Override
	public void remove(TreeNode<T> child) {
		children.remove(child);
	}

	@Override
	public Object clone() {
		return new TreeNode0<>(data, parent);
	}

	public static class RootTreeNode extends TreeNode0<ISourceItem> {
		public RootTreeNode() {
		}
	}

	public static TreeNode0 createSysNode(TreeNode<ISourceItem> parent, String name) {
		TreeNode0<ISourceItem> sysNode = new TreeNode0<>(new SourceItem(null, name), parent);
		parent.add(sysNode);
		return sysNode;
	}
}
