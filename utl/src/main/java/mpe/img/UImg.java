package mpe.img;


import mpc.fs.ext.EXT;
import mpc.fs.ext.GEXT;
import mpe.core.U;
import mpu.IT;
import mpu.X;
import mpc.fs.*;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mpz_deprecated.EER;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UImg {
	public static final Logger L = LoggerFactory.getLogger(UImg.class);

	// TODO
	public static void main(String[] args) throws IOException {


	}

	public static Integer[] getWH(String file) throws IOException {
		return getWH(file);
	}

	public static Integer[] getWH(File file) throws IOException {
		BufferedImage image = ImageIO.read(file);
		return new Integer[]{image.getWidth(), image.getHeight()};
	}

	public static Integer[] getWH(BufferedImage image) {
		return new Integer[]{image.getWidth(), image.getHeight()};
	}

	@Deprecated
	// See sunsmm
	private static void merge1(String fileImg1, String fileImg2, String fileResult) throws IOException {
		BufferedImage image = ImageIO.read(new File(fileImg1));
		BufferedImage overlay = ImageIO.read(new File(fileImg2));

		// create the new image, canvas size is the max. of both image sizes
		int w = Math.max(image.getWidth(), overlay.getWidth());
		int h = Math.max(image.getHeight(), overlay.getHeight());
		BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		// paint both images, preserving the alpha channels
		Graphics g = combined.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.drawImage(overlay, 0, 0, null);

		// Save as new image
		ImageIO.write(combined, "PNG", new File(fileResult));
	}

	public static void cropCenter(String file, int w, int h, String toFile, String type) throws IOException {
		BufferedImage image = ImageIO.read(new File(file));
		int lx = image.getWidth();
		int ly = image.getHeight();
		BufferedImage out = image.getSubimage(lx / 2 - w / 2, ly / 2 - h / 2, w, h);
		if (toFile == null) {
			toFile = file;
		}
		ImageIO.write(out, type, new File(toFile));
	}

	public static void crop(String file, int x, int y, int w, int h, String toFile, String type) throws IOException {
		BufferedImage image = ImageIO.read(new File(file));
		BufferedImage out = image.getSubimage(x, y, w, h);
		if (toFile == null) {
			toFile = file;
		}
		ImageIO.write(out, type, new File(toFile));
	}

	public static void image2File(String imageUrl, String destinationFile) throws IOException {
		URL url = new URL(imageUrl);
		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(destinationFile);

		byte[] b = new byte[2048];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}

		is.close();
		os.close();

		System.out.println("image2File success: " + Paths.get(destinationFile).toString());
	}

	public static String base64byUrl(String imageUrl, String typeImage) throws IOException {
		URL url = new URL(imageUrl);
		return base64byInputStream(url.openStream(), typeImage);

	}

	public static String base64byFile(String imageUrl, String typeImage) throws FileNotFoundException {
		return base64byInputStream(new FileInputStream(new File(imageUrl)), typeImage);
	}

	public static String base64byInputStream(InputStream is, String typeImage) {
		RenderedImage image;
		try {
			image = ImageIO.read(is);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ImageIO.write(image, typeImage, output);
			String base64 = Base64.encodeBase64String(output.toByteArray());
			return base64;
		} catch (IOException e) {
			return null;
		}
	}

	public static String normalizeFileImage2HexName(String image) throws IOException {
		String hex = new ImageHash(image).getHashString();
		String fullHexFile = Paths.get(image).getParent() + "/" + hex + "." + UF.getExtFromCleanUrl(image);

		if (Files.exists(Paths.get(fullHexFile)) && !UF.equalsFileName(fullHexFile, image)) {
			Files.deleteIfExists(Paths.get(image));
		} else {
			Files.move(Paths.get(image), Paths.get(fullHexFile));
		}
		return fullHexFile;
	}

	public static boolean isNiceImage_WH_RATIO(File file, int w, int h) {
		if (!isNiceImageWH(file, w, h)) {
			return false;
		}
		if (!isNiceImageRatio(file)) {
			return false;
		}
		return true;
	}

	public static boolean isNiceImageWH(File file, int w, int h) {
		try {
			Integer[] wh = UImg.getWH(file);
			if (wh[0] < w) {
				L.info("No width photo.." + wh[0] + " < " + w);
				return false;
			}
			if (wh[1] < h) {
				L.info("No height photo.." + wh[1] + " < " + h);
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean isNiceImageRatio(File file) {
		try {
			Integer[] hw = UImg.getWH(file);
			double hwv = (double) hw[0] / (double) hw[1];
			double whv = (double) hw[1] / (double) hw[0];
			if (hwv < 1.0) {
				L.info("No portrait photo..");
				return false;
			}
			if (whv > 1.7) {
				L.info("No too albom photo..");
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean bytes2img(byte[] data, String imgFile) throws IOException {
		File imgPath = new File(imgFile);
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(data));
		return ImageIO.write(img, "PNG", imgPath);
	}

	public static byte[] imgfile2bytes(String imgFile) throws IOException {
		// open image
		File imgPath = new File(imgFile);
		BufferedImage bufferedImage = ImageIO.read(imgPath);

		// get DataBufferBytes from Raster
		WritableRaster raster = bufferedImage.getRaster();
		DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

		return (data.getData());
	}

	public static Color hex2color(String colorHex67) {
		if (colorHex67.length() == 7) {
			colorHex67.substring(1);
		}
		if (colorHex67.length() != 6) {
			throw EER.IA.I("Illegal string of color :" + colorHex67);
		}
		return new Color(Integer.valueOf(colorHex67.substring(0, 2), 16),
				Integer.valueOf(colorHex67.substring(2, 4), 16), Integer.valueOf(colorHex67.substring(4, 6), 16));

	}

	public static String getFileColorBlank(String color, int w, int h) {
		String fname = "color-blanks/color-blank-" + color + "-" + w + "-" + h + ".png";
		UFS.MKDIR.mkdirsIfNotExist(Paths.get(fname).getParent());
		if (!UF.isFile(fname)) {
			L.info("File color blank not exist, because create :" + fname);
			fillColorBlankFile(fname, color, w, h);
		}
		return fname;
	}

	private static void fillColorBlankFile(String fname, String color, int w, int h) {

		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();

		graphics.setPaint(UImg.hex2color(color));
		graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		try {
			ImageIO.write(image, "png", new File(fname));
		} catch (IOException e) {
			throw EER.RIO.I(e);
		}

		// new
		// PaintOperation(w).onComposite(alphaChannel).onPaint(gradient).fill(r);

	}

//	public static String getFileColorBlank(String[] color, int w, int h) {
//		if (color[0].equals(color[1])) {
//			return getFileColorBlank(color[0], w, h);
//		}
//		String fname = "color-blanks/color-blank-" + color[0] + "-" + color[1] + "-" + w + "-" + h + ".png";
//		UFS.mkdirsQuicklyForParent(fname);
//		if (!UFS.isFile(fname)) {
//			L.info("File color blank not exist, because create :" + fname);
//			fillColorBlankFile(fname, color, w, h);
//		}
//		return fname;
//	}

//	private static void fillColorBlankFile(String fname, String[] color, int w, int h) {
//
//		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//		Graphics2D g = image.createGraphics();
//
//		Rectangle2D.Double r = new Rectangle2D.Double(0.0, 0.0, w, h);
//
//		GradientPaint gradient = new GradientPaint(new Point(0, 0), UImg.hex2color(color[0]), new Point(w, h),
//				UImg.hex2color(color[1]));
//
//		AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f);
//
//		new PaintOperation(g).onComposite(alphaChannel).onPaint(gradient).fill(r);
//
//		try {
//			ImageIO.write(image, "png", new File(fname));
//		} catch (IOException e) {
//			throw ER.RIO.I(e);
//		}
//
//		// new
//		// PaintOperation(w).onComposite(alphaChannel).onPaint(gradient).fill(r);
//
//	}

	public static Color[] hex2color(String[] colors) {
		return new Color[]{hex2color(colors[0]), hex2color(colors[1])};
	}

//	public static String[] hex2colorsPatternSlash(String colors, String[] def) {
//		if (UEQ.isEmpty(colors)) {
//			return def;
//		}
//		if (colors.contains("\\")) {
//			return colors.split("\\", 2);
//		} else {
//			return new String[]{colors, colors};
//		}
//
//	}

	public static BufferedImage img2bimg(Image src) {
		int w = src.getWidth(null);
		int h = src.getHeight(null);
		int type = BufferedImage.TYPE_INT_RGB; // other options
		BufferedImage dest = new BufferedImage(w, h, type);
		Graphics2D g2 = dest.createGraphics();
		g2.drawImage(src, 0, 0, null);
		g2.dispose();
		return dest;
	}

	public static BufferedImage file2bimg(String fileSrcPhoto) throws IOException {
		return file2bimg(new File(fileSrcPhoto));
	}

	public static BufferedImage file2bimg(Path srcPhoto) throws IOException {
		return ImageIO.read(IT.isFileExist(srcPhoto).toFile());
	}

	public static BufferedImage file2bimg(File srcPhoto) throws IOException {
		return ImageIO.read(IT.isFileExist(srcPhoto));
	}

	public static BufferedImage file2bimg(URL url) throws IOException {
		return ImageIO.read(url.openStream());
	}

	public static String bimg2file(BufferedImage bufferedImage, String toFile) throws IOException {
		bimg2file(bufferedImage, new File(toFile));
		return toFile;
	}

	public static File bimg2file(BufferedImage bufferedImage, File toFile) throws IOException {
		String ext = getExtImg(toFile.getName());
		if (ext == null) {
			throw new RuntimeException("Error write image to file with extenssion no image :" + toFile);
		}
		bimg2file(bufferedImage, toFile, ext);
		return toFile;
	}

	public static void bimg2file(BufferedImage bufferedImage, File toFile, String ext) throws IOException {
		if (!ImageIO.write(bufferedImage, ext, toFile)) {
			throw new RuntimeException("Error write image to file :" + toFile);
		}
	}

	public static void showFile(String file) {
		showFile(new File(file));
	}

	public static InputStream getInputStreamImage(String file, String url) throws MalformedURLException, IOException {
		// try {
		if (!X.empty(file)) {
			return new FileInputStream(new File(file));
		}

		if (!X.empty(url)) {
			return new URL(url).openStream();
		}

		throw EER.IS("set file or url of image");

		// } catch (IOException e) {
		// throw ER.IS(e);
		// }
	}

	public static Image getImage(String file, String url) throws IOException {
		return ImageIO.read(getInputStreamImage(file, url));
	}

	// public static void showImageWithOk1(String file, String url, String text)
	// {
	// try {
	// PhotoFrame s = new PhotoFrame(file, url, text);
	// s.showImage(true);
	// // showComponent(new JLabel(new ImageIcon(getImage(file, url))));
	// // JOptionPane.showMessageDialog(null, s);
	// } catch (HeadlessException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	public static void showFile(File image) {
		try {
			showComponent(new JLabel(new ImageIcon(file2bimg(image))));
		} catch (HeadlessException | IOException e) {
			e.printStackTrace();
		}
	}

	public static void showComponent(BufferedImage image) {
		showComponent(new JLabel(new ImageIcon(image)));
	}

	public static void showComponent(JLabel image) {
		JOptionPane.showMessageDialog(null, image);
	}

	public static int getImageTypeInt(String type) {
		return "png".equalsIgnoreCase(type) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
	}

//	public static boolean isExtGIFFromUrl(String url2src) {
//		try {
//			String ext = UF.getExtFromCleanUrl(url2src);
//			return Objects.equals(ext.toLowerCase(), "gif");
//		} catch (Exception ex) {
//			throw new UFIException(ex);
//		}
//	}

//	public static boolean isExtImgFromUrl(String url2src) {
//		try {
//			String ext = UF.getExtFromCleanUrl(url2src);
//			ext = ext.toLowerCase();
//			return EXT.EXT_IMGS.contains(ext);
//		} catch (Exception ex) {
//			U.L.error("UFI::isImageExtUrl::" + url2src, ex);
//			throw new UFIException(ex);
//		}
//	}

	public static String getNameFromUrlSafe(String url) {
		return UF.clearStringCyrRemoveSlash(getNameFromUrl(url));
	}

	public static String getNameFromUrl(String url) {
		try {
			String ext = UF.getExtFromCleanUrl(url);
			String li = url.substring(0, url.lastIndexOf("." + ext));
			li = li.substring(li.lastIndexOf('/') + 1, li.length());
			return li + "." + ext;
		} catch (Exception ex) {
			U.L.error("UFI::getImageNameFromUrl::" + url, ex);
			throw new UFIException(ex);
		}
	}

	public static String getExtImg(String file) {
		String ext = UF.getExtFromCleanUrl(file);
		return GEXT.IMG.has(ext) ? ext : null;
	}

//	public static Path convert(File file, EXT ext) {
//
//	}

	public static class TrimWhite {
		private BufferedImage img;

		public TrimWhite(File input) {
			try {
				img = ImageIO.read(input);
			} catch (IOException e) {
				throw new RuntimeException("Problem reading image", e);
			}
		}

		public void trim() {
			int width = getTrimmedWidth();
			int height = getTrimmedHeight();

			BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics g = newImg.createGraphics();
			g.drawImage(img, 0, 0, null);
			img = newImg;
		}

		public void write(File f) {
			try {
				ImageIO.write(img, "bmp", f);
			} catch (IOException e) {
				throw new RuntimeException("Problem writing image", e);
			}
		}

		private int getTrimmedWidth() {
			int height = this.img.getHeight();
			int width = this.img.getWidth();
			int trimmedWidth = 0;

			for (int i = 0; i < height; i++) {
				for (int j = width - 1; j >= 0; j--) {
					if (img.getRGB(j, i) != Color.WHITE.getRGB() && j > trimmedWidth) {
						trimmedWidth = j;
						break;
					}
				}
			}

			return trimmedWidth;
		}

		private int getTrimmedHeight() {
			int width = this.img.getWidth();
			int height = this.img.getHeight();
			int trimmedHeight = 0;

			for (int i = 0; i < width; i++) {
				for (int j = height - 1; j >= 0; j--) {
					if (img.getRGB(i, j) != Color.WHITE.getRGB() && j > trimmedHeight) {
						trimmedHeight = j;
						break;
					}
				}
			}

			return trimmedHeight;
		}

		public static void main(String[] args) {
			TrimWhite trim = new TrimWhite(new File("...\\someInput.bmp"));
			trim.trim();
			trim.write(new File("...\\someOutput.bmp"));
		}
	}

	public static void showImageUrlAndWaitTmp(String url, String text, Long waitMs) throws IOException {
		// String fileTmp = "/tmp/" + US.rands(5, 10) + ".jpg";
		// UNet.url2file(url, fileTmp);
		// showImageAndWait(file,)
	}

//	public static void showImageAndWait(String file, String url, String text, Long waitMs) throws IOException {
//		if (waitMs != null) {
//			PhotoFrame s = new PhotoFrame(file, url, text);
//			s.showImage(true);
//			U.sleep(waitMs);
//			s.closeWindow();
//		} else {
//			// showImageWithOk(file, url, text);
//			PhotoFrame s = new PhotoFrame(file, url, text);
//			s.showImage(false);
//
//			Container p = s.getContentPane();
//			// p.setSize(500, 500);
//			// p.setPreferredSize(new Dimension(800, 800));
//			// p.setMinimumSize(new Dimension(800, 800));
//			// p.setMaximumSize(new Dimension(800, 800));
//
//			JOptionPane.showMessageDialog(null, s.getContentPane());
//		}
//	}

	public static class PhotoFrame extends JFrame {
		private static final long serialVersionUID = 1L;
		final String file;
		final String url;
		final String text;

		int maxScaleHeight = 500;

		public PhotoFrame(String file, String url, String text) {
			this.file = file;
			this.url = url;
			this.text = text;
		}

//		public void showImage(boolean isWaitMs) throws IOException {
//			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			setTitle("Show image");
//			Image imageIS = ImageIO.read(UImg.getInputStreamImage(file, url));
//
//			imageIS = new ImageScaler(imageIS).createScaledImage(maxScaleHeight, ImageScaler.ScalingDirection.VERTICAL)
//					.getScaledImage();
//
//			ImageIcon image = new ImageIcon(imageIS);
//			JLabel label = new JLabel(image);
//
//			JScrollPane scrollPane = new JScrollPane(label);
//			// JPanel scrollPane = new JPanel();
//			// scrollPane.add(label);
//
//			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//			add(scrollPane, BorderLayout.CENTER);
//
//			// setResizable(true);
//
//			if (text != null) {
//				JTextArea textA = new JTextArea(text);
//				// textA.setSize(getPreferredSize());
//				add(textA, BorderLayout.SOUTH);
//				textA.setVisible(true);
//				// textA.setSize(maxScaleHeight, 200);
//				textA.setLineWrap(true);
//				// textA.setMaximumSize(new Dimension(550, 100));
//
//			}
//
//			// Dimension screenSize =
//			// Toolkit.getDefaultToolkit().getScreenSize();
//			// setBounds(0, 0, 111, 111);
//
//			if (isWaitMs) {
//				JButton b3 = new JButton("CLOSE");
//				b3.setBounds(50, 375, 250, 50);
//				b3.addActionListener(new ActionListener() {
//					public void actionPerformed(ActionEvent e) {
//						closeWindow();
//					}
//				});
//				add(b3, BorderLayout.SOUTH);
//
//			}
//
//			// setSize(500, 500);
//
//			if (isWaitMs) {
//				pack();
//				setVisible(true);
//			}
//		}

		// private static final int PREF_W = 600;
		// private static final int PREF_H = 300;
		//
		// @Override
		// public Dimension getPreferredSize() {
		// return new Dimension(PREF_W, PREF_H);
		// }

		public void closeWindow() {
			setVisible(false);
			dispose();
		}
	}

	public static class UFIException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public UFIException(Exception ex) {
			super(ex);
		}
	}

}
