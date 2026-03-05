package mp.jira;

import lombok.Getter;
import lombok.SneakyThrows;
import mpc.env.APP;
import mpc.env.Env;
import mpc.env.EnvTlp;
import mpc.log.L;
import mpc.types.opts.SeqOptions;
import mpe.cmsg.std.JqlCallMsg;
import mpu.IT;
import mpu.Sys;
import mpu.X;
import mpu.core.ARR;
import mpu.core.ENUM;
import mpu.pare.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

public class JiraCliCreator {

	public static final Logger L = LoggerFactory.getLogger(JiraCliCreator.class);

	private final String username;
	//
	//
	private final String[] hlp;
	private final SeqOptions seqOpts;

	//
	private final @Getter JqlCallMsg callMsg;


	public JiraCliCreator(String[] opts, String[] hlp) {

		this.seqOpts = SeqOptions.of(opts);

		this.hlp = hlp;

		this.callMsg = null;

		this.username = null;
	}

	public JiraCliCreator(JqlCallMsg callMsg) {
		this.callMsg = callMsg;

		this.seqOpts = null;
		this.hlp = null;

		this.username = null;
	}

	public JiraCliCreator(String user) {
		this.callMsg = null;

		this.seqOpts = null;
		this.hlp = null;

		this.username = user;
	}

	public JiraCli byHlpUser() {
		JiraUser jiraUser = JiraUser.create(hlp[2], new String[]{hlp[0], hlp[1]});
		return jiraUser.toJiraClient();
	}

	//
	//
	public static JiraUser createByHlp(Object[] hlp) {
		IT.isLength(hlp, 3);
		return createByHlp(new String[]{hlp[0] + "", hlp[1] + "", hlp[2] + ""});
	}

	public static JiraUser createByHlp(String[] hlp) {
		switch (hlp.length) {
			case 0:
			case 1:
				String usr;
				if (hlp.length == 0) {
					usr = Env.getUserName();
					if (L.isInfoEnabled()) {
						L.info("Use default os user name '{}' for env app 'jira'", usr);
					}
				} else {
					usr = hlp[1];
					if (L.isInfoEnabled()) {
						L.info("Use in user name '%s' for env app 'jira'", usr);
					}
				}
				EnvTlp enTlp = EnvTlp.ofSysAcc("jira", usr);
				return JiraUser.create(enTlp.readHostWithPort(), ARR.of(enTlp.readLogin(), enTlp.readPass()));
			case 2:
				return JiraUser.create(Sys.PKS.getValueFirst(JiraCli.EPK_JIRA_URL).val(), ARR.of(hlp[0], hlp[1]));
			case 3:
				return JiraUser.create(hlp[2], ARR.of(hlp[0], hlp[1]));
			case 4:
				return JiraUser.create(hlp[2] + ":" + IT.isInt0(hlp[3]), ARR.of(hlp[0], hlp[1]));
			default:
				throw new IllegalArgumentException("Support only 2(login/pass) or 3|4(with url2jira|port) arguments");
		}
	}

	public static JiraUser createBy(SeqOptions opts) {
		if (opts.hasDouble("elp", false)) {
			return JiraUser.create(Sys.PKS.getValueFirst(JiraCli.EPK_JIRA_URL).val(), ARR.of(Sys.PKS.getValueFirst(JiraCli.EPK_JIRA_LOGIN).val(), Sys.PKS.getValueFirst(JiraCli.EPK_JIRA_PASS).val()));
		}
		Path tlp = opts.getSingleAs("tlp", Path.class, null);
		String[] hlp;
		if (tlp != null) {
			hlp = EnvTlp.ofFile(tlp).readAsHLP4_loginRq();
		} else {
			hlp = HLP.getHLP_withNulls(opts);
		}
		return createByHlp(hlp);
	}


	private enum HLP {
		l, p, h, z;

		public static String[] getHLP4(SeqOptions args) {
			return Arrays.stream(values()).map(String::valueOf).toArray(String[]::new);
		}

		public static String[] getHLP_withNulls(SeqOptions args) {
			String[] hlp4 = new String[4];
			for (HLP hlp_ : values()) {
				String single = args.getSingle(hlp_.name(), null);
				if (single == null) {
					return Arrays.stream(hlp4).filter(X::NN).toArray(String[]::new);
				}
				hlp4[ENUM.indexOf(hlp_)] = single;
			}
			return hlp4;
		}
	}

	public static JiraCli getOrCreate(String jiraUrl, String[] userLP) {
		JiraUser usr = JiraUser.create(jiraUrl, new String[]{userLP[0], userLP[1]});
		return usr.toJiraClient();
	}

	//
	//


	@SneakyThrows
	public JiraCli buildByUsername() {
		String org = APP.getAppOrg(null);
		JqlCallMsg callMsg0 = getCallMsg();
		if (org == null && callMsg0 != null) {
			org = callMsg0.getAppOrg();
		}
		IT.NN(org, "set app.org, callMsg %s", callMsg0);
		return createUsr_ByOrg(org, username).toJiraClient();
	}


	@SneakyThrows
	public static JiraUser createUsr_ByOrg(String org, String usrName) {
		L.info("createUsr_ByOrg org:{} & usr:{}", org, usrName);
		EnvTlp envTlp = EnvTlp.ofHlpOrg(org, usrName);
		return JiraUser.create(envTlp.readHost(), new String[]{envTlp.readLogin(), envTlp.readPass()});
	}

	public JiraCli buildByOpts() {
		return createBy(seqOpts).toJiraClient();
	}

	//enum contract
//	enum ECT {
//		jira_login("."), jira_pass("."), jira_host(".");
//
//		ECT(String name0) {
//			this.name0 = name0.replace("_", name0);
//		}
//
//		final String name0;
//
//	}

	public JiraCli buildByMsg() {
		Map headersMap = callMsg.getHeaders_MAP();
		Tuple<String> authUsr = Tuple.ofMap(headersMap, "auth.usr");
		if (authUsr.hasNN(0)) {
			JiraCliCreator jiraCliCreator = new JiraCliCreator(authUsr.get(0)) {
				@Override
				public JqlCallMsg getCallMsg() {
					return JiraCliCreator.this.callMsg;
				}
			};
			return jiraCliCreator.buildByUsername();
		} else {
			Tuple tuple = Tuple.ofMap(headersMap, "jira.login", "jira.pass", "jira.host");
			IT.state(X.notEmptyAllObj_Str_Cll_Num(tuple.obs), "set all cred [ %s , %s , %s ] args %s", "jira.login", "jira.pass", "jira.host", tuple);
			JiraUser byHlp = createByHlp(tuple.obs);
			return byHlp.toJiraClient();
		}
	}
}
