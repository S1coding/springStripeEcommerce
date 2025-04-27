package springStripeEcommerceService.mainClasses.tree;

import springStripeEcommerceService.mainClasses.item.ItemEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchTree {
	private ArrayList<SearchTree> Children;
	private ArrayList<SearchTree> Parent;
	private ArrayList<String> category;
	private String uniqueId;
	private String text;
	private String name;
	private String brand;
	private String size;
	private String colour;
	private String gender;
	private long cost;
	private byte[] img;

	public static SearchTree convertToSearchTree(ItemEntity node){
		SearchTree searchTree = new SearchTree(
				new ArrayList<SearchTree>(),
				new ArrayList<SearchTree>(),
				new ArrayList<String>(),
				node.getUniqueId(),
				node.getText(),
				node.getName(),
				node.getBrand(),
				node.getSize(),
				node.getColour(),
				node.getGender(),
				node.getCost(),
				node.getImg()
		);

		return searchTree;
	}
	public boolean traverseTreeAndFindBoolean(SearchTree node, SearchTree root) {
		if (root.getText().equals(node.getText())) {
			return true;
		}

		for (SearchTree child : root.getChildren()) {
			if (traverseTreeAndFindBoolean(node, child)) {
				return true;
			}
		}

		return false;
	}


	public SearchTree traverseTreeAndFindNode(SearchTree node, SearchTree root) {
		if (root.getText().equals(node.getText())) {
			return root;
		}

		for (SearchTree child : root.getChildren()) {
			if (traverseTreeAndFindBoolean(node, child)) {
				return root;
			}
		}

		return null;
	}

	//assumes there no 2+ nodes with the same .getText()
	public static ArrayList<SearchTree> setChildrenType(List<ItemEntity> ItemEntityList) {

		//maps jsonNode to SearchTree
		ArrayList<SearchTree> trees = new ArrayList<>();
		for(ItemEntity node: ItemEntityList){
			trees.add(convertToSearchTree(node));
		}

		for(SearchTree searchTree: trees){

			for(int i=0; i<ItemEntityList.size(); i++){

				String[] parent = ItemEntityList.get(i).getParent();

				if(parent.length>0) {
					if (parent[0].equals(searchTree.getText())) {
						searchTree.getChildren().add(trees.get(i));
						trees.get(i).getParent().add(searchTree);
					}
				}

			}
		}
		return trees;
	}


}
