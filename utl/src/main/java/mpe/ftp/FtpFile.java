package mpe.ftp;

import lombok.RequiredArgsConstructor;
import mpc.fs.fd.EFT;
import mpc.fs.fd.IFd;
import mpu.core.QDate;
import org.apache.commons.net.ftp.FTPFile;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class FtpFile implements IFd {
	private final String dir;
	private final FTPFile ff;

	public String fDir() {
		return dir;
	}

	public String fName() {
		return ff.getName();
	}

	public QDate fCreated() {
		return QDate.of(ff.getTimestamp().getTime());
	}

	@Override
	public EFT fType(EFT... defRq) {
		return EFT.FILE;
	}

	@Override
	public String toString() {
		return toStringWithDate();
	}

	public String toStringNative() {
		return ff.getRawListing();
	}

}
