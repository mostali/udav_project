package zk_old_core.std;

import mpc.exception.WhatIsTypeException;
import org.zkoss.zk.ui.Component;
import zk_com.base.Css;
import zk_com.base.Js;
import zk_com.base.Pre;
import zk_old_core.std_core.CType;
import zk_old_core.mdl.FdModel;
import zk_page.ZulLoader;

import java.nio.file.Path;

public class AbsRsrcVF extends AbsVF {

	public AbsRsrcVF(FdModel fdm) {
		super(fdm);
	}

	@Override
	public String[] getAllowedExt() {
		return new String[]{ctype().name()};
	}

	@Override
	protected void initImpl() throws Exception {

		CType ctype = ctype();
		Path path = path();

		for (Path child : getRootChilds()) {
			switch (getViewMode()) {
				case view:
//					appendChild(Xml.buildComponentFromFile(child));
//					break;
				case edit:
					Component com;
					switch (ctype) {
						case ZUL:
							com = ZulLoader.buildComponentFromFile(child);
							break;
						case CSS:
							com = Css.buildComponentFromFile(child);
							break;
						case JS:
							com = Js.buildComponentFromFile(child);
						case SH:
							com = Pre.buildComponentFromFile(child);
							break;
						default:
							throw new WhatIsTypeException(ctype);
					}

					appendChild(com);

					break;

				case error:
				default:
					appendLb("Empty:" + name());
					break;

			}
		}


	}

//	private void addContextItem_REMOVE(SpanCtx parent) {
//		Menuitem rmm = new Menuitem("Remove");
//
//
//		rmm.addEventListener(Events.ON_CLICK, new EventRmmForm(path()));
//		parent.addContextMenuItem(rmm);
//	}

//	private void addContextItem_HIGHLIGHT(SpanCtx parent) {
//		Menuitem higlight = new Menuitem("Highlight");
//		higlight.addEventListener(Events.ON_CLICK, new EventHighlightForm(parent, false));
//		parent.addContextMenuItem(higlight);
//
//	}

}
