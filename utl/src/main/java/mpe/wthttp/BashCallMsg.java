package mpe.wthttp;

import lombok.Getter;
import lombok.Setter;
import mpc.exception.FIllegalStateException;
import mpc.fs.path.IPath;
import mpu.IT;
import mpu.Sys;
import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;
import mpu.pare.Pare;
import mpu.str.STR;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class BashCallMsg extends CallMsg {

	public static final String KEY = "#!/bin/bash";

	public static boolean isValidKeyFirstLine(String data) {
		return STR.startsWith(data, KEY);
	}

	@Override
	public String type(Object... defRq) {
		return KEY;
	}

	//	@SneakyThrows
//	public static Object invokeShFile(Path fileSh, Path s, boolean throwIfHasError) {
//		Pare<Integer, List<String>> codePare = Sys.exec_file("/bin/bash", fileSh, s, throwIfHasError);
//		return codePare;
//	}

	@Setter
	private @Getter Path workDir;

	@Setter
	private Path fileSh;

	@Setter
	private @Getter String fileData;

	public static void main(String[] args) {
		String data = "#!/bin/bash\n" +
				"echo \"file:$USER `pwd`\"";
		BashCallMsg callMsg = BashCallMsg.of(data);
		callMsg.setFileData(data);
		Pare<Integer, List<String>> integerListPare = callMsg.invokeShFile(true);
		X.empty(integerListPare);
	}


	public Pare<Integer, List<String>> invokeShFile(boolean throwIfHasError) {
		Path fileSh = getFileSh(null);
		if (fileSh != null) {
			IT.isFileExist(fileSh);
			Path workDir = getWorkDir() != null ? getWorkDir() : fileSh.getParent();
			Pare<Integer, List<String>> codePare = Sys.exec_file("/bin/bash", fileSh, workDir, throwIfHasError);
			return codePare;
		} else if (fileData != null) {
			Path workDir;
			if (getWorkDir() != null) {
				workDir = getWorkDir();
			} else if (fileSh != null) {
				workDir = getWorkDir();
			} else {
				workDir = null;
			}
			Pare<Integer, List<String>> codePare;
			if (workDir == null) {
				codePare = Sys.exec_filetmp("/bin/bash", getFileData(), null, throwIfHasError);
			} else {
				codePare = Sys.exec_filetmp("/bin/bash", getFileData(), workDir, throwIfHasError);
			}
			return codePare;
		}
		throw new FIllegalStateException("Set fileSh or fileData");
	}

//	public Pare<Thread0, Object> invokeJarMethodAsyncAndWait(long joinMs, Object... defRq) {
//		Thread0 objThread = new Thread0(getClass().getSimpleName() + "_Async", true) {
//			@Override
//			public void run() {
//				set_result_object(invokeShFile());
//			}
//		};
//		Object andWaitResult = objThread.getAndWaitResult(joinMs, defRq);
//		objThread.throwIfHasErrors();
//		return Pare.of(objThread, andWaitResult);
//	}

	public BashCallMsg(String fullMsg, boolean... lazyValid) {
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

		if (!KEY.equals(line0.trim())) {
			addError("Except file with first line %s", KEY);
		}
//		String classWithMethodStr = USToken.lastGreedy(line0, KEY + ":", null);
//		if (classWithMethodStr == null) {
//			addError("Set KEY & class with method use pattern [%s::package.classname#methhod] in line '%s'", KEY, line0);
//			return;
//		}
//
//		String[] classWithMethod = USToken.two(classWithMethodStr, "#", null);
//
//		if (classWithMethod == null) {
//			addError("Set class with method use pattern [package.classname#methhod] in line '%s'", line0);
//			return;
//		}

//		this.className = classWithMethod[0];
//		this.classMethodName = classWithMethod[1];

//		if (ARG.isDefEqTrue(lazyValid)) {
//			//ok, need lazy valid
//		} else {
//			doLazyValid(true);
//		}

	}

//	public BashCallMsg doLazyValid(boolean... silent) {
//		if (X.empty(className)) {
//			addErrorIfNotExists("Set CLASSNAME use pattern [package.classname#methhod] in line '%s'", line0);
//		} else if (X.empty(classMethodName)) {
//			addErrorIfNotExists("Set class METHOD name use pattern [package.classname#methhod] in line '%s'", line0);
//		}
//		if (ARG.isDefEqTrue(silent)) {
//			throwIsErr();
//		}
//		return this;
//	}

	@Override
	public String toString() {
		return "BashCallMsg{" +
//				"msg='" + fullMsg + '\'' +
				", errs=" + X.sizeOf0(getErrors()) + '}';
	}


	public static BashCallMsg of(IPath file, boolean... lazyValid) {
		return (BashCallMsg) ofQk(file, lazyValid).throwIsErr();
	}

	public static BashCallMsg ofQk(Path file, boolean... lazyValid) {
		return ofQk(IPath.of(file), lazyValid);
	}

	public static BashCallMsg ofQk(IPath file, boolean... lazyValid) {
		return (BashCallMsg) of(file.fCat(), lazyValid).setFromSrc(file);
	}

	public static BashCallMsg of(Path file, boolean... lazyValid) {
		return of(RW.readContent(file), lazyValid);
	}

	public static BashCallMsg of(String msg, boolean... lazyValid) {
		return (BashCallMsg) ofQk(msg, lazyValid).throwIsErr();
	}

	public static BashCallMsg ofQk(String msg, boolean... lazyValid) {
		return new BashCallMsg(msg, lazyValid);
	}

	public static boolean isValid(String data) {
		return BashCallMsg.ofQk(data).isValid();
	}

	public Path getFileSh(Path... defRq) {
		String fileSh = getHeaderBashValueByKey("fileSh", null);
		return fileSh != null ? Paths.get(fileSh) : ARG.toDefThrowMsg(() -> X.f("Except arg 'fileSh'"), defRq);
	}
}
