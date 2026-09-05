package zk_form.tree.old.tree;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;
import zk_form.tree.old.model.Car;
import zk_form.tree.old.model.CarService;
import zk_form.tree.old.model.CarServiceImpl;
import zk_form.tree.old.model.Category;

import java.util.LinkedList;
import java.util.List;

public class CategoryController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	private CarService carService = new CarServiceImpl();
	private TreeModel<TreeNode<Category>> categoriesModel;

	@Wire
	private Grid resultGrid;

	public CategoryController() {
		// construct tree nodes by categories
		Category categoryRoot = carService.getCarCategoriesRoot();
		CategoryTreeNode rootNode = constructCategoryTreeNode(categoryRoot);

		// create tree model
		categoriesModel = new DefaultTreeModel<Category>(rootNode);
		((DefaultTreeModel<Category>) categoriesModel).addOpenPath(new int[]{0});
	}

	private CategoryTreeNode constructCategoryTreeNode(Category category) {
		CategoryTreeNode categoryNode = new CategoryTreeNode(category, carService.countByFilter(category.getName()));
		LinkedList<CategoryTreeNode> queue = new LinkedList<CategoryTreeNode>(); // BFS
		queue.add(categoryNode);
		while (!queue.isEmpty()) {
			CategoryTreeNode node = queue.remove();
			for (Category childCategory : node.getData().getChildren()) {
				CategoryTreeNode child = new CategoryTreeNode(childCategory, carService.countByFilter(childCategory.getName()));
				node.add(child);
				queue.add(child);
			}
		}
		CategoryTreeNode rootNode = new CategoryTreeNode(null, -1); // won't show
		rootNode.add(categoryNode);
		return rootNode;
	}

	public TreeModel<TreeNode<Category>> getCategoriesModel() {
		return categoriesModel;
	}

	@Listen("onSelect = #categoriesTree")
	public void displayCars() {
		TreeNode<Category> selectedNode = ((DefaultTreeModel<Category>) categoriesModel).getSelection()
				.iterator().next();
		Category selectedCategory = selectedNode.getData();
		List<Car> cars = carService.queryByFilter(selectedCategory.getName());
		resultGrid.setModel(new ListModelList<Car>(cars));
	}
}
