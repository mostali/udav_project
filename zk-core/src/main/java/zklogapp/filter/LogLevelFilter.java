package zklogapp.filter;

import lombok.RequiredArgsConstructor;
import mpe.core.UBool;
import mpc.log.Lev;
import mpc.ui.ColorTheme;
import mpe.logs.filter.ILogFilter;
import mpe.logs.filter.filters.LineLevelCondition;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Cb;
import zk_com.base_ctr.Span0;
import zk_page.ZKS;
import zklogapp.ALI;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class LogLevelFilter extends Span0 implements ILogFilter {

	public final Map<Lev, Boolean> state = new HashMap<>();

	Cb cbOnOff;

	@Override
	protected void init() {
		super.init();

		initState();

		CbLogLevel[] cbs = new CbLogLevel[5];

		appendChild(cbOnOff = (Cb) new Cb(ALI.APPPLY).title("On/Off Log Level Filter"));
		cbOnOff.setChecked(true);
		cbOnOff.onDefaultAction(Events.ON_CLICK, event -> {
			Arrays.stream(cbs).forEach(c -> c.setChecked(cbOnOff.isChecked()));
			state.clear();
			LogLevelFilter.this.onDefaultActionEvent((Event)event);
		});

		appendChild(cbs[0] = (CbLogLevel) new CbLogLevel(Lev.TRACE, state, cbOnOff).onDefaultAction(Events.ON_CLICK, event -> LogLevelFilter.this.onDefaultActionEvent((Event)event)));
		appendChild(cbs[1] = (CbLogLevel) new CbLogLevel(Lev.DEBUG, state, cbOnOff).onDefaultAction(Events.ON_CLICK, event -> LogLevelFilter.this.onDefaultActionEvent((Event)event)));
		appendChild(cbs[2] = (CbLogLevel) new CbLogLevel(Lev.INFO, state, cbOnOff).onDefaultAction(Events.ON_CLICK, event -> {
			LogLevelFilter.this.onDefaultActionEvent((Event)event);
		}));
		appendChild(cbs[3] = (CbLogLevel) new CbLogLevel(Lev.WARN, state, cbOnOff).onDefaultAction(Events.ON_CLICK, event -> LogLevelFilter.this.onDefaultActionEvent((Event)event)));
		appendChild(cbs[4] = (CbLogLevel) new CbLogLevel(Lev.ERROR, state, cbOnOff).onDefaultAction(Events.ON_CLICK, event -> LogLevelFilter.this.onDefaultActionEvent((Event)event)));

		ZKS.FLOAT(this, false);
		ZKS.BGCOLOR(this, ColorTheme.GREEN[0]);

	}

	protected void initState() {
		Arrays.stream(Lev.values()).forEach(l -> state.put(l, true));
	}

	public class CbLogLevel extends Cb {
		public CbLogLevel(Lev level, Map state, Cb cbOnOff) {
			super(level.name().toUpperCase());
			setChecked(UBool.isTrue(state.get(level)));
			onCLICK((SerializableEventListener) event -> {
				boolean checked = isChecked();
				if (checked) {
					cbOnOff.setChecked(true);//update onOff state
					if (state.isEmpty()) {
						Arrays.stream(Lev.values()).forEach(i -> state.put(i, false));//init common state
						state.put(level, true);
					}
				}
				state.put(level, checked);//init specific state - !!! after reinit map-state !!!
				LogLevelFilter.this.onDefaultActionEvent(event);
			});
		}
	}

	@Override
	public LineLevelCondition toFilter() {
		return !cbOnOff.isChecked() ? null : new LineLevelCondition(state);
	}


}
