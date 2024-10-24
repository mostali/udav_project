//package zk_os.srv;
//
//import j2html.tags.ContainerTag;
//
//import static j2html.TagCreator.tag;
//
//public class ZComHtmlBuilder {
////		public static final String TEXTBOX = "<textbox value=\"test\" readonly=\"false\"/>";
////		public static final String LABEL = "<label style=\"color: red\" value=\"Hello World\" />";
//
//
////		public static void SET_PARENT_COMPONENT(Window window) {
////			TL_PARENT_COMPONENT.set(window);
////		}
//
////		enum Types {
////			TEXTBOX
////		}
//
//	public static class LABEL {
//		public static String VALUE(CharSequence value) {
//			return "<label style=\"color: red\" value=\"" + value + "\" />";
//		}
//	}
//
////		public static class DIV {
////			public static String VALUE(CharSequence html) {
////				return "<div border=\"none\" shadow=\"false\"  mode=\"overlapped\" >" +
////					   html +
////					   "</div>";
////			}
////		}
//
//	public static class TEXTBOX {
//		public static String VALUE(CharSequence value) {
//			return "<textbox  value=\"" + value + "\" readonly=\"false\"/>";
//		}
//	}
//
//	public static class WINDOW {
//		public static String VALUE(CharSequence html) {
//			return "<window border=\"none\" shadow=\"false\"  mode=\"overlapped\" >" +
//					html +
//					"</window>";
//		}
//
//		public static String absolute(String html, PosLT posLT, Dim dim) {
//			if (false) {
//				return "<window border=\"none\" shadow=\"false\" width=\"" + dim.width + "px\" mode=\"overlapped\" left=\"" + posLT.left + "px\" top=\"" + posLT.top + "px\">" +
//						html +
//						"</window>";
//			}
//
////				ContainerTag win = tag("window").with(h1("asd"));
//			ContainerTag win = tag("window").withText(html);
//
////				Node win = UJsoup.createElement("window");
//
//			if (posLT.hasTop()) {
//				win.attr("top", posLT.top + "px");
//			}
//			if (posLT.hasLeft()) {
//				win.attr("left", posLT.left + "px");
//			}
//			if (dim.hasWidth()) {
//				win.attr("width", dim.width + "px");
//			}
//			if (dim.hasHeigth()) {
//				win.attr("height", dim.heigth + "px");
//			}
//			win.attr("mode", "overlapped");
//			win.attr("border", "none");
//			win.attr("shadow", "false");
////				String outerHtml = win.outerHtml();
////				U.p(outerHtml);
//			return win.render();
//
//		}
//	}
//
//	public static class PosLT {
//		final int left;
//		final int top;
//
//		public PosLT(int left, int top) {
//			this.left = left;
//			this.top = top;
//		}
//
//		public static PosLT ofLT(int left, int top) {
//			return new PosLT(left, top);
//		}
//
//		public boolean hasTop() {
//			return top > -1;
//		}
//
//		public boolean hasLeft() {
//			return left > -1;
//		}
//	}
//
//	public static class Dim {
//		final int width;
//		final int heigth;
//
//		public Dim(int width, int heigth) {
//			this.width = width;
//			this.heigth = heigth;
//		}
//
//		public static Dim ofWH(int width, int heigth) {
//			return new Dim(width, heigth);
//		}
//
//		public boolean hasWidth() {
//			return width > -1;
//		}
//
//		public boolean hasHeigth() {
//			return heigth > -1;
//		}
//	}
//}
