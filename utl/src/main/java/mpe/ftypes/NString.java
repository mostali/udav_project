package mpe.ftypes;

import mpe.ftypes.core.FString;
import org.apache.commons.lang3.StringUtils;

/**
 * NULL String
 */
public class NString extends FString {
	public NString() {
	}

	public NString(String original) {
		super(original);
	}

	@Override
	public String toString() {
		return StringUtils.isBlank(original) ? null : original;
	}
}
