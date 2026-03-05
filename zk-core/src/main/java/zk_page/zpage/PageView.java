package zk_page.zpage;

import lombok.RequiredArgsConstructor;
import zk_com.base_ctr.Div0;

@RequiredArgsConstructor
public class PageView extends Div0 {

	public final ZPage zPage;

	public static PageView of(ZPage zPage) {
		return new PageView(zPage);
	}

	@Override
	protected void init() {
		super.init();


	}
}
