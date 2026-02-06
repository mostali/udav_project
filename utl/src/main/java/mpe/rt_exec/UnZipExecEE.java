package mpe.rt_exec;

import lombok.SneakyThrows;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import mpe.core.P;
import mpc.exception.CleanMessageRuntimeException;
import mpc.exception.EException;
import mpc.exception.NI;
import mpc.exception.SimpleMessageRuntimeException;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.fs.tmpfile.TmpFolderOperation;
import mpe.rt.core.ExecRq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * UnZip 6.00 of 20 April 2009, by Debian. Original by Info-ZIP.
 * <p>
 * Usage: unzip [-Z] [-opts[modifiers]] file[.zip] [list] [-x xlist] [-d exdir]
 * Default action is to extract files in list, except those in xlist, to exdir;
 * file[.zip] may be a wildcard.  -Z => ZipInfo mode ("unzip -Z" for usage).
 * <p>
 * -p  extract files to pipe, no messages     -l  list files (short format)
 * -f  freshen existing files, create none    -t  test compressed archive data
 * -u  update files, create if necessary      -z  display archive comment only
 * -v  list verbosely/show version info       -T  timestamp archive to latest
 * -x  exclude files that follow (in xlist)   -d  extract files into exdir
 * modifiers:
 * -n  never overwrite existing files         -q  quiet mode (-qq => quieter)
 * -o  overwrite files WITHOUT prompting      -a  auto-convert any text files
 * -j  junk paths (do not make directories)   -aa treat ALL files as text
 * -U  use escapes for all non-ASCII Unicode  -UU ignore any Unicode fields
 * -C  match filenames case-insensitively     -L  make (some) names lowercase
 * -X  restore UID/GID info                   -V  retain VMS version numbers
 * -K  keep setuid/setgid/tacky permissions   -M  pipe through "more" pager
 * -O CHARSET  specify a character encoding for DOS, Windows and OS/2 archives
 * -I CHARSET  specify a character encoding for UNIX and other archives
 * <p>
 * See "unzip -hh" or unzip.txt for more help.  Examples:
 * unzip data1 -x joe   => extract all files except joe from zipfile data1.zip
 * unzip -p foo | more  => send contents of foo.zip via pipe into program more
 * unzip -fo foo ReadMe => quietly replace existing ReadMe if archive file newer
 */
public class UnZipExecEE extends EException {

	public static final Logger L = LoggerFactory.getLogger(UnZipExecEE.class);

	public static void main(String[] args) throws Exception {
//		UFS_BASE.MV.moveWithReplaceExisting_()
		P.exit(unzip("/home/dav/pjbf_tasks/64/txt.zip", true, null));
		P.exit();
		String command = "unzip -d /home/dav/pjbf_tasks/64/1/test/inc_threaddumps_0911 /home/dav/pjbf_tasks/64/1/test/inc_threaddumps_0911.zip";
		ExecRq execRq = ExecRq.exec(false, new File("/home/dav/pjbf_tasks/64/1/test/"), command.split("\\s++"));
		String messageReport = execRq.getMessageReport(0);
		P.exit(messageReport);
	}

	public static String unzip(String file, Boolean overwrite_or_skip, String encoding) throws ExecRq {
		Path fileZip = IT.isFileExist(Paths.get(file));
		Path workDir = fileZip.getParent();
		if (overwrite_or_skip != null) {
			String[] opts = overwriteToArgs(overwrite_or_skip);
			if (X.notEmpty(encoding)) {
				opts = ARR.addElements(opts, "-O", encoding);
			}
			return unzip(workDir.toString(), fileZip.toString(), workDir.toString(), opts);
		} else {
			NI.stop();
			new TmpFolderOperation() {
				@SneakyThrows
				@Override
				public void doOperationImpl(Path tmpFile) {
					unzip(tmpFile.toString(), fileZip.toString(), workDir.toString(), null);
					if (UFS.isDirWoContent(tmpFile, true)) {
						L.info("Tmp dir '{}' is empty", UF.toStrConsole(tmpFile));
						return;
					}
//					UFS_BASE.COPY.copyDirectoryToDirectoryWithReplaceFilesSkipExist()
				}
			}.doOperation();
		}
		return null;
	}

	public static String[] overwriteToArgs(boolean overwrite_or_skip) {
		return new String[]{overwrite_or_skip ? "-o" : "-n"};
	}

	public static String unzip(Path workDir, Path file, Path outDir, String[] opts) throws ExecRq {
		return unzip(workDir.toAbsolutePath().toString(), file.toAbsolutePath().toString(), outDir.toAbsolutePath().toString(), opts);
	}

	public static String unzip(String workDir, String file, String outDir, String[] opts) throws ExecRq {
//		String command = "unzip " + X.toString(opts, "") + " -d " + ERR.isDirExist(outDir) + " " + ERR.isFileExist(file);
		String[] cmds = {"unzip"};
		if (X.notEmpty(opts)) {
			cmds = ARR.addElements(cmds, opts);
		}
		cmds = ARR.addElements(cmds, "-d", IT.isDirExist(outDir), IT.isFileExist(file));
		String message = ExecRq.exec(false, new File(IT.isDirExist(workDir)), cmds).getMessage();
		return message;
	}

	/**
	 * *************************************************************
	 * ---------------------------- INIT --------------------------
	 * *************************************************************
	 */
	@Override
	public UnZipExecEE.EE type() {
		return super.type(UnZipExecEE.EE.class);
	}

	public enum EE {
		NOSTATUS, DST_EXIST, EXEC_ERROR;

		public UnZipExecEE I() {
			return new UnZipExecEE(this);
		}

		public UnZipExecEE I(Throwable ex) {
			return new UnZipExecEE(this, ex);
		}

		public UnZipExecEE I(Throwable ex, String msg, Object... args) {
			return new UnZipExecEE(this, new SimpleMessageRuntimeException(ex, msg, args));
		}

		public UnZipExecEE I(String message) {
			return new UnZipExecEE(this, new SimpleMessageRuntimeException(message));
		}

		public UnZipExecEE I(String message, Object... args) {
			return new UnZipExecEE(this, new SimpleMessageRuntimeException(X.f(message, args)));
		}

		public UnZipExecEE M(String message, Object... args) {
			return new UnZipExecEE(this, new CleanMessageRuntimeException(X.f(message, args)));
		}
	}

	public UnZipExecEE() {
		super(UnZipExecEE.EE.NOSTATUS);
	}

	public UnZipExecEE(UnZipExecEE.EE error) {
		super(error);
	}

	public UnZipExecEE(UnZipExecEE.EE error, Throwable cause) {
		super(error, cause);
	}


}
