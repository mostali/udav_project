package mp.jira;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mpc.net.CON;
import mpu.IT;

@RequiredArgsConstructor
@Getter
public class JiraUser {

	private final String url2jira;
	private final String login;
	private final String pass;

	public static JiraUser create(String urlToJira, String[] loginPass) {
		IT.isLength(loginPass, 2, "For create Jira Client need 3 arguments (login, password, url2jira).");
		return new JiraUser(urlToJira, loginPass[0], loginPass[1]);
	}

	@Override
	public String toString() {
		return "JiraUser{" + "login='" + login + '\'' + ", pass='" + "*****" + '\'' + ", url2jira='" + url2jira + '\'' + '}';
	}

	public String[] getAuthBasic() {
		return CON.HEADER_AUTH_BASIC(new String[]{getLogin(), getPass()});
	}

	public JiraCli toJiraClient() {
		return new JiraCli(this);
	}
}
