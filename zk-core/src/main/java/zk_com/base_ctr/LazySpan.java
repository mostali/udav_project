//package zk_core.editable;
//
//import lombok.RequiredArgsConstructor;
//import org.zkoss.zk.ui.Page;
//import org.zkoss.zul.Span;
//
//
//@RequiredArgsConstructor
//public class LazySpan extends Span {
//
//	private final LazyBuilder<Span> lazyBuilder;
//
//	@Override
//	public void onPageAttached(Page newpage, Page oldpage) {
//		lazyBuilder.buildAndAppend(LazySpan.this);
//		super.onPageAttached(newpage, oldpage);
//	}
//}
//
