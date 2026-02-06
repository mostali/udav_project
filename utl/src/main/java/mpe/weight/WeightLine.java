package mpe.weight;

public class WeightLine {

	//	public static void main(String[] args) {
	//		WeightLine w = new WeightLine(0.0, 1.0, 10.0, 20.0);
	//		P.p(w.getWeightFor(21.0) + "");
	//	}

	public final Double x1, y1, x2, y2;

	public WeightLine(Double x1, Double y1, Double x2, Double y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public Double getWeightFor(Double x) {
		Double y = (x * (y2 - y1) - x1 * y2 + x2 * y1) / (x2 - x1);
		return y;
	}

}
