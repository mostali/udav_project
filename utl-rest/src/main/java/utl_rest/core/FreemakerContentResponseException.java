package utl_rest.core;

//	public static class FreeMakerContentResponseException extends ResponseException {
//		final String relativePathOfResourceFreemakerTemplate;
//		final Map model;
//
//		public FreeMakerContentResponseException(String relativePathOfResourceFreemakerTemplate, Map model) {
//			super();
//			this.relativePathOfResourceFreemakerTemplate = relativePathOfResourceFreemakerTemplate;
//			this.model = model;
//		}
//
//		@Override
//		public ResponseEntity tpResponseEntity() {
//			if (U.empty(relativePathOfResourceFreemakerTemplate)) {
//				return UWeb.getResponse_STATUS("relativePathOfResourceFreemakerTemplate is empty", HttpStatus.INTERNAL_SERVER_ERROR);
//			}
////			if (model == null) {
////				return UWeb.getResponse_STATUS("Model is empty", HttpStatus.INTERNAL_SERVER_ERROR);
////			}
//			return UWeb.getResponse_OK_FREEMAKER(relativePathOfResourceFreemakerTemplate, model);
//
//		}
//	}
