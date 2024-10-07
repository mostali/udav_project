package zk_old_core.admin.sys;

import mpe.core.UBool;
import mpf.SimpleHandler;
import mpc.exception.NotifyMessageRtException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import zk_os.AppZos;
import zk_com.core.LazyBuilder;
import zk_old_core.admin.sys.tabs_old.HeadLazyTabpanel;
import zk_form.notify.ZKI_Window;
import zk_form.notify.ZKI_Modal;

/**
 * @author dav 09.01.2022   21:07
 */
public class ControlLazyTabpanel extends HeadLazyTabpanel {
	public ControlLazyTabpanel(Object tab, LazyBuilder... lazyBuilder) {
		super(tab, lazyBuilder);
	}

	private LazyBuilder<? extends ControlLazyTabpanel> lb = null;

	@Override
	public LazyBuilder<? extends ControlLazyTabpanel> getLazyBuilder() {
		if (lb != null) {
			return lb;
		}
		lb = new LazyBuilder<ControlLazyTabpanel>() {
			@Override
			public void buildAndAppend(ControlLazyTabpanel controlLazyTabpanel) throws Exception {

				controlLazyTabpanel.getChildren().clear();

				Button restart = new Button("Restart");
				restart.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
					@Override
					public void onEvent(Event event) throws Exception {
						ZKI_Modal.showMessageBoxBlueYN_RMM("Restart Application..", "Restart App ?", new SimpleHandler() {
							@Override
							public Object handle(Object input) throws Exception {
								if (!UBool.isTrue(input)) {
									return null;
								}
								AppZos.restart();
								ZKI_Window.info(NotifyMessageRtException.LEVEL.LOG.I("App was restarted"));
								controlLazyTabpanel.invalidate();
								return null;
							}
						});
					}
				});
				controlLazyTabpanel.appendChild(restart);
			}
		};

		return lb;
	}

}
