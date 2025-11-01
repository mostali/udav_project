package mpe.call_msg.ext;

import mpe.call_msg.CallMsg;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import mpu.str.STR;
import mpu.str.TKN;

import java.util.function.Supplier;

public class SshpassCallMsg extends CallMsg {

	public static final String SSHPASS = "sshpass";
	public static final String PT_SSHP = "ssh -p ";

	final String[] hlp = new String[4];

	@Override
	public String toString() {
		return "SrcSeedStr:" + ARR.as(hlp) + "";
	}

	//sshpass  -p 'pass' ssh -p 22 root@1.2.4.3
	public SshpassCallMsg(String fullMsg) {
		super(fullMsg = fullMsg.trim(), true);

		if (!STR.startsWith(line0, true, SSHPASS)) {
			addError("Except first line with starts %s", SSHPASS);
		}

		String[] pass_i_hl = TKN.two(fullMsg, PT_SSHP, null);

		if (pass_i_hl == null) {
			addError("Except pattern '%s'", PT_SSHP);
			return;
		}
		Supplier<String[]> gethost = () -> {
			String host = TKN.lastGreedy(pass_i_hl[1], " ");
			return TKN.twoGreedy(host, "@");
		};
		Supplier<String> getpass = () -> {
			//sshpass  -p 'pass'
			String last = TKN.last(pass_i_hl[0], SSHPASS);
			last = STR.trimLeft(last);
			IT.state(last.startsWith("-p "), "except -p arg");
			return last.substring(3).trim();
		};
		try {
			String[] host = gethost.get();
			hlp[0] = host[0];
			hlp[1] = getpass.get();
			hlp[2] = host[1];
			hlp[3] = TKN.first(pass_i_hl[1], " ", Integer.class) + "";
		} catch (Exception ex) {
			addError(ex);
		}

	}

	public static SshpassCallMsg ofQk(String msg) {
		return new SshpassCallMsg(msg);
	}

	public static SshpassCallMsg of(String msg) {
		return (SshpassCallMsg) ofQk(msg).throwIsErr();
	}

	public static final String SSH = "ssh -p %s %s@%s";
	public static final String SSHFS = "PSWD=%s && sshfs -o port=%s -o password_stdin %s@%s:%s %s <<<$PSWD";
	public static final String SSHFS_OFF = "fusermount -u %s";
	public static final String SSHUSERADD = "sudo useradd -m -s /bin/bash %s &&\nsudo passwd %s &&sudo usermod -aG sudo %s/";

	public String sshuseraddSh(String usrname) {
		return X.f(SSHUSERADD, usrname, usrname, usrname);
	}

	public String sshfsSh(String src, String dst) {
		return X.f(SSHFS, hlp[1], hlp[3], hlp[0], hlp[2], IT.NE(src), IT.NE(dst));
	}

	public String sshfs_off_Sh(String dstdir) {
		return X.f(SSHFS_OFF, dstdir);
	}

	public String sshSh() {
		return X.f(SSH, hlp[3], hlp[0], hlp[2]);
	}

	public static final String SSHPASS0 = "sshpass -p %s %s";

	public String sshpassSh() {
		return X.f(SSHPASS0, hlp[1], sshSh());
	}
}
