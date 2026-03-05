package mp.jira.api;

import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.util.concurrent.Promise;
import lombok.SneakyThrows;
import mp.jira.Issue0;
import mp.jira.JiraCli;
import mpc.arr.S_;
import mpc.exception.WhatIsTypeException;
import mpu.IT;
import mpu.core.ARR;
import mpu.str.JOIN;

import java.util.*;

public class ApiJql extends Api0 {

	public ApiJql(JiraCli jiraCli) {
		super(jiraCli);
	}

	public Iterable<Issue> getAllDefaultTasksByProjects(List<String> projects) {
		String project = "project in (" + IT.NE(JOIN.allByComma(projects), "set projects") + ")";
		String status = "status in (Open, \"In Progress\", Resolved, Design, \"In Development\", \"In Build\", Tested, Analysis, Testing, Formed, \"Information Required\", Realization, Correction, \"Запрос информации\", Delay, \"В ожидании\", Пауза, CodeReview, \"Ready in build\", Blocked)";
		String issuetype = "issuetype in (Improvement, Task, \"Задача ДТА\", \"Request management\", \"Change request\", Request, \"Component Improvement\", Bug, Patch)";
		return getAllTasks(project, issuetype, status);
	}

	public Iterable<Issue> getAllTasks(String project, String issuetype, String status) {
		return getAllTasksByJql("assignee=currentuser() AND " + status + " AND " + project + " AND " + issuetype);
	}

	public Issue getWorkSubTask() {
		return getAllWorkSubTasks().iterator().next();
	}

	public Iterable<Issue> getAllWorkSubTasks() {
		return getAllTasksByJql("assignee=currentuser() AND issuetype = Sub-task AND Status=3");
	}

	@SneakyThrows
	public Iterable<Issue> getAllTasksByJql(String jql) {
		return getAllTasksByJql(jql, Issue.class);
	}

	public <T> Iterable<T> getAllTasksByJql(String jql, Class<T> asType) {
		Iterable tasks = loadAllTasksByJql(jql);
		if (asType == Issue.class) {
			return tasks;
		} else if (asType == Issue0.class) {
			return S_.itToList(tasks, Issue0::of);
		}
		throw new WhatIsTypeException(asType);
	}

	@SneakyThrows
	private Iterable<Issue> loadAllTasksByJql(String jql) {
		Promise<SearchResult> filters = jc.getRestClient().getSearchClient().searchJql(jql);
		SearchResult result = filters.get();
		if (result.getTotal() == 0) {
			return Collections.EMPTY_LIST;
		} else {
			return result.getIssues();
		}
	}


}
