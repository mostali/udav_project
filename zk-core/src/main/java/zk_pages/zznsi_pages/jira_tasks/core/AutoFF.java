package zk_pages.zznsi_pages.jira_tasks.core;

import lombok.RequiredArgsConstructor;
import mpc.map.MAP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_com.base.Tbx;
import zk_notes.node_state.impl.PageState;
import zk_page.ZKSession;
import zk_page.ZkCookie;

import java.util.Map;

@RequiredArgsConstructor
//AutoFillField
public class AutoFF {

	public static final Logger L = LoggerFactory.getLogger(AutoFF.class);

	final PageCtx pageCtx;

	public void doAutoFillField(Tbx tbx, String keyName) {
		for (PageCtx.Type type : pageCtx.allowed) {
			IAutoFF iAutoFF = type.newAutoFF();
			if (iAutoFF.loadAndSetField(tbx, keyName)) {
				L.info("Apply autoFF '{}' for key '{}'", type, keyName);
				return;
			}
		}
	}

	public interface IAutoFF {
		boolean loadAndSetField(Tbx tbx, String keyName);
	}

	public static class AffPageState implements IAutoFF {
		final PageState pageState = PageState.get();

		public boolean loadAndSetField(Tbx tbx, String keyName) {
			String s = pageState.get(keyName, null);
			if (s == null) {
				return false;
			}
			tbx.setValue(s);
			return true;
		}
	}

	public static class AffSession implements IAutoFF {
		final Map session = ZKSession.getSessionAttrsMap();

		public boolean loadAndSetField(Tbx tbx, String keyName) {
			String s = MAP.getAsString(session, keyName, null);
			if (s == null) {
				return false;
			}
			tbx.setValue(s);
			return true;
		}
	}

	static class AffCookie implements IAutoFF {

		public boolean loadAndSetField(Tbx tbx, String keyName) {
			String cookieValue = ZkCookie.getCookieValue(keyName, null);
			if (cookieValue == null) {
				return false;
			}
			tbx.setValue(cookieValue);
			return true;
		}
	}
}
