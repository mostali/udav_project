package zk_os.quartz_srv;

import lombok.Getter;
import mpc.exception.IErrorsCollector;
import mpc.rfl.RFL;
import mpc.types.opts.SeqOptions;
import mpe.wthttp.CallMsg;
import mpu.IT;
import mpu.X;
import mpu.core.*;
import mpu.str.SPLIT;
import mpu.str.STR;
import mpu.str.USToken;
import org.quartz.Job;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class QzCallMsg extends CallMsg {

	public static final String PFX_QZJOB = "qzjob:";

	//	public final QzCallMsgType qzCallMsgType;
	public final Class<? extends Job> jobClassName;

	public final LinkedList<JobLinePattern> jobs;

	public static boolean isValidFirstLine(String msg) {
//		String line0 = ARRi.firstLine(msg);
		if (!STR.startsWith(msg, PFX_QZJOB, true)) {
			return false;
		}
//		return QzCallMsgType.of(USToken.first(line0, ":", 1, null), null) != null;
		return true;
	}

	public QzCallMsg(String fullMsg) {
		super(fullMsg, false);

		if (X.empty(linesMsg)) {
			jobClassName = null;
			jobs = null;
			addError("Empty qz msg");
			return;
		}

		if (!STR.startsWith(line0, true, PFX_QZJOB)) {
			addError("Except first line with starts %s", PFX_QZJOB);
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
				} else if (!Job.class.isAssignableFrom(jobClassName)) {
					addError("JobClassName '%s' not assignable from Job.class", jobClassNameStr);
				}
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


//	public IJdbcUrl getJdbcUrl(IJdbcUrl... defRq) {
//		try {
//
//			String[] login = USToken.two(trimCommentPfx(linesMsg.get(0)), ":");
//			IT.isEq(login[0], "login", "set line with login , e.g. --login:login");
//
//			String[] pass = USToken.two(trimCommentPfx(linesMsg.get(1)), ":");
//			IT.isEq(pass[0], "password", "set line with password, e.g. --password:password");
//
//			return IJdbcUrl.ofULP(ARR.as(line0, login[1], pass[1]));
//		} catch (Exception ex) {
//			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Illegal JdbcUrl from sql '%s'", linesMsg), defRq);
//		}
//	}
//
//	public String getSql(String... defRq) {
//		try {
//			return IT.notEmpty(getBody(), "set sql body");
//		} catch (Exception ex) {
//			return ARG.toDefThrow(() -> new RequiredRuntimeException("Illegal sql body", ex), defRq);
//		}
//	}


	public static QzCallMsg of(Path file, boolean... silent) {
		return of(RW.readContent(file), silent);
	}

	public static QzCallMsg of(String msg, boolean... silent) {
		QzCallMsg httpCallMsg = new QzCallMsg(msg);
		return (QzCallMsg) httpCallMsg.throwIsErr(ARG.isDefEqTrue(silent));
	}


	public static boolean isValid(String data) {
		return QzCallMsg.of(data, true).isValid();
	}


	public static class JobLinePattern implements IErrorsCollector {
		public final SeqOptions opts;

		final @Getter QDate targetDate;
		final @Getter Long msBefore, msAfter, everyMs;
		final @Getter String line;
		final @Getter String msg;

		private @Getter List<Throwable> errors;

//		@Override
//		public String toString() {
//			return line;
//		}

		@Override
		public String toString() {
			return "JobLinePattern{" +
					"opts=" + opts +
					", targetDate=" + targetDate +
					", msBefore=" + msBefore +
					", msAfter=" + msAfter +
					", line='" + line + '\'' +
					", msg='" + msg + '\'' +
					", errors=" + errors +
					'}';
		}

		public JobLinePattern(String line) {
			this.line = line;
			opts = SeqOptions.of(line);

			List<String> tokens = SPLIT.allBySpace(line);

			{
				QDate dateMark0;
				try {
					dateMark0 = QDate.ofMono14(ARRi.first(tokens, 0));
				} catch (Exception ex) {
					dateMark0 = null;
					addError(ex);
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
				this.msBefore = toMsTimePeriodMark(opts.getSingle("b", null));
				if (msBefore == null) {
					addError("Illegal parameter 'before date' as pattern with time mark, e.g. '-b 1d'");
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
			return UTime.MarkPeriodTimeConverter.convertToMs(markPeriodTime, null);
		}

		@Override
		public void addError(Throwable... ex) {
			if (errors == null) {
				errors = new LinkedList<>();
			}
			for (Throwable e : ex) {
				errors.add(e);
			}
		}

		public static JobLinePattern of(String line) {
			return new JobLinePattern(line);
		}

		public QDate getTargetDate(boolean beforeOrAfter) {
			return targetDate.addMilliSeconds(IT.isInt0(beforeOrAfter ? -getMsBefore() : getMsAfter()));
		}
	}

//	public enum QzCallMsgType {
//		TG;
//
//		public static QzCallMsgType of(String name, QzCallMsgType... defRq) {
//			return ENUM.valueOf(name, QzCallMsgType.class, true, defRq);
//		}
//	}
}
