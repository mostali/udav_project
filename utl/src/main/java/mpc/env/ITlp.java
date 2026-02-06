package mpc.env;

public interface ITlp {
	String readLogin(String... defRq);

	String readPass(String... defRq);

	String readHost(String... defRq);

	String readPort(String... defRq);

}
