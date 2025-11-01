package zk_com.charts;


import org.zkoss.zul.Chart;
import org.zkoss.zul.PieModel;
import org.zkoss.zul.SimplePieModel;
import zk_com.core.IZCom;

public class Chart0 extends Chart implements IZCom {

	public Chart0() {

	}

	public static Chart0 newChart() {
		Chart0 chart0 = new Chart0();
		PieModel model = new SimplePieModel();
		model.setValue("C/C++", new Double(21.2));
		model.setValue("VB", new Double(10.2));
		model.setValue("Java", new Double(40.4));
		model.setValue("PHP", new Double(28.2));
		chart0.setModel(model);
		return chart0;
	}
}
