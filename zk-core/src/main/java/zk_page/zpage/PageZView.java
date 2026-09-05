package zk_page.zpage;

import lombok.RequiredArgsConstructor;
import zk_com.base_ctr.Div0;

@RequiredArgsConstructor
public class PageZView extends Div0 {

	public final ZPage zPage;

	public static PageZView of(ZPage zPage) {
		return new PageZView(zPage);
	}

	@Override
	protected void init() {
		super.init();


	}
}
