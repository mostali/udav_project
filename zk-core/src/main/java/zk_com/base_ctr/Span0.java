package zk_com.base_ctr;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zul.Span;
import zk_com.core.IZComExt;
import zk_com.core.IZHost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Span0 extends Span implements IZComExt {
	public static final Logger L = LoggerFactory.getLogger(Span0.class);

	//	@Getter
	@Setter
	private List<Component> coms;

	public List<Component> getComs() {
		return coms;
	}

	public static Span0 of(Component... coms) {
		return new Span0(coms);
	}

	public static Span0 of(List<Component> coms) {
		return new Span0(coms.toArray(new Component[coms.size()]));
	}

	public Span0(Component... coms) {
		this.coms = new ArrayList(Arrays.asList(coms));
	}


	protected boolean attachAll = true;

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		if (attachAll) {
			for (Component component : getComs()) {
				super.appendChild(component);
			}
		}
		super.onPageAttached(newpage, oldpage);
	}

	protected void init() {
	}

	@Override
	public boolean appendChild(Component child) {
		getComs().add(child);
		return super.appendChild(child);
	}

	private boolean revert = false;

	public Span0 isRevert(boolean revert) {
		this.revert = revert;
		return this;
	}

	@Override
	public boolean insertBefore(Component newChild, Component refChild) {
		if (revert) {
			int size = getComs().size();
			return super.insertBefore(newChild, getComs().get(size - (size > 1 ? 2 : 1)));
		}
		return super.insertBefore(newChild, refChild);
	}

	public void detachAll() {
		getComs().forEach(c -> c.detach());
	}
}
