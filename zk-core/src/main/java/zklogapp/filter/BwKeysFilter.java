package zklogapp.filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mpc.str.condition.StringCondition;
import mpc.ui.ColorTheme;
import mpe.logs.filter.ILogFilter;
import org.zkoss.zk.ui.event.Events;
import zk_com.base.Tbx;
import zk_com.base_ctr.Span0;
import zk_page.ZKS;

@RequiredArgsConstructor
public class BwKeysFilter extends Span0 implements ILogFilter {

	@Getter
	private Tbx tbxStartKey, tbxLastKey;

	@Override
	protected void init() {
		super.init();

		tbxStartKey = (Tbx) new Tbx().placeholder("Start key").onDefaultAction(Events.ON_CHANGE, event -> BwKeysFilter.this.onDefaultActionEvent(event));
		tbxLastKey = (Tbx) new Tbx().placeholder("Last key").onDefaultAction(Events.ON_CHANGE, event -> BwKeysFilter.this.onDefaultActionEvent(event));

		appendChilds(tbxStartKey, tbxLastKey);

		tbxStartKey.width(200);
		tbxLastKey.width(200);

		ZKS.FLOAT(this, false);
		ZKS.BGCOLOR(this, ColorTheme.GREEN[2]);
//		ZKS.DISPLAY(this, 1);
		ZKS.PADDING(this, "0px 10px");
		ZKS.MARGIN(this, "0px 5px");
//		ZKS.MARGIN(this, 5);

	}

	@Override
	public StringCondition toFilter() {
		return StringCondition.BwKeysStringCondition.build(getTbxStartKey().getValue(), getTbxLastKey().getValue());
	}

	@Override
	public String toStringFnPart() {
		return ILogFilter.toStringHashcode(getTbxStartKey().getValue(), getTbxLastKey().getValue());
	}
}
