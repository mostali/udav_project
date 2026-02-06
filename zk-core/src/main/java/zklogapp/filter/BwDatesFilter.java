package zklogapp.filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mpc.str.condition.LogGetterDate;
import mpc.str.condition.StringCondition;
import mpu.core.QDate;
import mpc.ui.ColorTheme;
import mpe.logs.filter.ILogFilter;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import zk_com.base.Tbx;
import zk_com.base_ctr.Span0;
import zk_os.AppZos;
import zk_page.ZKS;

import java.util.Date;

@RequiredArgsConstructor
public class BwDatesFilter extends Span0 implements ILogFilter {

	@Getter
//	private Dtx dtxFrom, dtxTo;
	private Tbx dtxFrom, dtxTo;

	@Override
	protected void init() {
		super.init();

		//		dtxFrom = (Dtx) new Dtx().formatLongMedium().placeholder("From").onDefaultAction(Events.ON_CHANGE, event -> LogBwDatesFilter.this.onDefaultActionEvent(event));
		//		dtxTo = (Dtx) new Dtx().formatLongMedium().placeholder("To").onDefaultAction(Events.ON_CHANGE, event -> LogBwDatesFilter.this.onDefaultActionEvent(event));

		dtxFrom = (Tbx) new Tbx().autoDateFormat().placeholder("From").onDefaultAction(Events.ON_CHANGE, event -> BwDatesFilter.this.onDefaultActionEvent((Event)event));
		dtxTo = (Tbx) new Tbx().autoDateFormat().placeholder("To").onDefaultAction(Events.ON_CHANGE, event -> BwDatesFilter.this.onDefaultActionEvent((Event)event));

		appendChilds(dtxFrom, dtxTo);

		dtxFrom.width(200);
		dtxTo.width(200);

		ZKS.FLOAT(this, false);
		ZKS.BGCOLOR(this, ColorTheme.GREEN[1]);
//		ZKS.DISPLAY(this, 1);
		ZKS.PADDING(this, "0px 10px");
		ZKS.MARGIN(this, "0px 5px");
//		ZKS.MARGIN(this, 5);

	}

	@Override
	public StringCondition toFilter() {
		LogGetterDate logGetterDate = AppZos.getLogGetterDate();
		Date minDate = getDtxFrom().getDate(QDate.MIN_DATE);
		Date maxDate = getDtxTo().getDate(QDate.MAX_DATE);
		if (minDate == QDate.MIN_DATE && maxDate == QDate.MAX_DATE) {
			return null;
		}
		return StringCondition.BwDateStringCondition.build(minDate, maxDate, logGetterDate);
	}

	@Override
	public String toStringFnPart() {
		Date minDate = getDtxFrom().getDate(QDate.MIN_DATE);
		Date maxDate = getDtxTo().getDate(QDate.MAX_DATE);
//		String debugMin=QDate.of(minDate).f(FDate.APP_STANDART_LOG_DATE_FORMAT);
//		String debugMax=QDate.of(maxDate).f(FDate.APP_STANDART_LOG_DATE_FORMAT);
		return ILogFilter.toStringFnPart(minDate, maxDate);
	}
}
