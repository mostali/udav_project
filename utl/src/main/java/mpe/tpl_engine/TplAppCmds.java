package mpe.tpl_engine;

import mpc.exception.FIllegalStateException;
import mpc.types.opts.Cmmd;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import mpu.str.Sb;
import mpu.str.TKN;
import mpu.str.UST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class TplAppCmds {

	public static final Logger L = LoggerFactory.getLogger(TplAppCmds.class);

	public static class TplByNumAppCmd extends AppCmds.RegexAppCmd<String> {
		public TplByNumAppCmd() {
			super("cat::\\d+", "Показать содержимое шаблона по номеру","cat::1");
		}

		@Override
		public String run(String cmd) {

			Integer num = TKN.last(cmd, "::", Integer.class);

			Sb sb = new Sb();

			sb.NL("Шаблон:" + num);

			List<TplEngine.Tpl> listTpl = TplEngine.findTplsWith_Sql_LIST();

			checkTplExistedIndex(num, listTpl);

			TplEngine.Tpl tpl = listTpl.get(num);

			sb.NL(tpl.getContentTemplate());

			return sb.toString();
		}
	}

	public static class TplTxtAppCmd extends AppCmds.NamedAppCmd<String> {

		public TplTxtAppCmd() {
			super("::*", "Показать все шаблоны");
		}

		@Override
		public String run(String cmd) {
			Sb sbTotal = new Sb();

			List<TplEngine.Tpl> listTpl = TplEngine.findTplsWith_Sql_LIST();

			for (int i = 0; i < listTpl.size(); i++) {
				TplEngine.Tpl tpl = listTpl.get(i);
				Sb sb = toStringReadme(i, tpl);
				sbTotal.append(sb);
				sbTotal.NL();
			}

			return sbTotal.toString();
		}

		private Sb toStringReadme(int i, TplEngine.Tpl tpl) {
			Sb sb = new Sb();
			String cmd_1 = AppCmds.SYM_CMD + i + "/" + tpl.getTplFilename() + " ";
			String sore_2 = AppCmds.SYM_STORE + "[" + tpl.getTplSrc().getTplSrcFilename() + "] ";
			String gr_3 = AppCmds.SYM_GROUPS + "" + tpl.getTplCnt().getGroups();
			String symCmd = cmd_1 + sore_2 + gr_3;
			sb.NL(symCmd);
			List<String> comment = tpl.toStringComments();
			if (X.empty(comment)) {
				//
			} else {
				comment = X.empty(comment) ? ARR.as("") : comment;
				comment.forEach(sb::NL);
			}
			return sb;
		}


	}

	public static class LsAppCmd extends AppCmds.NamedAppCmd<String> {
		public LsAppCmd() {
			super("::?", "Показать все команды");
		}

		@Override
		public String run(String cmd) {
			Sb sb = new Sb("Шаблоны команд:\n");
			for (Map.Entry<AppCmds.CmdTuple, AppCmds.AppCmd> e : AppCmds._CMDS.entrySet()) {
				AppCmds.CmdTuple tuple = e.getKey();
				AppCmds.AppCmd appCmd = e.getValue();
				String exStr = appCmd.isRegex() ? AppCmds.SYM_EX + "ПРИМЕР[" + tuple.example("no-example") + "]" : "";
				String key = appCmd.isRegex() ? "REGEX[" + tuple.key() + "]" : "[" + tuple.key() + "]";
				String line = AppCmds.SYM_CMD + " " + tuple.desc("no-desc") + AppCmds.SYM_EX + key + "" + exStr;
				sb.NL(line);
				sb.NL();
			}
			return sb.toString();
		}
	}

	//::1 -arg 123
	public static class SqlWithArgsAppCmd_ByNum extends AppCmds.RegexAppCmd<AppRun.Out> {

		public SqlWithArgsAppCmd_ByNum() {
			super("::\\d+\\s+.+", "Запустить шаблон по индексу с аргументами", "::5 -key val");
		}

		@Override
		public AppRun.Out run(String cmd) {

			cmd = cmd.substring(2);

			String[] num_i_cmd = TKN.two(cmd, " ");

			Integer num = UST.INT(num_i_cmd[0]);
			Cmmd cmmd = Cmmd.of(num_i_cmd[1]);

			List<TplEngine.Tpl> listTpl = TplEngine.findTplsWith_Sql_LIST();

			checkTplExistedIndex(num, listTpl);

			TplEngine.Tpl tpl = listTpl.get(num);

			return TplEngine.runSql(cmmd, tpl);
		}
	}

	//::1 -arg 123
	public static class SqlWithArgsAppCmd extends AppCmds.RegexAppCmd<AppRun.Out> {

		public SqlWithArgsAppCmd() {
			super("-\\w+\\d?\\s+.+", "Запустить шаблон с подходящими аргументами", "-key val");
		}

		@Override
		public AppRun.Out run(String cmd) {

			Cmmd cmmd = Cmmd.of(cmd);

			List<TplEngine.Tpl> listTpl = TplEngine.findTplsWith_Sql_LIST(t -> {
				List<String> groups = t.getTplCnt().getGroups();
				if (X.empty(groups)) {
					return false;
				}
				return TplEngine.NeedleMode.STRICT_OUT.validate(cmmd.keysSingly(), groups, true);
			});

			IT.state(listTpl.size() == 1, "Found many tpl::%s", listTpl);

			TplEngine.Tpl tpl = listTpl.get(0);

			AppRun.Out out = TplEngine.runSql(cmmd, tpl);

			return out;

		}
	}

	public static class SqlNoArgsAppCmd extends AppCmds.RegexAppCmd<AppRun.Out> {

		public SqlNoArgsAppCmd() {
			super("::\\d+", "Запустить шаблон по индексу", "::3");
		}

		@Override
		public AppRun.Out run(String cmd) {

			Integer num = TKN.last(cmd, "::", Integer.class);

			List<TplEngine.Tpl> listTpl = TplEngine.findTplsWith_Sql_LIST();

			checkTplExistedIndex(num, listTpl);

			TplEngine.Tpl tpl = listTpl.get(num);
			List<String> groups = tpl.getTplCnt().getGroups();
			if (X.notEmpty(groups)) {
				throw new FIllegalStateException("Tpl [%s] need args >> %s", tpl.getTplFilename(), groups);
			}


			return TplEngine.runSql(Cmmd.of(""), tpl);

		}
	}

	public static void checkTplExistedIndex(Integer num, List<TplEngine.Tpl> listTpl) {
		if (ARR.isNotIndex(num, listTpl)) {
			throw new FIllegalStateException("Шаблон [%s] не найден. Укажите существующий номер шаблона. Найдено всего [%s]", num, X.sizeOf0(listTpl));
		}
	}
}
