package mpe.ftypes;

import mpe.ftypes.core.FDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ddMMyyyy
 */
public class F1Date extends FDate {

	public static final SimpleDateFormat FORMAT = new SimpleDateFormat("ddMMyyyy");

	public F1Date() {
		this(System.currentTimeMillis());
	}

	public F1Date(Long time_ms) {
		super(time_ms, FORMAT);
	}

	public F1Date(Date date) {
		super(date, FORMAT);
	}

	public F1Date(String date) throws ParseException {
		super(date, FORMAT);
	}

	public static F1Date from(String string) throws ParseException {
		return new F1Date(FORMAT.parse(string));
	}

}
