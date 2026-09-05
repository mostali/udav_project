package zk_com.base_ctr;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zul.Caption;
import zk_com.core.IZCom;

import java.util.ArrayList;
import java.util.List;

public class Cap0 extends Caption implements IZCom {

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		super.onPageAttached(newpage, oldpage);
	}

	public void init() {
	}

	public List<Component> getComsWithChilds() {
		List<Component> children = getChildren();
		List l = new ArrayList();
		children.forEach(c -> {
			if (c instanceof Span0) {
				l.addAll(((Span0) c).getComs());
			} else if (c instanceof Div0) {
				l.addAll(((Div0) c).getComs());
			} else {
				l.add(c);
			}
		});
		return l;
	}

}
