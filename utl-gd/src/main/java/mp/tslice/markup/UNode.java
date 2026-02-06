package mp.tslice.markup;


import mp.tslice.SliceIterator;
import mp.tslice.TRow;
import mp.tslice.USlice;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mp.tslice.NVX;
import udav_net.UJsoup;

import java.util.*;
import java.util.stream.Collectors;


public class UNode {

	//	public static void main(String[] args) throws IOException {
	//		Element e = (Element) UJsoup.createElement("div");
	//		e.attr("s", "vv");
	//		U.exit(e);
	//		P.p(S_singleRow2);
	//		SEP.EQ__("");
	//		RNode node = new RNode(S_singleRow2.get(0));
	//		U.p("R-NODE:::" + node);
	//		U.exit(node.onBuild().getNode());
	//	}

	public static final Logger L = LoggerFactory.getLogger(UNode.class);

	public abstract static class PageBuilderHtml {

		private final List<Object> page_parent_row;
		private final List<List<Object>> page_body_rows;

		public List<Object> getParentRow() {
			return page_parent_row;
		}

		public List<List<Object>> getPageBodyRows() {
			return page_body_rows;
		}

		public String getParentNvxAsString(NVX nvx) {
			return TRow.getNvxObjectAsString(getParentRow(), nvx);
		}

		public PageBuilderHtml(List<List<Object>> page_slice) {
			super();
			this.page_parent_row = USlice.cutHeadRow(page_slice);
			this.page_body_rows = page_slice;
		}

		public abstract String[] getContext();

		private Element page;
		private Element head;
		private Element body;

		public Element getPageElement() {
			if (page != null) {
				return page;
			}
			page = (Element) UJsoup.createElement("html");
			return page;
		}

		public Element getJsoupHeadElement() {
			if (head != null) {
				return head;
			}
			head = (Element) UJsoup.createElement("head");
			return head;
		}

		public Element getJsoupBodyElement() {
			if (body != null) {
				return body;
			}
			body = (Element) UJsoup.createElement("body");
			return body;
		}

		private boolean builded = false;

		public PageBuilderHtml onBuild() {
			if (builded) {
				return this;
			}

			try {

				getPageElement().appendChild(getJsoupHeadElement());

				getPageElement().appendChild(getJsoupBodyElement());

				SliceIterator sit = SliceIterator.createIterator(getPageBodyRows());

				do {
					List<List<Object>> slice = sit.next();
					if (slice == null) {
						break;
					} else if (slice.isEmpty()) {
						continue;
					}

					resolveSlice(slice);

				} while (true);

				builded = true;

				return this;

			} catch (Exception ex) {

				page = null;

				throw ex;
			}
		}

		private final Map<String, List<List<Object>>> PROPERTYS = new HashMap<>();

		private void resolveSlice(List<List<Object>> slice) {
			String type = TRow.nameAsString(slice.get(0));

			Enum eType = UMarkup.MarkupType.getUniqType(type);

			if (UMarkup.MarkupType.PROPERTY == eType) {
				L.debug("PROPERTY:::" + type);
				PROPERTYS.put(type, slice);
			} else if (UMarkup.MarkupType.MARKDOWN == eType) {
				L.debug("MD:::" + type + " is SKIPPED");
			} else {
				L.debug("HTML5:::" + eType);
				RNode node = null;
				if (slice.size() == 1) {
					node = new RNode(1, slice.get(0)) {
						@Override
						public String[] getContext() {
							return PageBuilderHtml.this.getContext();
						}
					};
				} else {
					node = new YNode(1, slice) {
						@Override
						public String[] getContext() {
							return PageBuilderHtml.this.getContext();
						}
					};

				}
				node.appendToParent(getJsoupBodyElement());
			}
		}
	}

	public static List<Object> genRow(String mapping) {
		return Arrays.asList(mapping.split(";"));
	}

	public static List<List<Object>> genRows(String mapping) {
		return Arrays.asList(mapping.split("\\|")).stream().map(UNode::genRow).collect(Collectors.toList());
	}


	public static RNode createNode(List<List<Object>> slice) {
		switch (slice.size()) {
			case 0:
				return null;
			case 1:
				return RNode.createRNode(slice.get(0));
		}
		return YNode.createYNode(slice);
	}

	public static String singleRow0 = ";Data0";
	public static List<List<Object>> S_singleRow0 = genRows(singleRow0);


	public static String singleRow1 = ";h1;Header 1";
	public static List<List<Object>> S_singleRow1 = genRows(singleRow1);

	public static String singleRow2 = ";p;h2;Header 2;;;;class=sc;onclick;;";
	public static List<List<Object>> S_singleRow2 = genRows(singleRow2);

	public static String rows_0 = ";div;span;b;123|;;a;;;href;www.com|;;p";
	public static List<List<Object>> S_rows = genRows(rows_0);
	public interface ITagChecker {
		String checkTagName(String type);
	}

	public static List<ITagChecker> TAG_CHECKERS = new ArrayList<>();

	static {
		TAG_CHECKERS.add(new ITagChecker() {

			@Override
			public String checkTagName(String type) {
				Enum etype = UMarkup.MarkupType.getHtmlAnyTypeIgnoreCase(type);
				if (etype == null) {
					return null;
				}
				return etype.name();
			}
		});
	}

}
