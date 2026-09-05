package mpe.img;

import mpc.fs.UFS;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ImageHash {
	private String file;
	private String hex = null;

	public ImageHash(String file) {
		this.file = file;
	}

	@Override
	public String toString() {
		try {
			return getHashString();
		} catch (Exception e) {
			return null;
		}
	}

	public String getHashString() throws IOException {
		if (hex != null) {
			return hex;
		}
		File input = new File(file);
		if (!UFS.isFileWithContent(input.getAbsolutePath())) {
			throw new IllegalStateException("File not found :" + file);
		}

		BufferedImage buffImg;
		buffImg = ImageIO.read(input);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		String ext = "png";
		if (file.endsWith("jpg")) {
			ext = "jpg";
		}

		ImageIO.write(buffImg, ext, outputStream);
		byte[] data = outputStream.toByteArray();
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		md.update(data);
		byte[] hash = md.digest();
		hex = returnHex(hash);
		return hex;


	}
	// Below method of converting Byte Array to hex
	// Can be found at:
	// http://www.rgagnon.com/javadetails/java-0596.html

	private static String returnHex(byte[] inBytes) {
		String hexString = "";
		for (int i = 0; i < inBytes.length; i++) { // for loop ID:1
			hexString += Integer.toString((inBytes[i] & 0xff) + 0x100, 16).substring(1);
		} // Belongs to for loop ID:1
		return hexString;
	} // Belongs to returnHex class

}