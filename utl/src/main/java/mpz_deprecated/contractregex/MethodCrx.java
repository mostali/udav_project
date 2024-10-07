package mpz_deprecated.contractregex;

import mpu.Sys;

import java.util.regex.Pattern;

@Deprecated
public interface MethodCrx extends ContractRx {

	String TEST = "mt()";

	String METHOD_NAME = "\\s*(?<m>\\w+[a-zA-Z0-9_$]+)\\s*";
	String BODY = "\\s*(?<b>\\(.*?\\))\\s*";

	String RX = METHOD_NAME + BODY;

	Pattern PX = Pattern.compile(RX, Pattern.CASE_INSENSITIVE);

	String m();

	String b();

	static MethodCrx of(String data) {
		return ContractRx.buildContract(data, PX, MethodCrx.class);
	}

	static void main(String[] args) {
		Sys.exit(test());
	}

	static Object test() {
		return of(TEST).__map();
	}

	static boolean matches(String value) {
		return PX.matcher(value).matches();
	}
}
