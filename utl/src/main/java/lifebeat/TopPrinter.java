package lifebeat;

import mpu.Sys;

import java.io.InputStream;

public class TopPrinter {

	public static void main(String[] args) {
		Sys.p(print(0));
	}

	public static StringBuilder print(int tabLevel) {
		StringBuilder top = top();
		return top;
	}

	//https://phoenixnap.com/kb/top-command-in-linux
	private static StringBuilder top() {
		StringBuilder sb = new StringBuilder();
//		ProcessBuilder pb = new ProcessBuilder("top", "-l", "1");
		ProcessBuilder pb = new ProcessBuilder("top", "-b");
		pb.redirectError();
		try {
			Process p = pb.start();
			InputStream is = p.getInputStream();
			int value = -1;
			int ctr = 1000;
			while ((value = is.read()) != -1) {
				System.out.print(((char) value));
				sb.append(((char) value));
				if (ctr-- < 0) {
					p.destroyForcibly();
				}
			}
			int exitCode = p.waitFor();
//			System.out.println("Top exited with " + exitCode);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return sb;
	}

}
