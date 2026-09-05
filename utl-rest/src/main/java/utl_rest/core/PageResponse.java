package utl_rest.core;

import org.springframework.http.ResponseEntity;
import utl_rest.SrcResponseEntity;
import utl_rest.URest;

import java.util.Map;

public abstract class PageResponse {
	public final Map<String, String> args;

	public PageResponse(Map<String, String> args) {
		this.args = args;
	}

	public ResponseEntity<?> render() {
		try {
			return renderImpl();
		} catch (Exception ex) {
			if (URest.L.isErrorEnabled()) {
				URest.L.error("PageResponse", ex);
			}
			return SrcResponseEntity.C500(ex);
		}
	}

	public abstract ResponseEntity<?> renderImpl() throws Exception;
}
