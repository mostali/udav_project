package zk_form.events;

import lombok.RequiredArgsConstructor;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.util.Clients;

@RequiredArgsConstructor
public class RedirectHrefEvent implements SerializableEventListener {

	final String href;
	final boolean blank;

	@Override
	public void onEvent(Event event) throws Exception {
		String js;
		if (blank) {
			js = getJavaScript_WO_BLANK_MODAL(href);
		} else {
			js = getJavaScript_Location(href);
		}
		Clients.evalJavaScript(js);
	}

	public static String getJavaScript_Location(String href) {
		return "window.location.href = \"" + href + "\";";
	}

	public static String getJavaScript_WO_BLANK(String href) {
		return "window.open('" + href + "', '_blank');";
	}

	public static String getJavaScript_WO_BLANK_MODAL(String href) {
		String args = "'location=yes,height=570,width=520,scrollbars=yes,status=yes'";
		return "window.open('" + href + "', '_blank', " + args + " );";
	}
}
