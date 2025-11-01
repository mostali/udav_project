package mpc.console;

import mpu.X;
import mpu.core.ARRi;
import mpc.str.sym.SEP;
import mpu.str.UST;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public enum QuestAnswer {

	YES("y"), NO("n"), STOP("s"), NUMERIC("0-9"), CHARS("any symbols");

	//	private static void exInteger() {
	//		QA ans = QUEST("Hello?", NUMERIC.setValidator(IntegerValidator.range(0, 11)));
	//		p("Answer [" + ans.getAnswerAsString() + "]");
	//
	//	}
	//
	//	private static void exCHARS() {
	//		QA ans = QUEST("Any symbols?", NUMERIC.setValidator(IntegerValidator.range(0, 11)));
	//		p("Answer [" + ans.getAnswerAsString() + "]");
	//	}
	//

	public static class RangePair {

		private final Integer per, sec;

		public RangePair(Integer per, Integer last) {
			this.per = per;
			this.sec = last;
			if (per == null || sec == null) {
				throw new NullPointerException();
			}
		}

		public Integer getPer() {
			return per;
		}

		public Integer getSec() {
			return sec;
		}

	}

	public interface IValidator {

		boolean validate(String s);

	}

	public static class AbstractValidator<T> implements IValidator {

		public AbstractValidator() {
		}

		@Override
		public boolean validate(String s) {
			return false;
		}

	}

	public static class IntegerValidator extends AbstractValidator<Integer> implements IValidator {

		private RangePair _rangePair;

		public IntegerValidator() {
		}

		public IntegerValidator setRangePair(RangePair rangePair) {
			this._rangePair = rangePair;

			if (_rangePair == null || rangePair.getPer() == null || rangePair.getSec() == null) {
				throw new NullPointerException();
			}

			return this;
		}

		@Override
		public boolean validate(String s) {

			Integer i = UST.INTany(s, null);

			if (i == null) {
				return false;
			}
			if (i >= _rangePair.getPer() && i <= _rangePair.getSec()) {
				return true;
			}
			return false;
		}

		public static IntegerValidator range(int min, int max) {
			return new IntegerValidator().setRangePair(new RangePair(min, max));
		}
	}

	private final String shortString;

	private String answerStringObject;

	private String answerText;

	private AbstractValidator<? extends Object> validator;

	public QuestAnswer setValidator(AbstractValidator validator) {
		this.validator = validator;
		return this;
	}

	public IValidator getValidator() {
		return this.validator;
	}

	private QuestAnswer(String shortString) {
		this.shortString = shortString;
	}

	public String getAnswerName() {
		return name();
	}

	public String getShortName() {
		return shortString;
	}

	public boolean matches(String s) {

		IValidator validator = getValidator();

		if (QuestAnswer.NUMERIC == this) {

			try {
				if (validator != null) {

					return validator.validate(s);

				} else {

					int i = Integer.valueOf(s);

					return true;
				}
			} catch (NumberFormatException ex) {
				return false;
			}

		} else if (QuestAnswer.CHARS == this) {

			if (validator != null) {
				return validator.validate(s);
			}

			return (s != null && !s.isEmpty());

		} else {

			s = s.toUpperCase();

			try {

				if (s.equalsIgnoreCase(getShortName())) {
					return true;
				} else if (QuestAnswer.valueOf(s) == this) {
					return true;
				} else {
					return false;
				}

			} catch (IllegalArgumentException ex) {
				return false;
			}
		}

	}

	public static QuestAnswer getAnswer(QuestAnswer[] answers, String answer) {

		if (answer == null) {
			return null;
		}

		for (QuestAnswer a : answers) {
			if (a.matches(answer)) {
				return a.setAnswerObject(answer);
			}
		}

		return null;

	}

	public static QuestAnswer getAnswer(Integer[] range, String answer) {
		if (answer == null) {
			return null;
		}
		try {
			int answ = Integer.parseInt(answer);
			return (answ < range[0] || answ > range[1]) ? null : QuestAnswer.NUMERIC.setAnswerObject(answer);
		} catch (NumberFormatException ex) {
			return null;
		}

	}

	public String getAnswerAsString() {
		return this.answerStringObject;
	}

	public Integer getAnswerAsInteger() {
		return UST.INTany(getAnswerAsString(), null);
	}

	public boolean getAnswerAsBoolean() {
		return QuestAnswer.YES.equals(getAnswerAsString());
	}

	public QuestAnswer setAnswerObject(String answer) {
		this.answerStringObject = answer;
		return this;
	}

	public static CharSequence toString(QuestAnswer... answers) {

		if (answers == null || answers.length == 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("[ ");

		next:
		for (QuestAnswer a : answers) {
			switch (a) {

				case YES: {

					String text = (a.getPossibleAnswer() == null) ? a.getShortName() : a.getPossibleAnswer();

					appendStringAnswer(sb, a, "(", text, "), ");

					continue next;
				}
				case NO: {

					String text = (a.getPossibleAnswer() == null) ? a.getShortName() : a.getPossibleAnswer();

					appendStringAnswer(sb, a, "(", text, "), ");

					continue next;
				}
				case STOP: {

					String text = (a.getPossibleAnswer() == null) ? a.getShortName() : a.getPossibleAnswer();

					appendStringAnswer(sb, a, "(", text, "), ");

					continue next;
				}
				case NUMERIC: {

					String text = (a.getPossibleAnswer() == null) ? a.getShortName() : a.getPossibleAnswer();

					appendStringAnswer(sb, "(", text, "), ");

					continue next;
				}
				case CHARS: {

					String text = (a.getPossibleAnswer() == null) ? a.getShortName() : a.getPossibleAnswer();

					if (text.trim().isEmpty()) {
						appendStringAnswer(sb, ", ");
					} else {
						appendStringAnswer(sb, "(", text, "), ");
					}

					continue next;
				}
			}
		}

		sb.delete(sb.length() - 2, sb.length());

		sb.append(" ]");

		return sb;
	}

	private static CharSequence appendStringAnswer(StringBuilder sb, Object... args) {

		for (Object a : args) {
			sb.append(a);
		}

		return sb;
	}

	public static String QUEST(String quest) {
		p(HEAD(quest));
		String ans = null;
		try {
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(System.in));
			ans = bufReader.readLine();
		} finally {
			return ans;
		}
	}

	private static String HEAD(String hqadQuest) {
		return SEP.HOUSE.toLineSize(7) + " " + hqadQuest;
	}

	private static void p(String s) {
		System.out.println(s);
	}

	public static Enum QUEST(String quest, Enum... answers) {
		if (answers.length == 0) {
			throw new IllegalStateException("Set answers");
		}
		String answer = QUEST(quest, Arrays.stream(answers).map(Enum::name).toArray(String[]::new));
		for (Enum enu : answers) {
			if (enu.name().equals(answer)) {
				return enu;
			}
		}
		throw new IllegalStateException("Wrong loagic with anwser ::: " + answer + " ::: unknown enum");
	}

	public static String QUEST(String quest, String... answers) {
		return QUEST(quest, Arrays.asList(answers));
	}

	public static String QUEST(String quest, Iterable<String> answers) {
		StringBuilder sb = new StringBuilder();
		int last = -1;
		for (String answer : answers) {
			sb.append(++last).append(") ").append(answer).append("\n");
		}
		p(HEAD(quest));
		p(sb.toString());
		BufferedReader bufReader = new BufferedReader(new InputStreamReader(System.in));
		try {
			while (true) {
				String inputString = bufReader.readLine();
				Integer ind = UST.INT(inputString,null);
				if (ind == null) {
					continue;
				}
				String el = ARRi.item(answers, ind);
				if (el == null) {
					continue;
				}
				return el;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static QuestAnswer QUEST(String quest, QuestAnswer... answers) {
		p(HEAD(quest + QuestAnswer.toString(answers)));
		BufferedReader bufReader = new BufferedReader(new InputStreamReader(System.in));
		QuestAnswer answerObject = null;
		try {
			while (true) {
				String inputString = bufReader.readLine();
				answerObject = QuestAnswer.getAnswer(answers, inputString);
				if (answerObject != null) {
					return answerObject;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	public String getPossibleAnswer() {
		return answerText;
	}

	public QuestAnswer setPossibleAnswer(String answerText) {
		this.answerText = answerText;
		return this;
	}

	public static boolean CONTINUE_YN() {
		return CONTINUE_YN("Continue?");
	}

	public static boolean CONTINUE_YN(String message, Object... args) {
		message = args.length == 0 ? message : X.f(message, args);
		QuestAnswer ans = QuestAnswer.QUEST(message, QuestAnswer.YES, QuestAnswer.NO);
		return ans.YES.getShortName().equals(ans.getAnswerAsString());
	}

	public static boolean CONTINUE_YN_12(String message, Object... args) {
		message = args.length == 0 ? message : X.f(message, args);
		QuestAnswer answer = null;
		do {
			String ans = QuestAnswer.QUEST(message + "\n1) Yes\n2) No", QuestAnswer.NUMERIC).getAnswerAsString();
			answer = QuestAnswer.getAnswer(new Integer[]{1, 2}, ans);
		} while (answer == null);
		return answer.getAnswerAsInteger() == 1;
	}

	public static Boolean CONTINUE_YNS_123(String message, Object... args) {
		message = args.length == 0 ? message : X.f(message, args);
		QuestAnswer answer = null;
		do {
			String ans = QuestAnswer.QUEST(message + "\n1) Yes\n2) No\n3) Stop", QuestAnswer.NUMERIC).getAnswerAsString();
			answer = QuestAnswer.getAnswer(new Integer[]{1, 3}, ans);
		} while (answer == null);
		Integer ans = answer.getAnswerAsInteger();
		if (ans == 3) {
			return null;
		}
		return answer.getAnswerAsInteger() == 1;
	}
}
