package utl_ssh;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import mpc.fs.UFS;
import mpu.Sys;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpc.fs.fd.EFT;
import mpc.fs.fd.UFD;
import mpu.core.ARG;
import mpe.core.P;
import mpu.IT;
import mpu.X;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleSessionSftp extends SessionSftp {

	public SimpleSessionSftp(String host, int port, String user, String pass) {
		super(host, port, user, pass);
	}

	/**
	 * *************************************************************
	 * ---------------------------- DOWNLOAD -----------------------
	 * *************************************************************
	 */
	public void downloadFile2Dir(String srcFile, String destParentDir, boolean... mkdirsOrSingleDirOrCheck) throws SftpException {
		IT.isFileNotExist(destParentDir);
		IT.notNull(srcFile);
		IT.notNull(destParentDir);
		SimpleSessionSftp ses = this;
		try {
			ses.openSession(SshType.sftp);
			if (X.empty(destParentDir)) {
				destParentDir = "./";
			}
			UFS.MKDIR.createDirsOrSingleDirOrCheckExist(Paths.get(destParentDir), mkdirsOrSingleDirOrCheck);
			if (L.isInfoEnabled()) {
				L.info("Reciving by ftp [{}] to [{}]", srcFile, destParentDir);
			}
			ses.downloadFile_(srcFile, destParentDir);
		} finally {
			ses.closeSession();
		}
	}

	private void downloadFile_(String src, String dst) throws SftpException {
		checkOpenSession();
		if (channelSftp() == null) {
			throw new NullPointerException("wtf, channelFtp is null");
		}
		SftpProgressMonitor progress = new ProgressMonitor();
		try {
			channelSftp().get(src, dst, progress);
		} finally {
			progress.end();
		}
	}

	/**
	 * *************************************************************
	 * ---------------------------- UPLOAD -------------------------
	 * *************************************************************
	 */
	// /z/fm  /var/somdir/
	public void uploadDirContent2Dir(String srcLocalDir, String dstRemoteDir, boolean... mkdiriFnotExist_recursiveOrNotRecursive) throws SftpException {
		Path srcDirPath = Paths.get(srcLocalDir);
		List<Path> allSrcFiles = UFD.CHILDS.getAllChildPaths(srcDirPath, EFT.FILE);
		if (allSrcFiles.isEmpty()) {
			if (L.isInfoEnabled()) {
				L.info("Local DIR '{}' is empty", srcLocalDir);
			}
			return;
		}
		dstRemoteDir = UF.normDir(dstRemoteDir);
		for (Path childSrc : allSrcFiles) {
			Path relative = srcDirPath.relativize(childSrc);
			EFT fileType = EFT.of(childSrc);
			switch (fileType) {
				case FILE:
					String localParentOfFile = "";
					Path localParentPath = relative.getParent();
					if (localParentPath != null) {
						localParentOfFile = UF.normFileStart(localParentPath.toString());
					}
					String dstRemoteParent = dstRemoteDir + localParentOfFile;
					uploadFile2Dir(childSrc.toString(), dstRemoteParent, mkdiriFnotExist_recursiveOrNotRecursive);
					break;
				default:
					throw new WhatIsTypeException(fileType);
			}
		}

	}

	public void uploadFile2Dir(String srcFile, String destParentDir, boolean... mkdiriFnotExist_recursiveOrNotRecursive) throws SftpException {
		if (L.isInfoEnabled()) {
			L.info("SSH:uploadFile2Dir [{}] to [{}], [mkdiriFnotExist_recursiveOrNotRecursive]={}", srcFile, destParentDir, ARG.toDefBooleanOrNull(mkdiriFnotExist_recursiveOrNotRecursive));
		}

		if (!beforeUpload(srcFile, destParentDir)) {
			return;
		}

		IT.isFileExist(Paths.get(srcFile));
		IT.notNull(destParentDir);
		SimpleSessionSftp ses = this;

		if (X.empty(destParentDir)) {
			destParentDir = ".";
		}

		destParentDir = UF.normDirCurrentStartRel(destParentDir);
		if (X.empty(destParentDir)) {
			destParentDir = UF.CURRENT_DIR_UNIX;
		} else if (!ses.isEntryDir(destParentDir)) {
			// ses.printPwd();
			Boolean res = ARG.toDefBooleanOrNull(mkdiriFnotExist_recursiveOrNotRecursive);
			if (res == null) {
				throw new RequiredRuntimeException("Remote Folder '%s' not exist", destParentDir);
			}

			ses.mkdir(destParentDir, res);
		}

		ses.uploadFile_(srcFile, destParentDir);
	}

	private void uploadFile_(String src, String dst) throws SftpException {
		checkOpenSession();
		ProgressMonitor pm = new ProgressMonitor();
		channelSftp().put(src, dst, pm, ChannelSftp.OVERWRITE);
		//		pm.end();
		afterUpload(src, dst);
	}

	protected void uploadFile(String src, String dst, int mode) throws SftpException {
		checkOpenSession();
		channelSftp().put(src, dst, new ProgressMonitor(), mode);
		afterUpload(src, dst);
	}

	protected void afterUpload(String srcFile, String destParentDir) {
	}

	protected boolean beforeUpload(String srcFile, String destParentDir) {
		return true;
	}

	/**
	 * *************************************************************
	 * ---------------------------- MKDIR --------------------------
	 * *************************************************************
	 */

	public void mkdir(String path, boolean rekursive) throws SftpException {
		checkOpenSession();
		if (!rekursive) {
			mkdir_(path);
			return;
		}
		boolean isRoot = path.startsWith("/");
		Path p = Paths.get(path);
		String parent = "";
		for (int ind = 0; ind < p.getNameCount(); ind++) {
			if (ind == 0 && !isRoot) {
				parent += "" + p.getName(ind).toString();
			} else {
				parent += "/" + p.getName(ind).toString();
			}
			if (!isEntryDir(parent)) {
				mkdir_(parent);
			}
		}
	}

	private void mkdir_(String path) throws SftpException {
		IT.NE(path);
		if (L.isInfoEnabled()) {
			L.info("SSH:mkdir:" + path);
		}
		channelSftp().mkdir(path);
	}

	/**
	 * *************************************************************
	 * ---------------------------- MKDIR --------------------------
	 * *************************************************************
	 */

	private String lpwd() throws SftpException {
		checkOpenSession();
		return channelSftp().lpwd();
	}

	public List<String> lsfn(String path) throws SftpException {
		return ls_entrys(path).stream().filter(e -> {
			SftpATTRS attrs = e.getAttrs();
			return attrs.isReg();
		}).map(e -> e.getFilename()).collect(Collectors.toList());
	}

	public List<String> lsd(String path) throws SftpException {
		return ls_entrys(path).stream().filter(e -> {
			SftpATTRS attrs = e.getAttrs();
			return attrs.isDir();
		}).map(e -> e.getFilename()).collect(Collectors.toList());
	}

	public List<LsEntry> ls_entrys(String path) throws SftpException {
		if (L.isInfoEnabled()) {
			L.info("SSH:ls_entrys:" + path);
		}
		checkOpenSession();

		List<LsEntry> lsEntrys = new ArrayList<LsEntry>();

		ChannelSftp channelSftp = channelSftp();
		IT.notNull(channelSftp, "channelSftp");
		java.util.Vector vv = channelSftp.ls(path);
		if (vv != null) {
			for (int ii = 0; ii < vv.size(); ii++) {
				Object obj = vv.elementAt(ii);
				if (obj instanceof LsEntry) {
					LsEntry entry = ((LsEntry) obj);
					lsEntrys.add(entry);
				}

			}
		}
		return lsEntrys;
	}

	/**
	 * *************************************************************
	 * ---------------------------- IS ENTRY -----------------------
	 * *************************************************************
	 */
	public boolean isEntryFile(String pathFile) throws SftpException {

		if (X.empty(pathFile)) {
			return false;
		}

		try {
			List<LsEntry> list = ls_entrys(pathFile);
			if (list.size() == 1) {
				String fn = list.get(0).getFilename();
				String srcFn = Paths.get(pathFile).getFileName().toString();
				return fn.equals(srcFn);
			}
			return false;
		} catch (Exception ex) {
			return false;
		}
	}

	public boolean isEntryDir(String pathFile) throws SftpException {

		if (X.empty(pathFile) || isEntryFile(pathFile)) {
			return false;
		}

		try {
			List<LsEntry> list = ls_entrys(pathFile);
			return list.size() > 0;
		} catch (Exception ex) {
			return false;// no such file
		}
	}

	/**
	 * *************************************************************
	 * ---------------------------- COMMON -------------------------
	 * *************************************************************
	 */
	private String pwd() throws SftpException {
		checkOpenSession();
		return channelSftp().pwd();
	}

	public void cd(String current) throws SftpException {
		checkOpenSession();
		channelSftp().cd(current);
		L.info("cd:" + current);
	}

	/**
	 * *************************************************************
	 * ---------------------------- PRINT --------------------------
	 * *************************************************************
	 */
	public void printPwd() {
		checkOpenSession();

		try {
			L.info(pwd());
		} catch (SftpException e) {
			e.printStackTrace();
		}
	}

	public void printDirectory(String path) throws SftpException {
		printDirectory(this, path);
	}

	public static void printDirectory(SimpleSessionSftp t, String path) throws SftpException {
		List<LsEntry> list = t.ls_entrys(path);
		Sys.p("size:" + list.size());
		for (LsEntry lsEntry : list) {
			P.p("FTP FILE[%s],D[%s],F[%s],size[%s]", lsEntry.getFilename(), lsEntry.getAttrs().isDir(),
					lsEntry.getAttrs().isReg(), lsEntry.getAttrs().getSize());
		}
	}

	public static void printLocations(SimpleSessionSftp t) throws SftpException {
		String dir = t.pwd();
		String ldir = t.lpwd();
		Sys.p("remote dir:" + dir);
		Sys.p("remote local dir:" + ldir);
	}

	/**
	 * *************************************************************
	 * ---------------------------- OLD ----------------------------
	 * *************************************************************
	 */
//	@Deprecated
//	public void downloadQuitly(String src, String dest) throws SftpException {
//		openSession(SshType.sftp);
//		new File(dest).mkdirs();
//		downloadFile_(src, dest);
//		closeSession();
//	}
//
//	@Deprecated
//	public boolean isFileAsOld(String pathFile, boolean isFileOrDir) throws SftpException {
//		String current = pwd();
//		try {
//			List<LsEntry> list = ls_entrys(pathFile);
//
//			for (LsEntry lsEntry : list) {
//				Path p = Paths.get(pathFile);
//
//				String fileSrc = p.getFileName().toString();
//				String fileDst = lsEntry.getFilename();
//
//				// U.p("p:" + fileSrc + ":");
//				U.p("entry:" + fileDst + ":search:" + pathFile);
//
//				// U.p("File:" + fileDst + ":" + lsEntry.getAttrs().isReg());
//				// U.p("E:" + fileSrc.equals(fileDst));
//				//
//				// // U.p("isDir:" + lsEntry.getAttrs().isDir());
//				// // U.p("isFile:" + lsEntry.getAttrs().isReg());
//				// // U.p("isFile:" + lsEntry.getAttrs().isReg());
//				// U.p("E2:" + (isFileOrDir && lsEntry.getAttrs().isReg()));
//				// U.p("E3:" + (!isFileOrDir && lsEntry.getAttrs().isDir()));
//				// U.p("E4:" + (true | false));
//
//				if (fileSrc.equals(fileDst)) {
//					return (isFileOrDir && lsEntry.getAttrs().isReg()) | (!isFileOrDir && lsEntry.getAttrs().isDir());
//				}
//
//			}
//
//			return false;
//		} catch (Exception ex) {
//			// ex.printStackTrace();
//			return false;
//		} finally {
//			cd(current);
//		}
//	}
//
//	@Deprecated
//	public boolean isRegularFileOld(String pathFile) throws SftpException {
//		return isFileAsOld(pathFile, true);
//	}
//
//	@Deprecated
//	public boolean isDirectoryOld(String pathFile) throws SftpException {
//		return isFileAsOld(pathFile, false);
//	}
//
//	@Deprecated
//	public static void onUploadQuitly(DefaultSessionSftp session, String src, String dest) throws SftpException {
//
//		session.openSession(SshType.sftp);
//
//		try {
//			{
//				Path parent = Paths.get(dest).getParent();
//				String fn = parent.toFile().toString();
//
//				if (!session.isDirectoryOld(fn)) {
//					session.mkdir(fn, true);
//				}
//
//				L.info("Sending by ftp [{}] to [{}]", src, dest);
//
//				session.uploadFile_(src, dest);
//			}
//		} finally {
//			session.closeSession();
//		}
//	}
//
//	@Deprecated
//	public static <T extends DefaultSessionSftp> void onUploadQuitly(Class<T> session, String src, String dest)
//			throws SftpException {
//		Constructor<?> ctor;
//		try {
//			ctor = session.getConstructor();
//			DefaultSessionSftp i = (DefaultSessionSftp) ctor.newInstance();
//			onUploadQuitly(i, src, dest);
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			e.printStackTrace();
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Deprecated
//	public static <T extends DefaultSessionSftp> void onDownloadQuitly(Class<T> session, String src, String dest)
//			throws SftpException {
//		Constructor<?> ctor;
//		try {
//			ctor = session.getConstructor();
//			DefaultSessionSftp i = (DefaultSessionSftp) ctor.newInstance();
//			i.downloadQuitly(src, dest);
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			e.printStackTrace();
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		}
//	}

}
