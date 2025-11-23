package nett;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.fs.UFS;
import mpu.X;
import nett.appb.TgRoute;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.nio.file.Path;

@RequiredArgsConstructor
public class TgDefaultFileDownloader {
	final TgRoute ownRouteRoute;

	@SneakyThrows
	public Object doDownalod(Document document) {

		String error = getValidationErrorForDownloadDocument(document);
		if (error != null) {
			return error;
		}
		GetFile getFile = new GetFile();
		getFile.setFileId(document.getFileId());
		String filePath = null;
		File file = null;
		try {
			filePath = ownRouteRoute.getRootRoute().getTgBot().execute(getFile).getFilePath();
			File outputFile = buildOutputFile(document).toFile();
			file = ownRouteRoute.getRootRoute().getTgBot().downloadFile(filePath, outputFile);
		} catch (TelegramApiException e) {
			X.throwException(e);
		}
		return "Document loaded as '" + file.getName() + "'";
	}

	protected Path getDownloadDir() {
		return ownRouteRoute.getTgApp().getStoreRoot().resolve("downloads");
	}

	@SneakyThrows
	protected Path buildOutputFile(Document document) {
		Path downloads = getDownloadDir();
		UFS.MKDIR.createDirs_(downloads, true);
		Path outputFile = downloads.resolve(document.getFileName());
		return outputFile;
	}

	protected String getValidationErrorForDownloadDocument(Document document) {
		return "upload file unsupported";
	}
}
