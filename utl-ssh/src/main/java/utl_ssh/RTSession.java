package utl_ssh;

import com.jcraft.jsch.*;
import lombok.SneakyThrows;
import mpe.core.P;
import mpe.rt.SLEEP;
import mpu.pare.Pare;
import mpc.arr.QUEUE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Queue;

public class RTSession extends SimpleSessionSftp {


	public static void main(String[] args) throws Exception {

		String host = "*.*.*.*";
		int port = 22;
		String user = "login";
		String pass = "pass";
		RTSession rtSession = new RTSession(host, port, user, pass);
		Pare<Integer, Queue> result = rtSession.execSudo("cat h.log",false);
		P.p(result.val().size());
//		rtSession.exec("ls");
	}

	public RTSession(String host, int port, String user, String pass) {
		super(host, port, user, pass);
	}

	public CharSequence exec(String command) throws SftpException {

		L.trace("...start ssh exec :: " + command);

		openSession(SshType.exec, command);

		// L.trace("...session is OPEN");

		StringBuilder out = new StringBuilder();

		InputStream in = null;
		try {
			in = channelExec().getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			try {
				byte[] tmp = new byte[1024];
				while (true) {
					while (in.available() > 0) {
						int i = in.read(tmp, 0, 1024);
						if (i < 0) {
							break;
						}
						System.out.print(new String(tmp, 0, i));
						out.append(new String(tmp, 0, i));
						// return new String(tmp, 0, i);
					}
					if (channelExec().isClosed()) {
						L.trace("exit-status: " + channelExec().getExitStatus());
						break;
					}

					SLEEP.ms(1000);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			// finally {
			// channel.sendSignal("2"); // sends (CTRL+C) signal
			// }
			// commander.println(passwd);

			// commander.close();

			do {
				SLEEP.ms(1000);
			} while (!channelExec().isEOF());

			return out;

		} finally {
			closeSession();
		}

	}

	// http://www.jcraft.com/jsch/examples/Sudo.java.html

	@SneakyThrows
	public Pare<Integer, Queue> execSudo(String command, boolean sudo) {

		Pare<Integer, Queue> returnPare = null;
		JSch jsch = new JSch();

		// Session session = jsch.getSession("root", "192.168.0.1", 22);
		Session session = jsch.getSession(user, host, port);

		UserInfo ui = new MyUserInfo(passwd);
		session.setUserInfo(ui);

		session.connect();

//		String commandExm = "cat h.log";
		Channel channel = session.openChannel("exec");

		// man sudo
		// -S The -S (stdin) option causes sudo to read the password from
		// the
		// standard input instead of the terminal device.
		// -p The -p (prompt) option allows you to override the default
		// password prompt and use a custom one.
		if (sudo) {
			((ChannelExec) channel).setCommand("sudo -S -p '' " + command);
		} else {
			((ChannelExec) channel).setCommand(command);
		}

		InputStream in = channel.getInputStream();
		OutputStream out = channel.getOutputStream();
		((ChannelExec) channel).setErrStream(System.err);

		channel.connect();

		out.write((passwd + "\n").getBytes());
		out.flush();

		Integer status = 0;
		Queue rsltOut = QUEUE.cache_queue_sync_FILO(100000);
//		Queue rsltErr = UL.cache_queue_sync_FILO(100000);
		byte[] tmp = new byte[1024];
		while (true) {
			while (in.available() > 0) {
				int i = in.read(tmp, 0, 1024);
				if (i < 0) {
					break;
				}
				String line = new String(tmp, 0, i);
				P.p("work:" + line);
				rsltOut.add(line);
			}
			if (channel.isClosed()) {
				status = channel.getExitStatus();
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (Exception ee) {
			}
		}
		returnPare = Pare.of(status, rsltOut);
		channel.disconnect();
		session.disconnect();

		return returnPare;
	}

}
