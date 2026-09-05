package mpe.logs.filter;

import mpu.str.UST;
import mpc.str.condition.LogGetterDate;
import mpu.core.QDate;
import mpu.X;
import mpc.map.MAP;
import mpe.ftypes.core.FDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mpu.IT;

import java.util.Date;
import java.util.Map;

public class LogProcArgs {

	private static Logger L = LoggerFactory.getLogger(LogProcArgs.class);

	public static final String BY_KEY = "byKey";
	public static final String BY_MINUTES = "byMinutes";
	public static final String BY_DATE = "byDate";
	//
	public static final String BY_TIME_START = "byTimeStart";
	public static final String BY_TIME_END = "byTimeEnd";

	final String _byKey;
	final String _byMinutes;
	final String _byDate;
	final String _byTimeStart;
	final String _byTimeEnd;
	final String _byKeyStart;
	final String _byKeyEnd;

	final LogGetterDate getterDate;

	@Override
	public String toString() {
		return "LogArgsProcessor{ _byKey='" + _byKey + '\'' + ", _byMinutes='" + _byMinutes + '\'' + ", _byDate='" + _byDate + '\'' + ", _byTimeStart='" + _byTimeStart + '\'' + ", _byTimeEnd='" + _byTimeEnd + '\'' + ", _byKeyStart='" + _byKeyStart + '\'' + ", _byKeyEnd='" + _byKeyEnd + '\'' + '}';
	}

	public LogProcArgs(LogGetterDate getterDate, Map<String, String> keys) {
		this(getterDate, MAP.getNE(keys, BY_KEY, null), MAP.getNE(keys, BY_MINUTES, null), MAP.getNE(keys, BY_DATE, null), MAP.getNE(keys, BY_TIME_START, null), MAP.getNE(keys, BY_TIME_END, null), null, null);
	}

	public LogProcArgs(LogGetterDate getterDate, String _byKey) {
		this(null, _byKey, null, null, null, null, null, null);
	}

	public LogProcArgs(LogGetterDate getterDate, Date _byDate) {
		this(getterDate, null, null, getterDate.toString(_byDate), null, null, null, null);
	}

	public LogProcArgs(LogGetterDate getterDate, Date _byTimeStart, Date _byTimeEnd) {
		this(getterDate, null, null, null, getterDate.toString(_byTimeStart),getterDate.toString(_byTimeEnd), null, null);
	}

	public LogProcArgs(LogGetterDate getterDate, String _byKeyStart, String _byKeyEnd) {
		this(getterDate, null, null, null, null, null, _byKeyStart, _byKeyEnd);
	}

	public LogProcArgs(LogGetterDate getterDate, String _byKey, String _byMinutes, String _byDate, String _byTimeStart, String _byTimeEnd, String _byKeyStart, String _byKeyEnd) {

		this.getterDate = getterDate == null ? getterDate = new LogGetterDate(FDate.APP_SLDF_UFOS, QDate.now().year) : getterDate;

		this._byKey = _byKey;
		this._byMinutes = _byMinutes;
		this._byDate = _byDate == null ? null : IT.isDate(_byDate, getterDate.format);


		if (X.notEmptyAnyStr(_byTimeStart, _byTimeEnd)) {
			if (_byTimeStart != null) {
				IT.NN(_byTimeEnd, "also set end date");
			} else if (_byTimeEnd != null) {
				IT.NN(_byTimeEnd, "also set start date");
			}
			IT.isFalse(_byTimeStart.equals(_byTimeEnd), "start/end date is equals");
			IT.isDateAfter(UST.DATE(_byTimeStart, getterDate.format), UST.DATE(_byTimeEnd, getterDate.format));

			this._byTimeStart = _byTimeStart;
			this._byTimeEnd = _byTimeEnd;
		} else {
			this._byTimeStart = null;
			this._byTimeEnd = null;
		}

		this._byKeyStart = _byKeyStart;
		this._byKeyEnd = _byKeyEnd;


		if (L.isDebugEnabled()) {
			L.debug("LogArgsProcessor inited:::{}", toString());
		}
	}

	public boolean hasAnyFilter() {
		return X.notEmptyAnyStr(_byKey, _byMinutes, _byDate, _byTimeStart, _byTimeEnd);
	}

	public boolean hasOnly_ByKey() {
		return X.notEmpty(_byKey) && X.emptyAll(_byKey, _byMinutes, _byDate, _byTimeStart, _byTimeEnd);
	}


	public LogProc buildLogLinesProcessor() {
		LogProc logLinesProcessor = new LogProc(getterDate);
		if (X.notEmpty(_byKey)) {
			logLinesProcessor.addLineCondition_ByKey(_byKey);
		}
		if (X.notEmpty(_byMinutes)) {
			logLinesProcessor.addLineCondition_ByLastMinutesAgo(IT.isInt0(_byMinutes));
		}
		if (X.notEmpty(_byDate)) {
			logLinesProcessor.addLineCondition_BySingleDay(getterDate.getDateFrom(_byDate));
		}
		if (X.notEmptyAll(_byTimeStart, _byTimeEnd)) {
			logLinesProcessor.addLineCondition_ByBetweenDay(getterDate.getDateFrom(_byTimeStart), getterDate.getDateFrom(_byTimeEnd));
		}
		if (X.notEmpty(_byKeyStart) || X.notEmpty(_byKeyEnd)) {
			logLinesProcessor.addLineCondition_ByBetweenKeys(_byKeyStart, _byKeyEnd);
		}
		return logLinesProcessor;
	}

}
