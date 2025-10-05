package mp.tslice.markup;


import mp.tslice.TRow;
import mp.tslice.USlice;
import mpu.X;
import mpc.html.EHtml5;
import mpc.html.EHtml5Head;
import mpc.html.EHtml5Special;
import mpu.str.TKN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mpu.core.ENUM;
import mp.tslice.IFEMPTY;
import mpc.str.sym.SYM;
import mpu.str.STR;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static mp.tslice.USlice.toStringObj;

//https://confluence.atlassian.com/display/CONF55/Code+Block+Macro
//https://confluence.atlassian.com/doc/code-block-macro-139390.html
//https://confluence.atlassian.com/doc/confluence-markup-283640216.html
//https://confluence.atlassian.com/doc/confluence-storage-format-790796544.html?_ga=2.68663954.223665269.1588139875-863920086.1582636560
public class UMarkup {

	public static final Logger L = LoggerFactory.getLogger(UMarkup.class);

	public static final String[] ALLOWED_ATTR = new String[]{"id", "class", "style", "href"};
	public static final String ATTRIBUTE_NAME_ALLOWED_CHARS = SYM.WORD_EN_NUM_DASH + SYM.COLON;

	public static boolean isComment(String type) {
		return type != null && type.startsWith(PageFactory.PREFIX_COMMENT);
	}

	public static boolean isLinkPattern(String type) {
		return type != null && type.startsWith(PageFactory.TYPE_MARKER_LINK_PATTERN);
	}

	public enum MarkupType {
		MARKDOWN, PROPERTY, HTML;

		@Deprecated // NU
		public static MarkupType getSpecificType(Enum type) {
			for (MarkupType markupType : MarkupType.values()) {
				switch (markupType) {
					case MARKDOWN:
						if (MARKDOWN == type) {
							return MARKDOWN;
						}
					case HTML:
						if (type instanceof EHtml5 || type instanceof EHtml5Head) {
							return HTML;
						}
						break;
				}
			}
			throw new IllegalArgumentException("Unknown markup type ::: " + type);
		}

		@Deprecated // NU
		public static Enum getTypeFromRowsIgnoreCase(List<List<Object>> rows) {
			if (rows.isEmpty()) {
				return null;
			}
			List<Object> row = rows.get(0);
			return getTypeFromSingleRowIgnoreCase(row);
		}

		@Deprecated // NU
		public static Enum getTypeFromSingleRowIgnoreCase(List<Object> row) {
			if (row.isEmpty()) {
				return null;
			}
			String name = TRow.nameAsString(row);
			return getUniqType(name);
		}

		public static Enum getCommonType(String name) {
			return getSpecificType(name, true);
		}

		public static Enum getUniqType(String name) {
			return getSpecificType(name, false);
		}

		public static Enum getSpecificType(String name, boolean returnCommonOrSpecific) {
			for (MarkupType markupType : MarkupType.values()) {
				Enum type = markupType.getSpecificType(name);
				if (type != null) {
					return returnCommonOrSpecific ? markupType : type;
				}
			}
			return null;
		}

		public Enum getSpecificType(String name) {
			switch (this) {
				case MARKDOWN:
					if ("MD".equalsIgnoreCase(name)) {
						return MARKDOWN;
					}
					break;
				case PROPERTY:
					if (name.endsWith("@")) {
						return PROPERTY;
					}
					break;
				case HTML:
					Enum tag = getHtmlAnyTypeIgnoreCase(name);
					if (tag != null) {
						return tag;
					}
					break;
			}
			return null;
		}

		public static Enum getHtmlAnyTypeIgnoreCase(String name) {
			if (name == null || name.isEmpty()) {
				return null;
			}
			return ENUM.valueOf(name, new Class[]{EHtml5.class, EHtml5Head.class, EHtml5Special.class}, true, null);
		}
	}

	private static Map<String, String> getMapAttributes(List<Object> singleRow, int levelFirstAttr) {
		LinkedHashMap<String, String> mapAttrs = new LinkedHashMap<>();
		List<String> attrs = getDirtyListAttributes(levelFirstAttr, singleRow);
		String attrKey = null;

		for (int i = 0; i < attrs.size(); i++) {
			String colValue = attrs.get(i);
			if (colValue != null) {
				colValue = colValue.trim();
			}
			//check key - if present that it attr value
			if (attrKey != null) {
				mapAttrs.put(attrKey, colValue);
				attrKey = null;
				continue;
			}
			//check compound pattern - with standart attributes (style, class, id)
			String[] keyWithValue = getStandartAttrWithValue(colValue);
			if (keyWithValue != null) {
				mapAttrs.put(keyWithValue[0], keyWithValue[1]);
				continue;
			}
			//check dirty pattern - with attribute & content
			String[] two = TKN.twoByChars(colValue, ATTRIBUTE_NAME_ALLOWED_CHARS, TKN.SplitByChars.ALLOWED);
			if (X.notEmpty(two[1])) {
				String attrVal = two[1];
				if (attrVal.startsWith("=")) {
					attrVal = attrVal.substring(1);
				} else {
					L.error("Unhandled col-value (attribute) ::: " + colValue);
					continue;
				}
				mapAttrs.put(two[0], STR.untrim_with_char(attrVal, SYM.DQ, SYM.SQ));
				continue;
			}
			attrKey = colValue;
		}
		if (attrKey != null) {
			mapAttrs.put(attrKey, null);
		}
		return mapAttrs;
	}

	private static String[] getStandartAttrWithValue(String val) {
		for (String attr : ALLOWED_ATTR) {
			if (!val.startsWith(attr)) {
				continue;
			} else if (val.equals(attr)) {
				return null;
			} else if (val.charAt(attr.length()) == '=') {
				val = val.substring(attr.length() + 1);
				return new String[]{attr, STR.untrim_with_char(val, SYM.DQ, SYM.SQ)};
			}
		}
		return null;
	}

	private static List<String> getDirtyListAttributes(Integer levelFirstAttr, List<Object> singleRow) {
		List<String> attrs = new ArrayList<>();
		for (int i = levelFirstAttr; i < singleRow.size(); i++) {
			Object col = singleRow.get(i);
			if (!isDataCol(col, PageFactory.PREFIX_COMMENT)) {
				return attrs;
			}
			attrs.add(toStringObj(col, null));
		}
		return attrs;
	}

	private static boolean isDataCol(Object obj, String prefixComment) {
		String str = toStringObj(obj, IFEMPTY.BLANK);
		if (str.isEmpty()) {
			return false;
		} else {
			return !str.startsWith(prefixComment);
		}
	}

	private static boolean isCommentObj(Object obj, String prefixComment) {
		String str = toStringObj(obj, IFEMPTY.BLANK);
		if (str.isEmpty()) {
			return false;
		} else {
			return str.startsWith(prefixComment);
		}
	}

	public static Map<String, String> findAttributes(List<Object> singleRow, int startSearchFromLevel) {
		Integer levAttr = USlice.getLevelAfterSpace(startSearchFromLevel, singleRow, 2);
		if (levAttr == null) {
			return null;
		}
		Map<String, String> attrs = UMarkup.getMapAttributes(singleRow, levAttr);
		return attrs;
	}

	static interface INode {
		String type();

		INode appendToParent(INode iNode);
	}

	public static class NodeAttr implements Map.Entry<String, String> {
		private final String name;
		private String value;

		public NodeAttr(String name) {
			this.name = name;
		}

		@Override
		public String getKey() {
			return name;
		}

		@Override
		public String getValue() {
			return value;
		}

		@Override
		public String setValue(String value) {
			String oldValue = this.value;
			this.value = value;
			return oldValue;
		}

		@Override
		public String toString() {
			int lv = getValue() == null ? 0 : getValue().length();
			StringBuilder sb = new StringBuilder(name.length() + lv + 3);
			sb.append(getKey()).append(SYM.EQ).append(SYM.DQ).append(getValue()).append(SYM.DQ);
			return sb.toString();
		}
	}

	public static class HNode implements INode {
		final static String DEF_FULL_PATTERN = "<{0} {2}>{1}</{0}>";
		final static String DEF_SHORT_PATTERN = "<{0}/>";
		private String pattern;
		final String tagName;
		//
		private final List<NodeAttr> attrs = new ArrayList<>();
		//
		private String body;
		private INode bodyNode;

		public boolean isBody() {
			return getBody() != null;
		}

		public boolean isBodyNode() {
			return getBodyNode() != null;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
			this.html = null;
		}

		private INode getBodyNode() {
			return bodyNode;
		}

		public void setBodyNode(INode bodyNode) {
			this.bodyNode = bodyNode;
			this.html = null;
		}

		public String getPattern() {
			return pattern;
		}

		private List<NodeAttr> getAttrs() {
			return attrs;
		}

		public HNode(String tagName) {
			this.tagName = tagName.toLowerCase();
			this.pattern = init();
		}

		private boolean shortMode;

		public boolean isShortMode() {
			return shortMode;
		}

		private String init() {
			switch (this.tagName) {
				case "br":
				case "hr":
					shortMode = true;
					return DEF_SHORT_PATTERN;
				case "cdata":
					throw new IllegalStateException("need impl cdata");
				default:
					shortMode = false;
					return DEF_FULL_PATTERN;
			}
		}

		private String html = null;

		INode onBuild() {
			build();
			return this;
		}

		INode build() {
			if (html != null) {
				return this;
			}
			if (shortMode) {
				this.html = STR.format(this.pattern, type());
			}
			this.html = STR.format(this.pattern, type(), buildBodyContent(), buildAttrsContent());
			return this;
		}

		private String buildBodyContent() {
			return getAttrs().isEmpty() ? "" : getAttrs().stream().map(NodeAttr::toString).collect(Collectors.joining(" "));
		}

		private String buildAttrsContent() {
			if (getBody() != null) {
				return getBody();
			} else if (getBodyNode() != null) {
				return getBodyNode().toString();
			} else {
				return "";
			}
		}

		@Override
		public String type() {
			return tagName;
		}

		@Override
		public INode appendToParent(INode iNode) {
			return null;
		}
	}

}
