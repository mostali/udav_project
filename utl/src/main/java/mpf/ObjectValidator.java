package mpf;

import mpc.exception.ERxception;
import mpc.exception.MultiCauseExceptionExt;
import mpu.IT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ObjectValidator {
	public static final Logger L = LoggerFactory.getLogger(ObjectValidator.class);

	public static <T> T validate(String fieldpath, Object docvalue, boolean required, Class<T> clazz, int[] len, Pattern regex) {
		try {
			if (docvalue == null && !required) {
				return null;
			}
			return validate(fieldpath, docvalue, clazz, len, regex);
		} catch (Exception ex) {
			if (!required) {
				return null;
			}
			throw ex;
		}
	}

	public static <T> T validate(String fieldpath, Object docvalue, Class<T> clazz, int[] len, Pattern regex) {
		IT.notNull(clazz, "object class is null");
		boolean isnull = docvalue == null;
		if (isnull) {
			throw ValidatorTypeException.EErrors.NULLVALUE.I("Field '" + fieldpath + "' is required");
		}
		if (clazz == null) {
			throw ValidatorTypeException.EErrors.NULLTYPE.I("Type '" + fieldpath + "' is required");
		} else if (docvalue == null) {
			return (T) docvalue;
		}
		String val = docvalue.toString();
		if (BigDecimal.class.equals(clazz)) {
			try {
				val = val.replace(",", ".");
				BigDecimal bd = new BigDecimal(val);
				return (T) bd;
			} catch (Exception e) {
				List errors = Arrays.asList(new RuntimeException("Class BigDeciamal, val=" + val), e);
				throw ValidatorTypeException.EErrors.TYPEEQERROR.I(new MultiCauseExceptionExt(errors));
			}
		} else if (!clazz.isAssignableFrom(docvalue.getClass())) {
			throw ValidatorTypeException.EErrors.TYPEEQERROR.I("Field ::: '" + fieldpath + "' is not a class from value ::: " + clazz);
		}

		if (String.class.isAssignableFrom(docvalue.getClass())) {
			if (regex != null) {
				if (!regex.matcher(val).matches()) {
					throw ValidatorTypeException.EErrors.REGEX.I("Field ::: '" + fieldpath + "' is not matches regex ::: " + val + ":::" + regex);
				}
			} else if (len != null && len.length > 0) {
				int l = len[0] > 0 ? len[0] : -1;
				int r = len.length > 0 && len[1] > 0 ? len[1] : -1;
				if (l == -1 && r == -1) {
					//OK
				} else if (l == r) {
					if (val.length() != l) {
						throw ValidatorTypeException.EErrors.LENGTHEQERROR.I("l!=r");
					} else {
						//OK;
					}
				} else if (l > r) {
					throw ValidatorTypeException.EErrors.VALIDATORERROR.I("invalid logic validator l>r");
				} else {
					if (val.length() < l) {
						throw ValidatorTypeException.EErrors.LENGTHMINERROR.I("min len=" + l + ", val=" + val);
					} else if (val.length() > r) {
						throw ValidatorTypeException.EErrors.LENGTHMAXERROR.I("max len=" + r + ", val=" + val);
					} else {
						//OK;
					}
				}
			}
		} else if (Number.class.isAssignableFrom(docvalue.getClass())) {
			if (len == null) {
				return (T) docvalue;
			}
			int lenMax = len.length == 2 ? len[1] : -1;
			if (lenMax != -1) {
				if (val.length() > lenMax) {
					throw ValidatorTypeException.EErrors.LENGTHMAXERROR.I("Field ::: '" + fieldpath + "' must be MAX that  ::: " + lenMax + ":::" + val);
				}
			}

			int lenMin = len.length == 1 || len.length == 2 ? len[0] : -1;

			if (lenMin <= 0) {
				return (T) docvalue;
			}
			if (lenMin != -1) {
				if (val.length() < lenMin) {
					throw ValidatorTypeException.EErrors.LENGTHMINERROR.I("Field ::: '" + fieldpath + "' must be MIN that  ::: " + lenMin + ":::" + val);
				}
			}
		}
		return (T) docvalue;
	}

	public static class ValidatorTypeException extends ERxception {
		public enum EErrors {
			NULLVALUE("Поле не определено"),
			NULLTYPE("Тип поля не определен"),
			TYPEEQERROR("Тип значения не соответствует типу поля"),
			LENGTHMINERROR("Минимальная длина поля меньше требуемой"),
			LENGTHMAXERROR("Максимальная длина поля больше требуемой"),
			LENGTHEQERROR("Длина поля не равна требуемой"),
			VALIDATORERROR("Ошибка параметров валидатора"),
			REGEX("Значение поля не соответствует требуемой маске");

			public final String message;

			EErrors(String message) {
				this.message = message;
			}

			public ValidatorTypeException I() {
				return new ValidatorTypeException(this);
			}

			public ValidatorTypeException I(Throwable error) {
				return new ValidatorTypeException(this, error);
			}

			public ValidatorTypeException I(String message) {
				return new ValidatorTypeException(this, message);
			}
		}

		public ValidatorTypeException(Enum error) {
			super(error);
		}

		public ValidatorTypeException(Enum error, String message) {
			super(error, new RuntimeException(message));
		}

		public ValidatorTypeException(Enum error, Throwable throwable) {
			super(error, throwable);
		}
	}

}
