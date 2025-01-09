package mpe;

import java.io.*;
import java.util.UUID;

public class UJAXB {

	/**
	 * #OPT Метод можно оптимизировать. Сделать replace одним проходом
	 * Альтернатива (менее быстрая) - String result = IOUtils.toString(dataInputStream, "Windows-1251");
	 * https://habr.com/ru/company/luxoft/blog/278233/
	 */
	public static InputStream fixAmpersandAndDoubleQuote(InputStream dataInputStream) {
		try {
			ByteArrayOutputStream resultOS = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = dataInputStream.read(buffer)) != -1) {
				resultOS.write(buffer, 0, length);
			}
			String result = resultOS.toString("Windows-1251");
			if (result.indexOf('&') != -1) {
				result = result.replace("&amp;", "&");
				result = result.replace("&", "&amp;");
			}
			if (result.contains(FixDoubleQuote.BAD_DOUBLE_SEQ)) {
				result = FixDoubleQuote.fixDoubleQuote(result);

			}
			return new StringInputStream(result, "Windows-1251");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static class StringInputStream extends FilterInputStream {
		private static String DEFAULT_ENCODING = "ISO-8859-5";

		public StringInputStream(String s) {
			super(new ByteArrayInputStream(getStringAsBytes(s)));
		}

		public StringInputStream(String s, String coding) {
			super(new ByteArrayInputStream(getStringAsBytes(s, coding)));
		}

		private static byte[] getStringAsBytes(String s) {
			return getStringAsBytes(s, DEFAULT_ENCODING);
		}

		private static byte[] getStringAsBytes(String s, String coding) {
			try {
				return s.getBytes(coding);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}

	}

	public static class FixDoubleQuote {

		public static final String TRUE_SEQ = "\"";//"
		public static final String BAD_DOUBLE_SEQ = "\"\"";//""

		public static final String SEQ_NOVALID_V1 = "=\"\"\"";//="""
		public static final String SEQ_NOVALID_V2 = "= \"\"\"";//= """
		//

		public static final String SEQ_VALID = "=\"\"";//=""
		public static final String SEQ_VALID_V1 = "= \"\"";//= ""
		public static final String SEQ_HIDE = UUID.randomUUID().toString();

		public static String fixDoubleQuote(String str) {
			boolean hasHideSeq = false;
			//stash novalid v1
			if (str.contains(SEQ_NOVALID_V1)) {
				str = str.replace(SEQ_NOVALID_V1, SEQ_HIDE);
				hasHideSeq = true;
			}
			//stash novalid v2
			if (str.contains(SEQ_NOVALID_V2)) {
				str = str.replace(SEQ_NOVALID_V2, SEQ_HIDE);
				hasHideSeq = true;
			}
			//
			//stash valid
			if (str.contains(SEQ_VALID)) {
				str = str.replace(SEQ_VALID, SEQ_HIDE);
				hasHideSeq = true;
			}
			//stash valid v1
			if (str.contains(SEQ_VALID_V1)) {
				str = str.replace(SEQ_VALID_V1, SEQ_HIDE);
				hasHideSeq = true;
			}
			//
			//меняем novalid
			str = str.replace(BAD_DOUBLE_SEQ, TRUE_SEQ);

			//возвращаем
			if (hasHideSeq) {
				str = str.replace(SEQ_HIDE, SEQ_VALID);
			}
			return str;
		}
	}

}
