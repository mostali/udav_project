package mpe.wthttp;

import lombok.Getter;
import mpc.rfl.RFL;
import mpc.types.opts.SeqOptions;
import mpf.CallLine;
import mpu.IT;
import mpu.X;
import mpu.core.*;
import mpu.str.SPLIT;
import mpu.str.STR;
import mpu.str.USToken;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class QzTaskMsg extends CallMsg {

	public static final String QZJOB = "qztask";
	public final Class<?> jobClassName;

	public final LinkedList<JobLinePattern> jobs;

	public static boolean isValidKeyFirstLine(String msg) {
		return STR.startsWith(msg, QZJOB + ":", true);
	}

	public QzTaskMsg(String fullMsg) {
		super(fullMsg, false);

		if (X.empty(linesMsg)) {
			jobClassName = null;
			jobs = null;
			addError("Empty qz msg");
			return;
		}

		if (!STR.startsWith(line0, true, QZJOB + ":")) {
			addError("Except first line with starts %s", QZJOB + ":");
		}

		{
			String jobClassNameStr = USToken.first(line0, ":", 1, null);
			if (X.empty(jobClassNameStr)) {
				jobClassName = null;
				addError("Set JobClassName");
			} else {
				this.jobClassName = RFL.clazz(jobClassNameStr, null);
				if (jobClassName == null) {
					addError("JobClassName '%s' not found", jobClassNameStr);
				}
//				if (!Job.class.isAssignableFrom(jobClassName)) {
//					addError("JobClassName '%s' not assignable from Job.class", jobClassNameStr);
//				}
			}
		}

		jobs = new LinkedList<>();

		linesMsg.forEach(l -> jobs.add(new JobLinePattern(l)));

		jobs.forEach(jlp -> addError(jlp.getErrors()));

		if (X.empty(jobs)) {
			addError("Set lines with jobs, e.g. '20250107200100 -msg myMsg -b 3d -a 3d -e 6h'");
		}
	}

	@Override
	public String toString() {
		return "QzCallMsg{" +
				"msg='" + fullMsg + '\'' +
				", line='" + line0 + '\'' +
				", state=" + state +
				", jobs=" + X.sizeOf0(jobs) +
				", errs=" + X.sizeOf0(getErrors()) +
				'}';
	}

	public static QzTaskMsg of(Path file) {
		return of(RW.readContent(file));
	}

	public static QzTaskMsg of(String msg) {
		return (QzTaskMsg) ofQk(msg).throwIsErr();
	}

	public static QzTaskMsg ofQk(String msg) {
		return new QzTaskMsg(msg);
	}

	public static boolean isValid(String data) {
		return QzTaskMsg.of(data).isValid();
	}

	public static class JobLinePattern extends CallLine {
		public final SeqOptions opts;

		final @Getter QDate targetDate;
		final @Getter Long msBefore, msAfter, everyMs;
		final @Getter String msg;

		@Override
		public String toString() {
			return "JobLinePattern{" +
					"opts=" + opts +
					", targetDate=" + targetDate +
					", msBefore=" + msBefore +
					", msAfter=" + msAfter +
					", msg='" + msg + '\'' +
					", \nerrors=" + getErrors() +
					",\nline='" + super.line0 + '\'' +
					'}';
		}

		public JobLinePattern(String line) {
			super(line);

			opts = SeqOptions.of(line);

			List<String> tokens = SPLIT.allBySpace(line);

			if (tokens.size() < 2) {
				addError("Set date + job args, e.g. >>> 2025-01-21 20:00:01 -msg myMsg -a 1d -b 1h -e 30m");
				targetDate = null;
				msBefore = null;
				msAfter = null;
				everyMs = null;
				msg = null;

				return;
			} else {
				QDate dateMark0 = QDate.ofIso(ARRi.first(tokens, 0) + " " + ARRi.first(tokens, 1), null);
				if (dateMark0 == null) {
					dateMark0 = QDate.ofMono14(ARRi.first(tokens, 0), null);
				}
				if (dateMark0 == null) {
					addError("Date format [ISO | MONO14] not found ");
				}
				this.targetDate = dateMark0;
			}


			{
				this.msg = opts.getSingle("msg", null);
				if (msg == null) {
					addError("Set parameter 'message' as pattern, e.g. '-m myMsg'");
				}
			}

			{
				Long msBefore = toMsTimePeriodMark(opts.getSingle("b", null));
				if (msBefore != null) {
					this.msBefore = msBefore;
				} else {
					this.msBefore = 0L;
					//addError("Illegal parameter 'before date' as pattern with time mark, e.g. '-b 1d'");
				}

			}
			{
				this.msAfter = toMsTimePeriodMark(opts.getSingle("a", null));
				if (msAfter == null) {
					addError("Illegal parameter 'after date' as pattern with time mark, e.g. '-a 1d'");
				}
			}
			{
				this.everyMs = toMsTimePeriodMark(opts.getSingle("e", null));
				if (everyMs == null) {
					addError("Illegal parameter 'repeat every' as pattern with time mark, e.g. '-e 1h'");
				}
			}
		}

		private Long toMsTimePeriodMark(String markPeriodTime) {
			return TimeMark.convertToMs(markPeriodTime, null);
		}


		public static JobLinePattern of(String line) {
			return new JobLinePattern(line);
		}

		public QDate getTargetDate(boolean beforeOrAfter) {
			return targetDate.addMilliSeconds(IT.isInt0(beforeOrAfter ? -getMsBefore() : getMsAfter()));
		}
	}

}
