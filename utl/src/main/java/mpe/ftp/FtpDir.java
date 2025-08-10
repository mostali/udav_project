package mpe.ftp;

import lombok.RequiredArgsConstructor;
import mpc.fs.fd.EFT;
import mpc.fs.fd.IFd;
import mpu.core.QDate;
import org.apache.commons.net.ftp.FTPFile;

@RequiredArgsConstructor
public class FtpDir implements IFd {

	private final String dir;
	private final FTPFile ff;

	public String fDir() {
		return dir;
	}

	public String fName() {
		return ff.getName();
	}

	@Override
	public EFT fType(EFT... defRq) {
		return EFT.DIR;
	}

	public QDate fCreated() {
		return QDate.of(ff.getTimestamp().getTime());
	}

	@Override
	public String toString() {
		return toStringWithDate();
	}

	public String toStringWithDate() {
		return "ftp:" + fType() + ":" + dir + "/" + fName() + " <- [" + fCreated() + "]";
	}

	public String toStringNative() {
		return ff.getRawListing();
	}
}
