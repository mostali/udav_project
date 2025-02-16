//package utl_ftp.example_native;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.net.Socket;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.util.StringTokenizer;
//
///**
// * @author 'Αρης Κουρτέσας
// */
//public class FtpConnection {
//
//	private Socket socket = null;
//	private Socket dataSocket = null;
//	private BufferedReader reader = null;
//	private BufferedWriter writer = null;
//	private String ip = null;
//	private int port = -1;
//	private boolean isPassive = false;
//	private boolean isBinary = false;
//
//	public FtpConnection() {
//
//	}
//
//
//	public synchronized void doConnect(String host) throws IOException {
//		doConnect(host, 21);
//	}
//
//	public synchronized void doConnect(String host, int portNumber) throws IOException {
//		doConnect(host, portNumber, "anonymous", "anonymous");
//	}
//
//	public synchronized boolean doConnect(String host, int portNumber, String user, String pass) throws IOException {
//
//		Trace.ftpDialog = true;
//		Trace.connection = true;
//
//		if (socket != null) {
//			throw new IOException("FTP session already initiated, please disconnect first!");
//		}
//
//		socket = new Socket(host, portNumber);
//		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//
//		String hostResponse = readLine();
//
//		if (!hostResponse.startsWith("220")) {
//			throw new IOException("Received unknown response when connecting: " + hostResponse);
//		}
//
//		sendLine("USER " + user);
//
//		hostResponse = readLine();
//		if (!hostResponse.startsWith("331")) {
//			throw new IOException("Received unknown response providing username: " + hostResponse);
//		}
//
//		sendLine("PASS " + pass);
//
//		hostResponse = readLine();
//		if (!hostResponse.startsWith("230")) {
//			if (hostResponse.startsWith("530")) {
//				if (Trace.connection) {
//					Trace.trc("Incorrect server credentials, cannot connect!");
//				}
//				return false;
//			} else {
//				throw new IOException("Received unknown response when providing password: " + hostResponse);
//			}
//		}
//
//		if (Trace.connection) {
//			Trace.trc("Login successfull");
//		}
//
//		return true;
//
//	}
//
//	public synchronized void disconnect() throws IOException {
//
//		Trace.ftpDialog = true;
//
//		try {
//			sendLine("QUIT");
//		} finally {
//			socket.close();
//			socket = null;
//		}
//	}
//
//	public synchronized String pwd() throws IOException {
//
//		sendLine("PWD");
//		String dir = null;
//		String response = readLine();
//
//		if (response.startsWith("257 ")) {
//			int firstQuote = response.indexOf('\"');
//			int secondQuote = response.indexOf('\"', firstQuote + 1);
//			if (secondQuote > 0) {
//				dir = response.substring(firstQuote + 1, secondQuote);
//			}
//		}
//
//		return dir;
//	}
//
//	public synchronized boolean cwd(String dir) throws IOException {
//		Trace.ftpDialog = true;
//
//		sendLine("CWD " + dir);
//		String response = readLine();
//		boolean cmdOutcome = false;
//
//		if (response.startsWith("250")) {
//			if (Trace.connection) {
//				Trace.trc("Changed directory successfully");
//			}
//			cmdOutcome = true;
//			return cmdOutcome;
//		} else if (response.startsWith("550")) {
//			if (Trace.connection) {
//				Trace.trc("Directory not found");
//			}
//			return cmdOutcome;
//		}
//		return cmdOutcome;
//	}
//
//	public synchronized String list(String dir) throws IOException {
//
//		Trace.connection = true;
//		String response = null;
//		Trace.ftpDialog = true;
//
//		if (!isPassive) {
//			passv();
//		}
//		sendLine("LIST " + dir);
//		response = readLine();
//		if (!response.startsWith("150")) {
//			throw new IOException("Cannot list the remote directory");
//		}
//
//		BufferedInputStream input = new BufferedInputStream(dataSocket.getInputStream());
//
//		byte[] buffer = new byte[4096];
//		int bytesRead = 0;
//
//		String content = null;
//		while ((bytesRead = input.read(buffer)) != -1) {
//			content = new String(buffer, 0, bytesRead);
//		}
//		input.close();
//
//		response = readLine();
//		if (content != null) {
//			if (Trace.connection) {
//				Trace.trc("Listing remote directory...");
//				Trace.trc(content);
//			}
//		}
//		if (response.startsWith("226")) {
//			isPassive = false;
//			return content;
//		} else {
//			throw new IOException("Error");
//		}
//	}
//
//	public synchronized boolean passv() throws IOException {
//
//		Trace.ftpDialog = true;
//
//		sendLine("PASV");
//		String response = readLine();
//		if (!response.startsWith("227 ")) {
//			throw new IOException("Could not request PASSIVE mode: " + response);
//		}
//
//		ip = null;
//		port = -1;
//		int opening = response.indexOf('(');
//		int closing = response.indexOf(')', opening + 1);
//
//		if (closing > 0) {
//			String dataLink = response.substring(opening + 1, closing);
//			StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
//			try {
//				ip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "." + tokenizer.nextToken() + "." + tokenizer.nextToken();
//				port = Integer.parseInt(tokenizer.nextToken()) * 256 + Integer.parseInt(tokenizer.nextToken());
//			} catch (Exception e) {
//				throw new IOException("Received bad data information: " + response);
//			}
//		}
//		dataSocket = new Socket(ip, port);
//		isPassive = true;
//
//		return true;
//	}
//
//	public synchronized boolean stor(File file) throws IOException {
//		if (file.isDirectory()) {
//			throw new IOException("Cannot upload a directory");
//		}
//		String filename = file.getName();
//
//		return stor(new FileInputStream(file), filename);
//	}
//
//	public synchronized boolean stor(InputStream inputStream, String filename) throws IOException {
//
//		Trace.ftpDialog = true;
//
//		BufferedInputStream input = new BufferedInputStream(inputStream);
//		String response = null;
//
//		if (!isPassive) {
//			passv();
//		}
//
//		sendLine("STOR " + filename);
//
//		response = readLine();
//		if (!response.startsWith("150 ")) {
//			if (response.startsWith("550")) {
//				if (Trace.connection) {
//					Trace.trc("Incorrect permissions to upload file!");
//				}
//				return false;
//			}
//			throw new IOException("Not allowed to send file: " + response);
//		}
//
//		BufferedOutputStream output = new BufferedOutputStream(dataSocket.getOutputStream());
//
//		byte[] buffer = new byte[8192];
//		int bytesRead = 0;
//
//		while ((bytesRead = input.read(buffer)) != -1) {
//			output.write(buffer, 0, bytesRead);
//			output.flush();
//		}
//
//		output.close();
//		input.close();
//		isPassive = false;
//
//		response = readLine();
//
//		return checkFileOperationsStatus(response);
//	}
//
//	public synchronized boolean retr(String fileName) throws IOException {
//
//		Trace.connection = true;
//		Trace.ftpDialog = true;
//		String response = null;
//
//		if (!isPassive) {
//			passv();
//		}
//
//		String fullPath = pwd() + "/" + fileName;
//		Trace.trc("Will retrieve the following file: " + fullPath);
//
//		sendLine("RETR " + fullPath);
//		response = readLine();
//		if (!response.startsWith("150")) {
//			throw new IOException("Unable to download file from the remote server");
//		}
//
//		if (Trace.connection) {
//			Trace.trc("Downloading file '" + fileName + "'. Filesize: "
//					+ response.substring(response.indexOf("(") + 1, response.indexOf(")")));
//		}
//
//		BufferedInputStream input = new BufferedInputStream(dataSocket.getInputStream());
//		BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
//
//		byte[] buffer = new byte[4096];
//		int bytesRead = 0;
//		int bytesUploaded = 0;
//
//		while ((bytesRead = input.read(buffer)) != -1) {
//			output.write(buffer, 0, bytesRead);
//			bytesUploaded += bytesRead;
//			output.flush();
//		}
//
//		output.close();
//		input.close();
//
//		response = readLine();
//
//		return checkFileOperationsStatus(response);
//	}
//
//	public synchronized boolean mkd(String dirName) throws IOException {
//
//		String response = null;
//		boolean cmdOutput = false;
//
//		try {
//			sendLine("MKD " + dirName);
//			response = readLine();
//
//			if (Trace.connection) {
//				Trace.trc("Attempting to create new directory: " + dirName);
//			}
//			if (response.startsWith("257")) {
//				cmdOutput = response.startsWith("257");
//				if (Trace.connection) {
//					Trace.trc("Directory: " + dirName + " created successfully");
//				}
//			} else if (response.startsWith("550")) {
//				cmdOutput = !response.startsWith("257");
//				if (Trace.connection) {
//					Trace.trc("Could not create directory: " + dirName);
//				}
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return cmdOutput;
//	}
//
//	public synchronized boolean dele(String fileName) throws IOException {
//
//		boolean cmdOutput = false;
//		String response = null;
//
//		try {
//
//			sendLine("DELE " + fileName);
//			response = readLine();
//
//			if (Trace.connection) {
//				Trace.trc("Attempting to delete file: " + fileName);
//			}
//
//			if (response.startsWith("250")) {
//				cmdOutput = response.startsWith("250");
//				if (Trace.connection) {
//					Trace.trc("File: " + fileName + " deleted successfully");
//				}
//			} else if (response.startsWith("550")) {
//				cmdOutput = response.startsWith("250");
//				if (Trace.connection) {
//					Trace.trc("Could not delete file: " + fileName);
//				}
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return cmdOutput;
//	}
//
//	public synchronized boolean rmd(String dirName) throws IOException {
//
//		boolean cmdOutput = false;
//		String response = null;
//
//		try {
//
//			sendLine("RMD " + dirName);
//			response = readLine();
//
//			if (Trace.connection) {
//				Trace.trc("Attempting to delete directory: " + dirName);
//			}
//
//			if (response.startsWith("250")) {
//				cmdOutput = response.startsWith("250");
//				if (Trace.connection) {
//					Trace.trc("Directory: " + dirName + " deleted successfully");
//				}
//			} else if (response.startsWith("550")) {
//				cmdOutput = response.startsWith("250");
//				if (Trace.connection) {
//					Trace.trc("Could not delete directory: " + dirName);
//				}
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return cmdOutput;
//	}
//
//	public synchronized boolean abor() throws IOException {
//
//		Trace.ftpDialog = true;
//		String response = null;
//
//		try {
//
//			sendLine("ABOR");
//			response = readLine();
//
//			if (Trace.connection) {
//				Trace.trc("Aborting upload process");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return checkFileOperationsStatus(response);
//	}
//
//	public synchronized boolean bin() throws IOException {
//
//		Trace.ftpDialog = true;
//
//		sendLine("TYPE I");
//		String response = readLine();
//		isBinary = true;
//		return (response.startsWith("200 "));
//	}
//
//	public synchronized boolean ascii() throws IOException {
//
//		Trace.ftpDialog = true;
//
//		sendLine("TYPE A");
//		String response = readLine();
//		return (response.startsWith("200 "));
//	}
//
//	private void sendLine(String command) throws IOException {
//		if (socket == null) {
//			throw new IOException("Not connected to a host");
//		}
//		try {
//			writer.write(command + "\r\n");
//			writer.flush();
//			if (Trace.ftpDialog) {
//				Trace.trc(">" + command);
//			}
//		} catch (IOException e) {
//			socket = null;
//			e.printStackTrace();
//		}
//	}
//
//	private boolean checkFileOperationsStatus(String response) throws IOException {
//
//		if (!response.startsWith("226")) {
//			throw new IOException("Error");
//		} else {
//			isPassive = false;
//			return response.startsWith("226 ");
//		}
//
//	}
//
//	private String readLine() throws IOException {
//		String response = reader.readLine();
//		if (Trace.ftpDialog) {
//			Trace.trc("<" + response);
//		}
//		return response;
//	}
//
//}