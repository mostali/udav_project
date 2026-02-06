package mpe.rt_exec;

import mpc.fs.UFS;
import mpu.Sys;
import mpu.X;
import mpu.core.ARR;
import mpu.IT;
import mpc.exception.CleanMessageRuntimeException;
import mpc.exception.EException;
import mpc.exception.SimpleMessageRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UDIR;
import mpc.fs.fd.EFT;
import mpe.rt.core.ExecRq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ZipExecEE extends EException {

	public static final Logger L = LoggerFactory.getLogger(ZipExecEE.class);

	public static void main(String[] args) throws Exception {
		String dir = "tmp/dir";
		String zip = "/tmp/ok.zip";
		List<Path> allPaths = UDIR.lsAll(Paths.get(dir));
		Path rsltZip = ZipExecEE.zipAny(null, Paths.get(zip), allPaths, CreateZipOption.REPLACE);
		Sys.exit(rsltZip);
	}

	public enum CreateZipOption {
		UPDATE, REPLACE, THROW_ERROR, RETURN_NULL;

		public boolean check(Path dstZip) throws ZipExecEE {
			EFT ft = EFT.of(dstZip, null);
			if (ft == null) {
				return true;
			}
			switch (this) {
				case THROW_ERROR:
					throw EE.DST_EXIST.I("Dst [%s] path [%s] already exist", ft, dstZip);
				case RETURN_NULL:
					return false;
				case UPDATE:
					return true;
				case REPLACE:
					if (L.isInfoEnabled()) {
						L.info("CreateZipOption '{}' active. Existed path '{}' will be removed", this, dstZip);
					}
					UFS.RM.fdQk(dstZip);
					IT.isDirOrFileNotExist(dstZip);
					return true;
				default:
					throw new WhatIsTypeException(this);
			}
		}
	}

	public static Path zipAny(Path workDirForProcess_orNull, Path dstZip, Path any_files_and_dir, CreateZipOption IF_DST_EXIST_OPTION) {
		return zipAny(workDirForProcess_orNull, dstZip, ARR.as(any_files_and_dir), IF_DST_EXIST_OPTION);
	}


	public static Path zipFd(Path srcFolder, Path dstZip, CreateZipOption IF_DST_EXIST_OPTION) {
		srcFolder = srcFolder.toAbsolutePath();
		dstZip = dstZip.toAbsolutePath();
		return zipAny(srcFolder.getParent(), dstZip, ARR.as(srcFolder.getFileName()), IF_DST_EXIST_OPTION);
	}

	public static Path zipAny(Path workDirForProcess_orNull, Path dstZip, List<Path> any_files_and_dir, CreateZipOption IF_DST_EXIST_OPTION) {
		ExecRq exe = null;
		Exception err = null;
		try {
			File runFrom = workDirForProcess_orNull == null ? null : workDirForProcess_orNull.toFile();
			if (!IF_DST_EXIST_OPTION.check(dstZip)) {
				return null;
			}
			String[] cmd = {"zip", "-r", dstZip.toString()};
			String[] files = any_files_and_dir.stream().map(Path::toString).toArray(String[]::new);
			try {
				exe = ExecRq.exec(false, runFrom, ARR.mergeAll(cmd, files));
			} catch (ExecRq e) {
				err = e;
			}
			return dstZip;
		} catch (Exception ex) {
			err = ex;
		} finally {
			if (err == null) {
				if (L.isInfoEnabled()) {
					String rprt = exe.getMessageReport(0);
					L.info("ZIP:OK: location file://{} \n{}", dstZip, rprt);
				}
			} else {
				if (L.isErrorEnabled()) {
					L.error("ZIP:ERROR:", err);
				}
			}
		}
		return dstZip;
	}

	/**
	 * *************************************************************
	 * ---------------------------- INIT --------------------------
	 * *************************************************************
	 */
	@Override
	public ZipExecEE.EE type() {
		return super.type(ZipExecEE.EE.class);
	}

	public enum EE {
		NOSTATUS, DST_EXIST, EXEC_ERROR;

		public ZipExecEE I() {
			return new ZipExecEE(this);
		}

		public ZipExecEE I(Throwable ex) {
			return new ZipExecEE(this, ex);
		}

		public ZipExecEE I(Throwable ex, String msg, Object... args) {
			return new ZipExecEE(this, new SimpleMessageRuntimeException(ex, msg, args));
		}

		public ZipExecEE I(String message) {
			return new ZipExecEE(this, new SimpleMessageRuntimeException(message));
		}

		public ZipExecEE I(String message, Object... args) {
			return new ZipExecEE(this, new SimpleMessageRuntimeException(X.f(message, args)));
		}

		public ZipExecEE M(String message, Object... args) {
			return new ZipExecEE(this, new CleanMessageRuntimeException(X.f(message, args)));
		}
	}

	public ZipExecEE() {
		super(ZipExecEE.EE.NOSTATUS);
	}

	public ZipExecEE(ZipExecEE.EE error) {
		super(error);
	}

	public ZipExecEE(ZipExecEE.EE error, Throwable cause) {
		super(error, cause);
	}


}
