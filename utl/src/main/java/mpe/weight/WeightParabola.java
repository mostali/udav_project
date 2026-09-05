package mpe.weight;

public class WeightParabola {

	//	public static void main(String[] args) {
	//		P p1 = new P(-1.0, 2.0);
	//		P p2 = new P(1.0, 5.0);
	//		P p3 = new P(3.0, 2.0);
	//		WeightParabola w = new WeightParabola(p1, p2, p3);
	//		System.out.println(w.getWeightFor(0.0) + ":0");
	//		System.out.println(w.getWeightFor(1.0) + ":1");
	//		System.out.println(w.getWeightFor(10.0) + ":10");
	//
	//	}

	private final P p1, p2, p3;

	public static class P {
		final Double x, y;

		public P(Double x, Double y) {
			this.x = x;
			this.y = y;
		}

	}

	public WeightParabola(P p1, P p2_center, P p3) {
		this.p1 = p1;
		this.p2 = p2_center;
		this.p3 = p3;
	}

	public Double getWeightFor(Double x) {
		Double y = a() * (x * x) + b() * x + c();
		return y;
	}

	private Double a() {
		Double s1 = p1.y * (p2.x - p3.x);
		Double s2 = p2.y * (p3.x - p1.x);
		Double s3 = p3.y * (p1.x - p2.x);

		Double d1 = s1 + s2 + s3;
		Double d2 = d2();
		return d1 / d2;
	}

	private Double b() {
		Double m1 = (p1.y - p2.y) / (p1.x - p2.x);
		Double d1 = (p1.x + p2.x) * (p1.y * (p2.x - p3.x) + p2.y * (p3.x - p1.x) + p3.y * (p1.x - p2.x));
		Double d2 = d2();
		Double m2 = d1 / d2;
		return m1 - m2;
	}

	private Double d2() {
		return -1 * (p1.x - p2.x) * (p2.x - p3.x) * (p3.x - p1.x);
	}

	private double c() {
		return p1.y - a() * (p1.x * p1.x) - b() * p1.x;
	}

}
