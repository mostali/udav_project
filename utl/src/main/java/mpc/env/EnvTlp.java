package mpc.env;

import lombok.RequiredArgsConstructor;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.UFS;
import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;

import java.nio.file.Path;

@RequiredArgsConstructor
public class EnvTlp {
	final Path srcFileHLP;

	public static void main(String[] args) {
		X.exit(ofHlp("lime"));
	}

	public static EnvTlp ofHlp(String usrName, EnvTlp... defRq) {
		Path srcFileHLP = Env.PD_ENV_TLP.resolve("rs").resolve(".env." + usrName).resolve("hlp");
		return UFS.existFile(srcFileHLP) ? new EnvTlp(srcFileHLP) : ARG.toDefThrow(() -> new RequiredRuntimeException("Env hlp no found:" + usrName), defRq);
	}

	public static EnvTlp ofHlpOrg(String orgName, String usrName, EnvTlp... defRq) {
		Path srcFileHLP = Env.PD_ENV_TLP.resolve(orgName).resolve(usrName).resolve("hlp");
		return UFS.existFile(srcFileHLP) ? new EnvTlp(srcFileHLP) : ARG.toDefThrow(() -> new RequiredRuntimeException("Env hlp no found:" + orgName), defRq);
	}

	@Override
	public String toString() {
		return readLogin(null) + "/" + readPass(null) + " :: " + readHost(null) + "/" + readPort(null);
	}

	public String readLogin(String... defRq) {
		return readLine(0, defRq);
	}

	public String readPass(String... defRq) {
		return readLine(1, defRq);
	}

	public String readHost(String... defRq) {
		return readLine(2, defRq);
	}

	public String readPort(String... defRq) {
		return readLine(3, defRq);
	}

	public String readLine(int index, String... defRq) {
		return RW.readLine(srcFileHLP, index, defRq);
	}
}
