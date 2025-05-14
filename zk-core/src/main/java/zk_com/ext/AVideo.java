//package zk_com.ext;
//
//import lombok.SneakyThrows;
//import mpu.core.RW;
//
//import java.io.FileInputStream;
//import java.io.FileReader;
//import java.io.InputStream;
//import java.io.Reader;
//import java.nio.file.Path;
//
//public class AVideo implements org.zkoss.video.Video {
//
//	private final Path file0;
//
//	public AVideo(Path file0) {
//		this.file0 = file0;
//	}
//
//	@Override
//	public boolean isBinary() {
//		return true;
//	}
//
//	@Override
//	public boolean inMemory() {
//		return true;
//	}
//
//	@Override
//	public byte[] getByteData() {
//		return RW.readBytes(file0);
//	}
//
//	@Override
//	public String getStringData() {
//		return "123";
//	}
//
//	@SneakyThrows
//	@Override
//	public InputStream getStreamData() {
//		return new FileInputStream(file0.toFile());
//	}
//
//	@SneakyThrows
//	@Override
//	public Reader getReaderData() {
//		return new FileReader(file0.toFile());
//	}
//
//	@Override
//	public String getName() {
//		return file0.getFileName().toString();
//	}
//
//	@Override
//	public String getFormat() {
//		return "mp4";
//	}
//
//	@Override
//	public String getContentType() {
//		return "application/mp4";
//	}
//
//	@Override
//	public boolean isContentDisposition() {
//		return true;
//	}
//}
