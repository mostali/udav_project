package utl_rest.core;

import mpu.X;
import org.springframework.http.ResponseEntity;
import utl_rest.ResponseException;
import utl_rest.SrcResponseEntity;
import utl_rest.URest;

import java.util.Map;

public class VelocityContentResponseException extends ResponseException {
	final String relativePathOfResourceVelocityTemplate;
	final Map model;

	public VelocityContentResponseException(String relativePathOfResourceVelocityTemplate, Map model) {
		super();
		this.relativePathOfResourceVelocityTemplate = relativePathOfResourceVelocityTemplate;
		this.model = model;
	}

	@Override
	public ResponseEntity toResponseEntity() {
		if (X.empty(relativePathOfResourceVelocityTemplate)) {
			return SrcResponseEntity.C500("relativePathOfResourceVelocityTemplate is empty");
		}
		return URest.getResponse_OK_VELOCITY(relativePathOfResourceVelocityTemplate, model);

	}
}
