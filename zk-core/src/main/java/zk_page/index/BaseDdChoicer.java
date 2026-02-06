package zk_page.index;

import lombok.Getter;
import mpu.X;
import mpu.pare.Pare;
import zk_com.base.Cb;
import zk_com.base_ctr.Div0;

import java.util.LinkedList;
import java.util.List;

public abstract class BaseDdChoicer extends Div0 {

	private final @Getter List<Pare<String, Boolean>> opts = new LinkedList<>();

	@Override
	protected void init() {
		super.init();
		applyOpts();
	}

	private void applyOpts() {
		if (X.empty(opts)) {
			return;
		}
		opts.forEach(o -> appendChild(new Cb(o)));
	}
}
