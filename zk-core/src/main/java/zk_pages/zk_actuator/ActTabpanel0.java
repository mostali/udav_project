package zk_pages.zk_actuator;

import com.jayway.jsonpath.JsonPath;
import mpc.html.EHtml5;
import mpc.json.GsonMap;
import mpu.X;
import mpu.core.ARR;
import mpu.str.JOIN;
import mpu.str.SPLIT;
import mpu.str.STR;
import org.zkoss.zk.ui.Component;
import zk_com.base.Ln;
import zk_com.base.Xml;
import zk_com.base_ctr.Div0;
import zk_com.tabs.Tabpanel0;
import zk_form.notify.ZKI;
import zk_page.ZKME;
import zk_page.ZKS;
import zk_page.core.SpVM;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ActTabpanel0 extends Tabpanel0 {

	final ActRequest actRequest;
	final ActType actType;

	public ActTabpanel0(ActRequest actRequest, ActType actType) {
		super(actType.name());
		this.actRequest = actRequest;
		this.actType = actType;
	}

	public ActTabpanel0 build() {
		switch (actType) {
			case METRICS:
			case METRICS__:
				return buildMetrics();
			case BEANS:
				return buildBeans();
			default:
				return buildGsonMap();
		}
	}

	private ActTabpanel0 buildMetrics() {
		List<String> metricsNames = actRequest.getMetricsNames(ARR.EMPTY_LIST);
		for (String metricName : metricsNames) {
			Ln child = (Ln) new Ln(metricName).block();
			child.onCLICK((eventLn -> {
				try {
					ZKME.textReadonly(metricName, actRequest.getMetricsItemGm(metricName).toStringPrettyJson().toString(), true);
				} catch (Exception ex) {
					ZKI.alert(ex);
				}
			}));
			appendChild(child);
		}
		return this;
	}

	private ActTabpanel0 buildBeans() {
		Set<String> beanNames = actRequest.getBeanNames();
		for (String beanName : beanNames) {
			Ln child = (Ln) new Ln(beanName).block();
			child.onCLICK((eventLn -> {
				try {
					ZKME.textReadonly(beanName, actRequest.getBeanItemGm(beanName).toStringPrettyJson().toString(), true);
				} catch (Exception ex) {
					ZKI.alert(ex);
				}
			}));
			appendChild(child);
		}
		return this;
	}

	private ActTabpanel0 buildGsonMap() {
		onEventSelect(event -> {
//			if (!isInitedTab()) {
//				boolean cached = false;//need actual load
//				isInitedTab(cached);

			clearLazyTabContent();

			Component header = Xml.H(3, getCallerUrl());
			String json = getCallerGsonMap().toStringPrettyJson().toString();

			String jp = SpVM.get().getQuery().getFirstAsStr("jp", null);
			if (jp != null) {
				try {
					Object read = JsonPath.read(json, jp);
					if (read instanceof Collection) {
						Collection<Object> listJp = (Collection<Object>) read;
						json = EHtml5.i.with("Found [%s] items", X.sizeOf(listJp));
						json += STR.NL + JOIN.allByNL(listJp);
					} else {
						json = String.valueOf(read);
					}
				} catch (Exception ex) {
					ZKI.ViewType.BT_ERR.showView(ex.getMessage());
				}
			}

			Div0 child = Div0.buildMultilineDiv(SPLIT.allByNL(json));
			ZKS.enableDarkTheme(this);

			appendChildLazyTabContent(header);
			appendChildLazyTabContent(child);

//			prev.add(header);
//			prev.add(child);
//			}
		});
		return this;
	}

	private GsonMap getCallerGsonMap() {
		return actRequest.getActGm(actType);
	}

	private String getCallerUrl() {
		return actRequest.getActUrl(actType);
	}
}
