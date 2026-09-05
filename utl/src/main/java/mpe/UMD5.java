package mpe;

import mpu.core.RW;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;


public class UMD5 {
	private static byte[] createChecksum(InputStream fis) throws NoSuchAlgorithmException, IOException {
		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;
		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);
		fis.close();
		return complete.digest();
	}

	public static StringBuilder bytes2md5(byte[] b) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			result.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
		}
		return result;
	}

	public static boolean checkFileWithFileMD5(String targetFileLocation, String targetFileMD5Location) throws IOException, NoSuchAlgorithmException {
		return Objects.equals(UMD5.file2string(targetFileLocation), RW.readContent_(targetFileMD5Location));
	}

	public static boolean checkFileWithUrlFileMD5(String targetFileLocation, String urlMD5) throws IOException, NoSuchAlgorithmException {
		return Objects.equals(UMD5.file2string(targetFileLocation), UMD5.urlfile2string(urlMD5));
	}

	private static String urlfile2string(String url) throws IOException {
		return new String(IOUtils.toByteArray(new URL(url).openStream()));
	}

	public static String file2string(String filename) throws IOException, NoSuchAlgorithmException {
		return bytes2md5(createChecksum(new FileInputStream(filename))).toString();
	}

	public static class CheckMD5Exception extends IllegalStateException {
		public CheckMD5Exception(String message) {
			super(message);
		}
	}

}
