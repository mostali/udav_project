package mpe.ftypes.core;

import lombok.SneakyThrows;
import mpu.X;
import mpu.core.ARG;
import mpu.core.QDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Locale;

public class FDate extends Date {

	public static final String UTC_SECZ = "yyyy-MM-dd'T'HH:mm:ss'Z'";//2025-02-04T18:46:15Z
	public static final String UTC_MS = "yyyy-MM-dd'T'HH:mm:ss.SSS";//2025-02-04T21:47:36.000
	public static final String UTC_MSZ = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";//2025-02-04T21:47:36.000
	public static final String[] APP_FORMATS = {UTC_MSZ, UTC_SECZ};

	public static final String YYYY_DB_ISO_STANDART = "yyyy-MM-dd HH:mm:ss";
	public static final String FULL_YYYY_MM_DD_T_HH_MM_SS_SSSXXX = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

	public static final String MONO_YYYYMMDD = "yyyyMMdd";
	public static final String MONO_YYYYMMDDmm = "yyyyMMddmm";
	public static final String MONO_YYYYMMDDmmhh = "yyyyMMddHHmm";
	public static final String MONO_YYYYMMDDmmhhss = "yyyyMMddHHmmss";

	public static void main(String[] args) throws ParseException {
//			Date parse = parse("2025-02-04T21:47:36.000", UTC_MS);
		QDate parse = QDate.of("2025-02-04T21:47:36.000", UTC_MS);
		X.exit(parse.addHours(-3).f(FDate.UTC_SECZ));
//		X.exit(QDate.now().f(FDate.UTC_SEC));
//		P.exit(java.time.OffsetDateTime.parse("2022-10-15T11:42:18+03:00", java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime());
//		P.exit(OffsetDateTime.parse("2022-10-15T11:42:18+03:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime());
//		P.exit(OffsetDateTime.parse("2022-10-15T11:42:18+03:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSX")));
//		P.exit(parseWithOffset2("aa2022-10-15T11:42:18+03:00"));
//		P.exit(parseWithOffset("2022-10-26T12:39:02.829744"));
	}
//


	public static final String YYYYMMDD_mmhhss_S = "MMdd;HH-mm-ss.S";
	public static final String YYYY_MM_DD = "yyyy-MM-dd";

	public static final String APP_SLDF_UFOS = "MM-dd;HH:mm:ss.SSS";
	public static final String APP_SLDF_AI = "dd.MM.yyyy;HH:mm:ss.SSS";
	public static final String APP_SLDF_MDM = "yyyy-MM-dd HH:mm:ss,SSS";//2025-01-21 16:23:21,053
	public static final String APP_MONOTWICE = "yyyyMMdd:HHmmss";//20250121:16:23:21
	public static final String SHORT_NOW_MINUTES = "HH:mm";
	public static final String SHORT_DAY_MIN = "MM-dd HH:mm";


	@SneakyThrows
	public static Date DB_ISO_STANDART(String pattern, Date... defRq) {
		try {
			return new SimpleDateFormat(YYYY_DB_ISO_STANDART).parse(pattern);
		} catch (ParseException ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	//	public static final String YYYY_MM_DD_T_HH_MM_SS_SSSZZ = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";
	public static final String FNEXUS_YYYY_MM_DD_HH_MM_SS_S_Z = "yyyy-MM-dd HH:mm:ss.S Z";
	public static final String FNEXUSTHTML_EEE_MMM_D_HH_MM_SS_Z_YYYY = "EEE MMM d hh:mm:ss Z yyyy";//"Sat Sep 25 06:13:31 MSK 2021"
	public static final String FMONTH_MMMM_YY = "MMMM yy";//"October 2021"

	public static final ZoneOffset MSK_ZONE_OFFSET = ZoneOffset.of("+03:00");

	public final SimpleDateFormat format;

	@SneakyThrows
	public static Date parse(String date, String format) {
		return new SimpleDateFormat(format).parse(date);
	}

	public static Date parseWithOffset(String date) {
		OffsetDateTime offsetDateTime = OffsetDateTime.of(java.time.LocalDateTime.parse(date), MSK_ZONE_OFFSET);
		return Date.from(offsetDateTime.toInstant());
	}

	public static SimpleDateFormat FFULL_YYYY_MM_DD_T_HH_MM_SS_SSSXXX() {
		return new SimpleDateFormat(FULL_YYYY_MM_DD_T_HH_MM_SS_SSSXXX);
	}

	public static SimpleDateFormat FNEXUS_YYYY_MM_DD_HH_MM_SS_S_Z() {
		return new SimpleDateFormat(FNEXUS_YYYY_MM_DD_HH_MM_SS_S_Z);//z
	}

	public static SimpleDateFormat FMONTH_MMMM_YY() {
		return new SimpleDateFormat(FMONTH_MMMM_YY, Locale.ENGLISH);
	}

	public static SimpleDateFormat FNEXUSTHTML_EEE_MMM_D_HH_MM_SS_Z_YYYY() {
		return new SimpleDateFormat(FNEXUSTHTML_EEE_MMM_D_HH_MM_SS_Z_YYYY, Locale.ENGLISH);
	}

	public static SimpleDateFormat FORMAT_HTML_DAY() {
		return new SimpleDateFormat("yyyy-MM-dd");
	}

	public static SimpleDateFormat FORMAT_DDMMYYYY() {
		return new SimpleDateFormat("ddMMyyyy");
	}

	public static SimpleDateFormat FORMAT_DDMMYY() {
		return new SimpleDateFormat("ddMMyy");
	}

	public static SimpleDateFormat FORMAT_DDMMYYYY_DOT() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
		simpleDateFormat.setLenient(false);
		return simpleDateFormat;
	}

	public static String toString(Date date, String dateFormat) {
		return new SimpleDateFormat(dateFormat).format(date);
	}

	public SimpleDateFormat getFormat() {
		return format;
	}

	public FDate() {
		this(System.currentTimeMillis(), FFULL_YYYY_MM_DD_T_HH_MM_SS_SSSXXX());
	}

	public FDate(Date date) {
		this(date, FFULL_YYYY_MM_DD_T_HH_MM_SS_SSSXXX());
	}

	public FDate(String date, SimpleDateFormat format) throws ParseException {
		this(format.parse(date).getTime(), format);
	}

	public FDate(Date date, SimpleDateFormat format) {
		this(date.getTime(), format);
	}

	public FDate(long time_ms, SimpleDateFormat format) {
		setTime(time_ms);
		this.format = format;
	}

	public String toStringFormated() {
		return getFormat().format(this);
	}

	@Override
	public String toString() {
		return toStringFormated();
	}


}
