package mpc.env;

public class App {
	public static Boolean IS_APP_DEBUG = false;

	static {
		IS_APP_DEBUG = AP.isDebugEnable();
	}

}
