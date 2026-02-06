package utl_rest.core;

import org.springframework.http.ResponseEntity;
import utl_rest.ResponseException;
import utl_rest.SrcResponseEntity;
import utl_rest.URest;

import java.util.Collection;

public class FilesDownloadResponseException extends ResponseException {
	final Collection<String> files;

	public FilesDownloadResponseException(Collection<String> files) {
		super();
		this.files = files;
	}

	@Override
	public ResponseEntity toResponseEntity() {
		if (files == null) {
			return SrcResponseEntity.C500("Files is empty");
		}
		return URest.createResponseForDownloadFiles(files);
	}
}
