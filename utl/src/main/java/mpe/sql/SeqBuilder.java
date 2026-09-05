package mpe.sql;

import java.util.Collection;
import java.util.Map;

public class SeqBuilder {

	/**
	 * Строит такие шаблоны.
	 * Если значение равно NULL - добавляем как есть (не оборачиваем в кавычки)
	 * <p>
	 * String PART = "( part1 , .. , partN )"; // wrapQuote=false
	 * String PART = "( 'part1' , .. , 'partN')"; // wrapQuote=true
	 */
	public static StringBuilder generateSequenceWrap(Collection objects, boolean wrapQuoteOnValues) {
		return generateSequence(objects, wrapQuoteOnValues, true);
	}

	public static StringBuilder generateSequenceUnwrap(Collection objects, boolean wrapQuoteOnValues) {
		return generateSequence(objects, wrapQuoteOnValues, false);
	}

	interface ValueTransformer {
		String transform(Object value);
	}

	public static class PostgreeFieldDeclareTransformer implements ValueTransformer {
		public static final PostgreeFieldDeclareTransformer DEF = new PostgreeFieldDeclareTransformer(null, null);
		private final Map<String, String> nullValues;
		private final String pfxAlias;

		public PostgreeFieldDeclareTransformer(Map<String, String> nullValues, String pfxAlias) {
			this.nullValues = nullValues;
			this.pfxAlias = pfxAlias;
		}

		public static PostgreeFieldDeclareTransformer of(Map<String, String> nullValues) {
			return new PostgreeFieldDeclareTransformer(nullValues, null);
		}

		@Override
		public String transform(Object fieldValue) {
			if (fieldValue == null) {
				return null;
			}
			String vl = String.valueOf(fieldValue);
			if (isDate(vl)) {
				return "coalesce (" + vl + ", to_date('1990-01-01', 'yyyy-mm-dd'))";
			}
			if (nullValues != null) {
				String vlWoPfx = pfxAlias == null ? vl : (vl.startsWith(pfxAlias) ? vl.substring(pfxAlias.length()) : vl);
				String defVl = nullValues.get(vlWoPfx);
				if (defVl != null) {
					defVl = "0";
					return "coalesce (" + vl + ", '" + defVl + "')";
				}
			}
			return vl;
		}

		public static boolean isDate(String valStr) {
			return valStr.contains("DATE") && !valStr.contains(" ");
		}
	}

	public static StringBuilder generateSequence(Collection objects, boolean wrapQuoteOnValues, boolean wrapAllSequence) {
		return generateSequence(objects, wrapQuoteOnValues, wrapAllSequence, false);
	}

	public static StringBuilder generateSequence(Collection objects, boolean wrapQuoteOnValues, boolean wrapAllSequence, boolean allowEmptyResult) {
		return generateSequence(objects, wrapQuoteOnValues, wrapAllSequence, null, allowEmptyResult);
	}

	public static StringBuilder generateSequence(Collection objects, boolean wrapQuoteOnValues, boolean wrapAllSequence, ValueTransformer valueTransformer, boolean allowEmpty) {
		StringBuilder sb = new StringBuilder();
		if (objects.isEmpty()) {
			if (allowEmpty) {
				if (wrapAllSequence) {
					sb.append("()");
				}
				return sb;
			}
			throw new IllegalArgumentException("List with arguments is empty");
		}

		if (wrapAllSequence) {
			sb.append("( ");
		}

		for (Object value : objects) {
			Object tval = valueTransformer == null ? value : valueTransformer.transform(value);
			if (wrapQuoteOnValues) {
				if (tval == null) {
					sb.append(tval);
				} else {
					sb.append('\'').append(tval).append('\'');
				}
			} else {
				sb.append(tval);
			}
			sb.append(" , ");
		}

		sb.delete(sb.length() - 2, sb.length());//remove last 2 chars

		if (wrapAllSequence) {
			sb.append(")");
		}

		return sb;
	}

	public static String implode(CharSequence del, CharSequence... strings) {
		StringBuilder sb = new StringBuilder();
		for (CharSequence string : strings) {
			sb.append(string).append(del);
		}
		return sb.length() == 0 || sb.length() == del.length() ? "" : sb.delete(sb.length() - del.length(), sb.length()).toString();
	}
}
