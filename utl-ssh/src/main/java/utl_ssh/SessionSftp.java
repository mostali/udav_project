package utl_ssh;

import com.jcraft.jsch.*;
import mpu.Sys;
import org.slf4j.Logger;
import mpc.log.LoggerToSystemOut;

import javax.swing.*;
import java.awt.*;

public class SessionSftp {

	//	public static Logger L = LoggerFactory.getLogger(SessionSftp.class);
	public static Logger L = new LoggerToSystemOut();

	protected final String host, user, passwd;

	protected final int port;

	public SessionSftp(String host, int port, String user, String pass) {
		this.host = host;
		this.port = port;

		this.user = user;
		this.passwd = pass;

		// out = new BPrintStream();

	}

	protected void checkOpenSession() {
		if (channelSftp().isClosed()) {
			throw new RuntimeException("Session is close");
		}
		if (!channelSftp().isConnected()) {
			throw new RuntimeException("Session is not connected");
		}
	}

	protected Channel _channel = null;

	protected ChannelSftp channelSftp() {
		return (ChannelSftp) _channel;
	}

	protected ChannelExec channelExec() {
		return (ChannelExec) _channel;
	}

	protected Session session = null;
	protected final JSch jsch = new JSch();
	protected final static int defTryCount = 10;

	public enum SshType {
		sftp, exec
	}

	public SessionSftp openSessionSftp() {
		openSession(SshType.sftp);
		return this;
	}

	public SessionSftp openSession(SshType type) {
		openSession(type, null);
		return this;
	}

	public static final int TIMEOUT_MS = 20_000;

	public void openSession(SshType type, String cmd) {
		if (L.isInfoEnabled()) {
			L.info("SSH:Opening...");
		}
		int tryCount = defTryCount;

		while (tryCount-- > 0) {
			try {

				// String khosts = System.getProperty("user.home") +
//				String khosts = "root" + "/.ssh/known_hosts";
				// U.p("setKnownHosts :" + khosts);
				// jsch.setKnownHosts(khosts);
				// jsch.addIdentity("/root/.ssh/id_rsa", "keyssh21".getBytes());

				this.session = jsch.getSession(user, host, port);

				UserInfo ui = new MyUserInfo(passwd);
				session.setUserInfo(ui);
//				session.setPassword(passwd);

				// java.util.Properties config = new java.util.Properties();
				// config.put("StrictHostKeyChecking", "no");
				// session.setConfig(config);

				if (L.isInfoEnabled()) {
					L.info("SSH:Session connecting:{}:{}:{}/{}", user, host, port, TIMEOUT_MS);
				}
				session.connect(TIMEOUT_MS);
				Channel channel = session.openChannel(type.name());

				if (SshType.exec == type) {
					((ChannelExec) channel).setCommand(cmd);
				}
				// jsch.addIdentity(host, "".getBytes(), (byte[]) null, (byte[])
				// null);
				if (L.isInfoEnabled()) {
					L.info("SSH:Channel connecting/{}", TIMEOUT_MS);
				}
				channel.connect(TIMEOUT_MS);
				_channel = channel;
				break;
			} catch (Exception e) {
				if (RTException.isChannelNotOpen(e)) {
					L.trace("channel is not opened..try :" + tryCount);
				} else {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public void closeSession() {
		if (L.isInfoEnabled()) {
			L.info("SSH:Closing...");
		}
		if (_channel != null) {
			_channel.disconnect();
		}
		if (session != null) {
			session.disconnect();
		}
		_channel = null;
		session = null;
	}

	public static class MyUserInfo implements UserInfo, UIKeyboardInteractive {

		private String passwd;

		public MyUserInfo(String pass) {
			this.passwd = pass;
		}

		public String getPassword() {
			return passwd;
		}

		public boolean promptYesNo(String str) {
			// Object[] options = { "yes", "no" };
			// int foo = JOptionPane.showOptionDialog(null, str, "Warning",
			// JOptionPane.DEFAULT_OPTION,
			// JOptionPane.WARNING_MESSAGE, null, options, options[0]);
			// return foo == 0;
			return true;
		}

		// String passwd;
		JTextField passwordField = (JTextField) new JPasswordField(20);

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		public boolean promptPassword(String message) {
			// Object[] ob = { passwordField };
			// user
			// int result = JOptionPane.showConfirmDialog(null, ob, message,
			// JOptionPane.OK_CANCEL_OPTION);
			// if (result == JOptionPane.OK_OPTION) {
			// passwd = passwordField.getText();
			// return true;
			// } else {
			// return false;
			// }
			// this.passwd = SftpCopier.passwd;
			return true;
		}

		public void showMessage(String message) {
			JOptionPane.showMessageDialog(null, message);
		}

		final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		private Container panel;

		public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo) {
			panel = new JPanel();
			panel.setLayout(new GridBagLayout());

			gbc.weightx = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.gridx = 0;
			panel.add(new JLabel(instruction), gbc);
			gbc.gridy++;

			gbc.gridwidth = GridBagConstraints.RELATIVE;

			JTextField[] texts = new JTextField[prompt.length];
			for (int i = 0; i < prompt.length; i++) {
				gbc.fill = GridBagConstraints.NONE;
				gbc.gridx = 0;
				gbc.weightx = 1;
				panel.add(new JLabel(prompt[i]), gbc);

				gbc.gridx = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.weighty = 1;
				if (echo[i]) {
					texts[i] = new JTextField(20);
				} else {
					texts[i] = new JPasswordField(20);
				}
				panel.add(texts[i], gbc);
				gbc.gridy++;
			}

			if (JOptionPane.showConfirmDialog(null, panel, destination + ": " + name, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
				String[] response = new String[prompt.length];
				for (int i = 0; i < prompt.length; i++) {
					response[i] = texts[i].getText();
				}
				return response;
			} else {
				return null; // cancel
			}
		}
	}

	/*
	 * public static class MyProgressMonitor implements
	 * com.jcraft.jsch.ProgressMonitor{ JProgressBar progressBar; JFrame frame;
	 * long count=0; long max=0; public void init(String info, long max){
	 * this.max=max; if(frame==null){ frame=new JFrame(); frame.setSize(200,
	 * 20); progressBar = new JProgressBar(); } count=0; frame.setTitle(info);
	 * progressBar.setMaximum((int)max); progressBar.setMinimum((int)0);
	 * progressBar.setValue((int)count); progressBar.setStringPainted(true);
	 * JPanel p=new JPanel(); p.add(progressBar);
	 * frame.getContentPane().add(progressBar); frame.setVisible(true);
	 * System.out.println("!info:"+info+", max="+max+" "+progressBar); } public
	 * void count(long count){ this.count+=count;
	 * System.out.println("count: "+count);
	 * progressBar.setValue((int)this.count); } public void end(){
	 * System.out.println("end"); progressBar.setValue((int)this.max);
	 * frame.setVisible(false); } }
	 */
	public static class ProgressMonitor implements SftpProgressMonitor {
		ProgressMonitor monitor;
		long count = 0;
		long max = 0;

		boolean isEnd = false;

		public void init(int op, String src, String dest, long max) {
			this.max = max;

			count = 0;
			percent = -1;
		}

		private long percent = -1;

		public boolean count(long count) {
			this.count += count;

			if (percent >= this.count * 100 / max) {
				return true;
			}
			percent = this.count * 100 / max;

			Sys.p("Completed:" + this.count + "(" + percent + "%) out of " + max + ".");

			return !isEnd;
		}

		public void end() {
			isEnd = true;
		}
	}

}
