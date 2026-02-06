package zk_notes.control.maintbx.mvelconsole;

import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.VariableResolverFactory;
import zk_notes.control.maintbx.shconsole.AbstractConsolePanel;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MvelConsolePanel extends AbstractConsolePanel {

	private static final long serialVersionUID = 1L;

	private Map<String, Object> contextEnv = null;

	/**
	 * 0-print task out, 1 - print init context , 2 - print result context
	 */
	private int out_show_code = 0;
	public int out_show_code1 = 1;

	/**
	 * last created task
	 */
	private MvelTask<?> task = null;

	public Map<String, Object> getContextInit() {
		if (contextEnv == null) {
			contextEnv = new LinkedHashMap<String, Object>();
		}
		return contextEnv;
	}

	public void setContextInit(Map<String, Object> contextEnv) {
		this.contextEnv = contextEnv;
	}

	/**
	 * Run code
	 */
	@Override
	protected void onClickButton_RUN() {

		String cmd = getComIn().getText();

		if (cmd == null || cmd.trim().isEmpty()) {
			return;
		}

		task = MvelTask.createTask(cmd);

		task.putContextInit(getContextInit());

		task.exec();

		fillOutputPanel();

	}

	private void fillOutputPanel() {
		if (task.hasException()) {
			renderResultError(task);
		} else {
			renderResultInfo(task);
		}
		out_show_code = 0;

	}

	/**
	 * Print context
	 */
	@Override
	protected void onClickButton_HISTORY() {
		if (task == null) {
			out_show_code = 1;
		}
		switch (++out_show_code) {
			case 0:
				fillOutputPanel();
				break;
			case 1:
				setOut("result", buildStringContextResult());
				break;
			case 2:
				setOut("init", buildStringContextInit(getContextInit()));
				out_show_code = -1;
				break;
		}
	}

	/**
	 * Build context's
	 */
	private String buildStringContextResult() {
		MvelTask curTask = MvelTask.getHistoryFirst();
		VariableResolverFactory context = curTask.getContextResult();
		Set<String> vars = context.getKnownVariables();
		if (vars.isEmpty()) {
			return "ResultContext is empty";
		}
		StringBuilder sb = new StringBuilder();
		for (String var : vars) {
			VariableResolver varResolver = context.getVariableResolver(var);
			sb.append(var).append(" = ").append(varResolver.getValue()).append("[").append(varResolver.getType())
					.append("]").append("\n");
		}
		return sb.toString();
	}

	private String buildStringContextInit(Map<String, Object> context) {
		// MvelTask curTask = MvelTask.getHistoryFirst();
		// Map<String, Object> context = curTask.getContextInit();
		if (context.isEmpty()) {
			return "InitContext is empty";
		}
		StringBuilder sb = new StringBuilder();
		for (String varName : context.keySet()) {
			Object varValue = context.get(varName);
			String superClassName = null;
			if (MvelTask.VARNAME_THIS.equals(varName) && varValue != null) {
				Class superClass = varValue.getClass().getSuperclass();
				if (!superClass.equals(Object.class)) {
					superClassName = superClass.getName();
				}
			}
			sb.append(varName).append(" = ").append(varValue).append("[")
					.append(varValue == null ? "null" : varValue.getClass()).append("]");
			if (superClassName != null) {
				sb.append(", parent [").append(superClassName).append("]");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * Render results
	 */
	private void renderResultInfo(MvelTask task) {
		Object obj = task.getReturnedResult();
		Class clas = obj.getClass();
		String val = String.valueOf(obj);
		super.setOut(clas.getName(), val);
	}

	private void renderResultError(MvelTask task) {
		String exMessage = task.getException().getMessage();
		String strStackTrace = stacktrace2string(task.getException());
		super.setOut(task.getException().getClass().getName(), strStackTrace);
	}

	private static String stacktrace2string(Exception exception) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		String strStackTrace = sw.toString();
		return strStackTrace;
	}

}
