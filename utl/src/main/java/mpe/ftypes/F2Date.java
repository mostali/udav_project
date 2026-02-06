package mpe.ftypes;

import mpe.ftypes.core.FDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class F2Date extends FDate {

	public static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM.yyyy");

	public F2Date() {
		this(System.currentTimeMillis());
	}

	public F2Date(Long time_ms) {
		super(time_ms, FORMAT);
	}

	public F2Date(String date) throws ParseException {
		super(date, FORMAT);
	}

	public F2Date(Date date) {
		super(date, FORMAT);
	}

}
