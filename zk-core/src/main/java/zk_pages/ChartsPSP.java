package zk_pages;

import lombok.SneakyThrows;
import mpc.arr.STREAM;
import mpc.net.query.QueryUrl;
import mpu.IT;
import mpu.core.ARR;
import mpu.str.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Window;
import zk_com.charts.LineChart;
import zk_com.core.IZState;
import zk_com.sun_editor.IPerPage;
import zk_form.head.StdHeadLib;
import zk_os.sec.ROLE;
import zk_page.core.PageRoute;
import zk_page.core.PageSP;
import zk_page.core.SpVM;

import java.util.List;
import java.util.function.Function;

@PageRoute(pagename = "@@charts", role = ROLE.ANONIM)
public class ChartsPSP extends PageSP implements IPerPage, IZState {//WithLogo, WithSearch

	public static final Logger L = LoggerFactory.getLogger(ChartsPSP.class);

	public ChartsPSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

	@SneakyThrows
	public void buildPageImpl() {
		StdHeadLib.CHARTS_JS.addToPage();
		QueryUrl queryUrl = ppiq().queryUrl();

		String label = queryUrl.getFirstAsStr("label", "LinearChart");

		String labels = queryUrl.getFirstAsStr("labels", null);

		LineChart.ChartType chartType = LineChart.ChartType.line;
		if (queryUrl.getFirstAsStr("type", null) != null) {
			chartType = queryUrl.getFirstAs("type", LineChart.ChartType.class, null);
			IT.NN(chartType, "Allowed types - " + ARR.as(LineChart.ChartType.values()));
		}

		String data = queryUrl.getFirstAsStr("data", null);

		Function<String, List<Integer>> dataGetter = (dataset) -> {
			IT.NE(dataset, "set data params, e.g. '10,30,15'");
			dataset = STR.removeStartEndString(dataset, "[", "]");
			return STREAM.mapToList(SPLIT.allByComma(dataset), UST::INT);
		};
		Function<String, List<String>> labelsGetter = (labelsSet) -> {
			IT.NE(labelsSet, "set data params, e.g. 'a,b,c'");
			labelsSet = STR.removeStartEndString(labelsSet, "[", "]");
			return STREAM.mapToList(SPLIT.allByComma(labelsSet), l -> STR.wrapIfNot(l, "'"));
		};
		LineChart lineChart = LineChart.of(chartType, null, label, labels == null ? null : labelsGetter.apply(labels), dataGetter.apply(data));
		window.appendChild(lineChart);

	}

}
