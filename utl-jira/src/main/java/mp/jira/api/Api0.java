package mp.jira.api;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Transition;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import lombok.RequiredArgsConstructor;
import mp.jira.JiraCli;
import mpc.exception.EException;
import mpu.X;
import mpu.core.ARR;
import mpu.str.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class Api0 {

	public static final Logger L = LoggerFactory.getLogger(Api0.class);

	public final JiraCli jc;

	public JiraRestClient rc() {
		return jc.getRestClient();
	}

}
