package mp.tslice.markup;

import mp.tslice.SliceIterator;
import mp.tslice.TRow;
import mp.tslice.USlice;
import mpu.core.ARRi;
import mpu.IT;
import mpu.X;
import mp.tslice.NVX;
import mpu.str.TKN;
import org.jsoup.nodes.CDataNode;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mpc.str.sym.SYM;
import udav_net.UJsoup;
import mp.tslice.IFEMPTY;

import java.util.*;
import java.util.stream.Collectors;

import static mp.tslice.USlice.toStringObj;


public class RNode {
	public static final Logger L = LoggerFactory.getLogger(RNode.class);

	public final boolean isParentRow;
	private final int level;
	private final List<Object> current_row;
	private String abstractStringData;
	private String[] dataExt;

	private Node node;

	public boolean isElementNode() {
		return getNode() instanceof Element;
	}

	public boolean isCDataNode() {
		return getNode() instanceof CDataNode;
	}

	private RNodeType rNodeType = null;

	public boolean isType(RNodeType rNodeType) {
		return this.rNodeType == rNodeType;
	}

	@Override
	public String toString() {
		return "RNode{" +
			   "m=" + (isParentRow ? 1 : 0) +
//			   ", T=" + (typeAsString == null ? "" : typeAsString) +
			   ", L=" + level +
			   ", d='" + abstractStringData + '\'' +
			   ", o=" + node +
			   ", r=" + current_row +
			   '}';
	}

	public RNode(List<Object> row) {
		this(USlice.getLevelRow(row), row);
	}

	public RNode(int x, List<Object> row) {
		this(x, row, true);
	}

	private RNode(int level, List<Object> row, boolean isParentRow) {
		this.level = level;
		this.current_row = row;
		this.isParentRow = isParentRow;
	}

	public static RNode createRNode(List<Object> rows) {
		RNode rNode = new RNode(rows);
		return rNode;
	}

	public List<Object> getCurrentRow() {
		return current_row;
	}

	public int getParentLevel() {
		return level;
	}

	public int getNextParentLevel() {
		return level + 1;
	}

	public boolean isEmpty() {
		return abstractStringData == null;
	}

	public boolean isNode() {
		return node != null;
	}

	public String getAbstractStringData() {
		return abstractStringData;
	}

	public String[] getDataExt() {
		return dataExt;
	}

	private boolean hasAbstractStringData() {
		return getAbstractStringData() != null;
	}

	private boolean hasDataExt() {
		return getDataExt() != null;
	}

	public Node getNode() {
		return node;
	}

	private String getCustomTagType(String type) {
		for (UNode.ITagChecker tagFinder : UNode.TAG_CHECKERS) {
			String tag = tagFinder.checkTagName(type);
			if (tag != null) {
				return tag;
			}
		}
		return null;
	}

	enum RNodeType {
		TAGNAME, DATAXML, COMMENT, LINKPATTERN_HEAD, LINKPATTERN_PLACEHOLDER, ABSTRACT_DATA;
	}

	private void initTypes() {

		String type = getTypeStringFromRow();
		if (type == null) {
			return;
		}

		this.abstractStringData = type;

		if (type.startsWith("<")) {
			this.rNodeType = RNodeType.DATAXML;
			return;
		}

		if (UMarkup.isComment(type)) {
			this.rNodeType = RNodeType.COMMENT;
			return;
		}

		if (UMarkup.isLinkPattern(type)) {
			this.rNodeType = RNodeType.LINKPATTERN_HEAD;
			return;
		}

		if (type.startsWith("{{") && type.endsWith("}}")) {
			this.rNodeType = RNodeType.LINKPATTERN_PLACEHOLDER;
			return;
		}

		String validType = getCustomTagType(type);
		if (validType != null) {
			this.rNodeType = RNodeType.TAGNAME;
			return;
		}

		this.rNodeType = RNodeType.ABSTRACT_DATA;
	}

	public RNode onBuild() {
		return build();
	}

	public String[] getContext() {
		return null;
	}

	class LinkPatternBuilder {

		public LinkPatternBuilder() {
		}

		private String patternValue = null;

		boolean findPatternValue(String pattern) {
			patternValue = null;
			String[] patternType = toPatternType(pattern);
			IT.isEq(patternType[0], PageFactory.TYPE_MARKER_LINK_PATTERN, "This pattern is not link-pattern", pattern);
			String patternName = patternType[1];
			List<List<Object>> sliceLinkPattern = PageFactory.findLinkPattern(getContext(), patternName);
			if (sliceLinkPattern.isEmpty()) {
				L.warn("Slice with link-pattern is empty, pattern '" + patternName + "'");
				return false;
			}

			patternValue = TRow.getNvxObjectAsString(sliceLinkPattern.get(0), NVX.EXT);
			if (patternValue == null || patternValue.isEmpty()) {
				L.warn("Ext pattern value is empty, pattern '" + patternName + "'");
				return false;
			}

			return true;
		}

		private String[] toPatternType(String patternName) {
			return TKN.twoByChars(patternName, PageFactory.TYPE_MARKER_LINK_PATTERN, TKN.SplitByChars.ALLOWED);
		}

		public String fillData(List<Object> parentRow, List<List<Object>> bodyRows) {

			List<List<List<Object>>> all = new ArrayList<>();

			List<List<Object>> parentSlice = new ArrayList<>();
			parentSlice.add(parentRow);

			List<List<List<Object>>> slices = SliceIterator.toList(bodyRows);

			all.add(parentSlice);
			all.addAll(slices);

			int indName = 2;
			int[] indValue = new int[]{3, 4};
			String delimetrCol = " ";
			String delimetrRow = "\n";

			Map<String, String> params = createMapValues(all, indName, indValue, delimetrCol, delimetrRow);
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if (this.patternValue.contains(entry.getKey())) {
					this.patternValue = this.patternValue.replace(entry.getKey(), entry.getValue());
				}
			}

			return this.patternValue;
		}

	}

	private Map<String, String> createMapValues(List<List<List<Object>>> slices, int indName, int[] indValue, String delimetrCol, String delimetrRow) {
		Map<String, String> mapWithValues = new HashMap<>();
		for (List<List<Object>> slice : slices) {
			if (slice.isEmpty()) {
				continue;
			}
			String name = (String) ARRi.item(slice.get(0), indName);
			if (X.empty(name)) {
				L.warn("Name is empty ::: " + slice);
				continue;
			}
			String val = null;
			switch (slice.size()) {
				case 1:
					List vals = ARRi.itemsMany(slice.get(0), indValue);
					val = (String) vals.stream().filter(o -> o != null).collect(Collectors.joining(delimetrCol));
					break;
				default:
					val = TRow.concatValue(slice, indValue, delimetrCol, delimetrRow);
					break;
			}
			mapWithValues.put(name, val);
		}
		return mapWithValues;
	}


	public List<List<Object>> getBodyRows() {
		return Collections.emptyList();
	}

	protected RNode build() {

		initTypes();

		if (rNodeType == null) {
			return this;
		}

		switch (rNodeType) {

			case COMMENT:
				UNode.L.debug("Skip type comment ::: " + toString());
				return this;

			case LINKPATTERN_HEAD:
				String patternValue = getAbstractStringData();
				LinkPatternBuilder linkPatternBuilder = new LinkPatternBuilder();
				if (!linkPatternBuilder.findPatternValue(patternValue)) {
					L.error("LinkPattern not found with name ::: " + patternValue);
					return this;
				}
				String value = linkPatternBuilder.fillData(getCurrentRow(), getBodyRows());
				this.abstractStringData = value;
				return this;

			case TAGNAME:
				this.node = UJsoup.createElement(getAbstractStringData());
				if (isParentRow) {
					checkAndFillAttrsFor(this.node);
				}
				if (level >= current_row.size() - 1) {
					return this;
				}

				if (!(this instanceof RNode)) {
					String error = USlice.getErrorMessageCheckChildLevel(getParentLevel(), this.current_row);
					if (error != null) {
						UNode.L.error("RNode render error. ".concat(error));
						return this;
					}
				}
				RNode nextNode = new RNode(getNextParentLevel(), this.current_row, false);

				if (isNode()) {
					nextNode.appendToParent(getNode());
				} else {
					UNode.L.warn("Body(R) not append. Parent type invalid node ::: " + getCurrentRow());
				}
				return this;

			default:
				UNode.L.warn("Unhandled type ::: " + rNodeType + " ::: " + getCurrentRow());
				return this;

		}
	}


	private void initExtData() {
		int level = getParentLevel();
		Object ext1 = ARRi.item(getCurrentRow(), ++level);
		if (ext1 == null || USlice.isEmptyObj(ext1)) {
			return;
		}
		Object ext2 = ARRi.item(getCurrentRow(), ++level);
		if (ext2 == null) {
			this.dataExt = new String[]{ext1.toString()};
			return;
		}
		this.dataExt = new String[]{ext1.toString(), ext2.toString()};
	}

	private String getTypeStringFromRow() {
		return toStringObj(getCurrentRow().get(getParentLevel()), IFEMPTY.NULL);
	}

	private void checkAndFillAttrsFor(Node element) {
		Map attrs = UMarkup.findAttributes(this.current_row, getNextParentLevel());
		if (X.notEmpty(attrs)) {
			UJsoup.fillAttributes(element, attrs);
		}
	}

	protected RNode appendToParent(Node parent) {

		build();

		if (isEmpty()) {
			return this;
		}

		switch (rNodeType) {

			case COMMENT:
				return this;

			case LINKPATTERN_HEAD:
			case ABSTRACT_DATA:
			case DATAXML:
				apendDataToParent(parent);
				return this;
		}

		if (isNode()) {
			if (parent instanceof Element) {
				((Element) parent).appendChild(getNode());
			} else {
				UNode.L.error("Unknown Parent-Element with class ::: " + parent.getClass() + " ::: parent-row ::: " + getCurrentRow());
			}
		} else {
			UNode.L.error("Child node not append. No type found ::: " + rNodeType + " ::: " + toString());
		}

		return this;
	}

	private void apendDataToParent(Node parent) {
		if (!isType(RNodeType.LINKPATTERN_HEAD)) {
			initExtData();
		}

		if (parent instanceof Element) {
			Element element = (Element) parent;
			element.append(getAbstractStringData());
			if (!hasDataExt()) {
				return;
			}
			for (String col : getDataExt()) {
				element.append(col);
			}
		} else if (parent instanceof CDataNode) {
			CDataNode node = (CDataNode) parent;
			String existData = node.text();
			if (existData == null) {
				existData = "";
			}
			String data = buildCData(getAbstractStringData(), " ");
			if (!existData.isEmpty()) {
				existData = existData.concat(SYM.NEWLINE);
			}
			node.text(existData.concat(data));
		}
	}

	private String buildCData(String prefixData, String delimetr) {
		StringBuilder sb = new StringBuilder(prefixData);
		if (hasDataExt()) {
			sb.append(delimetr);
			for (String colExt : getDataExt()) {
				sb.append(colExt).append(delimetr);
			}
			sb.setLength(sb.length() - delimetr.length());
		}
		return sb.toString();
	}

}
