package utl_ssh;
/*
 * SSHManager
 *
 * @author cabbott
 * @version 1.0
 */

import com.jcraft.jsch.*;
import lombok.SneakyThrows;
import mpc.log.L;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

//https://stackoverflow.com/questions/2405885/run-a-command-over-ssh-with-jsch
public class SSHManager {
	private static final Logger LOGGER = Logger.getLogger(SSHManager.class.getName());
	private JSch jschSSHChannel;
	private String strUserName;
	private String strConnectionIP;
	private int intConnectionPort;
	private String strPassword;
	public Session sesConnection;
	private int intTimeOut;
	private boolean STRICT_HOST_KEY_CHECKING = false;//UnknownHostKey: 192.168.0.1 ECDSA key fingerprint is 51:23:72:ff:c5:b8:c3:5a:04:71:ab:d0:fb:e3:50:15]

	private void doCommonConstructorActions(String userName, String password, String connectionIP, String knownHostsFileName) {
		jschSSHChannel = new JSch();

		try {
			jschSSHChannel.setKnownHosts(knownHostsFileName);
		} catch (JSchException jschX) {
			logError(jschX.getMessage());
		}

		strUserName = userName;
		strPassword = password;
		strConnectionIP = connectionIP;
	}

	public SSHManager(String userName, String password, String connectionIP, String knownHostsFileName) {
		doCommonConstructorActions(userName, password, connectionIP, knownHostsFileName);
		intConnectionPort = 22;
		intTimeOut = 60000;
	}

	public SSHManager(String userName, String password, String connectionIP, String knownHostsFileName, int connectionPort) {
		doCommonConstructorActions(userName, password, connectionIP, knownHostsFileName);
		intConnectionPort = connectionPort;
		intTimeOut = 60000;
	}

	public SSHManager(String userName, String password, String connectionIP, String knownHostsFileName, int connectionPort, int timeOutMilliseconds) {
		doCommonConstructorActions(userName, password, connectionIP, knownHostsFileName);
		intConnectionPort = connectionPort;
		intTimeOut = timeOutMilliseconds;
	}

	public String tryConnect() {
		String errorMessage = null;
		try {
			sesConnection = jschSSHChannel.getSession(strUserName, strConnectionIP, intConnectionPort);
			sesConnection.setPassword(strPassword);
//			 UNCOMMENT THIS FOR TESTING PURPOSES, BUT DO NOT USE IN PRODUCTION
			if (STRICT_HOST_KEY_CHECKING == false) {
				L.warn("StrictHostKeyChecking is OFF");
				sesConnection.setConfig("StrictHostKeyChecking", "no");
			}
			sesConnection.connect(intTimeOut);
		} catch (JSchException jschX) {
			errorMessage = jschX.getMessage();
		}

		return errorMessage;
	}

	private String logError(String errorMessage) {
		if (errorMessage != null) {
			LOGGER.log(Level.SEVERE, "{0}:{1} - {2}", new Object[]{strConnectionIP, intConnectionPort, errorMessage});
		}

		return errorMessage;
	}

	private String logWarning(String warnMessage) {
		if (warnMessage != null) {
			LOGGER.log(Level.WARNING, "{0}:{1} - {2}", new Object[]{strConnectionIP, intConnectionPort, warnMessage});
		}

		return warnMessage;
	}

	@SneakyThrows
	public static String sendCommand(Session sesConnection, String command) {
		StringBuilder outputBuffer = new StringBuilder();

//		try {
		Channel channel = sesConnection.openChannel("exec");
		((ChannelExec) channel).setCommand(command);
		InputStream commandOutput = channel.getInputStream();
		channel.connect();
		int readByte = commandOutput.read();

		while (readByte != 0xffffffff) {
			outputBuffer.append((char) readByte);
			readByte = commandOutput.read();
		}

		channel.disconnect();
//		} catch (IOException ioX) {
//		if (L.isWarnEnabled()) {
//			L.warn("Send Ssh Command error", ioX);
//		}
//		return null;
//		} catch (JSchException jschX) {
//			logWarning(jschX.getMessage());
//			return null;
//		}

		return outputBuffer.toString();
	}

	public void close() {
		sesConnection.disconnect();
	}

}