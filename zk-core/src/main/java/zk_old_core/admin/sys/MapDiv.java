package zk_old_core.admin.sys;

import org.zkoss.zk.ui.Page;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;

import java.util.Map;

/**
 * @author dav 10.01.2022   01:27
 */
public class MapDiv extends Div {

	private final String name;
	private final Map<Object, Object> map;

	public MapDiv(String name, Map<Object, Object> map) {

		this.name = name;
		this.map = map;

//		onBuildComponent();
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		onBuildComponent();
		super.onPageAttached(newpage, oldpage);
	}

	protected void onBuildComponent() {
		if (map == null) {
			appendChild(new Html("<h5>Map '" + name + "' is empty</h5>"));
		} else {
			appendChild(new Html("<h3>Map '" + name + "', size: " + map.size() + "</h3>"));
		}
		for (Map.Entry<Object, Object> e : map.entrySet()) {
			Object key = e.getKey();
			Object value = e.getValue();
			String type = value == null ? "NULL" : value.getClass().getSimpleName();

			Div line = createCom(key, value, type);

			appendChild(line);

		}
	}

	private static Div createCom(Object key, Object value, String type) {
		Div mapPropertyComLine = new Div();
		mapPropertyComLine.appendChild(new Html("<b>" + key + "</b>"));
		mapPropertyComLine.appendChild(new Html("<i> ( " + type + " ) </i>"));
		mapPropertyComLine.appendChild(new Label(String.valueOf(value)));
		mapPropertyComLine.appendChild(new Html("<hr/>"));
		return mapPropertyComLine;
	}
}
