package mpe.restapp;

class RestApp {

	public static void main(String[] args) {
		RestRoute.of(8008, "/ping").on(ex0 -> {
			return Rsp.ok("ponk");
		});
	}
}
