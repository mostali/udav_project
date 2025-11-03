package mpe.call_msg;

import lombok.Getter;
import lombok.Setter;
import mpc.exception.FIllegalStateException;
import mpc.fs.UFS;
import mpc.fs.path.IPath;
import mpe.call_msg.core.INode;
import mpe.call_msg.injector.NodeData;
import mpe.call_msg.injector.TrackMap;
import mpe.call_msg.srv.CallMsgOut;
import mpu.*;
import mpu.core.ARG;
import mpu.core.RW;
import mpu.func.FunctionV2;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.str.STR;
import mpu.str.UST;

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

	@Override
	public Integer getAsyncWaitMs(Integer... defRq) {
		if (ARG.isDef(defRq)) {
			String headerBashValueByKey = getHeaderKey_AsBashComment(ASYNC_WAIT_MS, null);
			return UST.INT(headerBashValueByKey, defRq);
		} else {
			return UST.INT(getHeaderKey_AsBashComment(ASYNC_WAIT_MS));
		}
	}

	public Path getFileSh(Path... defRq) {
		String fileSh = getHeaderKey_AsBashComment("fileSh", null);
		return fileSh != null ? Paths.get(fileSh) : ARG.toDefThrowMsg(() -> X.f("Except arg 'fileSh'"), defRq);
	}


	public static void main(String[] args) {
		String data = "#!/bin/bash\n" + "echo \"file:$USER `pwd`\"";
		BashCallMsg callMsg = BashCallMsg.of(data);
		Pare<Integer, List<String>> integerListPare = callMsg.invokeShFile_V2(true);
		X.empty(integerListPare);
	}

	public BashCallMsg(INode iNode) {
		this(iNode.readNodeDataStr(), true);
	}

	public Object callWith(boolean throwIfHasError, Object... args) {
		return invokeShFile_V2(throwIfHasError, args);
	}

	@Override
	public Object call(boolean throwIfHasError, Object... args) {
		return invokeShFile_V2(throwIfHasError, args);
	}

	public Pare3<Integer, String, String> invokeShFile_V3(CallMsgOut callOutWriter, boolean... throwIfHasError) {

		Path workDir = getWorkDir() != null ? getWorkDir() : fileSh.getParent();

		Path fileSh = getFileSh(null);

//		boolean isSync = isSync();

//		if (isSync) {

		FunctionV2<String, String> funcOutErrLoggerDefault = (i, e) -> {
			if (i == null && e == null) {
				return;
			}
			if (callOutWriter != null) {
				if (true) {
					callOutWriter.write_state(i, e);
				} else {
					if (i != null) {
						callOutWriter.write_info(i);
					}
					if (e != null) {
						callOutWriter.write_info(e);
					}
				}
			} else {
				if (L.isDebugEnabled()) {
					L.debug("CallOutWriter is off");
				}
			}
		};

		Pare3<Integer, String, String> rslt;
		if (fileSh != null) {
			rslt = SysExecAsync.execSync_FileSh(workDir, fileSh, funcOutErrLoggerDefault);
		} else {
			rslt = SysExecAsync.execSync_ShData(workDir, getFileData(), funcOutErrLoggerDefault);
		}
		return rslt;
	}

	@Deprecated
	public Pare<Integer, List<String>> invokeShFile_V2(boolean throwIfHasError, Object... args) {
		Path fileSh = getFileSh(null);
		if (fileSh != null) {
			IT.isFileExist(fileSh);
			Path workDir = getWorkDir() != null ? getWorkDir() : fileSh.getParent();
			Pare<Integer, List<String>> codePare = SysExec.exec_file("/bin/bash", fileSh, workDir, throwIfHasError, args);
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
				codePare = SysExec.exec_filetmp("/bin/bash", super.fileData, null, throwIfHasError, args);
			} else {
				L.info("WorkDir*:" + UFS.ls(workDir));
				codePare = SysExec.exec_filetmp("/bin/bash", super.fileData, workDir, throwIfHasError, args);
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


	public static BashCallMsg of(INode node, TrackMap.TrackId trackId, boolean... lazyValid) {

		NodeData injected = node.inject(trackId);

//		NodeData nodeData = node.nodeDataInjected(trackId);
		BashCallMsg callMsg = BashCallMsg.of(injected.nodeDataStr(), lazyValid);

		injected.setCallMsg(callMsg);

		callMsg.setWorkDir(node.toPath());

//		BashCallMsg bashCallMsg = (BashCallMsg) ofQk(node, lazyValid).throwIsErr();
//		nodeData.setCallMsg(callMsg);
		return callMsg;
	}

	public static BashCallMsg ofQk(Path file, boolean... lazyValid) {
		return ofQk(IPath.of(file), lazyValid);
	}

	public static BashCallMsg ofQk(IPath file, boolean... lazyValid) {
		return (BashCallMsg) of(file.fCat(), lazyValid).setFromSrc(file);
	}

	public static BashCallMsg of(Path file, boolean... lazyValid) {
		return of(RW.readString(file), lazyValid);
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


}
