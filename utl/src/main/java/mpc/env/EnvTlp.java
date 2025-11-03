package mpc.env;

import lombok.RequiredArgsConstructor;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.UFS;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;

import java.nio.file.Path;
import java.util.Arrays;

@RequiredArgsConstructor
public class EnvTlp implements ITlp {
	public static final String FN_HLP = "hlp";
	final Path srcFileHLP;

	public static void main(String[] args) {
		X.exit(ofHlpRs("lime"));
	}

	public static EnvTlp ofHlpRs(String usrName, EnvTlp... defRq) {
		Path srcFileHLP = Env.PD_ENV_TLP.resolve("rs").resolve(".env." + usrName).resolve(FN_HLP);
		return UFS.existFile(srcFileHLP) ? new EnvTlp(srcFileHLP) : ARG.toDefThrow(() -> new RequiredRuntimeException("Env hlp no found:" + usrName), defRq);
	}

	public static EnvTlp ofHlpOrg(String orgName, String usrName, EnvTlp... defRq) {
		Path srcFileHLP = Env.PD_ENV_TLP.resolve(orgName).resolve(usrName).resolve(FN_HLP);
		return UFS.existFile(srcFileHLP) ? new EnvTlp(srcFileHLP) : ARG.toDefThrow(() -> new RequiredRuntimeException("Env org hlp no found:" + orgName), defRq);
	}

	public static EnvTlp ofHlpVkUsrTk(String name, EnvTlp... defRq) {
		Path srcFileHLP = Env.PD_ENV_TLP.resolve("vk/usrtk").resolve(name).resolve(FN_HLP);
		return UFS.existFile(srcFileHLP) ? new EnvTlp(srcFileHLP) : ARG.toDefThrow(() -> new RequiredRuntimeException("Env usrtk hlp no found:" + name), defRq);
	}

	public static EnvTlp ofHlpTtm(String name, EnvTlp... defRq) {
		return ofHlp("ttm", name, defRq);
	}

	public static EnvTlp ofHlp(String parent, String name, EnvTlp... defRq) {
		Path srcFileHLP = Env.PD_ENV_TLP.resolve(parent).resolve(name).resolve(FN_HLP);
		return UFS.existFile(srcFileHLP) ? new EnvTlp(srcFileHLP) : ARG.toDefThrow(() -> new RequiredRuntimeException("Env %s hlp no found:%s", parent, name), defRq);
	}

	public static EnvTlp ofSysAcc(String sys, EnvTlp... defRq) {
		return ofSysAcc(sys, Env.getUserName(), defRq);
	}

	public static EnvTlp ofSysAcc(String sys, String acc, EnvTlp... defRq) {
		Path srcFileHLP = Env.PD_ENV_TLP.resolve("sys").resolve(sys).resolve(acc).resolve(FN_HLP);
		return UFS.existFile(srcFileHLP) ? new EnvTlp(srcFileHLP) : ARG.toDefThrow(() -> new RequiredRuntimeException("Env no contain sys '%s' with entity '%s' not found", sys, acc), defRq);
	}

	public static String _usrOrg() {
		return RW.readString(usrPath(Env.getUserName(), "org"));
	}

	public static Path usrPath(String file) {
		return usrPath(Env.getUserName(), file);
	}

	public static Path usrPath(String usr, String fileOrDir, Path... defRq) {
		Path fileOrDirPath = Env.PD_ENV_TLP.resolve("usr").resolve(IT.isFilename(usr)).resolve(fileOrDir);
		return UFS.exist(fileOrDirPath) ? fileOrDirPath : ARG.toDefThrow(() -> new RequiredRuntimeException("Except existed usrPath '%s'", fileOrDirPath), defRq);
	}


	public static EnvTlp ofHlpOrgCU(EnvTlp... defRq) {
		return ofHlpOrg(_usrOrg(), Env.getUserName());
	}

	public static EnvTlp ofFile(Path tlp) {
		return new EnvTlp(tlp);
	}

	@Override
	public String toString() {
		return readLogin(null) + "/" + readPass(null) + " :: " + readHost(null) + "/" + readPort(null);
	}

	@Override
	public String readLogin(String... defRq) {
		return readLine(0, defRq);
	}

	@Override
	public String readPass(String... defRq) {
		return readLine(1, defRq);
	}

	@Override
	public String readHost(String... defRq) {
		return readLine(2, defRq);
	}

	@Override
	public String readPort(String... defRq) {
		return readLine(3, defRq);
	}

	public String readLine(int index, String... defRq) {
		return RW.readLine(srcFileHLP, index, defRq);
	}

	public String readHostWithPort() {
		String port = readPort(null);
		return readHost() + (port == null ? "" : ":" + port);
	}

	public String[] readAsHLP2() {
		return new String[]{readLogin(), readPass()};
	}

	public String[] readAsHLP3() {
		return new String[]{readLogin(), readPass(), readHost()};
	}

	public String[] readAsHLP4() {
		return new String[]{readLogin(), readPass(), readHost(), readPort()};
	}

	public String[] readAsHLP4_loginRq() {
		String[] strings = {readLogin(), readPass(null), readHost(null), readPort(null)};
		return Arrays.stream(strings).filter(X::NN).toArray(String[]::new);
	}

}
