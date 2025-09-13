package mpe.wthttp;

import lombok.Getter;
import lombok.Setter;
import mpc.exception.FIllegalStateException;
import mpc.fs.UFS;
import mpc.fs.path.IPath;
import mpc.log.L;
import mpe.wthttp.core.INode;
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

	@Setter
	private @Getter Path workDir;

	@Setter
	private Path fileSh;

	public static void main(String[] args) {
		String data = "#!/bin/bash\n" + "echo \"file:$USER `pwd`\"";
		BashCallMsg callMsg = BashCallMsg.of(data);
		Pare<Integer, List<String>> integerListPare = callMsg.invokeShFile(true);
		X.empty(integerListPare);
	}

	public BashCallMsg(INode iNode) {
		this(iNode.toNodeData(), true);
	}

	@Override
	public Object call(boolean throwIfHasError, Object... args) {
//		return invokeShFile(throwIfHasError,args);
		return invokeShFile(throwIfHasError, args);
	}

	public Pare<Integer, List<String>> invokeShFile(boolean throwIfHasError, Object... args) {
		Path fileSh = getFileSh(null);
		if (fileSh != null) {
			IT.isFileExist(fileSh);
			Path workDir = getWorkDir() != null ? getWorkDir() : fileSh.getParent();
			Pare<Integer, List<String>> codePare = Sys.exec_file("/bin/bash", fileSh, workDir, throwIfHasError, args);
			return codePare;
		} else if (super.fileData != null) {
			Path workDir;
			if (getWorkDir() != null) {
				workDir = getWorkDir();
			} else if (fileSh != null) {
				workDir = getWorkDir();
			} else {
				workDir = null;
			}
			L.info("WorkDir:" + UFS.ls(Paths.get(".")));
			Pare<Integer, List<String>> codePare;
			if (workDir == null) {
				codePare = Sys.exec_filetmp("/bin/bash", super.fileData, null, throwIfHasError, args);
			} else {
				L.info("WorkDir*:" + UFS.ls(workDir));
				codePare = Sys.exec_filetmp("/bin/bash", super.fileData, workDir, throwIfHasError, args);
			}
			return codePare;
		}
		throw new FIllegalStateException("Set fileSh or fileData");
	}

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

	}

	@Override
	public String toString() {
		return "BashCallMsg(errs*" + X.sizeOf0(getErrors()) + ")" + STR.NL + fileData;
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
