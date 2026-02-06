package mpc.time;

import mpe.core.P;
import mpu.core.ARR;
import mpu.IT;
import mpc.str.condition.LogGetterDate;
import mpe.ftypes.core.FDate;
import mpe.logs.filter.LogProc;
import mpu.core.QDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

public class LogTest {

	public static final QDate NOW = QDate.now();
	//	public static final QDate DATE1 = QDate.of("09-30;01:09:11.333 line1 key1", FDate.APP_STANDART_LOG_DATE_FORMAT);
	public static final QDate DATE2 = QDate.ofWithYear("09-30;01:11:22.555 line2 key2", FDate.APP_SLDF_UFOS);
	public static final QDate DATE3 = QDate.ofWithYear("09-30;01:13:33.777 line3 key3", FDate.APP_SLDF_UFOS);
	public static final QDate DATE4 = QDate.ofWithYear("09-30;01:15:55.999 line3 key3", FDate.APP_SLDF_UFOS);

	public static final String[] TEST = {//
			NOW.f(FDate.APP_SLDF_UFOS, null, null, null, null, 11, null, null, null) + " line1 key11",//
			NOW.f(FDate.APP_SLDF_UFOS, null, null, null, null, 13, null, null, null) + " line2 key13",//
			NOW.f(FDate.APP_SLDF_UFOS, null, null, null, null, 15, null, null, null) + " line3 key15",//
			NOW.f(FDate.APP_SLDF_UFOS, null, null, null, null, 17, null, null, null) + " line4 key17",//
			NOW.f(FDate.APP_SLDF_UFOS, null, null, null, null, 19, null, null, null) + " line5 key19" //
	};

	public static final String[] TEST_DAY = {//
			NOW.f(FDate.APP_SLDF_UFOS, null, null, 1, null, 11, null, null, null) + " line1 key11",//
			NOW.f(FDate.APP_SLDF_UFOS, null, null, 3, null, 13, null, null, null) + " line2 key13",//
			NOW.f(FDate.APP_SLDF_UFOS, null, null, 3, null, 15, null, null, null) + " line3 key15",//
			NOW.f(FDate.APP_SLDF_UFOS, null, null, 3, null, 17, null, null, null) + " line4 key17",//
			NOW.f(FDate.APP_SLDF_UFOS, null, null, 9, null, 19, null, null, null) + " line5 key19" //
	};

	public LogTest() {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testByKey() throws Exception {
		String[] logLines = TEST;
		P.rt(logLines, "before");
		List lines = LogProc.createByKey(LogGetterDate.buildByDefault(), "key11").process(logLines);
		IT.isLength(lines, 1);
		P.rt(lines, "after");
		IT.state(logLines[0].equals(lines.get(0)));
	}

	@Test
	public void testByDay() throws Exception {
		String[] logLines = TEST_DAY;
		P.rt(logLines, "before");

		LogGetterDate logGetterDate = LogGetterDate.buildByDefault();
		Date day = logGetterDate.getDateFrom(logLines[3]);
		List lines = LogProc.createByDay(logGetterDate, day).process(logLines);
		IT.isLength(lines, 3);
		P.rt(lines, "after");
		String[] sublist = ARR.sublist(logLines, 1, 3);
		IT.state(lines.equals(ARR.as(sublist)));
	}

	@Test
	public void copy_test() throws Exception {
	}

	@Test
	public void add_test() throws Exception {
	}


}
