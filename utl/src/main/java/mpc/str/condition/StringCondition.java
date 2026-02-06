package mpc.str.condition;

import mpu.IT;
import mpu.core.ARG;
import mpu.X;
import mpc.time.java7.UTime7;
import mpe.logs.filter.ILogFilter;

import java.util.*;

public abstract class StringCondition {

	public static List<StringCondition>[] splitIncludeExclude(List<StringCondition> lineConditionsSingly) {
		List include = new ArrayList();
		List exclude = new ArrayList();
		lineConditionsSingly.forEach(c -> (c.exclude() ? exclude : include).add(c));
		return new List[]{include, exclude};
	}

	public boolean exclude() {
		return false;
	}

	public static boolean hasSomeOneCondition(List<StringCondition> lineConditionsUna, String line) {
		for (StringCondition iLineCondition : lineConditionsUna) {
			if (iLineCondition.isEqualsLine(line)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasSomeOneStartCondition(List<IBetweenCondition> lineConditionsBetween, String line) {
		for (IBetweenCondition iLineCondition : lineConditionsBetween) {
			if (iLineCondition.hasStart(line)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasSomeOneEndCondition(List<IBetweenCondition> lineConditionsBetween, String line) {
		for (IBetweenCondition iLineCondition : lineConditionsBetween) {
			if (iLineCondition.hasEnd(line)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasCondition(StringCondition stringCondition, String val, boolean... ifNullConditionThatTrue) {
		if (stringCondition == null) {
			return ARG.isDefEqTrue(ifNullConditionThatTrue);
		} else {
			return stringCondition.isEqualsLine(val);
		}
	}

	public String toStringFnPart() {
		return getClass().getSimpleName();
	}

	public interface IBetweenCondition {
		boolean hasStart(String line);

		boolean hasEnd(String line);
	}

	public abstract boolean isEqualsLine(String line);

	public boolean isBetweenCondition() {
		return this instanceof IBetweenCondition;
	}

	public static class KeyContainsStringCondition extends StringCondition {
		public final String key;
		public final StringConditionType eqStringType;

		public KeyContainsStringCondition(String key, StringConditionType eqStringType) {
			this.key = key;
			this.eqStringType = eqStringType;
		}

		@Override
		public boolean isEqualsLine(String line) {
			return eqStringType.matches(line, key);
		}

		public static KeyContainsStringCondition build(String key, StringConditionType eqStringType) {
			return new KeyContainsStringCondition(key, eqStringType);
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + ":" + eqStringType + ":" + key;
		}
	}

	public static class BwKeysStringCondition extends StringCondition implements IBetweenCondition {
		public final String start;
		public final String end;
//		public final boolean hasStartKey;

		public static BwKeysStringCondition findBwKeysConditionWithKeys(List<IBetweenCondition> lineConditionsBetween) {
			return (BwKeysStringCondition) lineConditionsBetween.stream().filter(c -> c instanceof BwKeysStringCondition).findAny().orElse(null);
		}

		@Override
		public String toString() {
			return "BetweenKeysStringCondition{" + "start=" + start + ", end=" + end + '}';
		}

		public BwKeysStringCondition(String start, String end) {
			this.start = start;
			this.end = end;
			IT.notEmpty(start, "set startKey");
			IT.notEmpty(end, "set endKey");
//			hasStartKey = X.notEmpty(start);
		}

		@Override
		public boolean isEqualsLine(String line) {
			return true;
		}

		public static BwKeysStringCondition build(String start, String end) {
			return new BwKeysStringCondition(start, end);
		}

		@Override
		public boolean hasStart(String line) {
			if (X.empty(start)) {
				return true;
			}
			return line != null && line.contains(start);
		}

		@Override
		public boolean hasEnd(String line) {
			if (X.empty(end)) {
				return false;
			}
			return line != null && line.contains(end);
		}

	}

	public static class BwDateStringCondition extends StringCondition implements IBetweenCondition {
		public final Date start;
		public final Date end;
		public final IGetterDate<String> iGetterDate;

		@Override
		public String toString() {
			return "BetweenDateStringCondition{" + "start=" + iGetterDate.toDate(start) + ", end=" + iGetterDate.toDate(end) + '}';
		}

		@Override
		public String toStringFnPart() {
			return ILogFilter.toStringFnPart(start, end);
		}

		public BwDateStringCondition(Date start, Date end, IGetterDate<String> iGetterDate) {
			IT.isDateAfter(start, end);
			this.start = start;
			this.end = end;
			this.iGetterDate = iGetterDate;
		}

		@Override
		public boolean isEqualsLine(String line) {
			Date date = iGetterDate.getDateFrom(line);
			if (date == null) {
				return false;
			}
			return date.compareTo(start) >= 0 && date.compareTo(end) <= 0;
		}

		public static BwDateStringCondition build(Date start, Date end, IGetterDate<String> iGetterDate) {
			return new BwDateStringCondition(start, end, iGetterDate);
		}

		@Override
		public boolean hasStart(String line) {
			Date date = iGetterDate.getDateFrom(line, null);
			if (date == null) {
				return false;
			}
			boolean hasStart = date.compareTo(start) >= 0;
			return hasStart;
		}

		@Override
		public boolean hasEnd(String line) {
			Date logDate = iGetterDate.getDateFrom(line, null);
			if (logDate == null) {
				return false;
			}
			boolean isLogDateAfter = logDate.compareTo(end) > 0;
			return isLogDateAfter;
		}
	}

	public static class SingleDayStringCondition extends BwDateStringCondition {

		public SingleDayStringCondition(Date timeStart, Date timeEnd, IGetterDate<String> iGetterDate) {
			super(timeStart, timeEnd, iGetterDate);
		}

		public static SingleDayStringCondition build(Date day, IGetterDate<String> iGetterDate) {
			Date[] startEndDates = UTime7.getStartEndDatePeriodOf(day, Calendar.DAY_OF_MONTH);
			return new SingleDayStringCondition(startEndDates[0], startEndDates[1], iGetterDate);
		}

	}

	public static class MsAgoStringCondition extends BwDateStringCondition {

		public MsAgoStringCondition(Date timeStart, Date timeEnd, IGetterDate<String> iGetterDate) {
			super(timeStart, timeEnd, iGetterDate);
		}

		public static MsAgoStringCondition build(Date dayEnd, long msAgo, IGetterDate<String> iGetterDate) {
			Date dateAgo = new Date(dayEnd.getTime() - msAgo);
			return new MsAgoStringCondition(dateAgo, dayEnd, iGetterDate);
		}

	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ":" + super.toString();
	}

	public abstract static class IGetterDate<T> {
		public abstract Date getDateFrom(T from, Date... defRq);

		public abstract Date toDate(Date end);

		public abstract String toString(Date end, String... defRq);

	}

}
