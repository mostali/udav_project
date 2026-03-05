package mpe.cmsg.std;

import lombok.Setter;
import mpc.fs.path.IPath;
import mpe.cmsg.core.CallMsg;
import mpe.cmsg.core.INode;
import mpu.IT;
import mpu.SysExec;
import mpu.X;
import mpu.core.RW;
import mpu.str.STR;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PyCallMsg extends CallMsg {

	public static final String KEY = "python";
	public static final String LINE0 = "#python";

	public static boolean isValidKey(String data) {
		return STR.startsWith(data, LINE0);
	}

	public static boolean isValid(String data) {
		return PyCallMsg.ofQk(data).isValid();
	}

	public PyCallMsg(INode iNode) {
		super(iNode);
	}

	public PyCallMsg(String fullMsg) {
		super(fullMsg, true);

		switch (state) {
			case EMPTY:
				addError("Empty msg");
				return;

			case LINE:
				break;

			default:
			case BODY:
				break;
		}

	}

	private @Setter String workDir;

	@Override
	public Object call(boolean throwIfHasError, Object... args) { //TODO args nu
		return workDir != null ? SysExec.exePython3(IT.isDirExist(Paths.get(workDir), "Except work dir '%s' for python scripts", workDir), msg) : SysExec.exePython3(msg);
	}

	@Override
	public String toString() {
		return "PyCallMsg{" +
//				"msg='" + fullMsg + '\'' +
//				", class='" + className + '\'' + ", method='" + classMethodName + '\'' + ", headers=" + headersParams + ", context=" + context +
				", errs=" + X.sizeOf0(getErrors()) + '}';
	}


	public static PyCallMsg of(IPath file) {
		return (PyCallMsg) ofQk(file).throwIsErr();
	}

	public static PyCallMsg ofQk(Path file) {
		return ofQk(IPath.of(file));
	}

	public static PyCallMsg ofQk(IPath file) {
		return (PyCallMsg) of(file.fCat()).setFromSrc(file);
	}

	public static PyCallMsg of(Path file) {
		return of(RW.readString(file));
	}

	public static PyCallMsg of(String msg) {
		return (PyCallMsg) ofQk(msg).throwIsErr();
	}

	public static PyCallMsg ofQk(String msg) {
		return new PyCallMsg(msg);
	}


//	public static class PythonScriptRunner {
//
//		public static void main(String[] args) {
//			// Укажите путь к вашему Python-скрипту
//			String scriptPath = "/path/to/your/script.py"; // Замените на реальный путь к вашему скрипту
//
//			// Создаем ProcessBuilder
//			ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath);
//
//			// Устанавливаем рабочую директорию, если необходимо
//			processBuilder.directory(new java.io.File("/home/dav/pjnsi/insi/_cicd/nifi/export/export-flow.sh")); // Замените на нужный путь
//
//			try {
//				// Запускаем процесс
//				Process process = processBuilder.start();
//
//				// Читаем вывод скрипта
//				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//				String line;
//				while ((line = reader.readLine()) != null) {
//					L.info(line);
//				}
//
//				// Ждем завершения процесса и получаем код возврата
//				int exitCode = process.waitFor();
//				L.info("Exited with code: " + exitCode);
//			} catch (IOException | InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//	}

}
