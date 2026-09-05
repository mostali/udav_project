package mpe.ftypes;

import mpe.ftypes.core.FString;
import org.apache.commons.lang3.StringUtils;

/**
 * NULL String (as Long)
 */
public class NLongString extends FString {
	public NLongString() {
	}

	public NLongString(Number original) {
		this(original == null ? null : String.valueOf(original.longValue()));
	}

	public NLongString(String original) {
		super(original);
	}

	@Override
	public String toString() {
		return StringUtils.isBlank(original) ? null : original;
	}
}
