package zk_com.charts;

import mpc.arr.STREAM;
import mpc.exception.WhatIsTypeException;
import mpc.fs.fd.RES;
import mpc.map.MAP;
import mpe.str.URx;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import mpu.pare.Pare;
import mpu.str.Regexs;
import zk_com.base.Xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class LineChart extends Chart {

	public static final String RSRCC_LINECHART = "/_com/_charts/linear.html";
	public static final String RSRCC_LINECHARTN = "/_com/_charts/linearN.html";

	public LineChart(String html, Object... args) {
		super(html, args);
	}

	public static LineChart of(ChartType chartType, String id, String label, List<Pare<String, Integer>> dataSet) {
		return of(chartType, id, label, STREAM.mapToList(dataSet, Pare::key), STREAM.mapToList(dataSet, Pare::val));
	}

	public static LineChart of(ChartType chartType, String id, String label, List<String> labelSet, List<Integer> dataSet) {
		String cat = RES.ofRoot(Xml.class, RSRCC_LINECHART).cat();
		Function<String, Object> plResolver = (key) -> {
			switch (key) {
				case "id":
					return X.empty(id) ? LineChart.getRandomdId("lc") : IT.isRegex(id, Regexs.WORD);
				case "type":
					return wrapLabel(chartType.name());
				case "label":
					return wrapLabel(label);
				case "data":
					return dataSet.toString();
				case "labels":
					return labelSet == null ? dataSet.toString() : STREAM.mapToList(labelSet, Chart::wrapLabel).toString();
				default:
					throw new WhatIsTypeException(key);
			}
		};
		cat = URx.PlaceholderRegex.DOG.findAndReplaceAll(cat, plResolver);
		return new LineChart(cat);
	}

	public enum ChartType {
		line, pie, bar, bubble, doughnut, polarArea, radar, scatter
	}

	public static LineChart ofN(ChartType chartType, String id, Collection<String> labelSet, Map<String, Collection<Integer>> datasets) {
		String cat = RES.ofRoot(Xml.class, RSRCC_LINECHARTN).cat();
		Function<String, Object> plResolver = (key) -> {
			switch (key) {
				case "id":
					return X.empty(id) ? LineChart.getRandomdId("lc") : IT.isRegex(id, Regexs.WORD);
				case "type":
					return wrapLabel(chartType.name());
				case "datasets":
					return MapToStringConverter.convertMapToString(datasets);
				case "labels":
					return labelSet == null ? IT.NN(null) : STREAM.mapToList(labelSet, Chart::wrapLabel).toString();
				default:
					throw new WhatIsTypeException(key);
			}
		};
		cat = URx.PlaceholderRegex.DOG.findAndReplaceAll(cat, plResolver);
		return new LineChart(cat);
	}

	public static class MapToStringConverter {

		public static String convertMapToString(Map<String, Collection<Integer>> map) {
			StringBuilder result = new StringBuilder();
			result.append("[");

			List<String> entries = new ArrayList<>();

			for (Map.Entry<String, Collection<Integer>> entry : map.entrySet()) {
				String label = entry.getKey();
				Collection<Integer> data = entry.getValue();
				data = data instanceof List ? (List) data : ARR.asAL(data);
				StringBuilder dataBuilder = new StringBuilder();
				dataBuilder.append("[");

				for (int i = 0; i < data.size(); i++) {
					dataBuilder.append(((List<Integer>) data).get(i));
					if (i < data.size() - 1) {
						dataBuilder.append(",");
					}
				}

				dataBuilder.append("]");
				entries.add("{label: " + wrapLabel(label) + ", data: " + dataBuilder.toString() + "}");
			}

			result.append(String.join(",", entries));
			result.append("]");

			return result.toString();
		}

		public static void main(String[] args) {
			// Пример использования
			Map<String, Collection<Integer>> exampleMap = MAP.of(
					"label1", ARR.as(1, 5, 7),
					"label2", ARR.as(2, 3, 4)
			);

			String result = convertMapToString(exampleMap);
			System.out.println(result); // Вывод: [{label: label1, data: [1,5,7]},{label: label2, data: [2,3,4]}]
		}
	}


}
