//package zk_com.base_ctr;
//
//import org.zkoss.zk.ui.HtmlNativeComponent;
//
//public class MetaTag extends org.zkoss.zk.ui.AbstractComponent {
//	private String _name;
//	private String _content;
//	private String _property;
//	private String _httpEquiv;
//
//	public void setName(String name) {
//		_name = name;
//		smartUpdate("name", name);
//	}
//
//	public void setContent(String content) {
//		_content = content;
//		smartUpdate("content", content);
//	}
//
//	@Override
//	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
//			throws java.io.IOException {
//		super.renderProperties(renderer);
//
//		if (_name != null) {
//			render(renderer, "name", _name);
//		}
//		if (_content != null) {
//			render(renderer, "content", _content);
//		}
//		if (_property != null) {
//			render(renderer, "property", _property);
//		}
//		if (_httpEquiv != null) {
//			render(renderer, "http-equiv", _httpEquiv);
//		}
//	}
//
////	@Override
////	public String getOuterAttrs() {
////		return " meta";  // Будет рендериться как <meta>
////	}
//
//	public class MetaTag2 extends HtmlNativeComponent {
//		private String name;
//		private String content;
//
//		public String getContent() {
//			return "<meta name=\"" + encode(name) + "\" content=\"" + encode(content) + "\">";
//		}
//
//		@Override
//		public String getOuterContent() {
//			return "<meta name=\"" + encode(name) + "\" content=\"" + encode(content) + "\">";
//		}
//
//		// геттеры и сеттеры
//	}
//}
