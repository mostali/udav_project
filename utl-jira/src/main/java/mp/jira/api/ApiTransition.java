package mp.jira.api;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Transition;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import mp.jira.JiraCli;
import mpc.exception.EException;
import mpu.X;
import mpu.core.ARR;
import mpu.str.ToString;

import java.util.List;
import java.util.Objects;

public class ApiTransition extends Api0 {

	public ApiTransition(JiraCli jiraCli) {
		super(jiraCli);
	}

	public Iterable<Transition> getAllTransitionOfIssue(Issue issue) {
		return rc().getIssueClient().getTransitions(issue).claim();
	}


	//oldApp
	//
	//
	//


	public interface IStandartTransitionId {
		long id();

		String nameru();

		static Issue getTaskById(long id, Iterable<Issue> tasks) {
			for (Issue issue : tasks) {
				if (isSameIssueId(id, issue)) {
					return issue;
				}
			}
			return null;
		}

		static Transition getTransitionById(long id, Iterable<Transition> transitions) {
			for (Transition transition : transitions) {
				if (isSameTransitionId(id, transition)) {
					return transition;
				}
			}
			return null;
		}

		static boolean isSameIssueId(long id, Issue issue) {
			return Objects.equals(id, issue.getStatus().getId());
		}

		static boolean isSameTransitionId(long id, Transition transition) {
			return Objects.equals(id, transition.getId());
		}
	}

	enum OrgSubtaskStandartTransitionId implements IStandartTransitionId {
		PAUSE(711, "Pause"), INWORK(3, "В работе"), CLOSE(2, "Close");

		final long id;
		final String nameru;

		OrgSubtaskStandartTransitionId(long id, String nameru) {
			this.id = id;
			this.nameru = nameru;
		}


		@Override
		public long id() {
			return id;
		}

		@Override
		public String nameru() {
			return nameru;
		}
	}

	public static class SubtaskControlEE extends EException {

		//		public static final int PAUSE_STATUS = 10009;
		public static final int PAUSE_TRANSITION = 711;

		//		public static final int START_STATUS = 3;
		public static final int START_TRANSITION = 4;

		//		public static final int CLOSE_STATUS = 6;
		public static final int CLOSE_TRANSITION = 2;

		//		public static final int OPEN_STATUS = 3;
		public static final int OPEN_REOPEN_TRANSITION = 3;

		public void runPauseTransition(JiraCli jiraCli, Issue issue) throws SubtaskControlEE {
			runTransition(jiraCli, issue, PAUSE_TRANSITION);
		}

		public void runStartTransition(JiraCli jiraCli, Issue issue) throws SubtaskControlEE {
			runTransition(jiraCli, issue, START_TRANSITION);
		}

		public void runCloseTransition(JiraCli jiraCli, Issue issue) throws SubtaskControlEE {
			runTransition(jiraCli, issue, CLOSE_TRANSITION);
		}

		public void runReopenTransition(JiraCli jiraCli, Issue issue) throws SubtaskControlEE {
			runTransition(jiraCli, issue, OPEN_REOPEN_TRANSITION);
		}

		public void runTransition(JiraCli jiraCli, Issue issue, int transitionId) throws SubtaskControlEE {
//			final long staus = issue.getStatus().getId();
//			if (allowedStatuses.length == 0) {
//				throw EErrors.ALLOWED_STATUS_IS_EMPTY.I();
//			} else if (!Arrays.asList(allowedStatuses).contains(staus)) {
//				throw EErrors.STATUS_IS_NOT_IN_ALLOWED_STATUSES.I("Issue [%s] with status[%s] is not in allowed [%s]", issue.getKey(), staus, Arrays.asList(allowedStatuses));
//			}
			ApiTransition apiTransition = jiraCli._Transition();

			Iterable<Transition> allTransitionOfIssue = apiTransition.getAllTransitionOfIssue(issue);

			List<Transition> ts = ARR.toList(allTransitionOfIssue);
			if (!isInTransition(transitionId, ts)) {
				throw EErrors.TRANSITION_IS_NOT_IN_ALLOWED.I("Issue [%s] with status[%s] is not in allowed [%s]", issue.getKey(), issue.getStatus(), ToString.toNiceString(ts));
			}
			TransitionInput transitionInput = new TransitionInput(transitionId);

			apiTransition.rc().getIssueClient().transition(issue, transitionInput).claim();
		}

		private static boolean isInTransition(int transitionId, List<Transition> transitions) {
			return transitions.stream().anyMatch(t -> t.getId() == transitionId);
		}

//
//		public static void start(JiraCli cli, Issue issue) throws SubtaskControlEE {
//			final long id = issue.getStatus().getId();
//			switch ((int) id) {
//				case PAUSE_STATUS: {
//					TransitionInput transitionInput = new TransitionInput(START_TRANSITION);
//					cli.getJiraRestClient().getIssueClient().transition(issue, transitionInput).claim();
//					break;
//				}
//				case CLOSE_STATUS:
//					throw EErrors.ISSUE_IS_CLOSED.I();
//
//				case START_STATUS:
//					throw EErrors.ALREADY_IN_START.I();
//				default:
//					throw new WhatIsTypeException("What is status ID? " + issue);
//			}
//		}
//
//		public static void stop(JiraCli cli, Issue issue) throws SubtaskControlEE {
//			final long id = issue.getStatus().getId();
//			switch ((int) id) {
//				case PAUSE_STATUS: {
//					throw EErrors.ALREADY_ON_PAUSE.I();
//				}
//				case CLOSE_STATUS:
//					throw EErrors.ISSUE_IS_CLOSED.I();
//
//				case START_STATUS:
//					TransitionInput transitionInput = new TransitionInput(PAUSE_TRANSITION);
//					cli.getJiraRestClient().getIssueClient().transition(issue, transitionInput).claim();
//					break;
//				default:
//					throw new WhatIsTypeException("What is status ID? " + issue);
//			}
//		}

		public enum EErrors {
			NOSTATUS, TRANSITION_IS_NOT_IN_ALLOWED;

			public SubtaskControlEE I() {
				return new SubtaskControlEE(this);
			}

			public SubtaskControlEE I(Throwable ex) {
				SubtaskControlEE er = new SubtaskControlEE(this, ex);
				return er;
			}

			public SubtaskControlEE I(String message) {
				SubtaskControlEE er = new SubtaskControlEE(this, new RuntimeException(message));
				return er;
			}

			public SubtaskControlEE I(String message, Object... args) {
				SubtaskControlEE er = new SubtaskControlEE(this, new RuntimeException(X.f(message, args)));
				return er;
			}
		}

		public SubtaskControlEE(EErrors error) {
			super(error);
		}

		public SubtaskControlEE(EErrors error, Throwable cause) {
			super(error, cause);
		}
	}


	public enum IssueTypeId {
		TASK(3), SUBTASK(5);
		Long status;

		IssueTypeId(long status) {
			this.status = status;
		}

		boolean is(Issue issue) {
			return status.equals(issue.getIssueType().getId());
		}
	}

	public enum IssyeStatusTypeId {
		OPEN(3), PAUSE(10009), CLOSE(6);
		Long status;

		IssyeStatusTypeId(long status) {
			this.status = status;
		}

		boolean is(Issue issue) {
			return status.equals(issue.getStatus().getId());
		}

	}
}
