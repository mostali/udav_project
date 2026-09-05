package mpe.docker;

import lombok.SneakyThrows;
import mpu.X;
import mpc.net.DLD;
import mpc.fs.UF;
import mpe.rt.core.ExecRq;
import mpu.core.ARR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class TeseractParserPhotoViaDocker {

	@SneakyThrows
	public static void main(String[] args) {
		X.exit(TeseractParserPhotoViaDocker.parsePhoto(Paths.get("/home/dav/Загрузки/vosk.jpg"), "rus"));
	}

	private static final Logger L = LoggerFactory.getLogger(TeseractParserPhotoViaDocker.class);

	public static final String TSR_IMAGE = "tso-rus:1";

	public static final String TSR_IMAGE_FILE_CONTENT = "" + "FROM jitesoft/tesseract-ocr\n" + "RUN train-lang rus --best";

	public static final String TSR_LANG = "rus";

	//docker build -t %s -f %s %s
	//docker run -v /tmp/test.png:/tmp/img.png tso-image:1 /tmp/img0.png stdout -l rus
	public static final String TSR_CMD_RUN = "docker run --rm --privileged -v %s:/tmp/img.png %s /tmp/img.png stdout -l %s";

	public static String parsePhotoRus_WithAutoInstallDockerImage(String file_photo) throws ExecRq {
		return parsePhotoRus_WithAutoInstallDockerImage(Paths.get(file_photo));
	}

	@SneakyThrows
	public static String parsePhotoRusByUrl(String photo_url) {
		return parsePhotoRusByUrl0(photo_url);
	}

	public static String parsePhotoRusByUrl0(String photo_url) throws ExecRq {
		String fileTmp = "tmp/" + UUID.randomUUID() + ".png";
		try {
			DLD.url2file_withCreateParent(photo_url, fileTmp, true);
			return parsePhotoWithAutoInstallDockerImage(Paths.get(fileTmp), "rus");
		} catch (Exception e) {
			throw ExecRq.FAIL(e);
		} finally {
			new File(fileTmp).delete();
		}
	}

	public static String parsePhotoRus_WithAutoInstallDockerImage(Path file_photo) throws ExecRq {
		return parsePhotoWithAutoInstallDockerImage(file_photo, "rus");
	}

	public static String parsePhotoEng_WithAutoInstallDockerImage(Path file_photo) throws ExecRq {
		return parsePhotoWithAutoInstallDockerImage(file_photo, "eng");
	}

	public static String parsePhotoWithAutoInstallDockerImage(Path file_photo, String lang) throws ExecRq {
		try {
			return parsePhoto(file_photo, lang);
		} catch (ExecRq execRq) {
			String msg = execRq.getMessage();

			String errMsg = X.f("Unable to find image '%s' locally", TSR_IMAGE);
			if (!ARR.containsAllStringNeedle(msg, false, errMsg)) {
				throw execRq;
			}
			if (L.isInfoEnabled()) {
				L.info("Docker Image {} not found, try create...", TSR_IMAGE);
			}
			buildTsrDockerImage();
			return parsePhoto(file_photo, lang);
		}
	}

	private static void buildTsrDockerImage() throws ExecRq {
		DockerBuilderImage.buildDockerImage(TSR_IMAGE, TSR_IMAGE_FILE_CONTENT);
	}

	public static String parsePhoto(Path file_photo, String lang) throws ExecRq {
		if (L.isInfoEnabled()) {
			L.info("TESERACT({})/Start parse file_photo {}", UF.ln(file_photo));
		}
		String dockerCmd = X.f(TSR_CMD_RUN, file_photo.toAbsolutePath(), TSR_IMAGE, lang);
		ExecRq exe = ExecRq.exec(false, dockerCmd);
		String text = exe.getMessage(true);
		return text;
	}

}
