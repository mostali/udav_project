package mp.tslice.markup;

import mp.tslice.SliceIterator;
import mp.tslice.USlice;
import org.jsoup.nodes.Element;

import java.util.List;


public class YNode extends RNode {

	final List<List<Object>> body_rows;

	public YNode(List<List<Object>> slice) {
		this(USlice.getLevelRow(slice.get(0)), slice);
	}

	public YNode(int x, List<List<Object>> slice) {
		super(x, USlice.cutHeadRow(slice));
		this.body_rows = slice;
	}

	@Override
	public String toString() {
		return "YNode{" +
			   "body=" + body_rows.size() +
			   ", " + super.toString() +
			   '}';
	}

	public YNode onBuild() {
		return (YNode) super.onBuild();
	}

	@Override
	public List<List<Object>> getBodyRows() {
		return body_rows;
	}

	public static YNode createYNode(List<List<Object>> rows) {
		YNode yNode = new YNode(rows);
		return yNode;
	}


	@Override
	protected YNode build() {

		//-----------------render parent row-----------------
		super.build();

		if (getBodyRows().isEmpty()) {
			UNode.L.error("Body is empty");
			return this;
		}
		if (!isNode()) {
				//				L.warn("Body not append. Parent type not found ::: " + super.getParentRow());
			return this;
		}

		//-----------------single row-----------------
		if (getBodyRows().size() == 1) {
			if (isElementNode()) {
				new RNode(getNextParentLevel(), getBodyRows().get(0)).appendToParent((Element) super.getNode());
			} else {
				UNode.L.warn("Body(1) not append. Parent type invalid node ::: " + super.getCurrentRow());
			}
			return this;
		}

		//-----------------many rows-----------------
		SliceIterator sit = SliceIterator.createIterator(getBodyRows());

		int nextLevel = super.getNextParentLevel();
		RNode yNode = null;
		do {
			List<List<Object>> slice = sit.next(nextLevel);
			if (slice == null) {
				break;
			} else if (slice.isEmpty()) {
				continue;
			} else if (slice.size() == 1) {
				yNode = new RNode(nextLevel, slice.get(0));
			} else {
				yNode = new YNode(nextLevel, slice);
			}

			if (isNode()) {
				yNode.appendToParent(super.getNode());
			} else {
				UNode.L.warn("Body(N) not append. Parent type invalid node ::: " + super.getCurrentRow());
			}

		} while (true);

		return this;
	}

}
