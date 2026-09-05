package utl_rest;

import lombok.SneakyThrows;
import mpc.arr.STREAM;
import mpc.env.Env;
import mpc.fs.UFS;
import mpc.net.DLD;
import mpe.docker.TeseractParserPhotoViaDocker;
import mpe.rt.core.ExecRq;
import mpu.IT;
import mpu.X;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

public class URestPhotoParser {

	public static final Logger L = LoggerFactory.getLogger(URestPhotoParser.class);

	@SneakyThrows
	public static ResponseEntity<Map> parse_photo_text(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam(value = "url", required = false) String url) {
		//Map keys = URest.createKeys(args);
		if (url == null && file == null) {
			throw StatusException.C400("set file (or url)");
//			return URest.getResponse_OK_JSON_CUSTOM(UMap.mapOf("error", ));
		}
		String nameFileOrUrl = STREAM.findFirstNotEmpty(file == null ? null : file.getOriginalFilename(), url);
		Path dirTask = Env.TMP.resolve(UUID.randomUUID().toString());

		UFS.MKDIR.createDirsOrSingleDirOrCheckExist(dirTask, true);
		Path photoFile;
		if (file != null) {
			try {
				photoFile = URest.storeMultipartFileToDir(file, dirTask);
			} catch (IOException e) {
				L.error("Write file {}" + file, e);
				throw StatusException.C500(e, "storeMultipartFileToDir");
			}
		} else {
			IT.isUrl0(url);
			try {
				String fileImg = DLD.url2fileimagehex(url, dirTask.toAbsolutePath().toString());
				photoFile = Paths.get(fileImg);
			} catch (IOException e) {
				L.error("Write file from url {}" + file, e);
				throw StatusException.C500(e, "url2fileimagehex");
			}
		}
		try {
			String text = TeseractParserPhotoViaDocker.parsePhotoRus_WithAutoInstallDockerImage(photoFile);
			if (L.isInfoEnabled()) {
				String msg = X.fl("Parsed text from photo '{}'\n{}", nameFileOrUrl, text);
				L.info(msg);
			}
			return SrcResponseEntity.OK_JSM("parse_photo_text", text);
		} catch (ExecRq e) {
			L.error("Parse photo from file:" + nameFileOrUrl, e);
			throw StatusException.C500(e, "parsePhotoRus");
		}

	}
}
