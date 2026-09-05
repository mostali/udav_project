package mpe.rt.core;

import mpu.Sys;
import mpu.str.SPLIT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Вспомогательный класс для запуска и корректной обработки
 * резульаттов выполнения внешних приложений
 * //Откуда-то взято, подпилено
 */
public class ExecThread extends Thread {

	private static final Logger L = LoggerFactory.getLogger(ExecThread.class);
	private final InputStream is;
	private final String type;
	private final List<String> lines;

	ExecThread(InputStream is, String type, List<String> lines) {
		this.is = is;
		this.type = type;
		this.lines = lines;
	}

	public static Process buildProcess(String... command) throws IOException {
		Runtime rt = Runtime.getRuntime();
		Process proc = command.length == 1 ? rt.exec(command[0]) : rt.exec(command);
		return proc;
	}

	public static Process buildProcess(File dir, String... command) throws IOException {
		ProcessBuilder pb = new ProcessBuilder();
		pb = pb.directory(dir);
		pb = command.length == 1 ? pb.command(command[0]) : pb.command(command);
		return pb.start();
	}

	@Override
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				Sys.p(line);
				if (L.isTraceEnabled()) {
					L.trace("{}>{}", type, line);
				}
				if (lines != null) {
					lines.add(line);
				}
			}
		} catch (IOException ioe) {
			L.error(ioe.getMessage(), ioe);
		}
	}

	public static int execCommand(String command, List<String> out, List<String> err) {
		return execCommand(new String[]{command}, out, err);
	}

	public static int execCommandAsLine(String command, List<String> out, List<String> err) {
		return execCommand(SPLIT.argsBy(command, " "), out, err);
	}

	public static int execCommand(String[] command, List<String> out, List<String> err) {
		return execCommand(null, command, out, err);
	}

	public static int execCommand(File dir, String[] command, List<String> st_out, List<String> err_out) {
		int exitVal = -1;
		try {
			if (L.isDebugEnabled()) {
				L.debug("Start execution of command [{}]", command);
			}

			Process proc = dir == null ? buildProcess(command) : buildProcess(dir, command);

			exitVal = collectLogAsync(proc, st_out, err_out);

			L.debug("Command [{}] exit with value: {}", command, exitVal);
		} catch (Throwable t) {
			L.error("Error during execution command [{}]", command, t);
		}
		return exitVal;
	}

	public static int collectLogAsync(Process process, List<String> st_out, List<String> err_out) throws InterruptedException {
		ExecThread outputGobbler = new ExecThread(process.getInputStream(), "OUT", st_out);
		ExecThread errorGobbler = new ExecThread(process.getErrorStream(), "ERR", err_out);
		errorGobbler.start();
		outputGobbler.start();
		errorGobbler.join();
		outputGobbler.join();
		int status = process.waitFor();
		return status;
	}

	public static int execSync(Process process, List<String> st_out, List<String> err_out) throws IOException, InterruptedException {
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		List<String> list = new ArrayList<String>();
		List<String> listError = new ArrayList<String>();
		String s = null;
		while ((s = stdInput.readLine()) != null) {
			if (st_out != null) {
				st_out.add(s);
			}
		}
		while ((s = stdError.readLine()) != null) {
			if (err_out != null) {
				err_out.add(s);
			}
		}
		int status = process.waitFor();
		return status;
	}

}
