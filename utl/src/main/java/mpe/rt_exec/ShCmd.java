//package mpe.rt_exec;
//
//import mpu.X;
//import mpu.IT;
//import org.apache.commons.lang3.StringUtils;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//
//public class ShCmd {
//
//	final String shCommandPattern;
//	private int countArgs = -1;
//
//	public ShCmd(String shCommandPattern) {
//		this.shCommandPattern = shCommandPattern;
//	}
//
//	public int getCountArgs() {
//		return countArgs < 0 ? StringUtils.countMatches(shCommandPattern, "%s") : countArgs;
//	}
//
//	int exitValue = -1;
//
//	public StringBuilder getOutput() {
//		return output;
//	}
//
//	private StringBuilder output = new StringBuilder();
//	private StringBuilder outputErr = new StringBuilder();
//
//	public ShCmd exec(Object... args) {
//		IT.isEq(getCountArgs(), args.length);
//		String shCommand = X.f(shCommandPattern, args);
//
//		ProcessBuilder processBuilder = new ProcessBuilder();
//
//		processBuilder.command("bash", "-c", shCommand);
//
//		try {
//
//			Process process = processBuilder.start();
//
//			BufferedReader reader = new BufferedReader(
//					new InputStreamReader(process.getInputStream()));
//
//			String line;
//			while ((line = reader.readLine()) != null) {
//				output.append(line).append("\n");
//			}
//
//			BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//			while ((line = stdError.readLine()) != null) {
//				outputErr.append(line).append("\n");
//			}
//
//			exitValue = process.waitFor();
//
//			if (exitValue == 0) {
//				return this;
//			}
//
//			if (needIgnore(exitValue)) {
//				return this;
//			}
//
//			throw new IllegalStateException(outputErr.toString());
//		} catch (IOException e) {
//			throw new IllegalStateException(e);
//		} catch (InterruptedException e) {
//			throw new IllegalStateException(e);
//		}
//	}
//
//	private boolean needIgnore(int exitValue) {
//		return ignoreErrors == exitValue;
//	}
//
//	int ignoreErrors;
//
//	public ShCmd ignoreErrors(int error) {
//		this.ignoreErrors = error;
//		return this;
//	}
//}
