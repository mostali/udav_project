package mp.gd;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import mpu.core.ENUM;
import mpc.json.JSoon;


public class GdException extends Exception {
	public  enum GdErrors {
		NOSTATUS, SUCCESS, SHEET_EXISTS, PARSE_RANGE;

		public void ON() throws GdException {
			throw I();
		}

		public GdException I() {
			return new GdException(this);
		}

		public boolean isHappens(Exception ex) {
			if (!(ex instanceof GoogleJsonResponseException)) {
				return false;
			}

			String errorGdMessage = null;
			{//NEW QT2
				try {
					String errorJsonContent = ((GoogleJsonResponseException) ex).getContent();
					JSoon json = JSoon.of(errorJsonContent);
					errorGdMessage = json.first("message").toStringJson();
				} catch (Exception e1) {
					ApiGdExt.L.error(e1.getMessage());
					return false;
				}
			}
			switch (this) {
				case SHEET_EXISTS:
					try {

						return errorGdMessage.contains("A sheet with the name \"")
							   && errorGdMessage.endsWith("\" already exists. Please enter another name.");
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;

				case PARSE_RANGE:
					try {
						return errorGdMessage.startsWith("Unable to parse range");
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				default:
					break;
			}

			return false;
		}

	}

	public static final long serialVersionUID = 1L;

	final int type;

	public GdException(GdErrors error) {
		super(error.name());
		this.type = ENUM.indexOf(error);
	}

	public String messageOf() {
		return messageOf(type);
	}

	private static String messageOf(int type) {
		Enum etype = ENUM.getEnum(type, GdErrors.class, GdErrors.NOSTATUS);
		return etype.name();
	}

}
